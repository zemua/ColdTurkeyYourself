package devs.mrp.coolyourturkey.databaseroom.grouplimit;

import android.app.Application;

import java.security.acl.Group;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;

public class GroupLimitRepository {

    private GroupLimitDao mDao;
    private static GroupLimitRepository mRepo;

    private GroupLimitRepository(Application application) {
        TurkeyDatabaseRoom db = TurkeyDatabaseRoom.getDatabase(application);
        // mDao = db.GroupLimitDao(); // TODO add to db
    }

    public static GroupLimitRepository getRepo(Application application) {
        if (mRepo == null) {
            mRepo = new GroupLimitRepository(application);
        }
        return mRepo;
    }

    // TODO complete methods

}
