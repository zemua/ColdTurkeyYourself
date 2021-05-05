package devs.mrp.coolyourturkey.watchdog.groups;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LifecycleOwner;

import devs.mrp.coolyourturkey.databaseroom.grouplimit.GroupLimitRepository;

public class LimitHandler {

    private TimeLogHandler mTimeLogHandler;
    private Context mContext;
    private Application mApplication;
    private LifecycleOwner mLifecycleOwner;

    private GroupLimitRepository mGroupLimitRepository;

    public LimitHandler(TimeLogHandler timeLogHandler, Context context, Application application, LifecycleOwner lifecycleOwner) {
        mTimeLogHandler = timeLogHandler;
        mContext = context;
        mApplication = application;
        mLifecycleOwner = lifecycleOwner;

        mGroupLimitRepository = GroupLimitRepository.getRepo(application);
    }



}
