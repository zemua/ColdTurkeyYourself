package devs.mrp.coolyourturkey.watchdog.groups;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
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
import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.databaseroom.apptogroup.AppToGroup;
import devs.mrp.coolyourturkey.databaseroom.apptogroup.AppToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup.ConditionToGroup;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup.ConditionToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLogger;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLoggerRepository;

public class TimeLogHandler {

    private static final String TAG = "TIME_LOG_HANDLER";

    private final Long TIME_BETWEEN_FILES_REFRESH = 60*1000*1L; // 1 minute between file refreshes

    private Application mApplication;
    private Context mContext;
    private LifecycleOwner mLifecycleOwner;
    private Handler mMainHandler;
    private TimeLoggerRepository timeLoggerRepository;
    private AppToGroupRepository appToGroupRepository;
    private ConditionToGroupRepository conditionToGroupRepository;
    private LiveData<List<ConditionToGroup>> mConditionsLiveData;
    private Observer<List<ConditionToGroup>> mConditionsLiveDataObserver; // to remove the observers individually in main thread
    private List<ConditionToGroup> mConditions;
    private Map<String, AppToGroup> appsToGroupsVSpackageNameMap; // to get the group from the packagename
    private Map<Integer, List<ConditionToGroup>> conditionToGroupListVSgroupIdMap; // to get list of group-conditions for each group
    private Map<Integer, List<ConditionToGroup>> conditionToFileListVSgroupIdMap; // to get list of file-conditions for each group
    private List<LiveData<List<TimeLogger>>> listOfLoggerLiveDatas; // for removing observers before re-observing new ones
    private Map<LiveData<List<TimeLogger>>,List<Observer<List<TimeLogger>>>> listOfLoggerLiveDataObservers; // to remove the observers individually and avoid bulk-remove while adding new ones in a different thread
    private Map<String, TimeSummary> timeSummaryMap; // to get the time for each group and condition, be it group, file...
    private TimeLogger timeLogger;
    private Long dayRefreshed = 0L;
    private Long mLastFilesChecked;

    public TimeLogHandler(Context context, Application application, LifecycleOwner lifecycleOwner){
        mApplication = application;
        mContext = context;
        mLifecycleOwner = lifecycleOwner;
        mMainHandler = new Handler(mContext.getMainLooper());

        timeLoggerRepository = TimeLoggerRepository.getRepo(application);
        appToGroupRepository = AppToGroupRepository.getRepo(application);
        appToGroupRepository.findAllAppToGroup().observe(lifecycleOwner, new Observer<List<AppToGroup>>() {
            @Override
            public void onChanged(List<AppToGroup> appToGroups) {
                appsToGroupsVSpackageNameMap = appToGroups.stream().collect(Collectors.toMap(AppToGroup::getAppName, app -> app));
            }
        });

        conditionToGroupListVSgroupIdMap = new HashMap<>();
        conditionToFileListVSgroupIdMap = new HashMap<>();
        listOfLoggerLiveDatas = new ArrayList<>();
        listOfLoggerLiveDataObservers = new HashMap<>();
        timeSummaryMap = new HashMap<>();
        conditionToGroupRepository = ConditionToGroupRepository.getRepo(application);
        mConditionsLiveData = conditionToGroupRepository.findAllConditionToGroup();
        refreshDayCounting();
    }

    public void watchDog() {
        refreshDayCounting();
        refreshTimeLoggedOnFiles();
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
        if (appsToGroupsVSpackageNameMap.containsKey(packageName)){
            Integer groupId = appsToGroupsVSpackageNameMap.get(packageName).getGroupId();
            return ifAllGroupConditionsMet(groupId);
        } else {
            Log.d(TAG, "no packageName found in appsToGroupsVSpackageNameMap for ifAllAppConditionsMet");
            Log.d(TAG, "this package is not assigned to a group, and so it has no conditions to meet");
            return true;
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
        decrease(millis);
    }

    private void clearTimeLoggerReference() {
        timeLogger = null;
    }

    public void insertTimeBadApp(String packageName, Long millis) throws Exception {
        initTimeLogger(packageName, millis);
        timeLogger.setPositivenegative(TimeLogger.Type.NEGATIVE);
        send();
    }

    public void insertTimeGoodApp(String packageName, Long millis) throws Exception {
        initTimeLogger(packageName, millis);
        timeLogger.setPositivenegative(TimeLogger.Type.POSITIVE);
        send();
    }

    public void insertTimeNeutralApp(String packageName, Long millis) throws Exception {
        initTimeLogger(packageName, millis);
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
        timeLogger.setUsedtimemilis(millis);
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
        if (appsToGroupsVSpackageNameMap.containsKey(name)) {
            timeLogger.setGroupId(appsToGroupsVSpackageNameMap.get(name).getGroupId());
        } else {
            Log.d(TAG, "no package found in appsToGroupsVSpackageNameMap for assignGroupIdFromPackageName");
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

    private void refreshConditionsObserver() { // called when we change the day
        clearConditionsObserver();
        mConditionsLiveDataObserver = new Observer<List<ConditionToGroup>>() {
            @Override
            public void onChanged(List<ConditionToGroup> conditionToGroups) {
                if (conditionToGroupListVSgroupIdMap != null) {conditionToGroupListVSgroupIdMap.clear();}
                if (conditionToFileListVSgroupIdMap != null) {conditionToFileListVSgroupIdMap.clear();}
                clearLogObservers();
                clearFileObservers();
                conditionToGroups.stream().forEach(c -> {
                    if (c.getType().equals(ConditionToGroup.ConditionType.GROUP)) {
                        addConditionToGroupMap(c);
                        observeTimeLoggedOnGroup(c);
                    } else if (c.getType().equals(ConditionToGroup.ConditionType.FILE)) {
                        addConditionToFileMap(c);
                        observeTimeLoggedOnFile(c);
                    }
                });
            }
        };
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mConditionsLiveData.observe(mLifecycleOwner, mConditionsLiveDataObserver);
            }
        });
    }

    private void refreshDayCounting() {
        // to refresh the observers when the day changes, so they look for time spent from a new "start day"
        if (dayRefreshed != currentDay()){
            dayRefreshed = currentDay();
            refreshConditionsObserver();
        }
    }

    /**
     *
     * Condition checkers for all types
     *
     */

    private Long getTimeCountedOnGroupCondition(Integer groupId, Integer conditionId) {
        TimeSummary t = new TimeSummary(groupId, conditionId);
        String key = t.getKey();
        Long time;
        if (timeSummaryMap.containsKey(key)) {
            time = timeSummaryMap.get(key).getSummedTime();
        } else {
            Log.d(TAG, "no time found on timeSummaryMap for getTimeCountedOnGroupCondition");
            time = 0L;
        }
        return time;
    }

    private Long getMillisRequiredOnGroupCondition(Integer groupId, Integer conditionId) {
        if (conditionToGroupListVSgroupIdMap.containsKey(groupId)) {
            return milisecondsRequiredByCondition(conditionToGroupListVSgroupIdMap, groupId, conditionId);
        }
        if (conditionToFileListVSgroupIdMap.containsKey(groupId)){
            return milisecondsRequiredByCondition(conditionToFileListVSgroupIdMap, groupId, conditionId);
        }
        Log.d(TAG, "no matching condition found for getMillisRequiredOnGroupCondition");
        return 0L;
    }

    private Long milisecondsRequiredByCondition(Map<Integer, List<ConditionToGroup>> map, Integer groupId, Integer conditionId){
        ConditionToGroup con = new ConditionToGroup();
        map.get(groupId).stream().forEach(c -> {
            if (c.getId().equals(conditionId)) {
                con.cloneCondition(c);
            }
        });
        return MilisToTime.getMilisDeMinutos(con.getConditionalminutes());
    }

    private boolean ifConditionMet(Integer groupId, Integer conditionId) { // TODO check times spent on each condition
        if (getTimeCountedOnGroupCondition(groupId, conditionId) >= getMillisRequiredOnGroupCondition(groupId, conditionId)){
            return true;
        } return false;
    }

    private List<Integer> getListOfConditionIdsOfGroup(Integer groupId) {
        List<Integer> list = new ArrayList<>();
        if (conditionToGroupListVSgroupIdMap.containsKey(groupId)){
            list.addAll(conditionToGroupListVSgroupIdMap.get(groupId).stream().map(con -> con.getId()).collect(Collectors.toList()));
        }
        if (conditionToFileListVSgroupIdMap.containsKey(groupId)) {
            list.addAll(conditionToFileListVSgroupIdMap.get(groupId).stream().map(con -> con.getId()).collect(Collectors.toList()));
        }
        return list;
    }

    private boolean ifAllGroupConditionsMet(Integer groupId) {
        BooleanWrap b = new BooleanWrap();
        b.set(true);
        getListOfConditionIdsOfGroup(groupId).stream().forEach(c -> {
            if (!ifConditionMet(groupId, c)){
                b.set(false);
            }
        });
        Log.d(TAG, "all group conditions met? " + b.get());
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

    private void clearConditionsObserver() {
        clearLogObservers();
        if (mConditionsLiveDataObserver!=null) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    mConditionsLiveData.removeObserver(mConditionsLiveDataObserver);
                }
            });
        }
    }

    private void addConditionToGroupMap(ConditionToGroup condition) {
        if (!conditionToGroupListVSgroupIdMap.containsKey(condition.getGroupid())) {
            conditionToGroupListVSgroupIdMap.put(condition.getGroupid(), new ArrayList<>());
        }
        conditionToGroupListVSgroupIdMap.get(condition.getGroupid()).add(condition);
    }

    private void clearLogObservers() {
        listOfLoggerLiveDatas.stream().forEach(ld -> {
            // observers are removed individually instead of bulk
            // to prevent deleting new added observers in background thread
            // since observers can only be removed in main thread
            listOfLoggerLiveDataObservers.get(ld).stream().forEach(obs -> {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ld.removeObserver(obs);
                    }
                });
            });
        });
        timeSummaryMap.clear();
    }

    private void observeTimeLoggedOnGroup(ConditionToGroup c) {
        LiveData<List<TimeLogger>> timesLogged = timeLoggerRepository.findByNewerThanAndGroupId(offsetDayInMillis(c.getFromlastndays().longValue()), c.getGroupid());
        listOfLoggerLiveDatas.add(timesLogged);
        Observer<List<TimeLogger>> observer = new Observer<List<TimeLogger>>() {
            @Override
            public void onChanged(List<TimeLogger> timeLoggers) {
                Long sum = timeLoggers.stream().collect(Collectors.summingLong(t -> t.getCountedtimemilis()));
                TimeSummary summary = new TimeSummary(c.getGroupid(), c.getConditionalgroupid(), sum);
                timeSummaryMap.put(summary.getKey(), summary);
            }
        };
        if (!listOfLoggerLiveDataObservers.containsKey(timesLogged)) {
            listOfLoggerLiveDataObservers.put(timesLogged, new ArrayList<>());
        }
        listOfLoggerLiveDataObservers.get(timesLogged).add(observer);
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                timesLogged.observe(mLifecycleOwner, observer);
            }
        });
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
                    timeSummaryMap.put(ts.getKey(), ts);
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
        if (now-TIME_BETWEEN_FILES_REFRESH > mLastFilesChecked && conditionToFileListVSgroupIdMap != null) {
            Log.d(TAG, "updating time logged in files");
            mLastFilesChecked = now;
            Set<Integer> set = conditionToFileListVSgroupIdMap.keySet();
            set.stream().forEach(groupId -> {
                List<ConditionToGroup> conditionsList = conditionToFileListVSgroupIdMap.get(groupId);
                conditionsList.stream().forEach(c -> observeTimeLoggedOnFile(c));
            });
        }
    }

    private void addConditionToFileMap(ConditionToGroup condition) {
        if (!conditionToFileListVSgroupIdMap.containsKey(condition.getGroupid())) {
            conditionToFileListVSgroupIdMap.put(condition.getGroupid(), new ArrayList<>());
        }
        conditionToFileListVSgroupIdMap.get(condition.getGroupid()).add(condition);
    }

    private void clearFileObservers(){ // called when ConditionToGroup list changes
        if (conditionToFileListVSgroupIdMap != null) {
            conditionToFileListVSgroupIdMap.clear();
        }
    }

}
