package devs.mrp.coolyourturkey.grupos.gruposnegativos;

import android.content.Intent;

import devs.mrp.coolyourturkey.comun.FeedbackerFragment;
import devs.mrp.coolyourturkey.grupos.GruposActivity;

public class GruposNegativosActivity extends GruposActivity {

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
