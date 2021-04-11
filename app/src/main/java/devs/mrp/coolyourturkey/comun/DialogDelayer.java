package devs.mrp.coolyourturkey.comun;

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

public class DialogDelayer implements Executor, Feedbacker<Integer> {

    public static final Integer FEEDBACK_OK = 0;
    private List<FeedbackListener<Integer>> listeners = new ArrayList<>();
    public static final int CUENTA_ATRAS_SEGUNDOS = 30;

    AlertDialog mDialog;
    Integer countStart;
    MyThread t;
    String mMensaje;
    Context mContext;
    Handler mHandler;
    MisPreferencias mMisPreferencias;
    Integer tiempo;

    public DialogDelayer(AlertDialog ad, Integer start, String finishMsg, Context context){
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
                    tiempo = countStart;
                } else {
                    tiempo = 0;
                }
                while (tiempo > 0){
                    try {
                        Thread.sleep(1000);
                        tiempo--;
                        final int t2 = tiempo; // para poder usarlo en el runnable
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Button b = mDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                                b.setText(String.valueOf(t2));
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
            }
        };
    }

    private class MyThread extends Thread {
        boolean keepRunning = true;

        MyThread() {
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
                tiempo = countStart;
            } else {
                tiempo = 0;
            }
            while (tiempo > 0 && keepRunning){
                try {
                    Thread.sleep(1000);
                    tiempo--;
                    final int t2 = tiempo; // para poder usarlo en el runnable
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Button b = mDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            b.setText(String.valueOf(t2));
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
        }

        void stopRunning(){
            keepRunning = false;
        }
    }

    @Override
    public void giveFeedback(int tipo, Integer feedback) {
        listeners.forEach((listener)->{
            listener.giveFeedback(tipo, feedback);
        });
    }

    public Integer getTiempo(){
        return tiempo;
    }

    @Override
    public void addFeedbackListener(FeedbackListener<Integer> listener) {
        listeners.add(listener);
    }

    @Override
    public void execute(Runnable r) {
        t = new MyThread();
        t.start();
    }

    public void interrumpe(){
        t.stopRunning();
    }
}
