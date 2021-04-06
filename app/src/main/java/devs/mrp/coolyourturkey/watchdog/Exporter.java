package devs.mrp.coolyourturkey.watchdog;

import android.content.Context;
import android.content.UriPermission;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import devs.mrp.coolyourturkey.configuracion.ConfiguracionFragment;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.databaseroom.ExpImpHandler;
import devs.mrp.coolyourturkey.databaseroom.valuemap.ValueMap;
import devs.mrp.coolyourturkey.databaseroom.valuemap.ValueMapRepository;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.ListIterator;

public class Exporter {

    private static final String TAG = "EXPORTER CLASS";

    private ValueMapRepository mRepo;
    private LifecycleService mContext;
    private Long mLastExport;
    private MisPreferencias mMisPreferencias;

    private LiveData<List<ValueMap>> mExportPathString;
    //private LiveData<List<ValueMap>> mExportFlag;

    //private boolean exportFlag = false;
    private Uri exportUri;

    public Exporter(LifecycleService context) {
        mContext = context;
        mRepo = ValueMapRepository.getRepo(mContext.getApplication());
        mLastExport = 0L;

        mMisPreferencias = new MisPreferencias(context);
        /*
        mExportFlag = mRepo.getValueOf(ConfiguracionFragment.EXPORT_TXT_YES_NO_KEY);
        mExportFlag.observe(mContext, new Observer<List<ValueMap>>() {
            @Override
            public void onChanged(List<ValueMap> valueMaps) {
                if (valueMaps.size() > 0) {
                    if (valueMaps.get(0).getValor().equals(ConfiguracionFragment.TRUE)) {
                        exportFlag = true;
                    } else if (valueMaps.get(0).getValor().equals(ConfiguracionFragment.FALSE)) {
                        exportFlag = false;
                    }
                } else {
                    exportFlag = false;
                }
            }
        }); */

        mExportPathString = mRepo.getValueOf(ConfiguracionFragment.EXPORT_TXT_VALUE_MAP_KEY);
        mExportPathString.observe(mContext, new Observer<List<ValueMap>>() {
            @Override
            public void onChanged(List<ValueMap> valueMaps) {
                if (valueMaps.size() > 0) {
                    exportUri = Uri.parse(valueMaps.get(0).getValor());
                }
            }
        });
    }

    private boolean isExportTimeout() {
        Long lnow = System.currentTimeMillis();
        if (lnow - mMisPreferencias.getMilisInterloSync() > mLastExport) {
            return true;
        }
        return false;
    }

    public void export(Long milis) {
        if (isExportTimeout()) {

            /**
             * Bloque para exportar datos a la carpeta compartida
             */
            if (exportUri != null && tenemosPermisoEscritura(mContext, exportUri) && mMisPreferencias.getExport()) {
                try (ParcelFileDescriptor pfd = mContext.getContentResolver().openFileDescriptor(exportUri, "w");
                     FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
                     PrintWriter pw = new PrintWriter(fileOutputStream, false);
                     FileChannel outChan = fileOutputStream.getChannel();
                ) {


                    // El FileOutputStream debería truncar el archivo nada más abrirlo
                    // pero esto no está sucediendo en mi teléfono y hay que truncarlo "manualmente"
                    outChan.truncate(0);

                    pw.print(milis);
                    pw.flush();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /**
             * Bloque para guardar copia interna en caso de desinstalar y reinstalar
             */
            ExpImpHandler eih = ExpImpHandler.getInstance();
            eih.writeToFile(mContext, milis);

            // test para ver el valor del archivo escrito
            // Long resultado = eih.readFromFile(mContext);
            // Log.d(TAG, "se ha guardado en archivos internos: " + String.valueOf(resultado));

            /**
             * Resetear el tiempo para el próximo ciclo
             */
            mLastExport = System.currentTimeMillis();
        }
    }

    public static boolean tenemosPermisoEscritura(Context context, Uri uri) {
        List<UriPermission> uriPermissions = context.getContentResolver().getPersistedUriPermissions();
        ListIterator<UriPermission> iterator = uriPermissions.listIterator();
        while (iterator.hasNext()) {
            UriPermission perm = iterator.next();
            if (perm.getUri().equals(uri) && perm.isWritePermission()) {
                return true;
            }
        }
        return false;
    }
}
