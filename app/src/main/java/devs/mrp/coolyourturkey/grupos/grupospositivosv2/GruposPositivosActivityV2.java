package devs.mrp.coolyourturkey.grupos.grupospositivosv2;

import android.content.Intent;

import devs.mrp.coolyourturkey.comun.FeedbackerFragment;
import devs.mrp.coolyourturkey.grupos.GruposActivity;

public class GruposPositivosActivityV2 extends GruposActivity {

    private static String TAG = "GruposPositivosActivity-V2";

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected FeedbackerFragment<Intent> returnFragmentInstance() {
        return new GruposPositivosFragmentV2();
    }
}
