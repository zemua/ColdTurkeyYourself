package devs.mrp.coolyourturkey.databaseroom;

import android.app.Application;

import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.databaseroom.contador.ContadorRepository;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLoggerRepository;
import devs.mrp.coolyourturkey.databaseroom.valuemap.EntryCleaner;

public class EntryCleanerImpl implements EntryCleaner {

    private static long mDay = 0L;
    private static int mOffset = 30; // delete entries older than these days

    private TimeLoggerRepository loggerRepo;
    private ContadorRepository contadorRepo;

    public EntryCleanerImpl(Application app) {
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
        contadorRepo.clearOlderThan(olderThan);
    }
}
