package devs.mrp.coolyourturkey.databaseroom;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;

import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.databaseroom.contador.ContadorRepository;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLoggerRepository;
import devs.mrp.coolyourturkey.databaseroom.valuemap.EntryCleaner;

public class EntryCleanerImpl implements EntryCleaner {

    private static long mDay = 0L;
    private static int mOffset = 30; // delete entries older than these days

    private TimeLoggerRepository loggerRepo;
    private ContadorRepository contadorRepo;
    private LifecycleOwner mOwner;

    public EntryCleanerImpl(Application app, LifecycleOwner owner) {
        mOwner = owner;
        loggerRepo = TimeLoggerRepository.getRepo(app);
        contadorRepo = ContadorRepository.getRepo(app);
    }

    @Override
    public void cleanOlEntries() {
        // perform the clean only once per day
        long today = MilisToTime.currentDay();
        if (!(mDay == today)) {
            mDay = today;
            long target = MilisToTime.offsetDayInMillis(mOffset);
            cleanLogger(target);
            cleanContador(target);
        }
    }

    private void cleanLogger(long olderThan) {
        loggerRepo.deleteByEarlierThan(olderThan);
    }

    private void cleanContador(long olderThan) {
        // make sure that we have at least one entry that is not going to be deleted
        contadorRepo.getUltimoContador().observe(mOwner, contadores -> {
            if (contadores.size() > 0 && contadores.get(0).getDayOfEpoch()>olderThan) {
                contadorRepo.clearOlderThan(olderThan);
            }
        });
    }
}
