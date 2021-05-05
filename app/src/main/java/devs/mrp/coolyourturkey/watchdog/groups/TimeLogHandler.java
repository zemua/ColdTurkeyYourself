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
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.BooleanWrap;
import devs.mrp.coolyourturkey.comun.FileReader;
import devs.mrp.coolyourturkey.comun.IntegerWrap;
import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.comun.Notificador;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.databaseroom.apptogroup.AppToGroup;
import devs.mrp.coolyourturkey.databaseroom.apptogroup.AppToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup.ConditionToGroup;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup.ConditionToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.grupoexport.GrupoExport;
import devs.mrp.coolyourturkey.databaseroom.grupoexport.GrupoExportRepository;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivo;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivoRepository;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLogger;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLoggerRepository;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

public class TimeLogHandler implements Feedbacker<Object> {

    private static final String TAG = "TIME_LOG_HANDLER";

    private final Long TIME_BETWEEN_FILES_REFRESH = 60*1000*1L; // 1 minute between file refreshes
    private final Long TIME_BETWEEN_NOTIFICATION_REFRESH = 60*1000*5L; // 5 minutes between checks for notifications
    public static final int FEEDBACK_LOGGERS_CHANGED = 0;

    private List<FeedbackListener<Object>> feedbackListeners = new ArrayList<>();

    private Application mApplication;
    private Context mContext;
    private LifecycleOwner mLifecycleOwner;
    private Handler mMainHandler;
    private LimitHandler mLimitHandler;

    private TimeLoggerRepository timeLoggerRepository;
    private AppToGroupRepository appToGroupRepository;
    private ConditionToGroupRepository conditionToGroupRepository;
    private GrupoExportRepository mGrupoExportRepository;
    private GrupoPositivoRepository mGrupoPositivoRepository;

    private LiveData<List<ConditionToGroup>> mConditionsLiveData; // to remove and re-add observer only
    private Observer<List<ConditionToGroup>> mConditionsLiveDataObserver; // to be cleared from livedata only
    private Map<LiveData<List<TimeLogger>>, Observer<List<TimeLogger>>> mMapOfLoggerLiveDataObserversByConditionId; // to clear observers from livedata only

    private Map<LiveData<List<TimeLogger>>, Observer<List<TimeLogger>>> mMapOfLoggerLiveDataObserversByGroupId; // to clear observers from livedata only

    private Map<Integer, List<TimeLogger>> mTimeLoggersByConditionId;
    private Map<String, TimeSummary> mFileTimeSummaryMap = new HashMap<>();
    private List<AppToGroup> mAppToGroups;
    private List<ConditionToGroup> mAllConditionsToGroup;
    private Map<Integer, Boolean> mAllGruposPositivosIfConditionsMet;
    private List<GrupoPositivo> mAllGruposPositivos;

    private List<GrupoExport> mGrupoExportList;
    private Map<Integer, List<TimeLogger>> mTimeLoggersByGroupId;

    private TimeLogger timeLogger;
    private Long dayRefreshed = 0L;
    private Long mLastFilesChecked;
    private Long mLastFilesExported;
    private Long mLastNotificationsRefreshed;
    private Notificador mNotificador;
    private MisPreferencias mMisPreferencias;

    public TimeLogHandler(Context context, Application application, LifecycleOwner lifecycleOwner){
        mApplication = application;
        mContext = context;
        mLifecycleOwner = lifecycleOwner;
        mMainHandler = new Handler(mContext.getMainLooper());
        mNotificador = new Notificador(application, context);
        mMisPreferencias = new MisPreferencias(context);
        mLimitHandler = new LimitHandler(this, context, application, lifecycleOwner);

        mMapOfLoggerLiveDataObserversByConditionId = new HashMap<>();

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

        mMapOfLoggerLiveDataObserversByGroupId = new HashMap<>();
        mTimeLoggersByGroupId = new HashMap<>();
        mGrupoExportRepository = GrupoExportRepository.getRepo(mApplication);
        mGrupoExportRepository.findAllGrupoExport().observe(mLifecycleOwner, new Observer<List<GrupoExport>>() {
            @Override
            public void onChanged(List<GrupoExport> grupoExports) {
                try { clearExportObservers(); } catch (Exception e) { e.printStackTrace(); }
                mGrupoExportList = grupoExports;
                grupoExports.stream().forEach(export -> {
                    Observer<List<TimeLogger>> observer = new Observer<List<TimeLogger>>() {
                        @Override
                        public void onChanged(List<TimeLogger> timeLoggers) {
                            mTimeLoggersByGroupId.put(export.getGroupId(), timeLoggers);
                        }
                    };
                    LiveData<List<TimeLogger>> liveData = timeLoggerRepository.findByNewerThanAndGroupId(offsetDayInMillis(export.getDays().longValue()), export.getGroupId());
                    mMapOfLoggerLiveDataObserversByGroupId.put(liveData, observer);
                    liveData.observe(mLifecycleOwner, observer);
                });
            }
        });

        mAllGruposPositivosIfConditionsMet = new HashMap<>();
        mGrupoPositivoRepository = GrupoPositivoRepository.getRepo(mApplication);
        mGrupoPositivoRepository.findAllGrupoPositivo().observe(mLifecycleOwner, new Observer<List<GrupoPositivo>>() {
            @Override
            public void onChanged(List<GrupoPositivo> grupoPositivos) {
                mAllGruposPositivos = grupoPositivos;
                grupoPositivos.stream().forEach(grupo -> {
                    if (!mAllGruposPositivosIfConditionsMet.containsKey(grupo.getId())) {
                        mAllGruposPositivosIfConditionsMet.put(grupo.getId(), false);
                    }
                }); // TODO to avoid re-notification when groups change, need to compare ids, not objects
            }
        });

        mNotificador.createNotificationChannel(R.string.condition_met_channel_name, R.string.condition_met_channel_description, Notificador.CONDITION_MET_CHANNEL_ID);

        conditionToGroupRepository = ConditionToGroupRepository.getRepo(application);
        mConditionsLiveData = conditionToGroupRepository.findAllConditionToGroup();
        refreshConditionsObserver();
        refreshDayCounting();
    }

    /**
     * Method that is executed periodically in the WatchdogService to keep the object up to date
     */
    public void watchDog() {
        refreshDayCounting();
        refreshTimeLoggedOnFiles();
        refreshTimeExportedToFiles();
        refreshNotifications();
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

    public boolean ifLimitsReachedForAppName(String packageName) {
        return mLimitHandler.ifLimitsReachedForGroupId(getGroupIdFromPackageName(packageName));
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
        if (ifAllAppConditionsMet(packageName) && !ifLimitsReachedForAppName(packageName)) {
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
        timeLogger.setGroupId(getGroupIdFromPackageName(name));
    }

    private Integer getGroupIdFromPackageName(String name) {
        AppToGroup app = appsToGroupContainsPackageName(name);
        if (app != null) {
            return app.getGroupId();
        } else {
            return -1;
        }
    }

    private Long days(Long milliseconds) {return TimeUnit.MILLISECONDS.toDays(milliseconds);}
    private Long millis(Long days) {return TimeUnit.DAYS.toMillis(days);}
    private Long currentDay() {return days(System.currentTimeMillis());}
    private Long offsetDay(Long nDays) {return currentDay()-nDays;}
    public Long offsetDayInMillis(Long nDays) {return millis(offsetDay(nDays));}


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
            if (mMapOfLoggerLiveDataObserversByConditionId != null) {
                mMapOfLoggerLiveDataObserversByConditionId.keySet().stream().forEach(key -> {
                    key.removeObserver(mMapOfLoggerLiveDataObserversByConditionId.get(key));
                });
                mMapOfLoggerLiveDataObserversByConditionId.clear();
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
        TimeSummary ts = new TimeSummary(cond.getGroupid(), cond.getConditionalgroupid());
        if (mTimeLoggersByConditionId.containsKey(cond.getId())) {
            time = mTimeLoggersByConditionId.get(cond.getId()).stream().collect(Collectors.summingLong(l -> l.getCountedtimemilis()));
        } else if(mFileTimeSummaryMap.containsKey(ts.getKey())) {
            time = mFileTimeSummaryMap.get(ts.getKey()).getSummedTime();
        } else {
            time = 0L;
            Log.d(TAG, "entry for condition not found");
        }
        return time;
    }

    public boolean ifConditionMet(ConditionToGroup cond) {
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
        mMapOfLoggerLiveDataObserversByConditionId.put(timeLoggerLD, observer);
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
                builder.setTimeZone(TimeZone.getTimeZone("GMT"));
                Calendar cal = builder.build();
                Long dateMilis = cal.getTimeInMillis();
                Long offset = offsetDayInMillis(condition.getFromlastndays().longValue());
                if (dateMilis >= offset) {
                    TimeSummary ts = new TimeSummary(condition.getGroupid(), condition.getConditionalgroupid(), consumption);
                    mFileTimeSummaryMap.put(ts.getKey(), ts);
                } else {
                    TimeSummary ts = new TimeSummary(condition.getGroupid(), condition.getConditionalgroupid(), 0L);
                    mFileTimeSummaryMap.put(ts.getKey(), ts);
                    Log.d(TAG, "days offset of file doesn't match requirements for " + condition.getFiletarget());
                }
            } else {
                TimeSummary ts = new TimeSummary(condition.getGroupid(), condition.getConditionalgroupid(), 0L);
                mFileTimeSummaryMap.put(ts.getKey(), ts);
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



    /**
     * Of exporter for sync
     */

    private void refreshTimeExportedToFiles() {
        if (mLastFilesExported == null) {
            mLastFilesExported = 0L;
        }
        Long now = System.currentTimeMillis();
        if (now - TIME_BETWEEN_FILES_REFRESH > mLastFilesExported && mGrupoExportList != null) {
            mLastFilesExported = now;
            mGrupoExportList.stream().forEach(export -> {
                if (FileReader.ifHaveWrittingRights(mContext, Uri.parse(export.getArchivo()))) {
                    Long timeMillis = mTimeLoggersByGroupId.get(export.getGroupId()).stream().collect(Collectors.summingLong(logger -> logger.getCountedtimemilis()));
                    FileReader.writeTextToUri(mApplication, Uri.parse(export.getArchivo()), daysToFileFormat(export.getDays(), timeMillis));
                }
            });
        }
    }

    private void clearExportObservers() throws Exception {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mMapOfLoggerLiveDataObserversByGroupId.keySet().stream().forEach(livedata -> {
                livedata.removeObserver(mMapOfLoggerLiveDataObserversByGroupId.get(livedata));
            });
            mMapOfLoggerLiveDataObserversByGroupId.clear();
        } else {
            throw new Exception("clearExportObservers shall be called from the main thread");
        }
    }

    private String daysToFileFormat(Integer offsetDays, Long millisToAppend) {
        // Format is:
        // YYYY-MM-DD-XXXXX

        Long millis = offsetDayInMillis(offsetDays.longValue());

        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(millis);

        int year = date.get(Calendar.YEAR);
        int monthnum = date.get(Calendar.MONTH);
        int daynum = date.get(Calendar.DAY_OF_MONTH);

        String month;
        if (monthnum<10) {
            month = "0"+monthnum;
        } else {
            month = String.valueOf(monthnum);
        }

        String day;
        if (daynum<10) {
            day = "0"+daynum;
        } else {
            day = String.valueOf(daynum);
        }

        StringBuilder builder = new StringBuilder();
        builder.append(String.valueOf(year)).append("-")
                .append(month).append("-")
                .append(day).append("-")
                .append(String.valueOf(millisToAppend));
        return builder.toString();
    }

    /**
     * Notifications when new groups are available to sum points
     */

    private void refreshNotifications() {
        if (mLastNotificationsRefreshed == null) {
            mLastNotificationsRefreshed = 0L;
        }
        Long now = System.currentTimeMillis();
        if (now - TIME_BETWEEN_NOTIFICATION_REFRESH > mLastNotificationsRefreshed) {
            mLastNotificationsRefreshed = now;
            mAllGruposPositivosIfConditionsMet.keySet().stream().forEach(key -> {
                if (!mAllGruposPositivosIfConditionsMet.get(key) && ifAllGroupConditionsMet(key)) {
                    mAllGruposPositivosIfConditionsMet.put(key, true);
                    StringBuilder builder = new StringBuilder();
                    mAllGruposPositivos.stream().forEach(grupo -> {
                        if (grupo.getId() == key) {
                            builder.append(grupo.getNombre());
                        }
                    });
                    String title = builder.toString();
                    String description = mApplication.getString(R.string.cumple_las_condiciones);
                    if (mMisPreferencias.getNotifyConditionsJustMet()) {
                        mNotificador.createNotification(R.drawable.clock_time_eight, title, description, Notificador.CONDITION_MET_CHANNEL_ID, key);
                    }
                } else if (mAllGruposPositivosIfConditionsMet.get(key) && !ifAllGroupConditionsMet(key)) {
                    mAllGruposPositivosIfConditionsMet.put(key, false);
                }
            });
        }
    }

}