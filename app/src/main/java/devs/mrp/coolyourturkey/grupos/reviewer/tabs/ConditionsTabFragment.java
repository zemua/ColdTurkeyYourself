package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.comun.ObjectWrapperForBinder;
import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;

public class ConditionsTabFragment extends Fragment {

    private static final String KEY_BUNDLE_ID_ACTUAL = "key.bundle.id.actual";
    private static final String KEY_BUNDLE_NAME_ACTUAL = "key.bundle.name.actual";
    private static final String KEY_BUNDLE_GRUPOS_LIST = "key.bundle.grupos.list";
    private static final String KEY_BUNDLE_CONDITION_FOR_EDIT = "key.bundle.condition.for.edit";
    private static final String KEY_BUNDLE_IF_IS_EDIT_ACTION = "key.bundle.if.is.edit.action";

    private Integer mGroupId;
    private String mGroupName;
    private List<Grupo> mGrupos;

    public ConditionsTabFragment(Integer groupId, String groupName) {
        this.mGroupId = groupId;
        this.mGroupName = groupName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mGroupId = savedInstanceState.getInt(KEY_BUNDLE_ID_ACTUAL);
            mGroupName = savedInstanceState.getString(KEY_BUNDLE_NAME_ACTUAL);
            mGrupos = (List<Grupo>) ((ObjectWrapperForBinder)savedInstanceState.getBinder(KEY_BUNDLE_GRUPOS_LIST)).getData();
            // TODO ...
        } else {
            mGrupos = new ArrayList<>();
        }
    }

}
