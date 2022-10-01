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
import java.time.LocalDateTime;
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
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger.TimeBlockLogger;
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
import devs.mrp.coolyourturkey.watchdog.groups.impl.AppUsageExportObserver;

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
    //private LimitHandler mLimitHandler; // Positive groups are limited no more, no need for limitHandler for now

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

    private ExportObserver appUsageExportObserver;

    private Map<Integer, Boolean> mAllGruposPositivosIfConditionsMet;
    private List<Grupo> mAllGrupos = new ArrayList<>();
    private Map<Integer, List<TimeLogger>> mTodayTimeByGroupMap;

    private List<GrupoExport> mGrupoExportList;
    private Map<Integer, List<TimeLogger>> mTimeLoggersByGroupId;
    private Map<Integer, List<TimeBlockLogger>> mTimeBlockLoggersByGroupId;

    private TimeLogger timeLogger;
    private Long beginningOfDayRefreshed = 0L;
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
        //mLimitHandler = new LimitHandler(this, context, application, lifecycleOwner);
        conditionChecker = ConditionCheckerFactory.getConditionChecker(application, lifecycleOwner);

        appUsageExportObserver = new AppUsageExportObserver(mContext,mLifecycleOwner,mApplication);

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
        mTimeBlockLoggersByGroupId = new HashMap<>();
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
                        mGrupoExportList = grupoExports;
                        grupoExports.stream().forEach(export -> {
                            // APP usage loggers
                            Observer<List<TimeLogger>> observer = new Observer<List<TimeLogger>>() {
                                @Override
                                public void onChanged(List<TimeLogger> timeLoggers) {
                                    mTimeLoggersByGroupId.put(export.getGroupId(), timeLoggers);
                                }
                            };
                            appUsageExportObserver.observe(export.getGroupId(), export.getDays().longValue(), observer);
                            // Random Checks usage loggers
                            Observer<List<TimeBlockLogger>> blockObserver = new Observer<List<TimeBlockLogger>>() {
                                @Override
                                public void onChanged(List<TimeBlockLogger> timeBlockLoggers) {
                                    mTimeBlockLoggersByGroupId.put(export.getGroupId(), timeBlockLoggers);
                                }
                            };
                            // TODO
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


    /**
     * Refresh for all kinds of conditions
     */

    private void refreshDayCounting() {
        // to refresh the observers when the day changes, so they look for time spent from a new "start day"
        Long beginningOfCurrentDay = MilisToTime.beginningOfTodayConsideringChangeOfDay(mContext);
        if (!beginningOfDayRefreshed.equals(beginningOfCurrentDay)) {
            beginningOfDayRefreshed = beginningOfCurrentDay;
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
                                .filter(tl -> {
                                    LocalDateTime ldt = MilisToTime.millisToLocalDateTime(tl.getMillistimestamp());
                                    LocalDateTime ldt2 = MilisToTime.beginningOfOffsetDaysConsideringChangeOfDayInLocalDateTime(days, mContext);
                                    return ldt.isAfter(ldt2);
                                })
                                // filter only those values that are within the same day
                                .filter(tl -> {
                                    LocalDateTime ldt = MilisToTime.millisToLocalDateTime(tl.getMillistimestamp());
                                    LocalDateTime ldt2 = MilisToTime.endOfOffsetDaysConsideringChangeOfDayInLocalDateTime(days, mContext);
                                    return ldt.isBefore(ldt2);
                                })
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

    private String daysToFileFormat(Integer offsetDays, Long millisToAppend) {
        // Format is:
        // YYYY-MM-DD-XXXXX

        LocalDate localDate = MilisToTime.beginningOfOffsetDaysConsideringChangeOfDayInLocalDateTime(offsetDays.longValue(), mContext).toLocalDate();

        int year = localDate.getYear();
        int monthnum = localDate.getMonthValue()-1; // The month value used in the exported .txt files starts in 0
        int daynum = localDate.getDayOfMonth();

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

    private void observeTodayGroupTime(Grupo grupo) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                LiveData<List<TimeLogger>> liveData = timeLoggerRepository.findByNewerThanAndGroupId(MilisToTime.beginningOfTodayConsideringChangeOfDay(mContext) ,grupo.getId());
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