package devs.mrp.coolyourturkey.condicionesnegativas;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.databaseroom.conditionnegativetogroup.ConditionNegativeToGroup;
import devs.mrp.coolyourturkey.databaseroom.conditionnegativetogroup.ConditionNegativeToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLogger;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLoggerRepository;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

public class NegativeConditionTimeChecker implements Feedbacker<List<ConditionNegativeToGroup>> {

    private static final String TAG = "TIME_LOG_HANDLER";

    public static final int FEEDBACK_CONDITIONS_LOADED = 1;
    public static final int FEEDBACK_TIMES_LOADED = 2;

    private List<FeedbackListener<List<ConditionNegativeToGroup>>> listeners = new ArrayList<>();

    private List<LiveData<List<TimeLogger>>> timeLoggerLiveDatas;

    private TimeLoggerRepository timeLoggerRepository;
    private ConditionNegativeToGroupRepository conditionNegativeRepository;

    private List<ConditionNegativeToGroup> mConditions;
    private Map<Integer, Long> mTimeByConditionIdMap;

    private Handler mMainHandler;

    private Context mContext;
    private Application mApplication;
    private LifecycleOwner mLifecycleOwner;

    public NegativeConditionTimeChecker(Context context, Application application, LifecycleOwner lifecycleOwner) {
        mContext = context;
        mApplication = application;
        mLifecycleOwner = lifecycleOwner;

        mMainHandler = new Handler(context.getMainLooper());

        timeLoggerRepository = TimeLoggerRepository.getRepo(application);
        conditionNegativeRepository = ConditionNegativeToGroupRepository.getRepo(mApplication);
        mTimeByConditionIdMap = new HashMap<>();
        timeLoggerLiveDatas = new ArrayList<>();

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
        });
    }

    private void clearTimeLoggerObservers() {
        timeLoggerLiveDatas.stream().forEach(lvdt -> {
            lvdt.removeObservers(mLifecycleOwner);
        });
        timeLoggerLiveDatas.clear();
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
}
