package devs.mrp.coolyourturkey.watchdog.checkscheduling;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

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
        RandomCheckWorker.configureNotification(mNotificador, mApp);
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
                Log.d(TAG, "going to refresh workers from watchdog");
                refreshWorkers();
            }
        });
    }

    private void run() {
        mainHandler.post(() -> {
            mNotificador.createNotificationChannel(R.string.notification_channel_for_random_checks_name, R.string.notification_channel_for_random_checks_description, RandomCheckWorker.NOTIFICATION_CHANNEL_ID);
            // get all time-blocks and set observer
            mFacade.getAll((tipo, blocks) -> {
                Log.d(TAG, "updated observer of Facade for the TimeBlocks");
                // update time blocks
                mBlocks = blocks.stream().peek(b -> Log.d(TAG, "block: " + b.getName())).collect(Collectors.toMap(b -> b.getId(), b -> b));
                // remove any schedules of time-blocks that no longer exist
                Iterator<Map.Entry<Integer, Long>> i = mSchedules.entrySet().iterator();
                while (i.hasNext()) {
                    Map.Entry<Integer, Long> next = i.next();
                    if (!mBlocks.containsKey(next.getKey())) {
                        i.remove();
                    }
                }
                mBlocks.forEach((a,b) -> {
                    Log.d(TAG, "setting worker for " + b.getName() + " with current schedule " + mSchedules.get(b.getId()));
                    setWorkerFor(b);
                });
            });
        });
    }

    private void refreshWorkers() {
        ExecutorService servicio = Executors.newSingleThreadExecutor();
        mBlocks.forEach((id, block) -> {
            FutureTask<List<WorkInfo>> task = new FutureTask<List<WorkInfo>>(() -> mWorkManager.getWorkInfosForUniqueWork(workUniqueName(id)).get()){
                @Override
                protected void done() {
                    try {
                        List<WorkInfo> workInfos = get();
                        if (workInfos.size() == 0) {
                            resetWorker(block);
                        } else {
                            workInfos.forEach(wi -> {
                                if (wi.getState() == null || wi.getState().equals(WorkInfo.State.BLOCKED) || wi.getState().equals(WorkInfo.State.CANCELLED) || wi.getState().equals(WorkInfo.State.FAILED) || wi.getState().equals(WorkInfo.State.SUCCEEDED)) {
                                    resetWorker(block);
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
        RandomCheckWorker.addBlock(block);
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(RandomCheckWorker.class)
                .setInitialDelay(getDelay(block), TimeUnit.MILLISECONDS)
                .build();
        WorkManager.getInstance(mApp)
                .beginUniqueWork(workUniqueName(block.getId()), ExistingWorkPolicy.REPLACE, workRequest)
                .enqueue();
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
            if (workInfo.size() == 0) {
                Log.d(TAG, "setting new worker from observer because size == 0 for block " + block.getName());
                setWorkerFor(block);
            } else {
                workInfo.forEach(wi -> {
                    if (wi.getState() == null || wi.getState().equals(WorkInfo.State.BLOCKED) || wi.getState().equals(WorkInfo.State.CANCELLED) || wi.getState().equals(WorkInfo.State.FAILED) || wi.getState().equals(WorkInfo.State.SUCCEEDED)) {
                        Log.d(TAG, "setting new worker from observer for block " + block.getName());
                        setWorkerFor(block);
                    }
                });
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
