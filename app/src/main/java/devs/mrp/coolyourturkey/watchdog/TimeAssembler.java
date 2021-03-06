package devs.mrp.coolyourturkey.watchdog;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.databaseroom.contador.Contador;
import devs.mrp.coolyourturkey.databaseroom.contador.ContadorViewModel;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

import java.util.ArrayList;
import java.util.List;

public class TimeAssembler implements Feedbacker<Long> {

    public static final int FEEDBACK_SUMATORIO = 0;
    private List<FeedbackListener<Long>> listeners = new ArrayList<>();

    private Application mApp;
    private LiveData<List<Contador>> mContadores;
    private ContadorViewModel mViewModel;
    private LifecycleOwner mOwner;
    private Importer mImporter;
    private MisPreferencias mMisPreferencias;

    private boolean contadorFlag = false;
    private boolean importadoFlag = false;
    private Contador mUltimoContador;
    //private Long tiempoContador = 0L;
    private Long tiempoImportado = 0L;

    public TimeAssembler(Application app, LifecycleOwner owner){
        mApp = app;
        mOwner = owner;
        mViewModel = new ContadorViewModel(app);
        mMisPreferencias = new MisPreferencias(app);

        mImporter = new Importer(owner, app);
        mImporter.addFeedbackListener((tipo, feedback, args)->{
            if (tipo == Importer.FEEDBACK_TIEMPO){
                tiempoImportado = feedback;
                importadoFlag = true;
                /*if (mUltimoContador != null) {
                    giveFeedback(FEEDBACK_SUMATORIO, (mUltimoContador.getAcumulado() + tiempoImportado) / mMisPreferencias.getProporcionTrabajoOcio());
                } else {
                    giveFeedback(FEEDBACK_SUMATORIO, (tiempoImportado) / mMisPreferencias.getProporcionTrabajoOcio());
                }*/
                giveFeedback(FEEDBACK_SUMATORIO, getLast());
            }
        });

        mContadores = mViewModel.getUltimoContador();
        mContadores.observe(owner, new Observer<List<Contador>>() {
            @Override
            public void onChanged(List<Contador> contadors) {
                if (contadors.size() > 0){
                    //tiempoContador = contadors.get(0).getAcumulado();
                    mUltimoContador = contadors.get(0);
                    contadorFlag = true;
                    //giveFeedback(FEEDBACK_SUMATORIO, (mUltimoContador.getAcumulado()+tiempoImportado)/mMisPreferencias.getProporcionTrabajoOcio());
                    giveFeedback(FEEDBACK_SUMATORIO, getLast());
                }
            }
        });
    }

    public Long getLast(){
        if (contadorFlag && importadoFlag) {
            return (mUltimoContador.getAcumulado() + mImporter.importarTiempoTotal()) / mMisPreferencias.getProporcionTrabajoOcio();
        }
        return 0L;
    }

    @Override
    public void giveFeedback(int tipo, Long feedback) {
        if (tipo != FEEDBACK_SUMATORIO || (contadorFlag && importadoFlag)) {
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
