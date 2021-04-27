package devs.mrp.coolyourturkey.watchdog.groups;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.comun.BooleanWrap;
import devs.mrp.coolyourturkey.comun.FileReader;
import devs.mrp.coolyourturkey.comun.IntegerWrap;
import devs.mrp.coolyourturkey.comun.LongWrapper;
import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.databaseroom.apptogroup.AppToGroup;
import devs.mrp.coolyourturkey.databaseroom.apptogroup.AppToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup.ConditionToGroup;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup.ConditionToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLogger;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLoggerRepository;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

public class TimeLogHandler implements Feedbacker<Object> {

    // TODO test and debug file-conditions are read correctly

    private static final String TAG = "TIME_LOG_HANDLER";

    private final Long TIME_BETWEEN_FILES_REFRESH = 60*1000*1L; // 1 minute between file refreshes
    public static final int FEEDBACK_LOGGERS_CHANGED = 0;

    private List<FeedbackListener<Object>> feedbackListeners = new ArrayList<>();

    private Application mApplication;
    private Context mContext;
    private LifecycleOwner mLifecycleOwner;
    private Handler mMainHandler;
    private TimeLoggerRepository timeLoggerRepository;
    private AppToGroupRepository appToGroupRepository;
    private ConditionToGroupRepository conditionToGroupRepository;

    private LiveData<List<ConditionToGroup>> mConditionsLiveData; // to remove and re-add observer only
    private Observer<List<ConditionToGroup>> mConditionsLiveDataObserver; // to be cleared from livedata only
    private Map<LiveData<List<TimeLogger>>, Observer<List<TimeLogger>>> mMapOfLoggerLiveDataObservers; // to clear observers from livedata only

    private Map<Integer, List<TimeLogger>> mTimeLoggersByConditionId;
    private Map<String, TimeSummary> mFileTimeSummaryMap = new HashMap<>();
    private List<AppToGroup> mAppToGroups;
    private List<ConditionToGroup> mAllConditionsToGroup;

    private TimeLogger timeLogger;
    private Long dayRefreshed = 0L;
    private Long mLastFilesChecked;

    public TimeLogHandler(Context context, Application application, LifecycleOwner lifecycleOwner){
        mApplication = application;
        mContext = context;
        mLifecycleOwner = lifecycleOwner;
        mMainHandler = new Handler(mContext.getMainLooper());

        mMapOfLoggerLiveDataObservers = new HashMap<>();

        timeLoggerRepository = TimeLoggerRepository.getRepo(application);
        mTimeLoggersByConditionId = new HashMap<>();
        appToGroupRepository = AppToGroupRepository.getRepo(application);
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                appToGroupRepository.findAllAppToGroup().observe(lifecycleOwner, new Observer<List<AppToGroup>>() {
                    @Override
                    public void onChanged(List<AppToGroup> appToGroups) {
                        mAppToGroups = appToGroups;
                    }
                });
            }
        });

        conditionToGroupRepository = ConditionToGroupRepository.getRepo(application);
        mConditionsLiveData = conditionToGroupRepository.findAllConditionToGroup();

        refreshConditionsObserver();
        refreshDayCounting();
    }

    public void watchDog() {
        refreshDayCounting();
        refreshTimeLoggedOnFiles();
    }

    @Override
    public void giveFeedback(int tipo, Object feedback) {
        feedbackListeners.stream().forEach(l -> {
            l.giveFeedback(tipo, feedback);
        });
    }

    @Override
    public void addFeedbackListener(FeedbackListener<Object> listener) {
        feedbackListeners.add(listener);
    }

    private class TimeSummary {
        private Integer groupId;
        private Integer conditionGroupId;
        private Long summedTime;
        TimeSummary(Integer groupId, Integer conditionGroupId, Long summedTime) {
            this.groupId = groupId;
            this.conditionGroupId = conditionGroupId;
            this.summedTime = summedTime;
        }
        TimeSummary(Integer groupId, Integer conditionGroupId) {
            this.groupId = groupId;
            this.conditionGroupId = conditionGroupId;
        }
        String getKey() { return "A" + groupId + "B" + conditionGroupId; }
        Integer getGroupId() { return groupId; }
        Integer getConditionGroupId() { return conditionGroupId; }
        Long getSummedTime() { return summedTime; }
    }

    public boolean ifAllAppConditionsMet(String packageName) {
        AppToGroup app = appsToGroupContainsPackageName(packageName);
        if (app != null){
            return ifAllGroupConditionsMet(app.getGroupId());
        } else {
            Log.d(TAG, "no packageName found in appsToGroupsVSpackageNameMap for ifAllAppConditionsMet");
            Log.d(TAG, "this package is not assigned to a group, and so it has no conditions to meet");
            return true;
        }
    }


    private AppToGroup appsToGroupContainsPackageName(String packageName){
        IntegerWrap id = new IntegerWrap(-1);
        StringBuilder name = new StringBuilder();
        IntegerWrap groupId = new IntegerWrap(-1);
        BooleanWrap containsKey = new BooleanWrap();
        containsKey.set(false);
        mAppToGroups.stream().forEach(k -> {
            if(k.getAppName().equals(packageName)){
                containsKey.set(true);
                id.set(k.getId());
                name.append(k.getAppName());
                groupId.set(k.getGroupId());
            }
        });
        if(containsKey.get()){
            AppToGroup app = new AppToGroup(name.toString(), groupId.get());
            app.setId(id.get());
            return app;
        } else {
            return null;
        }
    }




    /**
     *
     * From here are the inputs received that are
     * going to be saved into the db
     *
     *
     *
     *
     */

    private void initTimeLogger(String packageName, Long millis){
        timeLogger = new TimeLogger();
        setPackageNameAndAutoAssignGroupId(packageName);
        setCurrent(millis);
    }

    private void clearTimeLoggerReference() {
        timeLogger = null;
    }

    public void insertTimeBadApp(String packageName, Long millis) throws Exception {
        initTimeLogger(packageName, millis);
        decrease(millis);
        timeLogger.setPositivenegative(TimeLogger.Type.NEGATIVE);
        send();
    }

    public void insertTimeGoodApp(String packageName, Long millis) throws Exception {
        initTimeLogger(packageName, millis);
        increase(millis);
        if (ifAllAppConditionsMet(packageName)) {
            timeLogger.setPositivenegative(TimeLogger.Type.POSITIVECONDITIONSMET);
        } else {
            timeLogger.setPositivenegative(TimeLogger.Type.POSITIVECONDITIONSNOTMET);
        }
        send();
    }

    public void insertTimeNeutralApp(String packageName, Long millis) throws Exception {
        initTimeLogger(packageName, millis);
        timeLogger.setCountedtimemilis(0L);
        timeLogger.setPositivenegative(TimeLogger.Type.NEUTRAL);
        send();
    }

    private void send() throws Exception {
        if (!checkIfDataEnoughAndSubmit()){
            throw new Exception("Data provided in the submitted TimeLogger is not enough");
        }
        clearTimeLoggerReference();
    }

    private boolean checkIfDataEnoughAndSubmit() {
        if (timeLogger.getCountedtimemilis() == null) {
            Log.d(TAG, "CountedTime for summatory purposes not set for timeLogger");
            return false;
        }
        else if (timeLogger.getPackageName() == null){
            Log.d(TAG, "Package Name is not set for timeLogger");
            return false;
        }
        else if (timeLogger.getMillistimestamp() == null) {
            Log.d(TAG, "TimeStamp is not set for timeLogger");
            return false;
        }
        else if (timeLogger.getPositivenegative() == null) {
            Log.d(TAG, "Positive/Negative/Neutral Type is not set for timeLogger");
            return false;
        }
        else if (timeLogger.getUsedtimemilis() == null) {
            Log.d(TAG, "UsedTime is not set for timeLogger");
            return false;
        }
        else {
            submitTimeLogger();
            return true;
        }
    }

    private void submitTimeLogger() {
        timeLoggerRepository.insert(timeLogger);
    }

    private void setCurrent(Long millis) {
        if (millis >= 0) {
            timeLogger.setUsedtimemilis(millis);
        } else {
            timeLogger.setUsedtimemilis(-millis);
        }
        timeLogger.setMillistimestamp(System.currentTimeMillis());
    }

    private void increase(Long milis) {
        if (milis >= 0){
            timeLogger.setCountedtimemilis(milis);
        } else {
            timeLogger.setCountedtimemilis(-milis);
        }
    }

    private void decrease(Long milis) {
        if (milis <= 0) {
            timeLogger.setCountedtimemilis(milis);
        } else {
            timeLogger.setCountedtimemilis(-milis);
        }
    }

    private void maintain() {
        timeLogger.setCountedtimemilis(0L);
    }

    private void setGroupId(Integer groupId) {
        timeLogger.setGroupId(groupId);
    }

    private void setPackageName(String packageName) {
        timeLogger.setPackageName(packageName);
    }

    private void setPackageNameAndAutoAssignGroupId(String packageName) {
        timeLogger.setPackageName(packageName);
        assignGroupIdFromPackageName(packageName);
    }

    private void assignGroupIdFromPackageName(String name) {
        AppToGroup app = appsToGroupContainsPackageName(name);
        if (app != null) {
            timeLogger.setGroupId(app.getGroupId());
        } else {
            //Log.d(TAG, "no package found in appsToGroupsVSpackageNameMap for assignGroupIdFromPackageName");
            timeLogger.setGroupId(-1);
        }
    }

    private Long days(Long milliseconds) {return TimeUnit.MILLISECONDS.toDays(milliseconds);}
    private Long millis(Long days) {return TimeUnit.DAYS.toMillis(days);}
    private Long currentDay() {return days(System.currentTimeMillis());}
    private Long offsetDay(Long nDays) {return currentDay()-nDays;}
    private Long offsetDayInMillis(Long nDays) {return millis(offsetDay(nDays));}


    /**
     * Refresh for all kinds of conditions
     */

    private void refreshDayCounting() {
        // to refresh the observers when the day changes, so they look for time spent from a new "start day"
        Long currentDay = currentDay();
        if (!dayRefreshed.equals(currentDay) && mAllConditionsToGroup != null){
            dayRefreshed = currentDay;
            refreshConditionsObserver();
        }
    }

    private void refreshConditionsObserver() { // called when we change the day
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    clearConditionsObserver();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mConditionsLiveDataObserver = new Observer<List<ConditionToGroup>>() {
                    @Override
                    public void onChanged(List<ConditionToGroup> conditionToGroups) {
                        mAllConditionsToGroup = conditionToGroups;
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    clearLogObservers();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                conditionToGroups.stream().forEach(c -> {
                                    if (c.getType().equals(ConditionToGroup.ConditionType.GROUP)) {
                                        try {
                                            observeTimeLoggedOnGroup(c);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else if (c.getType().equals(ConditionToGroup.ConditionType.FILE)) {
                                        observeTimeLoggedOnFile(c);
                                    }
                                });
                            }
                        });
                    }
                };
                mConditionsLiveData.observe(mLifecycleOwner, mConditionsLiveDataObserver);
            }
        });
    }

    private void clearConditionsObserver() throws Exception {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            clearLogObservers();
            if (mConditionsLiveData != null) {
                mConditionsLiveData.removeObserver(mConditionsLiveDataObserver);
            }
        } else {
            throw new Exception("clearConditionsObserver to be called in main thread");
        }
    }

    private void clearLogObservers() throws Exception {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (mMapOfLoggerLiveDataObservers != null) {
                mMapOfLoggerLiveDataObservers.keySet().stream().forEach(key -> {
                    key.removeObserver(mMapOfLoggerLiveDataObservers.get(key));
                });
                mMapOfLoggerLiveDataObservers.clear();
            }
        } else {
            throw new Exception("clearLogObservers to be called in main thread");
        }
    }

    /**
     *
     * Condition checkers for all types
     *
     */

    public Long getTimeCountedOnGroupCondition(ConditionToGroup cond) {
        Long time;
        if (mTimeLoggersByConditionId.containsKey(cond.getId())) {
            time = mTimeLoggersByConditionId.get(cond.getId()).stream().collect(Collectors.summingLong(l -> l.getCountedtimemilis()));
        } else {
            time = 0L;
        }
        Log.d(TAG, "time counted: " + time);
        return time;
    }

    public boolean ifConditionMet(ConditionToGroup cond) {
        Log.d(TAG, "time needed: " + MilisToTime.getMilisDeMinutos(cond.getConditionalminutes()));
        if (getTimeCountedOnGroupCondition(cond) >= MilisToTime.getMilisDeMinutos(cond.getConditionalminutes())){
            return true;
        } return false;
    }

    private List<ConditionToGroup> getListOfConditionIdsOfGroup(Integer groupId) {
        List<ConditionToGroup> list = new ArrayList<>();
        if (mAllConditionsToGroup != null) {
            mAllConditionsToGroup.stream().forEach(c -> {
                if (c.getGroupid() == groupId) {
                    list.add(c);
                }
            });
        }
        return list;
    }

    private boolean ifAllGroupConditionsMet(Integer groupId) {
        BooleanWrap b = new BooleanWrap();
        b.set(true);
        getListOfConditionIdsOfGroup(groupId).stream().forEach(c -> {
            if (!ifConditionMet(c)){
                b.set(false);
            }
        });
        return b.get();
    }

    /**
     * From here are the reading times for conditions
     * with time spent on other groups
     *
     *
     *
     *
     *
     *
     *
     */

    private void observeTimeLoggedOnGroup(ConditionToGroup c) throws Exception {
        LiveData<List<TimeLogger>> timeLoggerLD = timeLoggerRepository.findByNewerThanAndGroupId(offsetDayInMillis(c.getFromlastndays().longValue()), c.getConditionalgroupid());
        Observer<List<TimeLogger>> observer = new Observer<List<TimeLogger>>() {
            @Override
            public void onChanged(List<TimeLogger> timeLoggers) {
                mTimeLoggersByConditionId.put(c.getId(), timeLoggers);
                giveFeedback(FEEDBACK_LOGGERS_CHANGED, null);
            }
        };
        mMapOfLoggerLiveDataObservers.put(timeLoggerLD, observer);
        timeLoggerLD.observe(mLifecycleOwner, observer);
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new Exception("observeTimeLoggedOnGroup shall be called from main thread");
        }
    }







    /**
     *
     * From here the data to be read from files
     * is to be handled
     *
     *
     *
     *
     *
     *
     *
     */

    private void observeTimeLoggedOnFile(ConditionToGroup condition){
        Uri uri = Uri.parse(condition.getFiletarget());
        if (FileReader.ifHaveReadingRights(mContext, uri)) {
            /**
             * Important, the file content must be in the following format and have one single entry:
             * YYYY-MM-DD-XXXXX
             * YYYY = year
             * MM = month
             * DD = day
             * XXXXX = time to be accounted in milliseconds
             */
            String value = FileReader.readTextFromUri(mApplication, uri);
            if (value.matches("^\\d{4}-\\d{2}-\\d{2}-\\d+$")) {
                String[] values = value.split("-");
                Long year = Long.valueOf(values[0]);
                Long month = Long.valueOf(values[1]);
                Long day = Long.valueOf(values[2]);
                Long consumption = Long.valueOf(values[3]);

                Calendar.Builder builder = new Calendar.Builder();
                builder.setDate(year.intValue(), month.intValue(), day.intValue());
                Calendar cal = builder.build();
                Long dateMilis = cal.getTimeInMillis();
                if (dateMilis >= offsetDayInMillis(condition.getFromlastndays().longValue())) {
                    TimeSummary ts = new TimeSummary(condition.getGroupid(), condition.getConditionalgroupid(), consumption);
                    mFileTimeSummaryMap.put(ts.getKey(), ts);
                }
            } else {
                Log.d(TAG, "content of file doesn't match requirements for " + condition.getFiletarget());
            }
        } else {
            Log.d(TAG, "no rights to read file " + condition.getFiletarget());
        }
    }

    private void refreshTimeLoggedOnFiles() {
        if (mLastFilesChecked == null) {
            mLastFilesChecked = 0L;
        }
        Long now = System.currentTimeMillis();
        if (now-TIME_BETWEEN_FILES_REFRESH > mLastFilesChecked && mAllConditionsToGroup != null) {
            mLastFilesChecked = now;
            mAllConditionsToGroup.stream().forEach(c -> {
                if (c.getType() == ConditionToGroup.ConditionType.FILE){
                    observeTimeLoggedOnFile(c);
                }
            });
        }
    }
}
