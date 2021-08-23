package devs.mrp.coolyourturkey.condicionesnegativas;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import devs.mrp.coolyourturkey.databaseroom.apptogroup.AppToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.conditionnegativetogroup.ConditionNegativeToGroup;
import devs.mrp.coolyourturkey.databaseroom.conditionnegativetogroup.ConditionNegativeToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup.ConditionToGroup;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLoggerRepository;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;

public class NegativeConditionTimeChecker implements Feedbacker<Object> {

    private static final String TAG = "TIME_LOG_HANDLER";

    public static final int FEEDBACK_CONDITIONS_LOADED = 1;

    private List<FeedbackListener> listeners = new ArrayList<>();

    private TimeLoggerRepository timeLoggerRepository;
    private ConditionNegativeToGroupRepository conditionNegativeRepository;

    private List<ConditionNegativeToGroup> mConditions;

    private Context mContext;
    private Application mApplication;
    private LifecycleOwner mLifecycleOwner;

    public NegativeConditionTimeChecker(Context context, Application application, LifecycleOwner lifecycleOwner) {
        mContext = context;
        mApplication = application;
        mLifecycleOwner = lifecycleOwner;

        conditionNegativeRepository.findAllConditionToGroup().observe(lifecycleOwner, new Observer<List<ConditionNegativeToGroup>>() {
            @Override
            public void onChanged(List<ConditionNegativeToGroup> conditionNegativeToGroups) {
                mConditions = conditionNegativeToGroups;
                giveFeedback(FEEDBACK_CONDITIONS_LOADED, mConditions);
            }
        });
    }

    @Override
    public void giveFeedback(int tipo, Object feedback) {
        listeners.stream().forEach(l -> {
            l.giveFeedback(tipo, feedback);
        });
    }

    @Override
    public void addFeedbackListener(FeedbackListener<Object> listener) {
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
        conditions.stream().forEach(c -> {
            timeLoggerRepository.findByNewerThanAndGroupId(offsetDayInMillis(c.getFromlastndays().longValue()), c.getConditionalgroupid())
        });
    }
}
