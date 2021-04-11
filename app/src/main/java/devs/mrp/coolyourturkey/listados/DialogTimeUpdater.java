package devs.mrp.coolyourturkey.listados;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class DialogTimeUpdater implements Executor, Feedbacker<Integer> {

    public static final Integer FEEDBACK_OK = 0;
    private List<FeedbackListener<Integer>> listeners = new ArrayList<>();

    AlertDialog mDialog;
    Integer countStart;
    MyThread t;
    String mMensaje;
    Context mContext;
    Handler mHandler;
    private Integer mTiempo;
    MisPreferencias mMisPreferencias;

    DialogTimeUpdater(AlertDialog ad, Integer start, String finishMsg, Context context){
        mDialog = ad;
        countStart = start;
        mMensaje = finishMsg;
        mContext = context;
        mHandler = new Handler(mContext.getApplicationContext().getMainLooper());
        mMisPreferencias = new MisPreferencias(context);
    }

    public void go(){
        execute(getRunnable());
    }

    @Override
    public void execute(Runnable r) {
        t = new MyThread();
        t.start();
    }

    private Runnable getRunnable(){
        return new Runnable() {
            @Override
            public void run() {
                DialogInterface.OnClickListener emptyListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // nada hasta que la cuenta llegue a cero
                    }
                };

                if (mMisPreferencias.getTiempoDeGraciaActivado()) {
                    mTiempo = countStart;
                } else {
                    mTiempo = 0;
                }
                try {
                    while (mTiempo > 0){
                            Thread.sleep(1000);
                            mTiempo--;
                            final int t2 = mTiempo; // para poder usarlo en el runnable
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Button b = mDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                                    b.setText(String.valueOf(t2));
                                }
                            });

                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Button b = mDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            b.setText(mMensaje);
                            mDialog.setButton(DialogInterface.BUTTON_POSITIVE, mMensaje, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    giveFeedback(FEEDBACK_OK, null);
                                }
                            });
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private class MyThread extends Thread{
        boolean keepRunning = true;
        MyThread(){
            super();
        }

        @Override
        public void run() {
            DialogInterface.OnClickListener emptyListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // nada hasta que la cuenta llegue a cero
                }
            };

            if (mMisPreferencias.getTiempoDeGraciaActivado()) {
                mTiempo = countStart;
            } else {
                mTiempo = 0;
            }
            try { // TODO solve bug of double thread
                while (mTiempo > 0 && keepRunning){
                    Thread.sleep(1000);
                    mTiempo--;
                    final int t2 = mTiempo; // para poder usarlo en el runnable
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Button b = mDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            b.setText(String.valueOf(t2));
                        }
                    });

                }
                if (keepRunning) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Button b = mDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            b.setText(mMensaje);
                            mDialog.setButton(DialogInterface.BUTTON_POSITIVE, mMensaje, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    giveFeedback(FEEDBACK_OK, null);
                                }
                            });
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void stopThread(){
            keepRunning = false;
        }
    }

    public void interrumpe(){
            t.stopThread();
    }

    public Integer getTiempo(){
        return mTiempo;
    }

    @Override
    public void giveFeedback(int tipo, Integer feedback) {
        listeners.forEach((listener)->{
            listener.giveFeedback(tipo, feedback);
        });
    }

    @Override
    public void addFeedbackListener(FeedbackListener<Integer> listener) {
        listeners.add(listener);
    }
}
