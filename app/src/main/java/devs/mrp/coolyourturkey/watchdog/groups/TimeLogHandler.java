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
import devs.mrp.coolyourturkey.comun.FileReader;
import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.comun.Notificador;
import devs.mrp.coolyourturkey.comun.impl.FileTimeGetterImpl;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger.TimeBlockLoggerRepository;
import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.databaseroom.grupo.GrupoRepository;
import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementToGroup;
import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementType;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupoexport.GrupoExport;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupoexport.GrupoExportRepository;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLogger;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLoggerRepository;
import devs.mrp.coolyourturkey.grupos.conditionchecker.ConditionCheckerCommander;
import devs.mrp.coolyourturkey.grupos.conditionchecker.impl.ConditionCheckerFactory;
import devs.mrp.coolyourturkey.grupos.timing.GroupGeneralAssembler;
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
    private Map<String, ElementToGroup> elementsByPackageName;
    private ElementToGroupRepository elementToGroupRepository;
    private GrupoExportRepository mGrupoExportRepository;
    private GrupoRepository mGrupoRepository;

    private LiveData<List<GrupoExport>> mGrupoExportLiveData;
    private List<LiveData<?>> observableGroups;
    private List<LiveData<?>> observableLoggersByGroupId;
    private List<LiveData<?>> observableByGroupCondition;
    private List<LiveData<?>> observableByRandomCheckCondition;

    private Map<Integer, Boolean> mAllGruposPositivosIfConditionsMet;
    private List<Grupo> mAllGrupos = new ArrayList<>();
    private Map<Integer, List<TimeLogger>> mTodayTimeByGroupMap;

    private List<GrupoExport> mGrupoExportList;
    private Map<Integer, List<TimeLogger>> mTimeLoggersByGroupId;

    private TimeLogger timeLogger;
    private Long dayRefreshed = 0L;
    private Long mLastFilesExported;
    private Notificador mNotificador;
    private ConditionCheckerCommander conditionChecker;

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
        mLimitHandler = new LimitHandler(this, context, application, lifecycleOwner);
        conditionChecker = ConditionCheckerFactory.getConditionChecker(application, lifecycleOwner);

        mTodayTimeByGroupMap = new HashMap<>();

        timeLoggerRepository = TimeLoggerRepository.getRepo(application);
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

        refreshDayCounting();
    }

    /**
     * Set the observers for the exported group files, to be called on day-change too
     */
    private void setExportObservers() { // TODO consider hour for change of day
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

    public void onAllConditionsMet(String packageName, Consumer<Boolean> areMet) {
        mMainHandler.post(() -> {
            conditionChecker.onAllConditionsMet(this.getGroupIdFromPackageName(packageName), met -> {
                areMet.accept(met);
            });
        });
    }

    public void onGrupoFromPackageName(String packageName, Consumer<Grupo> grupo) {
        int groupId = getGroupIdFromPackageName(packageName);
        mMainHandler.post(() -> {
            LiveData<List<Grupo>> liveData = mGrupoRepository.findGrupoById(groupId);
            liveData.observe(mLifecycleOwner, grupos -> {
                liveData.removeObservers(mLifecycleOwner);
                if (grupos.size()>0) {
                    grupo.accept(grupos.get(0));
                }
            });
        });
    }

    /**
     * Method that is executed periodically in the WatchdogService to keep the object up to date
     */
    public void watchDog() {
        refreshDayCounting();
        refreshTimeExportedToFiles();
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

    public void insertTimeGoodApp(String packageName, Long millis) {
        initTimeLogger(packageName, millis);
        increase(millis);
        onAllConditionsMet(packageName, areMet -> {
            if (areMet) {
                timeLogger.setPositivenegative(TimeLogger.Type.POSITIVECONDITIONSMET);
            } else {
                timeLogger.setPositivenegative(TimeLogger.Type.POSITIVECONDITIONSNOTMET);
            }
            try {
                send();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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

    private void refreshDayCounting() { // TODO consider hour for change of day
        // to refresh the observers when the day changes, so they look for time spent from a new "start day"
        Long currentDay = currentDay();
        if (!dayRefreshed.equals(currentDay)) {
            dayRefreshed = currentDay;
            setExportObservers();
            refreshTodayGroupObservers();
            // positive groups have a limit no more, no need for mLimitHandler
            /*mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    mLimitHandler.resetObserversOnDayChange();
                }
            });*/
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
     * Of exporter for sync
     */

    private void refreshTimeExportedToFiles() { // TODO consider hour for change of day
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
     * Various utils
     */

    public boolean appIsGrouped(String packageName) {
        return elementsByPackageName.containsKey(packageName);
    }

    private void observeTodayGroupTime(Grupo grupo) { // TODO consider hour for change of day
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

    public void onGroupTimeToday(GroupGeneralAssembler assembler, int groupId, Consumer<String> consumer) {
        mMainHandler.post(() -> {
                assembler.forGroupToday(groupId, longResult -> {
                    consumer.accept(MilisToTime.getFormatedHM(longResult));
                });
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