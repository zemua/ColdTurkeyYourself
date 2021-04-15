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
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivo;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivoDao;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListada;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListadaDao;
import devs.mrp.coolyourturkey.databaseroom.urisimportar.Importables;
import devs.mrp.coolyourturkey.databaseroom.urisimportar.ImportablesDao;
import devs.mrp.coolyourturkey.databaseroom.valuemap.ValueMap;
import devs.mrp.coolyourturkey.databaseroom.valuemap.ValueMapDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Añade aquí tus Entities
@Database(entities = {AplicacionListada.class, ValueMap.class, Contador.class, Importables.class, GrupoPositivo.class, AppToGroup.class, ConditionToGroup.class}, version = 5)
public abstract class TurkeyDatabaseRoom extends RoomDatabase {

    // Anñade aquí tus DAOs
    public abstract AplicacionListadaDao aplicacionListadaDao();
    public abstract ValueMapDao valueMapDao();
    public abstract ContadorDao contadorDao();
    public abstract ImportablesDao importablesDao();
    public abstract GrupoPositivoDao grupoPositivoDao();
    public abstract AppToGroupDao appToGroupDao();
    public abstract ConditionToGroupDao conditionToGroupDao();

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

    public static TurkeyDatabaseRoom getDatabase(final Context context) {
        mContext = context;
        if (INSTANCE == null) {
            synchronized (TurkeyDatabaseRoom.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), TurkeyDatabaseRoom.class, "apps_listadas")
                            .addCallback(sRoomDatabaseCallback) //inicialización de la base de datos
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5) // add the migration schemas separated by commas
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
