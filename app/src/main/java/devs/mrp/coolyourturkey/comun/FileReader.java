package devs.mrp.coolyourturkey.comun;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

import devs.mrp.coolyourturkey.configuracion.ConfiguracionFragment;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;

public class FileReader {

    public static void openTextFile(Fragment fragment, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(ConfiguracionFragment.TEXT_MIME_TYPE);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static class DayConsumption {
        private LocalDate localDate;
        private long consumption;

        public DayConsumption(String s) {
            String[] values = s.split("-");
            localDate = LocalDate.of(Integer.valueOf(values[0]), Integer.valueOf(values[1])+1, Integer.valueOf(values[2]));
            consumption = Long.valueOf(values[3]);
        }

        public LocalDate getLocalDate() {
            return localDate;
        }

        public long getConsumption() {
            return consumption;
        }
    }

    public static Uri getFileReadPermission(Context context, Intent resultData){
        final int takeFlags = resultData.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (resultData != null) {
            Uri uri = resultData.getData();
            if (uri == null) {
                return null;
            }
            context.getContentResolver().takePersistableUriPermission(uri, takeFlags);
            return uri;
        }
        return null;
    }

    private static boolean isCorrectImportFormat(String s) {
        return s.matches("^\\d{4}\\-\\d{1,2}\\-\\d{1,2}\\-\\d+$");
    }

    /*public static String readTextFromUri(Application app, Uri uri){
        String longString = "";
        try {
            if (ifHaveReadingRights(app, uri)) {
                longString = extractTextFromUri(app, uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return longString;
    }*/

    public static Map<Long,DayConsumption> readPastDaysConsumption(Application app, Uri uri) {
        return readPastDaysConsumptionUntilEpoch(app, uri, LocalDate.now());
    }

    public static Map<Long,DayConsumption> readPastDaysConsumptionConsideringChangeOfDay(Application app, Uri uri) {
        MisPreferencias prefs = new MisPreferencias(app);
        int hours = prefs.getHourForChangeOfDay();
        LocalDate date = LocalDateTime.now().minusHours(hours).toLocalDate();
        return readPastDaysConsumptionUntilEpoch(app, uri, date);
    }

    private static Map<Long,DayConsumption> readPastDaysConsumptionUntilEpoch(Application app, Uri uri, LocalDate date) {
        Map<Long,DayConsumption> map = new HashMap<>();
        try {
            if (ifHaveReadingRights(app, uri)) {
                try (InputStream inputStream = app.getContentResolver().openInputStream(uri);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (isCorrectImportFormat(line)) {
                            DayConsumption dayConsumption = new DayConsumption(line);
                            long lastDays = dayConsumption.getLocalDate().until(date, ChronoUnit.DAYS);
                            map.put(lastDays, dayConsumption);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static boolean ifHaveReadingRights(Context context, Uri uri) {
        if (uri == null) {
            return false;
        }
        List<UriPermission> uriPermissions = context.getContentResolver().getPersistedUriPermissions();
        ListIterator<UriPermission> iterator = uriPermissions.listIterator();
        while (iterator.hasNext()) {
            UriPermission perm = iterator.next();
            if (perm.getUri().equals(uri) && perm.isReadPermission()) {
                return ifContentResolverFileExists(context, uri);
            }
        }
        return false;
    }

    /*private static String extractTextFromUri(Application app, Uri uri) {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = app.getContentResolver().openInputStream(uri);
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
    }*/

    public static void createTextFile(Fragment fragment, int requestCode, String fileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(ConfiguracionFragment.TEXT_MIME_TYPE);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        fragment.startActivityForResult(intent, requestCode);
    }

    /*public static Uri getFileWritePermissions(Context context, Intent resultData) {
        final int takeFlags = resultData.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (resultData != null) {
            Uri uri = resultData.getData();
            if (uri == null) {
                return null;
            }
            context.getContentResolver().takePersistableUriPermission(uri, takeFlags);
            return uri;
        }
        return null;
    }*/

    public static void writeTextToUri(Application app, Uri uri, String text) {
        try {
            if (ifHaveWrittingRights(app, uri)) {
                outputTextToFile(app, uri, text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void outputTextToFile(Application app, Uri uri, String text) {
        try (ParcelFileDescriptor pfd = app.getContentResolver().openFileDescriptor(uri, "w");
             FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
             PrintWriter pw = new PrintWriter(fileOutputStream, false);
             FileChannel outChan = fileOutputStream.getChannel();) {
            // El FileOutputStream debería truncar el archivo nada más abrirlo
            // pero esto no está sucediendo en mi teléfono y hay que truncarlo "manualmente"
            outChan.truncate(0);
            pw.print(text);
            pw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean ifHaveWrittingRights(Context context, Uri uri) {
        if (uri == null) {
            return false;
        }
        List<UriPermission> uriPermissions = context.getContentResolver().getPersistedUriPermissions();
        ListIterator<UriPermission> iterator = uriPermissions.listIterator();
        while (iterator.hasNext()) {
            UriPermission perm = iterator.next();
            if (perm.getUri().equals(uri) && perm.isWritePermission()) {
                return ifContentResolverFileExists(context, uri);
            }
        }
        return false;
    }

    private static boolean ifContentResolverFileExists(Context context, Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        try (Cursor c = context.getContentResolver().query(uri, projection, null, null, null);){
            if (c != null && c.getCount() >= 1) {
                // already inserted
                return true;
            } else {
                // row does not exist or there is a problem accessing it
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
