package devs.mrp.coolyourturkey.databaseroom;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import androidx.lifecycle.LifecycleOwner;

import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger.TimeBlockLoggerRepository;
import devs.mrp.coolyourturkey.databaseroom.contador.ContadorRepository;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLoggerRepository;
import devs.mrp.coolyourturkey.databaseroom.valuemap.EntryCleaner;

public class EntryCleanerImpl implements EntryCleaner {

    private static long mDayEpoch = 0L;
    public static final int DAYS_OLD_TO_DELETE = 30; // delete entries older than these days

    private TimeBlockLoggerRepository blockLoggerRepo;
    private TimeLoggerRepository loggerRepo;
    private ContadorRepository contadorRepo;
    private LifecycleOwner mOwner;
    private Context mContext;

    public EntryCleanerImpl(Application app, LifecycleOwner owner, Context context) {
        mOwner = owner;
        loggerRepo = TimeLoggerRepository.getRepo(app);
        contadorRepo = ContadorRepository.getRepo(app);
        blockLoggerRepo = TimeBlockLoggerRepository.getRepo(app);
        mContext = context;
    }

    @Override
    public void cleanOlEntries() {
        // perform the clean only once per day
        long todayEpoch = MilisToTime.beginningOfTodayConsideringChangeOfDay(mContext);
        if (!(mDayEpoch == todayEpoch)) {
            mDayEpoch = todayEpoch;
            long target = MilisToTime.beginningOfOffsetDaysConsideringChangeOfDay(DAYS_OLD_TO_DELETE, mContext);
            cleanLogger(target);
            cleanContador(target);
        }
    }

    private void cleanLogger(long olderThan) {
        blockLoggerRepo.deleteByEarlierThan(olderThan);
        loggerRepo.deleteByEarlierThan(olderThan);
    }

    private void cleanContador(long olderThan) {
        // make sure that we have at least one entry that is not going to be deleted
        Handler mainLooper = new Handler(mContext.getMainLooper());
        mainLooper.post(() -> {
            contadorRepo.getUltimoContador().observe(mOwner, contadores -> {
                if (contadores.size() > 0) {
                    long lastEpoch = contadores.get(0).getDayOfEpoch();
                    contadorRepo.clearOlderThan(lastEpoch>olderThan ? olderThan : lastEpoch);
                }
            });
        });
    }
}
