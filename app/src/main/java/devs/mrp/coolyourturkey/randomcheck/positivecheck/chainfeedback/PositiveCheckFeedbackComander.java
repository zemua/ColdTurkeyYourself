package devs.mrp.coolyourturkey.randomcheck.positivecheck.chainfeedback;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import devs.mrp.coolyourturkey.comun.ChainComander;
import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.dtos.randomcheck.PositiveCheck;

public class PositiveCheckFeedbackComander implements ChainComander {

    private Activity mActivity;

    public PositiveCheckFeedbackComander(Activity a) {
        mActivity = a;
    }

    @Override
    public ChainHandler getHandlerChain() {
        ChainHandler<PositiveCheck> crearNuevo = new PositiveCheckFeedbackSaveNew(mActivity);
        ChainHandler<PositiveCheck> saveExisting = new PositiveCheckFeedbackSaveExisting(mActivity);
        ChainHandler<PositiveCheck> deleteThis = new PositiveCheckFeedbackDelete(mActivity);


        crearNuevo.setNextHandler(saveExisting);
        saveExisting.setNextHandler(deleteThis);

        return crearNuevo;
    }
}
