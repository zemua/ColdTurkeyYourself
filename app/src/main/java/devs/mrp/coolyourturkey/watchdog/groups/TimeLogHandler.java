package devs.mrp.coolyourturkey.watchdog.groups;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.BooleanWrap;
import devs.mrp.coolyourturkey.comun.FileReader;
import devs.mrp.coolyourturkey.comun.FileTimeGetter;
import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.comun.Notificador;
import devs.mrp.coolyourturkey.comun.impl.FileTimeGetterImpl;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger.TimeBlockLogger;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger.TimeBlockLoggerRepository;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup_old_deprecated.ConditionToGroup;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup_old_deprecated.ConditionToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementToGroup;
import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementType;
import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.databaseroom.grupo.GrupoRepository;
import devs.mrp.coolyourturkey.databaseroom.grupoexport.GrupoExport;
import devs.mrp.coolyourturkey.databaseroom.grupoexport.GrupoExportRepository;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivo;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivoRepository;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLogger;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLoggerRepository;
import devs.mrp.coolyourturkey.grupos.packagemapper.PackageMapper;
import devs.mrp.coolyourturkey.grupos.packagemapper.impl.PackageMapperFactory;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

public class TimeLogHandler implements Feedbacker<Object> {

    private static final String TAG = "TIME_LOG_HANDLER";

    private final Long TIME_BETWEEN_FILES_REFRESH = 60 * 1000 * 1L; // 1 minute between file refreshes
    private final Long TIME_BETWEEN_NOTIFICATION_REFRESH = 60 * 1000 * 5L; // 5 minutes between checks for notifications
    public static final int FEEDBACK_LOGGERS_CHANGED = 0;

    private List<FeedbackListener<Object>> feedbackListeners = new ArrayList<>();

    private Application mApplication;
    private Context mContext;
    private LifecycleOwner mLifecycleOwner;
    private Handler mMainHandler;
    private LimitHandler mLimitHandler;

    private TimeLoggerRepository timeLoggerRepository;
    private TimeBlockLoggerRepository timeBlockLoggerRepository;
    private Map<String, ElementToGroup> elementsByPackageName;
    private ElementToGroupRepository elementToGroupRepository;
    private ConditionToGroupRepository conditionToGroupRepository;
    private GrupoExportRepository mGrupoExportRepository;
    private GrupoRepository mGrupoRepository;

    private LiveData<List<ConditionToGroup>> mConditionsLiveData;
    private LiveData<List<GrupoExport>> mGrupoExportLiveData;
    private List<LiveData<?>> observableGroups;
    private List<LiveData<?>> observableLoggersByGroupId;
    private List<LiveData<?>> observableByGroupCondition;
    private List<LiveData<?>> observableByRandomCheckCondition;
    private List<LiveData<?>> observableConditions;

    private Map<Integer, List<TimeLogger>> mTimeLoggersByConditionId;
    private Map<Integer, List<TimeBlockLogger>> mRandomCheckLoggersByConditionId;
    //private Map<String, TimeSummary> mFileTimeSummaryMap = new HashMap<>();
    private FileTimeGetter fileTimeGetter;
    private List<ConditionToGroup> mAllConditionsToGroup;
    private Map<Integer, Boolean> mAllGruposPositivosIfConditionsMet;
    private List<Grupo> mAllGrupos;
    private Map<Integer, List<TimeLogger>> mTodayTimeByGroupMap;

    private List<GrupoExport> mGrupoExportList;
    private Map<Integer, List<TimeLogger>> mTimeLoggersByGroupId;

    private TimeLogger timeLogger;
    private Long dayRefreshed = 0L;
    private Long mLastFilesChecked;
    private Long mLastFilesExported;
    private Long mLastNotificationsRefreshed;
    private Notificador mNotificador;
    private MisPreferencias mMisPreferencias;

    public TimeLogHandler(Context context, Application application, LifecycleOwner lifecycleOwner) {
        mApplication = application;
        mContext = context;
        mLifecycleOwner = lifecycleOwner;
        observableGroups = new ArrayList<>();
        observableLoggersByGroupId = new ArrayList<>();
        observableByGroupCondition = new ArrayList<>();
        observableByRandomCheckCondition = new ArrayList<>();
        mMainHandler = new Handler(mContext.getMainLooper());
        mNotificador = new Notificador(application, context);
        mMisPreferencias = new MisPreferencias(context);
        mLimitHandler = new LimitHandler(this, context, application, lifecycleOwner);
        fileTimeGetter = new FileTimeGetterImpl(application);

        mTodayTimeByGroupMap = new HashMap<>();

        timeLoggerRepository = TimeLoggerRepository.getRepo(application);
        timeBlockLoggerRepository = TimeBlockLoggerRepository.getRepo(application);
        mTimeLoggersByConditionId = new HashMap<>();
        mRandomCheckLoggersByConditionId = new HashMap<>();
        elementToGroupRepository = ElementToGroupRepository.getRepo(application);
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                elementToGroupRepository.findElementsOfType(ElementType.APP).observe(lifecycleOwner, elements -> {
                    Map<String, ElementToGroup> elementsMap = new HashMap<>();
                    elements.stream().forEach(e -> elementsMap.put(e.getName(), e));
                    elementsByPackageName = elementsMap;
                });
            }
        });

        mTimeLoggersByGroupId = new HashMap<>();
        mGrupoExportRepository = GrupoExportRepository.getRepo(mApplication);
        mGrupoExportLiveData = mGrupoExportRepository.findAllGrupoExport();
        setExportObservers();

        mAllGruposPositivosIfConditionsMet = new HashMap<>();
        mGrupoRepository = GrupoRepository.getRepo(mApplication);
        mGrupoRepository.findGruposPositivos().observe(mLifecycleOwner, new Observer<List<Grupo>>() {
            @Override
            public void onChanged(List<Grupo> grupoPositivos) {
                mAllGrupos = grupoPositivos;
                grupoPositivos.stream().forEach(grupo -> {
                    if (!mAllGruposPositivosIfConditionsMet.containsKey(grupo.getId())) {
                        mAllGruposPositivosIfConditionsMet.put(grupo.getId(), false);
                    }
                    observeTodayGroupTime(grupo);
                });
            }
        });

        mNotificador.createNotificationChannel(R.string.condition_met_channel_name, R.string.condition_met_channel_description, Notificador.CONDITION_MET_CHANNEL_ID);

        conditionToGroupRepository = ConditionToGroupRepository.getRepo(application);
        mConditionsLiveData = conditionToGroupRepository.findAllConditionToGroup();
        refreshConditionsObserver();
        refreshDayCounting();
    }

    /**
     * Set the observers for the exported group files, to be called on day-change too
     */
    private void setExportObservers() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mGrupoExportLiveData != null) {
                    mGrupoExportLiveData.removeObservers(mLifecycleOwner);
                }
                Observer<List<GrupoExport>> exportObserver = new Observer<List<GrupoExport>>() {
                    @Override
                    public void onChanged(List<GrupoExport> grupoExports) {
                        try {
                            clearExportObservers();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mGrupoExportList = grupoExports;
                        grupoExports.stream().forEach(export -> {
                            Observer<List<TimeLogger>> observer = new Observer<List<TimeLogger>>() {
                                @Override
                                public void onChanged(List<TimeLogger> timeLoggers) {
                                    mTimeLoggersByGroupId.put(export.getGroupId(), timeLoggers);
                                }
                            };
                            LiveData<List<TimeLogger>> liveData = timeLoggerRepository.findByNewerThanAndGroupId(offsetDayInMillis(export.getDays().longValue()), export.getGroupId());
                            observableLoggersByGroupId.add(liveData);
                            liveData.observe(mLifecycleOwner, observer);
                        });
                    }
                };
                mGrupoExportLiveData.observe(mLifecycleOwner, exportObserver);
            }
        });
    }

    /**
     * Method that is executed periodically in the WatchdogService to keep the object up to date
     */
    public void watchDog() {
        refreshDayCounting();
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
        private Integer days;
        private Long summedTime;

        TimeSummary(Integer groupId, Integer conditionGroupId, Integer days, Long summedTime) {
            this.groupId = groupId;
            this.conditionGroupId = conditionGroupId;
            this.summedTime = summedTime;
            this.days = days;
        }

        TimeSummary(Integer groupId, Integer conditionGroupId, Integer days) {
            this.groupId = groupId;
            this.conditionGroupId = conditionGroupId;
            this.days = days;
        }

        String getKey() {
            return "A" + groupId + "B" + conditionGroupId + "C" + days;
        }

        Integer getGroupId() {
            return groupId;
        }

        Integer getConditionGroupId() {
            return conditionGroupId;
        }

        Long getSummedTime() {
            return summedTime;
        }

        Integer getDays() {
            return days;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TimeSummary that = (TimeSummary) o;
            return groupId.equals(that.groupId) && conditionGroupId.equals(that.conditionGroupId) && days.equals(that.days) && summedTime.equals(that.summedTime);
        }

        @Override
        public int hashCode() {
            return Objects.hash(groupId, conditionGroupId, days, summedTime);
        }
    }

    private class NegativeTimeSummary {

    }

    public boolean ifAllAppConditionsMet(String packageName) {
        ElementToGroup app = appsToGroupContainsPackageName(packageName);
        if (app != null) {
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

    public boolean ifLimitReachedAndShallBlock(String packageName) {
        return mLimitHandler.ifLimitsReachedForGroupIdAndShallBlock(getGroupIdFromPackageName(packageName));
    }

    @Nullable
    private ElementToGroup appsToGroupContainsPackageName(String packageName) {
        return Optional.ofNullable(elementsByPackageName)
                .filter(map -> map.containsKey(packageName))
                .map(map -> map.get(packageName))
                .orElse(null);
    }


    /**
     * From here are the inputs received that are
     * going to be saved into the db
     */

    private void initTimeLogger(String packageName, Long millis) {
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
        if (!checkIfDataEnoughAndSubmit()) {
            throw new Exception("Data provided in the submitted TimeLogger is not enough");
        }
        clearTimeLoggerReference();
    }

    private boolean checkIfDataEnoughAndSubmit() {
        if (timeLogger.getCountedtimemilis() == null) {
            Log.d(TAG, "CountedTime for summatory purposes not set for timeLogger");
            return false;
        } else if (timeLogger.getPackageName() == null) {
            Log.d(TAG, "Package Name is not set for timeLogger");
            return false;
        } else if (timeLogger.getMillistimestamp() == null) {
            Log.d(TAG, "TimeStamp is not set for timeLogger");
            return false;
        } else if (timeLogger.getPositivenegative() == null) {
            Log.d(TAG, "Positive/Negative/Neutral Type is not set for timeLogger");
            return false;
        } else if (timeLogger.getUsedtimemilis() == null) {
            Log.d(TAG, "UsedTime is not set for timeLogger");
            return false;
        } else {
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
        if (milis >= 0) {
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
        ElementToGroup app = appsToGroupContainsPackageName(name);
        if (app != null) {
            return app.getGroupId();
        } else {
            return -1;
        }
    }

    public GrupoPositivo getGrupoPositivoFromPackageName(String name) {
        Integer groupId = getGroupIdFromPackageName(name);
        GrupoPositivo grupo = new GrupoPositivo("");
        mAllGrupos.stream().forEach(g -> {
            if (g.getId() == groupId) {
                grupo.setId(g.getId());
                grupo.setNombre(g.getNombre());
            }
        });
        return grupo;
    }

    private Long days(Long milliseconds) {
        return TimeUnit.MILLISECONDS.toDays(milliseconds);
    }

    private Long millis(Long days) {
        return TimeUnit.DAYS.toMillis(days);
    }

    private Long currentDay() {
        return days(System.currentTimeMillis());
    }

    private Long offsetDay(Long nDays) {
        return currentDay() - nDays;
    }

    public Long offsetDayInMillis(Long nDays) {
        return millis(offsetDay(nDays));
    }


    /**
     * Refresh for all kinds of conditions
     */

    private void refreshDayCounting() {
        // to refresh the observers when the day changes, so they look for time spent from a new "start day"
        Long currentDay = currentDay();
        if (!dayRefreshed.equals(currentDay) && mAllConditionsToGroup != null) {
            dayRefreshed = currentDay;
            refreshConditionsObserver();
            setExportObservers();
            refreshTodayGroupObservers();
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    mLimitHandler.resetObserversOnDayChange();
                }
            });
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
                Observer<List<ConditionToGroup>> conditionsObserver = new Observer<List<ConditionToGroup>>() {
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
                                    } else if (c.getType().equals(ConditionToGroup.ConditionType.RANDOMCHECK)) {
                                        try {
                                            observeTimeLoggedOnRandomCheck(c);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else if (c.getType().equals(ConditionToGroup.ConditionType.FILE)) {
                                        giveFeedback(FEEDBACK_LOGGERS_CHANGED, null);
                                    }
                                });
                            }
                        });
                    }
                };
                mConditionsLiveData.observe(mLifecycleOwner, conditionsObserver);
            }
        });
    }

    private void clearConditionsObserver() throws Exception {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            clearLogObservers();
            if (mConditionsLiveData != null) {
                mConditionsLiveData.removeObservers(mLifecycleOwner);
            }
        } else {
            throw new Exception("clearConditionsObserver to be called in main thread");
        }
    }

    private void clearLogObservers() throws Exception {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            observableByGroupCondition.stream().forEach(ld -> ld.removeObservers(mLifecycleOwner));
            observableByRandomCheckCondition.stream().forEach(ld -> ld.removeObservers(mLifecycleOwner));
        } else {
            throw new Exception("clearLogObservers to be called in main thread");
        }
    }

    /**
     * Condition checkers for all types
     */

    public Long getTimeCountedOnGroupCondition(ConditionToGroup cond) {
        Long time;
        TimeSummary ts = new TimeSummary(cond.getGroupid(), cond.getConditionalgroupid(), cond.getFromlastndays());
        if (cond.getType().equals(ConditionToGroup.ConditionType.GROUP) && mTimeLoggersByConditionId.containsKey(cond.getId())) {
            time = mTimeLoggersByConditionId.get(cond.getId()).stream().collect(Collectors.summingLong(l -> l.getCountedtimemilis()));
        } else if (cond.getType().equals(ConditionToGroup.ConditionType.RANDOMCHECK) && mRandomCheckLoggersByConditionId.containsKey(cond.getId())) {
            time = mRandomCheckLoggersByConditionId.get(cond.getId()).stream().collect(Collectors.summingLong(l -> l.getTimecounted()));
        } else if (cond.getType().equals(ConditionToGroup.ConditionType.FILE)) {
            time = fileTimeGetter.fromFileLastDays(cond.getFromlastndays(), Uri.parse(cond.getFiletarget()));
        } else {
            time = 0L;
            Log.d(TAG, "entry for condition not found for " + cond.getType() + " checkid: " + cond.getConditionalrandomcheckid() + " groupid: " + cond.getConditionalgroupid());
        }
        return time;
    }

    public boolean ifConditionMet(ConditionToGroup cond) {
        if (getTimeCountedOnGroupCondition(cond) >= MilisToTime.getMilisDeMinutos(cond.getConditionalminutes())) {
            return true;
        }
        return false;
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

    public boolean ifAllGroupConditionsMet(Integer groupId) {
        BooleanWrap b = new BooleanWrap();
        b.set(true);
        getListOfConditionIdsOfGroup(groupId).stream().forEach(c -> {
            if (!ifConditionMet(c)) {
                b.set(false);
            }
        });
        return b.get();
    }

    /**
     * From here are the reading times for conditions
     * with time spent on other groups
     */

    private void observeTimeLoggedOnGroup(ConditionToGroup c) throws Exception {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new Exception("observeTimeLoggedOnGroup shall be called from main thread");
        }
        LiveData<List<TimeLogger>> timeLoggerLD = timeLoggerRepository.findByNewerThanAndGroupId(offsetDayInMillis(c.getFromlastndays().longValue()), c.getConditionalgroupid());
        Observer<List<TimeLogger>> observer = new Observer<List<TimeLogger>>() {
            @Override
            public void onChanged(List<TimeLogger> timeLoggers) {
                mTimeLoggersByConditionId.put(c.getId(), timeLoggers);
                giveFeedback(FEEDBACK_LOGGERS_CHANGED, null);
            }
        };
        observableByGroupCondition.add(timeLoggerLD);
        timeLoggerLD.observe(mLifecycleOwner, observer);
    }


    private void observeTimeLoggedOnRandomCheck(ConditionToGroup c) throws Exception {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new Exception("observeTimeLoggedOnRandomCheck shall be called from main thread");
        }
        Log.d(TAG, "find for " + c.getConditionalrandomcheckid());
        LiveData<List<TimeBlockLogger>> timeLoggedLD = timeBlockLoggerRepository.findByTimeNewerAndBlockId(offsetDayInMillis(c.getFromlastndays().longValue()), c.getConditionalrandomcheckid());
        Observer<List<TimeBlockLogger>> observer = new Observer<List<TimeBlockLogger>>() {
            @Override
            public void onChanged(List<TimeBlockLogger> timeBlockLoggers) {
                mRandomCheckLoggersByConditionId.put(c.getId(), timeBlockLoggers);
                giveFeedback(FEEDBACK_LOGGERS_CHANGED, null);
            }
        };
        observableByRandomCheckCondition.add(timeLoggedLD);
        timeLoggedLD.observe(mLifecycleOwner, observer);
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
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < export.getDays(); i++) {
                        final long days = i;
                        Long timeMillis = mTimeLoggersByGroupId.get(export.getGroupId())
                                .stream()
                                // filter only those values that are after the given offset day
                                .filter(tl -> MilisToTime.millisToLocalDateTime(tl.getMillistimestamp()).isAfter(LocalDate.now().atStartOfDay().minusDays(days)))
                                // filter only those values that are within the same day
                                .filter(tl -> MilisToTime.millisToLocalDateTime(tl.getMillistimestamp()).isBefore(LocalDate.now().atStartOfDay().minusDays(days - 1)))
                                .collect(Collectors.summingLong(logger -> logger.getCountedtimemilis()));
                        if (i > 0) {
                            builder.append(System.lineSeparator());
                        }
                        builder.append(daysToFileFormat(i, timeMillis));
                    }
                    FileReader.writeTextToUri(mApplication, Uri.parse(export.getArchivo()), builder.toString());
                }
            });
        }
    }

    private void clearExportObservers() throws Exception {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            observableLoggersByGroupId.stream().forEach(ld -> ld.removeObservers(mLifecycleOwner));
            observableLoggersByGroupId.clear();
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
        if (monthnum < 10) {
            month = "0" + monthnum;
        } else {
            month = String.valueOf(monthnum);
        }

        String day;
        if (daynum < 10) {
            day = "0" + daynum;
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
                    mAllGrupos.stream().forEach(grupo -> {
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

    /**
     * Various utils
     */

    public boolean appIsGrouped(String packageName) {
        return elementsByPackageName.containsKey(packageName);
    }

    private void observeTodayGroupTime(Grupo grupo) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                LiveData<List<TimeLogger>> liveData = timeLoggerRepository.findByNewerThanAndGroupId(millis(currentDay()), grupo.getId());
                Observer<List<TimeLogger>> observer = new Observer<List<TimeLogger>>() {
                    @Override
                    public void onChanged(List<TimeLogger> timeLoggers) {
                        mTodayTimeByGroupMap.put(grupo.getId(), timeLoggers);
                    }
                };
                liveData.observe(mLifecycleOwner, observer);
                observableGroups.add(liveData);
            }
        });
    }

    private void refreshTodayGroupObservers() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                observableGroups.stream().forEach(ld -> ld.removeObservers(mLifecycleOwner));
                observableGroups.clear();
                mAllGrupos.stream().forEach(grupo -> observeTodayGroupTime(grupo));
            }
        });
    }

    public Long todayTimeOnAppGroup(String packageName) {
        ElementToGroup group = appsToGroupContainsPackageName(packageName);
        if (group != null && group.getId() != null) {
            return todayTimeOnPositiveGroup(group.getGroupId());
        } else {
            return 0L;
        }
    }

    public void onGroupTimeToday(LifecycleOwner owner, int groupId, Consumer<String> consumer) {
        mMainHandler.post(() -> {
                // TODO
            });
    }

    public Long todayTimeOnPositiveGroup(Integer groupId) {
        if (mTodayTimeByGroupMap.containsKey(groupId)) {
            return mTodayTimeByGroupMap.get(groupId).stream()
                    // let pass only positive
                    .filter(logger -> !TimeLogger.Type.NEGATIVE.equals(logger.getPositivenegative()))
                    .filter(logger -> !TimeLogger.Type.NEUTRAL.equals(logger.getPositivenegative()))
                    .collect(Collectors.summingLong(logger -> logger.getCountedtimemilis()));
        } else {
            return 0L;
        }
    }

    public String todayStringTimeOnPositiveGroup(GrupoPositivo group) {
        return MilisToTime.getFormatedHM(todayTimeOnPositiveGroup(group.getId()));
    }

    public Long todayTimeOnNegativeGroup(Integer groupId) {
        if (mTodayTimeByGroupMap.containsKey(groupId)) {
            return mTodayTimeByGroupMap.get(groupId).stream()
                    // let pass only negative
                    .filter(logger -> TimeLogger.Type.NEGATIVE.equals(logger.getPositivenegative()))
                    .collect(Collectors.summingLong(logger -> logger.getCountedtimemilis()));
        } else {
            return 0L;
        }
    }

    public String todayStringTimeOnNegativeGroup(Grupo group) {
        return MilisToTime.getFormatedHM(todayTimeOnNegativeGroup(group.getId()));
    }

}