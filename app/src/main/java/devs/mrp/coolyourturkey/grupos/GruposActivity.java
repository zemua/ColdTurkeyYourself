package devs.mrp.coolyourturkey.grupos;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import devs.mrp.coolyourturkey.comun.SingleFragmentActivity;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivo;
import devs.mrp.coolyourturkey.grupos.grupospositivos.AddGroupActivity;
import devs.mrp.coolyourturkey.grupos.grupospositivos.ReviewGroupActivity;

public abstract class GruposActivity extends SingleFragmentActivity<Object> {

    private static final int LAUNCH_ADD = 1;
    private static final int LAUNCH_REVIEW = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_ADD) {
            if (resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra(AddGroupActivity.EXTRA_NAME);
                GrupoPositivo g = new GrupoPositivo(result);
                if (getFragment() instanceof GruposFragment) {
                    ((GruposFragment)getFragment()).addGrupoToDb(g);
                }
            }
        }
        if (requestCode == LAUNCH_REVIEW) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.hasExtra(ReviewGroupActivity.RESULT_DELETE)) {
                    Integer groupIdToDelete = data.getIntExtra(ReviewGroupActivity.RESULT_DELETE, -1);
                    Log.d(getTag(), "going to process delete of group with id " + groupIdToDelete.toString());
                    if (getFragment() instanceof  GruposFragment) {
                        ((GruposFragment)getFragment()).removeGrupoFromDb(groupIdToDelete);
                    }
                }
            }
        }
    }
}
