package devs.mrp.coolyourturkey.databaseroom;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.CheckTimeBlock;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.CheckTimeBlockDao;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.TimeBlockAndChecksCrossRef;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.export.TimeBlockExport;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.export.TimeBlockExportDao;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger.TimeBlockLogger;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger.TimeBlockLoggerDao;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.schedules.TimeBlockSchedule;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.schedules.TimeBlockScheduleDao;
import devs.mrp.coolyourturkey.databaseroom.contador.Contador;
import devs.mrp.coolyourturkey.databaseroom.contador.ContadorDao;
import devs.mrp.coolyourturkey.databaseroom.deprecated.apptogroup.AppToGroup;
import devs.mrp.coolyourturkey.databaseroom.deprecated.apptogroup.AppToGroupDao;
import devs.mrp.coolyourturkey.databaseroom.deprecated.conditionnegativetogroup.ConditionNegativeToGroup;
import devs.mrp.coolyourturkey.databaseroom.deprecated.conditionnegativetogroup.ConditionNegativeToGroupDao;
import devs.mrp.coolyourturkey.databaseroom.deprecated.conditiontogroup_old_deprecated.ConditionToGroup;
import devs.mrp.coolyourturkey.databaseroom.deprecated.conditiontogroup_old_deprecated.ConditionToGroupDao;
import devs.mrp.coolyourturkey.databaseroom.deprecated.grouplimit.GroupLimit;
import devs.mrp.coolyourturkey.databaseroom.deprecated.grouplimit.GroupLimitDao;
import devs.mrp.coolyourturkey.databaseroom.deprecated.grupopositivo.GrupoPositivo;
import devs.mrp.coolyourturkey.databaseroom.deprecated.grupopositivo.GrupoPositivoDao;
import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.databaseroom.grupo.GrupoDao;
import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementToGroup;
import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementToGroupDao;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition.GrupoCondition;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition.GrupoConditionDao;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupoexport.GrupoExport;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupoexport.GrupoExportDao;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListada;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListadaDao;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.RandomCheck;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.RandomCheckDao;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLogger;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLoggerDao;
import devs.mrp.coolyourturkey.databaseroom.urisimportar.Importables;
import devs.mrp.coolyourturkey.databaseroom.urisimportar.ImportablesDao;
import devs.mrp.coolyourturkey.databaseroom.valuemap.ValueMap;
import devs.mrp.coolyourturkey.databaseroom.valuemap.ValueMapDao;

// Añade aquí tus Entities
@Database(entities = {AplicacionListada.class, ValueMap.class, Contador.class, Importables.class, GrupoPositivo.class,
        AppToGroup.class, ConditionToGroup.class, ConditionNegativeToGroup.class, TimeLogger.class, GrupoExport.class,
        GroupLimit.class, RandomCheck.class, CheckTimeBlock.class, TimeBlockAndChecksCrossRef.class, TimeBlockSchedule.class,
        TimeBlockLogger.class, TimeBlockExport.class, Grupo.class, ElementToGroup.class, GrupoCondition.class},
        version = 30)
public abstract class TurkeyDatabaseRoom extends RoomDatabase {

    // Anñade aquí tus DAOs
    public abstract AplicacionListadaDao aplicacionListadaDao();
    public abstract ValueMapDao valueMapDao();
    public abstract ContadorDao contadorDao();
    public abstract ImportablesDao importablesDao();
    public abstract GrupoPositivoDao grupoPositivoDao();
    public abstract GrupoDao grupoDao();
    public abstract ElementToGroupDao elementToGroupDao();
    public abstract GrupoConditionDao grupoConditionDao();
    public abstract AppToGroupDao appToGroupDao();
    public abstract ConditionToGroupDao conditionToGroupDao();
    public abstract ConditionNegativeToGroupDao conditionNegativeToGroupDao();
    public abstract TimeLoggerDao timeLoggerDao();
    public abstract GrupoExportDao grupoExportDao();
    public abstract GroupLimitDao groupLimitDao();
    public abstract RandomCheckDao randomCheckDao();
    public abstract CheckTimeBlockDao checkTimeBlockDao();
    public abstract TimeBlockScheduleDao timeBlockScheduleDao();
    public abstract TimeBlockLoggerDao timeBlockLoggerDao();
    public abstract TimeBlockExportDao timeBlockExportDao();

    private static volatile TurkeyDatabaseRoom INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static Context mContext;

    /**
     * Migrate from:
     * version 1
     * to
     * version 2 - no changes
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
        }
    };

    /**
     * Migrate from:
     * version 2
     * to
     * version 3 - new table grupopositivo
     * with primary key id, autogenerate
     * with field string nombre
     */
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'grupopositivo' ('id' INTEGER NOT NULL, 'nombre' TEXT NOT NULL, PRIMARY KEY('id'))");
        }
    };

    /**
     * Migrate from:
     * version 3
     * to
     * version 4
     */
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'apptogroup' ('id' INTEGER NOT NULL, 'appname' TEXT NOT NULL, 'groupid' INTEGER, PRIMARY KEY('id'))");
        }
    };

    /**
     * Migrate from:
     * version 4
     * to
     * version 5
     */
    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'conditiontogroup' ('id' INTEGER NOT NULL, 'groupid' INTEGER NOT NULL, 'type' TEXT NOT NULL, 'filetarget' TEXT, 'conditionalgroupid' INTEGER, 'conditionalminutes' INTEGER, 'fromlastndays' INTEGER, PRIMARY KEY('id'))");
        }
    };

    /**
     * Migrate from:
     * version 5
     * to
     * version 6
     */
    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'timelogger' ('id' BIGINT NOT NULL, 'millistimestamp' BIGINT NOT NULL, 'packagename' TEXT NOT NULL, 'groupid' INTEGER, 'positivenegative' TEXT NOT NULL, 'usedtime' BIGINT NOT NULL, 'countedtime' BIGINT NOT NULL, PRIMARY KEY ('id'))");
        }
    };

    /**
     * Migrate from:
     * version 6
     * to
     * version 7
     */
    static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE IF EXISTS 'timelogger'");
            database.execSQL("CREATE TABLE IF NOT EXISTS 'timelogger' ('id' INTEGER NOT NULL, 'millistimestamp' INTEGER NOT NULL, 'packagename' TEXT NOT NULL, 'groupid' INTEGER, 'positivenegative' TEXT NOT NULL, 'usedtime' INTEGER NOT NULL, 'countedtime' INTEGER NOT NULL, PRIMARY KEY ('id'))");
        }
    };

    /**
     * Migrate from:
     * version 7
     * to
     * version 8
     */
    static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'grupoexport' ('groupid' INTEGER NOT NULL, 'archivo' TEXT NOT NULL, PRIMARY KEY ('groupid'))");
        }
    };

    /**
     * Migrate from:
     * version 8
     * to
     * version 9
     */
    static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE IF EXISTS 'grupoexport'");
            database.execSQL("CREATE TABLE IF NOT EXISTS 'grupoexport' ('groupid' INTEGER NOT NULL, 'archivo' TEXT NOT NULL, 'days' INTEGER NOT NULL, PRIMARY KEY ('groupid'))");
        }
    };

    /**
     * Migrate from:
     * version 9
     * to
     * version 10
     */
    static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'grouplimit' ('id' INTEGER NOT NULL, 'groupid' INTEGER NOT NULL, 'offsetdays' INTEGER NOT NULL, 'minuteslimit' INTEGER NOT NULL, PRIMARY KEY ('id'))");
        }
    };

    /**
     * Migrate from:
     * version 10
     * to
     * version 11
     */
    static final Migration MIGRATION_10_11 = new Migration(10, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'grouplimit' ADD 'blocking' INTEGER NOT NULL DEFAULT(0)");
        }
    };

    /**
     * Migrate from:
     * version 11
     * to
     * version 12
     */
    static final Migration MIGRATION_11_12 = new Migration(11, 12) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'grouplimit' ADD 'solosicondiciones' INTEGER NOT NULL DEFAULT(1)");
        }
    };

    /**
     * Migrate from:
     * version 12
     * to
     * version 13
     */
    static final Migration MIGRATION_12_13 = new Migration(12, 13) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'conditionnegativetogroup' ('id' INTEGER NOT NULL, 'type' TEXT NOT NULL, 'filetarget' TEXT, 'conditionalgroupid' INTEGER, 'conditionalminutes' INTEGER, 'fromlastndays' INTEGER, PRIMARY KEY('id'))");
        }
    };

    /**
     * Migrate from:
     * version 13
     * to
     * version 14
     */
    static final Migration MIGRATION_13_14 = new Migration(13, 14) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'randomcheck' ('id' INTEGER NOT NULL, 'type' TEXT NOT NULL, 'name' TEXT NOT NULL, 'question' TEXT NOT NULL, 'multiplicador' INTEGER, PRIMARY KEY('id'))");
        }
    };

    /**
     * Migrate from:
     * version 14
     * to
     * version 15
     */
    static final Migration MIGRATION_14_15 = new Migration(14, 15) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'checktimeblock' ('blockid' INTEGER NOT NULL, 'name' TEXT NOT NULL, 'fromtime' INTEGER NOT NULL, 'totime' INTEGER NOT NULL, 'minlapse' INTEGER NOT NULL, 'maxlapse' INTEGER NOT NULL, 'monday' INTEGER NOT NULL DEFAULT(0), 'tuesday' INTEGER NOT NULL DEFAULT(0), 'wednesday' INTEGER NOT NULL DEFAULT(0), 'thursday' INTEGER NOT NULL DEFAULT(0), 'friday' INTEGER NOT NULL DEFAULT(0), 'saturday' INTEGER NOT NULL DEFAULT(0), 'sunday' INTEGER NOT NULL DEFAULT(0), PRIMARY KEY('blockid'))");
            database.execSQL("CREATE TABLE IF NOT EXISTS 'timeblockandcheckcrossref' ('blockid' INTEGER NOT NULL, 'id' INTEGER NOT NULL, PRIMARY KEY('blockid', 'id'))");
        }
    };

    /**
     * Migrate from:
     * version 15
     * to
     * version 16
     */
    static final Migration MIGRATION_15_16 = new Migration(15, 16) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'timeblockschedule' ('scheduleid' INTEGER NOT NULL, 'schedulemillis' INTEGER NOT NULL, PRIMARY KEY ('scheduleid'))");
        }
    };

    /**
     * Migrate from:
     * version 16
     * to
     * version 17
     */
    static final Migration MIGRATION_16_17 = new Migration(16, 17) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'timeblocklogger' ('loggerid' INTEGER NOT NULL, 'epoch' INTEGER NOT NULL, 'blockid' INTEGER NOT NULL, 'timecounted' INTEGER NOT NULL, PRIMARY KEY ('loggerid'))");
        }
    };

    /**
     * Migrate from:
     * version 17
     * to
     * version 18
     */
    static final Migration MIGRATION_17_18 = new Migration(17, 18) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'conditiontogroup' ADD 'conditionalrandomcheckid' INTEGER");
        }
    };

    /**
     * Migrate from:
     * version 18
     * to
     * version 19
     */
    static final Migration MIGRATION_18_19 = new Migration(18, 19) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'conditionnegativetogroup' ADD 'conditionalblockid' INTEGER");
        }
    };

    /**
     * Migrate from:
     * version 19
     * to
     * version 20
     */
    static final Migration MIGRATION_19_20 = new Migration(19, 20) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'timeblockexport' ('expblockid' INTEGER NOT NULL, 'archivo' TEXT NOT NULL, 'days' INTEGER NOT NULL, PRIMARY KEY ('expblockid'))");
        }
    };

    /**
     * Migrate from:
     * version 20
     * to
     * version 21
     */
    static final Migration MIGRATION_20_21 = new Migration(20, 21) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'checktimeblock' ADD 'prizeammount' INTEGER NOT NULL DEFAULT(0)");
        }
    };

    /**
     * Migrate from:
     * version 21
     * to
     * version 22 - new table grupo
     * with primary key id, autogenerate
     * with field string nombre
     */
    static final Migration MIGRATION_21_22 = new Migration(21, 22) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'grupo' ('id' INTEGER NOT NULL, 'nombre' TEXT NOT NULL, 'type' TEXT NOT NULL, PRIMARY KEY('id'))");
        }
    };

    /**
     * Migrate from:
     * version 22
     * to
     * version 23 - new table ElementToGroup
     */
    static final Migration MIGRATION_22_23 = new Migration(22, 23) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'elementtogroup' ('id' INTEGER NOT NULL, 'name' TEXT NOT NULL, 'toid' INTEGER NOT NULL, 'type' TEXT NOT NULL, 'groupid' INTEGER NOT NULL, PRIMARY KEY('id'))");
        }
    };

    /**
     * Migrate from:
     * version 23
     * to
     * version 24
     */
    static final Migration MIGRATION_23_24 = new Migration(23, 24) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'grupocondition' ('id' INTEGER NOT NULL, 'groupid' INTEGER NOT NULL, 'conditionalgroupid' INTEGER NOT NULL, 'conditionalminutes' INTEGER NOT NULL, 'fromlastndays' INTEGER NOT NULL, PRIMARY KEY('id'))");
        }
    };

    /**
     * Migrate from:
     * version 24
     * to
     * version 25
     */
    static final Migration MIGRATION_24_25 = new Migration(24, 25) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'timeblocklogger' ADD 'groupid' INTEGER");
        }
    };

    /**
     * Migrate from:
     * version 25
     * to
     * version 26
     */
    static final Migration MIGRATION_25_26 = new Migration(25, 26) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'grupo' ADD 'preventclose' INTEGER NOT NULL DEFAULT(0)");
        }
    };

    /**
     * Migrate from:
     * version 26
     * to
     * version 27
     */
    static final Migration MIGRATION_26_27 = new Migration(26, 27) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'randomcheck' ADD 'frequency' INTEGER NOT NULL DEFAULT(1)");
        }
    };

    /**
     * Migrate from:
     * version 27
     * to
     * version 28
     */
    static final Migration MIGRATION_27_28 = new Migration(27, 28) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE INDEX IF NOT EXISTS 'index_timelogger_millistimestamp' ON timelogger(millistimestamp)");
            database.execSQL("CREATE INDEX IF NOT EXISTS 'index_timeblocklogger_epoch' ON timeblocklogger(epoch)");
        }
    };

    /**
     * Migrate from:
     * version 28
     * to
     * version 29
     */
    static final Migration MIGRATION_28_29 = new Migration(28, 29) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE INDEX IF NOT EXISTS 'index_timelogger_millistimestamp_groupid' ON timelogger(millistimestamp, groupid)");
            database.execSQL("CREATE INDEX IF NOT EXISTS 'index_timeblocklogger_epoch_groupid' ON timeblocklogger(epoch, groupid)");
        }
    };

    /**
     * Migrate from:
     * version 29
     * to
     * version 30
     */
    static final Migration MIGRATION_29_30 = new Migration(29, 30) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'grupo' ADD 'ignoreconditions' INTEGER NOT NULL DEFAULT(0)");
        }
    };

    /**
     * No more migration scripts
     * Need to include them in the following in getDatabase()
     */

    public static TurkeyDatabaseRoom getDatabase(final Context context) {
        mContext = context;
        if (INSTANCE == null) {
            synchronized (TurkeyDatabaseRoom.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), TurkeyDatabaseRoom.class, "apps_listadas")
                            .addCallback(sRoomDatabaseCallback) //inicialización de la base de datos
                            // add the migration schemas separated by commas
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8,
                                    MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11, MIGRATION_11_12, MIGRATION_12_13, MIGRATION_13_14, MIGRATION_14_15,
                                    MIGRATION_15_16, MIGRATION_16_17, MIGRATION_17_18, MIGRATION_18_19, MIGRATION_19_20, MIGRATION_20_21, MIGRATION_21_22,
                                    MIGRATION_22_23, MIGRATION_23_24, MIGRATION_24_25, MIGRATION_25_26, MIGRATION_26_27, MIGRATION_27_28, MIGRATION_28_29,
                                    MIGRATION_29_30)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {

        @Override
        public void onCreate(SupportSQLiteDatabase db) {
            super.onCreate(db);

            ExpImpHandler eih = ExpImpHandler.getInstance();
            Long acumulado = eih.readFromFile(mContext);
            databaseWriteExecutor.execute(() -> {
                ContadorDao dao = INSTANCE.contadorDao();
                Contador contador = new Contador(System.currentTimeMillis(), acumulado);
                dao.insert(contador);
            });
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };
}
