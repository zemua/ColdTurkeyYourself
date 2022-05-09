package devs.mrp.coolyourturkey.grupos.gruposnegativos;

import android.content.Intent;

import devs.mrp.coolyourturkey.comun.FeedbackerFragment;
import devs.mrp.coolyourturkey.comun.SingleFragmentActivity;
import devs.mrp.coolyourturkey.databaseroom.gruponegativo.Grupo;

public class GruposNegativosActivity extends SingleFragmentActivity<Intent> {

    private static String TAG = "GruposNegativosActivity";

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected FeedbackerFragment<Intent> returnFragmentType() {
        return new GruposNegativosFragment();
    }
}
