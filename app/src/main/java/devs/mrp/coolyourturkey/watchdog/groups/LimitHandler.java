package devs.mrp.coolyourturkey.watchdog.groups;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.comun.BooleanWrap;
import devs.mrp.coolyourturkey.databaseroom.grouplimit.GroupLimit;
import devs.mrp.coolyourturkey.databaseroom.grouplimit.GroupLimitRepository;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivo;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivoRepository;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLogger;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLoggerRepository;

public class LimitHandler {

    private static final String TAG = "LIMIT_HANDLER";

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
    private Map<Integer, Map<LiveData<List<TimeLogger>>, Observer<List<TimeLogger>>>> mTimeLoggerObserverMap;

    /**
     * Collections to work with the data
     */
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
        mGroupLimitObserversMap.clear();
    }

    private void observeLimitsInGroup(GrupoPositivo grupo) {
        LiveData<List<GroupLimit>> liveData = mGroupLimitRepository.findGroupLimitByGroupId(grupo.getId());
        Observer<List<GroupLimit>> observer = new Observer<List<GroupLimit>>() {
            @Override
            public void onChanged(List<GroupLimit> groupLimits) {
                clearTimeLoggerObservers(grupo.getId());
                mGroupLimitsByGroupId.put(grupo.getId(), groupLimits);
                groupLimits.stream().forEach(limit -> {
                    observeLoggersOnLimit(limit);
                });
            }
        };
        liveData.observe(mLifecycleOwner ,observer);
        mGroupLimitObserversMap.put(liveData, observer);
    }

    private void clearTimeLoggerObservers(Integer groupId) {
        Map<LiveData<List<TimeLogger>>, Observer<List<TimeLogger>>> map = mTimeLoggerObserverMap.get(groupId);
        if (map != null) {
            map.keySet().stream().forEach(livedata -> {
                livedata.removeObserver(map.get(livedata));
            });
            map.clear();
        }
    }

    private void observeLoggersOnLimit(GroupLimit limit) {
        Long newerthan = mTimeLogHandler.offsetDayInMillis(limit.getOffsetDays().longValue());
        LiveData<List<TimeLogger>> liveData = mTimeLoggerRepository.findByNewerThanAndGroupId(newerthan, limit.getGroupId());
        Observer<List<TimeLogger>> observer = new Observer<List<TimeLogger>>() {
            @Override
            public void onChanged(List<TimeLogger> timeLoggers) {
                mTimeLoggersByLimitId.put(limit.getId(), timeLoggers);
            }
        };
        if (!mTimeLoggerObserverMap.containsKey(limit.getGroupId())) {
            mTimeLoggerObserverMap.put(limit.getGroupId(), new HashMap<>());
        }
        mTimeLoggerObserverMap.get(limit.getGroupId()).put(liveData, observer);
        liveData.observe(mLifecycleOwner, observer);
    }

    public boolean ifLimitsReachedForGroupId(Integer groupId) {
        BooleanWrap resultado = new BooleanWrap();
        resultado.set(false);
        if (!mGroupLimitsByGroupId.containsKey(groupId)) {
            return false;
        }
        List<GroupLimit> limites = mGroupLimitsByGroupId.get(groupId);
        limites.stream().forEach(limite -> {
            if (limitLoggedMillis(limite) >= groupLimitInMillis(limite)) {
                resultado.set(true);
            }
        });
        return resultado.get();
    }

    private Long groupLimitInMillis(GroupLimit limit) {
        return limit.getMinutesLimit() * 60 * 1000L;
    }

    private Long limitLoggedMillis(GroupLimit limit) {
        if (mTimeLoggersByLimitId.containsKey(limit.getId())) {
            Long count = mTimeLoggersByLimitId.get(limit.getId()).stream().collect(Collectors.summingLong(logger -> {
                if (logger.getCountedtimemilis() > 0L && logger.getPositivenegative() == TimeLogger.Type.POSITIVECONDITIONSMET) {
                    return logger.getCountedtimemilis();
                } else {
                    return 0L;
                }
            }));
            return count;
        }
        return 0L;
    }

}
