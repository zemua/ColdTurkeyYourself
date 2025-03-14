package devs.mrp.coolyourturkey.grupos.reviewer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import dagger.hilt.android.AndroidEntryPoint;
import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.DialogWithDelay;
import devs.mrp.coolyourturkey.comun.FeedbackerFragment;
import devs.mrp.coolyourturkey.comun.SingleFragmentActivity;
import devs.mrp.coolyourturkey.configuracion.ConfiguracionFragment;
import devs.mrp.coolyourturkey.databaseroom.grupo.GrupoRepository;
import devs.mrp.coolyourturkey.databaseroom.grupo.GrupoType;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupoexport.GrupoExport;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupoexport.GrupoExportRepository;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.ReviewerFeedbackCodes;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;

@AndroidEntryPoint
public class ReviewerActivity extends SingleFragmentActivity<Intent> {

    public static final String EXTRA_GROUP_ID = "extra_group_id";
    public static final String EXTRA_GROUP_NAME = "extra_group_name";
    public static final String EXTRA_GROUP_TYPE = "extra_group_type";
    public static final String EXTRA_PREVENT_CLOSE = "extra_prevent_close";
    public static final String EXTRA_IGNORE_CONDITIONS = "extra_ignore_conditions";

    private static final int EXPORT_LAST_DAYS = 30;

    private static final String TAG = "ReviewerActivity";

    private int mGroupId;
    private String mGroupName;
    private GrupoType mGroupType;
    private boolean mPreventClose;
    private boolean mIgnoreBasedConditions;

    private GrupoRepository grupoRepository;
    private GrupoExportRepository grupoExportRepository;
    private ActivityResultLauncher<Intent> syncFileLauncher;

    @Override
    protected void initListeners(FeedbackerFragment frgmnt) {
        if (frgmnt instanceof ReviewerFragment) {
            ReviewerFragment fragment = (ReviewerFragment) frgmnt;
            fragment.addFeedbackListener(new FeedbackListener<Intent>() {
                @Override
                public void giveFeedback(int tipo, Intent feedback, Object... args) {
                    switch (tipo) {
                        case (ReviewerFeedbackCodes.DELETE):
                            final DialogWithDelay dialog = new DialogWithDelay(R.drawable.trash_can_outline, getString(R.string.borrar), getString(R.string.estas_seguro), 0, 30, (tipo2, feedback2, args2) -> {
                                if (tipo2 == DialogWithDelay.FEEDBACK_ALERT_DIALOG_ACEPTAR) {
                                    grupoRepository.deleteById(mGroupId);
                                    finish();
                                }
                            });
                            dialog.show(getSupportFragmentManager(), "");
                            break;
                        case (ReviewerFeedbackCodes.SYNC):
                            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType(ConfiguracionFragment.TEXT_MIME_TYPE);
                            intent.putExtra(Intent.EXTRA_TITLE, mGroupName);
                            syncFileLauncher.launch(intent);
                            break;
                    }
                }
            });
        }
    }

    @Override
    protected void initCallbackRegisters() {
        grupoRepository = GrupoRepository.getRepo(getApplication());
        grupoExportRepository = GrupoExportRepository.getRepo(getApplication());
        syncFileLauncher = syncFileLauncher();
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected FeedbackerFragment<Intent> returnFragmentInstance() {
        Intent intent = getIntent();
        mGroupId = intent.getIntExtra(EXTRA_GROUP_ID, -1);
        mGroupName = intent.getStringExtra(EXTRA_GROUP_NAME);
        mGroupType = GrupoType.valueOf(intent.getStringExtra(EXTRA_GROUP_TYPE));
        mPreventClose = intent.getBooleanExtra(EXTRA_PREVENT_CLOSE, false);
        mIgnoreBasedConditions = intent.getBooleanExtra(EXTRA_IGNORE_CONDITIONS, false);
        ReviewerFragment fragment = new ReviewerFragment(mGroupId, mGroupName, mGroupType, mPreventClose, mIgnoreBasedConditions);
        return fragment;
    }

    private ActivityResultLauncher<Intent> syncFileLauncher() {
        return registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri == null) {
                            return;
                        }
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        GrupoExport grupoExport = new GrupoExport(mGroupId, uri.toString(), EXPORT_LAST_DAYS);
                        grupoExportRepository.insert(grupoExport);
                    }
                }
            }
        });
    }

}
