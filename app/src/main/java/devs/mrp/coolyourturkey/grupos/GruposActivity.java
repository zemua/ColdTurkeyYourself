package devs.mrp.coolyourturkey.grupos;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import devs.mrp.coolyourturkey.comun.FeedbackerFragment;
import devs.mrp.coolyourturkey.comun.SingleFragmentActivity;
import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.grupos.grupospositivos_old_deprecated.AddGroupActivity;
import devs.mrp.coolyourturkey.grupos.grupospositivos_old_deprecated.ReviewGroupActivity;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;

public abstract class GruposActivity extends SingleFragmentActivity<Intent> {

    private ActivityResultLauncher<Intent> addLauncher;
    private ActivityResultLauncher<Intent> reviewLauncher;

    @Override
    protected void initCallbackRegisters() {
        addLauncher = addLauncher();
        reviewLauncher = reviewLauncher();
    }

    @Override
    protected void initListeners(FeedbackerFragment frgmnt) {
        if (frgmnt instanceof GruposFragment){
            GruposFragment fragment = (GruposFragment) frgmnt;
            fragment.addFeedbackListener(new FeedbackListener<Intent>() {
                @Override
                public void giveFeedback(int tipo, Intent feedback, Object... args) {
                    switch (tipo) {
                        case GruposFragment.ADD_INTENT:
                            addLauncher.launch(feedback);
                            break;
                        case GruposFragment.REVIEW_INTENT:
                            reviewLauncher.launch(feedback);
                            break;
                    }
                }
            });
        }
    }

    private ActivityResultLauncher<Intent> addLauncher() {
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
            }
        });
    }

    private ActivityResultLauncher<Intent> reviewLauncher() {
        return registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK){
                    Integer groupIdToDelete = result.getData().getIntExtra(ReviewGroupActivity.RESULT_DELETE, -1);
                    Log.d(getTag(), "going to process delete of group with id " + groupIdToDelete.toString());
                    if (getFragment() instanceof  GruposFragment) {
                        ((GruposFragment)getFragment()).removeGrupoFromDb(groupIdToDelete);
                    }
                }
            }
        });
    }
}
