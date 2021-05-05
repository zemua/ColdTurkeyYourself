package devs.mrp.coolyourturkey.watchdog.groups;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import devs.mrp.coolyourturkey.databaseroom.grouplimit.GroupLimit;
import devs.mrp.coolyourturkey.databaseroom.grouplimit.GroupLimitRepository;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivo;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivoRepository;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLogger;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLoggerRepository;

public class LimitHandler {

    private TimeLogHandler mTimeLogHandler;
    private Context mContext;
    private Application mApplication;
    private LifecycleOwner mLifecycleOwner;

    private GrupoPositivoRepository mGrupoPositivoRepository;
    private GroupLimitRepository mGroupLimitRepository;
    private TimeLoggerRepository mTimeLoggerRepository;

    /**
     * Maps for clearing observers on changes
     */
    private Map<LiveData<List<GroupLimit>>, Observer<List<GroupLimit>>> mGroupLimitObserversMap;
    private Map<LiveData<List<TimeLogger>>, Observer<List<TimeLogger>>> mTimeLoggerObserverMap;

    /**
     * Collections to work with the data
     */
    private List<GrupoPositivo> mAllGrupos;
    private Map<Integer, List<GroupLimit>> mGroupLimitsByGroupId;
    private Map<Integer, List<TimeLogger>> mTimeLoggersByLimitId;

    public LimitHandler(TimeLogHandler timeLogHandler, Context context, Application application, LifecycleOwner lifecycleOwner) {
        mTimeLogHandler = timeLogHandler;
        mContext = context;
        mApplication = application;
        mLifecycleOwner = lifecycleOwner;

        mGrupoPositivoRepository = GrupoPositivoRepository.getRepo(application);
        mGroupLimitRepository = GroupLimitRepository.getRepo(application);
        mTimeLoggerRepository = TimeLoggerRepository.getRepo(application);

        mGroupLimitObserversMap = new HashMap<>();
        mGroupLimitsByGroupId = new HashMap<>();
        mTimeLoggerObserverMap = new HashMap<>();
        mTimeLoggersByLimitId = new HashMap<>();

        mGrupoPositivoRepository.findAllGrupoPositivo().observe(lifecycleOwner, new Observer<List<GrupoPositivo>>() {
            @Override
            public void onChanged(List<GrupoPositivo> grupoPositivos) {
                mAllGrupos = grupoPositivos;
                clearGroupLimitObservers();
                grupoPositivos.stream().forEach(grupo -> {
                    observeLimitsInGroup(grupo);
                });
            }
        });
    }

    private void clearGroupLimitObservers() {
        mGroupLimitObserversMap.keySet().stream().forEach(livedata -> {
            livedata.removeObserver(mGroupLimitObserversMap.get(livedata));
        });
    }

    private void observeLimitsInGroup(GrupoPositivo grupo) {
        LiveData<List<GroupLimit>> liveData = mGroupLimitRepository.findGroupLimitByGroupId(grupo.getId());
        Observer<List<GroupLimit>> observer = new Observer<List<GroupLimit>>() {
            @Override
            public void onChanged(List<GroupLimit> groupLimits) {
                mGroupLimitsByGroupId.put(grupo.getId(), groupLimits);
                groupLimits.stream().forEach(limit -> {
                    observeLoggersOnLimit(limit);
                });
            }
        };
        liveData.observe(mLifecycleOwner ,observer);
        mGroupLimitObserversMap.put(liveData, observer);
    }

    private void clearTimeLoggerObservers() {
        mTimeLoggerObserverMap.keySet().stream().forEach(livedata -> {
            livedata.removeObserver(mTimeLoggerObserverMap.get(livedata));
        });
    }

    private void observeLoggersOnLimit(GroupLimit limit) {
        clearTimeLoggerObservers();
        Long newerthan = mTimeLogHandler.offsetDayInMillis(limit.getOffsetDays().longValue());
        LiveData<List<TimeLogger>> liveData = mTimeLoggerRepository.findByNewerThanAndGroupId(newerthan, limit.getGroupId());
        Observer<List<TimeLogger>> observer = new Observer<List<TimeLogger>>() {
            @Override
            public void onChanged(List<TimeLogger> timeLoggers) {
                mTimeLoggersByLimitId.put(limit.getId(), timeLoggers);
            }
        };
        liveData.observe(mLifecycleOwner, observer);
        mTimeLoggerObserverMap.put(liveData, observer);
    }

}
