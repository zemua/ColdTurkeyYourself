package devs.mrp.coolyourturkey.grupos.reviewer.tabs.addcondition;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import devs.mrp.coolyourturkey.comun.FeedbackerFragment;
import devs.mrp.coolyourturkey.comun.ObjectWrapperForBinder;
import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.databaseroom.grupocondition.GrupoCondition;

public class AddConditionFragment extends FeedbackerFragment<GrupoCondition> {

    private static final String KEY_BUNDLE_ID_ACTUAL = "key.bundle.id.actual";
    private static final String KEY_BUNDLE_NAME_ACTUAL = "key.bundle.name.actual";
    private static final String KEY_BUNDLE_GRUPOS_LIST = "key.bundle.grupos.positivos.list";
    private static final String KEY_BUNDLE_CONDITION_FOR_EDIT = "key.bundle.condition.for.edit";
    private static final String KEY_BUNDLE_IF_IS_EDIT_ACTION = "key.bundle.if.is.edit.action";

    private int mGroupId;
    private String mGroupName;
    private List<Grupo> mGrupos;
    private GrupoCondition mGrupoCondition;
    private boolean mIsEdit;

    public AddConditionFragment(int groupId, String groupName) {
        super();
        mGroupId = groupId;
        mGroupName = groupName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mGroupId = savedInstanceState.getInt(KEY_BUNDLE_ID_ACTUAL, -1);
            mGroupName = savedInstanceState.getString(KEY_BUNDLE_NAME_ACTUAL, "");
            mGrupos = (List<Grupo>) Optional.ofNullable(((ObjectWrapperForBinder)savedInstanceState.getBinder(KEY_BUNDLE_GRUPOS_LIST))).map(ObjectWrapperForBinder::getData).orElse(new ArrayList<>());
            mGrupoCondition = (GrupoCondition) Optional.ofNullable(((ObjectWrapperForBinder)savedInstanceState.getBinder(KEY_BUNDLE_CONDITION_FOR_EDIT))).map(ObjectWrapperForBinder::getData).orElse(new GrupoCondition());
            mIsEdit = savedInstanceState.getBoolean(KEY_BUNDLE_IF_IS_EDIT_ACTION, false);
        } else {
            mGrupos = new ArrayList<>();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }
}
