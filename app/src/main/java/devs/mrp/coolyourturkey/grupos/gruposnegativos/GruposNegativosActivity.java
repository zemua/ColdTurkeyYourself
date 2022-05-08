package devs.mrp.coolyourturkey.grupos.gruposnegativos;

import devs.mrp.coolyourturkey.comun.FeedbackerFragment;
import devs.mrp.coolyourturkey.comun.SingleFragmentActivity;

public class GruposNegativosActivity extends SingleFragmentActivity<Object> {

    private static String TAG = "GruposNegativosActivity";

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected FeedbackerFragment<Object> returnFragmentType() {
        return new GruposNegativosFragment();
    }
}
