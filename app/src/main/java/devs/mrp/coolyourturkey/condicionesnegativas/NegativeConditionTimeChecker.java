package devs.mrp.coolyourturkey.condicionesnegativas;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.FileReader;
import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.comun.Notificador;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger.TimeBlockLogger;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger.TimeBlockLoggerRepository;
import devs.mrp.coolyourturkey.databaseroom.conditionnegativetogroup.ConditionNegativeToGroup;
import devs.mrp.coolyourturkey.databaseroom.conditionnegativetogroup.ConditionNegativeToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup.ConditionToGroup;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLogger;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLoggerRepository;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;

public class NegativeConditionTimeChecker implements Feedbacker<List<ConditionNegativeToGroup>> {

    private static final String TAG = "TIME_LOG_HANDLER";

    private final Long TIME_BETWEEN_FILES_REFRESH = 60*1000*1L; // 1 minute between file refreshes
    public static final int FEEDBACK_CONDITIONS_LOADED = 1;
    public static final int FEEDBACK_TIMES_LOADED = 2;

    private final Long TIME_BETWEEN_NOTIFICATION_REFRESH = 5L * 60 * 1000;
    private final int NOTIFICATION_ID = -32;

    private List<FeedbackListener<List<ConditionNegativeToGroup>>> listeners = new ArrayList<>();

    private List<LiveData<List<TimeLogger>>> timeLoggerLiveDatas;
    private List<LiveData<List<TimeBlockLogger>>> timeBlockLoggerLiveDatas;
    private Long dayRefreshed;
    private Long mLastFilesChecked;

    private TimeLoggerRepository timeLoggerRepository;
    private TimeBlockLoggerRepository timeBlockLoggerRepository;
    private ConditionNegativeToGroupRepository conditionNegativeRepository;

    private List<ConditionNegativeToGroup> mConditions;
    private Map<Integer, Long> mTimeByConditionIdMap;

    private Handler mMainHandler;
    private Notificador mNotificador;
    private Long mLastNotificationRefreshed;
    private MisPreferencias mMisPreferencias;
    private boolean mAllConditionsMet;

    private Context mContext;
    private Application mApplication;
    private LifecycleOwner mLifecycleOwner;

    public NegativeConditionTimeChecker(Context context, Application application, LifecycleOwner lifecycleOwner) {
        mContext = context;
        mApplication = application;
        mLifecycleOwner = lifecycleOwner;

        mMainHandler = new Handler(context.getMainLooper());
        mNotificador = new Notificador(application, context);
        mNotificador.createNotificationChannel(R.string.condition_met_channel_name, R.string.condition_met_channel_description, Notificador.CONDITION_MET_CHANNEL_ID);
        mMisPreferencias = new MisPreferencias(context);
        mAllConditionsMet = false;

        dayRefreshed = currentDay();
        timeLoggerRepository = TimeLoggerRepository.getRepo(application);
        timeBlockLoggerRepository = TimeBlockLoggerRepository.getRepo(application);
        conditionNegativeRepository = ConditionNegativeToGroupRepository.getRepo(mApplication);
        mTimeByConditionIdMap = new HashMap<>();
        timeLoggerLiveDatas = new ArrayList<>();
        timeBlockLoggerLiveDatas = new ArrayList<>();

        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                conditionNegativeRepository.findAllConditionToGroup().observe(lifecycleOwner, new Observer<List<ConditionNegativeToGroup>>() {
                    @Override
                    public void onChanged(List<ConditionNegativeToGroup> conditionNegativeToGroups) {
                        mConditions = conditionNegativeToGroups;
                        loadTimes(mConditions);
                        giveFeedback(FEEDBACK_CONDITIONS_LOADED, mConditions);
                    }
                });
            }
        });
    }

    @Override
    public void giveFeedback(int tipo, List<ConditionNegativeToGroup> feedback) {
        listeners.stream().forEach(l -> {
            l.giveFeedback(tipo, feedback);
        });
    }

    @Override
    public void addFeedbackListener(FeedbackListener<List<ConditionNegativeToGroup>> listener) {
        listeners.add(listener);
    }

    public List<ConditionNegativeToGroup> getConditions() {
        return mConditions;
    }

    private Long days(Long milliseconds) {return TimeUnit.MILLISECONDS.toDays(milliseconds);}
    private Long millis(Long days) {return TimeUnit.DAYS.toMillis(days);}
    private Long currentDay() {return days(System.currentTimeMillis());}
    private Long offsetDay(Long nDays) {return currentDay()-nDays;}
    public Long offsetDayInMillis(Long nDays) {return millis(offsetDay(nDays));}

    private void loadTimes(List<ConditionNegativeToGroup> conditions) {
        clearTimeLoggerObservers();
        conditions.stream().forEach(c -> {
            if (c.getType().equals(ConditionNegativeToGroup.ConditionType.GROUP)) {
                LiveData<List<TimeLogger>> liveData = timeLoggerRepository.findByNewerThanAndGroupId(offsetDayInMillis(c.getFromlastndays().longValue()), c.getConditionalgroupid());
                Observer<List<TimeLogger>> observer = new Observer<List<TimeLogger>>() {
                    @Override
                    public void onChanged(List<TimeLogger> timeLoggers) {
                        Long totalTime = timeLoggers.stream().mapToLong(t -> t.getUsedtimemilis()).sum();
                        mTimeByConditionIdMap.put(c.getId(), totalTime);
                        giveFeedback(FEEDBACK_TIMES_LOADED, mConditions);
                    }
                };
                liveData.observe(mLifecycleOwner, observer);
                timeLoggerLiveDatas.add(liveData);
            } else if (c.getType().equals(ConditionNegativeToGroup.ConditionType.RANDOMCHECK)) {
                LiveData<List<TimeBlockLogger>> liveData = timeBlockLoggerRepository.findByTimeNewerAndBlockId(offsetDayInMillis(c.getFromlastndays().longValue()), c.getConditionalblockid());
                Observer<List<TimeBlockLogger>> observer = new Observer<List<TimeBlockLogger>>() {
                    @Override
                    public void onChanged(List<TimeBlockLogger> timeBlockLoggers) {
                        Long totalTime = timeBlockLoggers.stream().mapToLong(t -> t.getTimecounted()).sum();
                        mTimeByConditionIdMap.put(c.getId(), totalTime);
                        giveFeedback(FEEDBACK_TIMES_LOADED, mConditions);
                    }
                };
                liveData.observe(mLifecycleOwner, observer);
                timeBlockLoggerLiveDatas.add(liveData);
            } else if (c.getType().equals(ConditionNegativeToGroup.ConditionType.FILE)) {
                observeTimeLoggedOnFile(c);
                giveFeedback(FEEDBACK_TIMES_LOADED, mConditions);
            }
        });
    }

    private void clearTimeLoggerObservers() {
        timeLoggerLiveDatas.stream().forEach(lvdt -> {
            lvdt.removeObservers(mLifecycleOwner);
        });
        timeLoggerLiveDatas.clear();
        timeBlockLoggerLiveDatas.stream().forEach(lvdt -> {
            lvdt.removeObservers(mLifecycleOwner);
        });
        timeBlockLoggerLiveDatas.clear();
    }

    public Long getTimeCountedOnCondition(ConditionNegativeToGroup condition) {
        Long time;
        if (mTimeByConditionIdMap.containsKey(condition.getId())) {
            time = mTimeByConditionIdMap.get(condition.getId());
        } else {
            time = 0L;
            Log.d(TAG, "entry for condition not found");
        }

        return time;
    }

    public boolean ifConditionMet(ConditionNegativeToGroup condition) {
        if (getTimeCountedOnCondition(condition) >= MilisToTime.getMilisDeMinutos(condition.getConditionalminutes())) {
            return true;
        } return false;
    }

    public boolean ifAllConditionsMet() {
        Iterator<ConditionNegativeToGroup> iterator = mConditions.listIterator();
        while (iterator.hasNext()) {
            ConditionNegativeToGroup condition = iterator.next();
            if (!ifConditionMet(condition)) {
                return false;
            }
        }
        return true;
    }

    public void refreshDayCounting() {
        Long currentDay = currentDay();
        if (dayRefreshed == null || !dayRefreshed.equals(currentDay)) {
            dayRefreshed = currentDay;
            if (mConditions != null) {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        loadTimes(mConditions);
                    }
                });
            }
        }
    }

    public void refreshNotifications() {
        if (mLastNotificationRefreshed == null) {
            mLastNotificationRefreshed = 0L;
        }
        Long now = System.currentTimeMillis();
        if (now - TIME_BETWEEN_NOTIFICATION_REFRESH > mLastNotificationRefreshed) {
            mLastNotificationRefreshed = now;
            if (!mAllConditionsMet && ifAllConditionsMet()){
                mAllConditionsMet = true;
                if (mMisPreferencias.getNotifyConditionsJustMet()) {
                    String title = mApplication.getString(R.string.apps_malas);
                    String description = mApplication.getString(R.string.ahora_se_pueden_usar);
                    mNotificador.createNotification(R.drawable.bug, title, description, Notificador.CONDITION_MET_CHANNEL_ID, NOTIFICATION_ID);
                }
            } else if(mAllConditionsMet && !ifAllConditionsMet()) {
                mAllConditionsMet = false;
            }

        }
    }

    /**
     * FILEs
     */

    private void observeTimeLoggedOnFile(ConditionNegativeToGroup condition) {
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
                    mTimeByConditionIdMap.put(condition.getId(), consumption);
                } else {
                    mTimeByConditionIdMap.put(condition.getId(), 0L);
                    Log.d(TAG, "days offset of file doesn't match requirements for " + condition.getFiletarget());
                }
            } else {
                mTimeByConditionIdMap.put(condition.getId(), 0L);
                Log.d(TAG, "content of file doesn't match requirements for " + condition.getFiletarget());
            }
        } else {
            Log.d(TAG, "no rights to read file " + condition.getFiletarget());
        }
    }

    public void refreshTimeLoggedOnFiles() {
        if (mLastFilesChecked == null) {
            mLastFilesChecked = 0L;
        }
        Long now = System.currentTimeMillis();
        if (now-TIME_BETWEEN_FILES_REFRESH > mLastFilesChecked && mConditions != null) {
            mLastFilesChecked = now;
            mConditions.stream().forEach(c -> {
                if (c.getType() == ConditionNegativeToGroup.ConditionType.FILE){
                    observeTimeLoggedOnFile(c);
                }
            });
        }
    }

}
