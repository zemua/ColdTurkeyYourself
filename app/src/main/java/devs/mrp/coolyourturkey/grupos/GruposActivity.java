package devs.mrp.coolyourturkey.grupos;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import devs.mrp.coolyourturkey.comun.SingleFragmentActivity;
import devs.mrp.coolyourturkey.databaseroom.gruponegativo.Grupo;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivo;
import devs.mrp.coolyourturkey.grupos.grupospositivos.AddGroupActivity;
import devs.mrp.coolyourturkey.grupos.grupospositivos.ReviewGroupActivity;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

public abstract class GruposActivity extends SingleFragmentActivity<Intent> {

    private ActivityResultLauncher<Intent> initLauncher() {
        return registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        String name = result.getData().getStringExtra(AddGroupActivity.EXTRA_NAME);
                        Grupo g = new Grupo(name);
                        if (getFragment() instanceof GruposFragment) {
                            ((GruposFragment)getFragment()).addGrupoToDb(g);
                        }
                    }
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (result.getData().hasExtra(ReviewGroupActivity.RESULT_DELETE)) {
                            Integer groupIdToDelete = result.getData().getIntExtra(ReviewGroupActivity.RESULT_DELETE, -1);
                            Log.d(getTag(), "going to process delete of group with id " + groupIdToDelete.toString());
                            if (getFragment() instanceof  GruposFragment) {
                                ((GruposFragment)getFragment()).removeGrupoFromDb(groupIdToDelete);
                            }
                        }
                    }
            }
        });
    }

    @Override
    protected void stuffAfterOnCreate(Fragment frgmnt) {
        if (frgmnt instanceof GruposFragment){
            ActivityResultLauncher<Intent> launcher = initLauncher();
            GruposFragment fragment = (GruposFragment) frgmnt;
            fragment.addFeedbackListener(new FeedbackListener<Intent>() {
                @Override
                public void giveFeedback(int tipo, Intent feedback, Object... args) {
                    launcher.launch(feedback);
                }
            });
        }
    }
}
