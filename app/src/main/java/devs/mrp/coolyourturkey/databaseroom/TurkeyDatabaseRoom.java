package devs.mrp.coolyourturkey.databaseroom;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import devs.mrp.coolyourturkey.databaseroom.apptogroup.AppToGroup;
import devs.mrp.coolyourturkey.databaseroom.apptogroup.AppToGroupDao;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup.ConditionToGroup;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup.ConditionToGroupDao;
import devs.mrp.coolyourturkey.databaseroom.contador.Contador;
import devs.mrp.coolyourturkey.databaseroom.contador.ContadorDao;
import devs.mrp.coolyourturkey.databaseroom.grouplimit.GroupLimit;
import devs.mrp.coolyourturkey.databaseroom.grouplimit.GroupLimitDao;
import devs.mrp.coolyourturkey.databaseroom.grupoexport.GrupoExport;
import devs.mrp.coolyourturkey.databaseroom.grupoexport.GrupoExportDao;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivo;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivoDao;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListada;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListadaDao;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLogger;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLoggerDao;
import devs.mrp.coolyourturkey.databaseroom.urisimportar.Importables;
import devs.mrp.coolyourturkey.databaseroom.urisimportar.ImportablesDao;
import devs.mrp.coolyourturkey.databaseroom.valuemap.ValueMap;
import devs.mrp.coolyourturkey.databaseroom.valuemap.ValueMapDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Añade aquí tus Entities
@Database(entities = {AplicacionListada.class, ValueMap.class, Contador.class, Importables.class, GrupoPositivo.class, AppToGroup.class, ConditionToGroup.class, TimeLogger.class, GrupoExport.class, GroupLimit.class}, version = 12)
public abstract class TurkeyDatabaseRoom extends RoomDatabase {

    // Anñade aquí tus DAOs
    public abstract AplicacionListadaDao aplicacionListadaDao();
    public abstract ValueMapDao valueMapDao();
    public abstract ContadorDao contadorDao();
    public abstract ImportablesDao importablesDao();
    public abstract GrupoPositivoDao grupoPositivoDao();
    public abstract AppToGroupDao appToGroupDao();
    public abstract ConditionToGroupDao conditionToGroupDao();
    public abstract TimeLoggerDao timeLoggerDao();
    public abstract GrupoExportDao grupoExportDao();
    public abstract GroupLimitDao groupLimitDao();

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
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11, MIGRATION_11_12) // add the migration schemas separated by commas
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
