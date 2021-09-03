package devs.mrp.coolyourturkey.watchdog;

import android.app.Application;
import android.content.Context;
import android.content.UriPermission;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.databaseroom.urisimportar.Importables;
import devs.mrp.coolyourturkey.databaseroom.urisimportar.ImportablesRepository;
import devs.mrp.coolyourturkey.databaseroom.valuemap.ValueMapRepository;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class Importer implements Feedbacker<Long> {

    private static final String TAG = "IMPORTER CLASS";

    private List<FeedbackListener<Long>> listeners = new ArrayList<>();
    public static final int FEEDBACK_TIEMPO = 0;

    private LifecycleOwner mContext;
    private Application mApp;
    private ImportablesRepository mImportRepo;
    private ValueMapRepository mMapRepo;
    private static Long mLastImport = 0L;
    private static Long mResult = 0L;
    private static boolean importablesFlagLoaded;
    private MisPreferencias mMisPreferencias;

    private LiveData<List<Importables>> mImportables;
    private List<Uri> importUris;


    Importer(LifecycleOwner context, Application app) {
        //importFlagLoaded = false;
        importablesFlagLoaded = false;
        mContext = context;
        mApp = app;
        mMapRepo = ValueMapRepository.getRepo(app);
        mImportRepo = ImportablesRepository.getRepo(app);
        importUris = new ArrayList<>();
        mMisPreferencias = new MisPreferencias(app);

        mImportables = mImportRepo.getAllImportables();
        mImportables.observe(mContext, new Observer<List<Importables>>() {
            @Override
            public void onChanged(List<Importables> importables) {
                importUris.clear();
                importables.forEach((importable) -> {
                    importUris.add(Uri.parse(importable.getUri()));
                });
                importablesFlagLoaded = true;
                giveFeedback(FEEDBACK_TIEMPO, importarTiempoTotal());
            }
        });

        giveFeedback(FEEDBACK_TIEMPO, importarTiempoTotal());
    }

    private boolean isImportTimeout() {
        Long lnow = System.currentTimeMillis();
        if (lnow - mMisPreferencias.getMilisInterloSync() > mLastImport) {
            return true;
        }
        return false;
    }

    public Long importarTiempoTotal() {
        if (isImportTimeout() && mMisPreferencias.getImport() && importUris != null && importablesFlagLoaded) {
            mLastImport = System.currentTimeMillis();
            Long preResult = 0L;
            ListIterator<Uri> literator = importUris.listIterator();
            while (literator.hasNext()) {
                Uri uri = literator.next();
                try {
                    if (tenemosPermisoLectura(mApp, uri)) {
                        String longString = readTextFromUri(uri);
                        Long longNum = Long.parseLong(longString.trim());
                        if (longNum != null) {
                            preResult += longNum;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mResult = preResult;
        }
        if (!mMisPreferencias.getImport()){
            return 0L;
        }
        return mResult;
    }

    public static boolean tenemosPermisoLectura(Context context, Uri uri) {
        List<UriPermission> uriPermissions = context.getContentResolver().getPersistedUriPermissions();
        ListIterator<UriPermission> iterator = uriPermissions.listIterator();
        while (iterator.hasNext()) {
            UriPermission perm = iterator.next();
            if (perm.getUri().equals(uri) && perm.isReadPermission()) {
                return true;
            }
        }
        return false;
    }

    private String readTextFromUri(Uri uri) {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = mApp.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    @Override
    public void giveFeedback(int tipo, Long feedback) {
        if (tipo != FEEDBACK_TIEMPO || importablesFlagLoaded) {
            listeners.forEach((listener) -> {
                listener.giveFeedback(tipo, feedback);
            });
        }
    }

    @Override
    public void addFeedbackListener(FeedbackListener<Long> listener) {
        listeners.add(listener);
    }
}
