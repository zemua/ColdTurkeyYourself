package devs.mrp.coolyourturkey.watchdog.checkscheduling;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.Notificador;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;
import devs.mrp.coolyourturkey.dtos.timeblock.facade.FTimeBlockFacade;
import devs.mrp.coolyourturkey.dtos.timeblock.facade.ITimeBlockFacade;

public class CheckManager implements ICheckManager{

    private static final String TAG = "CheckManager";

    public static final String EXTRA_BLOCK_ID = "extra.block.id";
    public static final String EXTRA_BLOCK_NAME = "extra.block.name";

    private ITimeBlockFacade mFacade;
    private static CheckManager instance;
    private Map<Integer, Long> mSchedules;
    private Map<Integer, AbstractTimeBlock> mBlocks;
    private Application mApp;
    private LifecycleOwner mOwner;
    private IScheduler mScheduler;
    private Notificador mNotificador;
    private WorkManager mWorkManager;
    private Map<Integer, LiveData<List<WorkInfo>>> liveDatas = new HashMap<>();
    private Handler mainHandler;

    private long lastRefresh = 0L;
    private long betweenRefreshes = 10*60*1000; // 10 minutes

    private CheckManager(Application app, LifecycleOwner owner) {
        mFacade = FTimeBlockFacade.getNew(app, owner);
        mApp = app;
        mOwner = owner;
        mSchedules = new HashMap<>();
        mBlocks = new HashMap<>();
        mScheduler = new Scheduler();
        mNotificador = new Notificador(app, app);
        mWorkManager = WorkManager.getInstance(app);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public static CheckManager getInstance(Application app, LifecycleOwner owner) {
        if (instance == null) {
            instance = new CheckManager(app, owner);
            instance.run();
        }
        return instance;
    }

    @Override
    public void refresh() {
        mainHandler.post(() -> {
            long now = System.currentTimeMillis();
            if (now > lastRefresh+betweenRefreshes) {
                lastRefresh = now;
                refreshWorkers();
            }
        });
    }

    private void run() {
        mainHandler.post(() -> {
            mNotificador.createNotificationChannel(R.string.notification_channel_for_random_checks_name, R.string.notification_channel_for_random_checks_description, RandomCheckWorker.NOTIFICATION_CHANNEL_ID);
            // delete existing works
            mBlocks.entrySet().stream()
                    .forEach(s -> stopWorkOfId(s.getKey()));
            // delete observer for work re-start
            liveDatas.entrySet().stream().forEach(s -> s.getValue().removeObservers(mOwner));
            // get all time-blocks and set observer
            mFacade.getAll((tipo, blocks) -> {
                // update time blocks
                mBlocks = blocks.stream()
                        .filter(b -> b.getDays().size() > 0) // filter out time blocks that have no days assigned
                        .filter(b -> b.getPositiveChecks().size() > 0) // filter out time blocks that have no positive checks
                        .filter(b -> b.getMaximumLapse() > 50000) // filter out time blocks that have too low lapse by mistake
                        //.peek(b -> Log.d(TAG, "Time Block: " + b.getName()))
                        //.peek(b -> b.getPositiveChecks().forEach(c -> Log.d(TAG, "Positive Check: " + c.getName())))
                        //.peek(b -> b.getNegativeChecks().forEach(c -> Log.d(TAG, "Negative Check: " + c.getName())))
                        .collect(Collectors.toMap(b -> b.getId(), b -> b));
                // remove any schedules of time-blocks that no longer exist
                Iterator<Map.Entry<Integer, Long>> i = mSchedules.entrySet().iterator();
                while (i.hasNext()) {
                    Map.Entry<Integer, Long> next = i.next();
                    if (!mBlocks.containsKey(next.getKey())) {
                        i.remove();
                    }
                }
                mBlocks.forEach((a,b) -> {
                    setWorkerFor(b);
                });
            });
        });
    }

    private void refreshWorkers() {
        // delete works if existing for non valid time blocks
        mBlocks.entrySet().stream()
                .filter(s -> s.getValue().getDays().size() <= 0)
                .forEach(s -> stopWorkOfId(s.getKey()));
        mBlocks.entrySet().stream()
                .filter(s -> s.getValue().getPositiveChecks().size() <= 0)
                .forEach(s -> stopWorkOfId(s.getKey()));
        mBlocks.entrySet().stream()
                .filter(s -> s.getValue().getMaximumLapse() <= 50000)
                .forEach(s -> stopWorkOfId(s.getKey()));

        // set works for valid time blocks
        ExecutorService servicio = Executors.newSingleThreadExecutor();
        mBlocks.entrySet().stream()
                .filter(s -> s.getValue().getPositiveChecks().size() > 0)
                .filter(s -> s.getValue().getDays().size() > 0)
                .filter(s -> s.getValue().getMaximumLapse() > 50000)
                .forEach(set -> {
            FutureTask<List<WorkInfo>> task = new FutureTask<List<WorkInfo>>(() -> mWorkManager.getWorkInfosForUniqueWork(workUniqueName(set.getKey())).get()){
                @Override
                protected void done() {
                    try {
                        List<WorkInfo> workInfos = get();
                        if (workInfos.size() == 0) {
                            resetWorker(set.getValue());
                        } else {
                            workInfos.forEach(wi -> {
                                if (wi.getState() == null || wi.getState().equals(WorkInfo.State.BLOCKED) || wi.getState().equals(WorkInfo.State.CANCELLED) || wi.getState().equals(WorkInfo.State.FAILED) || wi.getState().equals(WorkInfo.State.SUCCEEDED)) {
                                    resetWorker(set.getValue());
                                }
                            });
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            };
            servicio.execute(task);
        });
    }

    private void resetWorker(AbstractTimeBlock block) {
        mSchedules.put(block.getId(), 0L);
        setWorkerFor(block);
    }

    private void setWorkerFor(AbstractTimeBlock block) {
        Log.d(TAG, "set worker for " + block.getName());
        if (block.getMaximumLapse() < 50000 || block.getPositiveChecks().size() <= 0 || block.getDays().size() <= 0) { return; }
        long delay = getDelay(block);
        Data.Builder data = new Data.Builder()
                .putInt(EXTRA_BLOCK_ID, block.getId())
                .putString(EXTRA_BLOCK_NAME, block.getName());
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(RandomCheckWorker.class)
                .setInputData(data.build())
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build();
        Log.d(TAG ,"work request built");
        WorkManager.getInstance(mApp)
                .beginUniqueWork(workUniqueName(block.getId()), ExistingWorkPolicy.REPLACE, workRequest)
                .enqueue();
        Log.d(TAG, "work request enqueued with delay " + delay);
        observeWorkerToRestart(block);
    }

    private void observeWorkerToRestart(AbstractTimeBlock block) {
        if (liveDatas.containsKey(block.getId())){
            liveDatas.get(block.getId()).removeObservers(mOwner);
            liveDatas.remove(block.getId());
        }
        LiveData<List<WorkInfo>> ld = mWorkManager.getWorkInfosForUniqueWorkLiveData(workUniqueName(block.getId()));
        liveDatas.put(block.getId(), ld);
        ld.observe(mOwner, workInfo -> {
            if (mBlocks.containsKey(block.getId()) && block.getMaximumLapse() > 50000 && block.getDays().size() > 0 && block.getPositiveChecks().size() > 0) {
                if (workInfo.size() == 0) {
                    setWorkerFor(mBlocks.get(block.getId()));
                } else {
                    workInfo.forEach(wi -> {
                        if (wi.getState() == null || wi.getState().equals(WorkInfo.State.BLOCKED) || wi.getState().equals(WorkInfo.State.CANCELLED) || wi.getState().equals(WorkInfo.State.FAILED) || wi.getState().equals(WorkInfo.State.SUCCEEDED)) {
                            setWorkerFor(mBlocks.get(block.getId()));
                        }
                    });
                }
            } else {
                ld.removeObservers(mOwner);
            }
        });
    }

    public String workUniqueName(int blockId) {
        return "work.check.notif.unique.name." + blockId;
    }

    private long getDelay(AbstractTimeBlock block) {
        long opt;
        if (mSchedules.containsKey(block.getId())) {
            opt = mSchedules.get(block.getId());
        } else {
            opt = 0;
        }
        long schedule = mScheduler.schedule(block, opt);
        mSchedules.put(block.getId(), schedule);
        return schedule - mScheduler.getNow();
    }

    public void stopWorkOfId(int blockId) {
        mWorkManager.cancelUniqueWork(workUniqueName(blockId));
        liveDatas.remove(blockId);
        mSchedules.remove(blockId);
    }

}
