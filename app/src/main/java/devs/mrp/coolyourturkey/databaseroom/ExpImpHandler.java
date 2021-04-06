package devs.mrp.coolyourturkey.databaseroom;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class ExpImpHandler {

    private static ExpImpHandler instance;
    private static final String TAG = "EXP_IMP_HANDLER CLASS";

    private String mDirectory;
    private String mTxtFile;

    private ExpImpHandler() {
        mDirectory = "respaldosdb";
        mTxtFile = "lastcounter.txt";
    }

    public static ExpImpHandler getInstance() {
        if (instance == null) {
            instance = new ExpImpHandler();
        }
        return instance;
    }

    public File getDirectory(Context context) {
        return context.getDir(mDirectory, 0);
    }

    public File getFile(Context context) {
        File f = new File(getDirectory(context), mTxtFile);
        return f;
    }

    public void writeToFile(Context context, Long milis) {
        File f = getFile(context);
        try {
            if (!f.exists()){
                f.createNewFile();
            }
            try (FileOutputStream fileOutputStream = new FileOutputStream(f);
                 FileChannel outchan = fileOutputStream.getChannel();){

                outchan.truncate(0);
                fileOutputStream.write(String.valueOf(milis).getBytes());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Long readFromFile(Context context) {
        File f = getFile(context);
        if (f.exists()) {
            try (FileReader fr = new FileReader(f);
                 BufferedReader br = new BufferedReader(fr);) {
                String line = br.readLine();
                Long l = Long.parseLong(line);
                return l;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0L;
    }
}
