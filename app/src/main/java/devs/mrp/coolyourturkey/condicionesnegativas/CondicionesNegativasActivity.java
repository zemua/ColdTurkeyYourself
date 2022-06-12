package devs.mrp.coolyourturkey.condicionesnegativas;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MyObserver;
import devs.mrp.coolyourturkey.comun.ObjectWrapperForBinder;
import devs.mrp.coolyourturkey.condicionesnegativas.add.AddNegativeConditionActivity;
import devs.mrp.coolyourturkey.databaseroom.deprecated.conditionnegativetogroup.ConditionNegativeToGroup;

public class CondicionesNegativasActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_ID = "extra_group_id";
    public static final String EXTRA_GROUP_NAME = "extra_group_name";
    public static final String EXTRA_CONDITION_ID = "extra_condition_id";
    public static final String EXTRA_CONDITION = "extra_condition";

    public static final String RESULT_DELETE = "result_delete_negative_condition";

    private Fragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlefragment);

        FragmentManager fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = new CondicionesNegativasFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }

        ((CondicionesNegativasFragment)fragment).addObserver(new MyObserver<ConditionNegativeToGroup>() {
            @Override
            public void callback(String tipo, ConditionNegativeToGroup feedback) {
                Intent intent;
                switch (tipo) {
                    case CondicionesNegativasFragment.CALLBACK_ADD_CONDITION:
                        intent = new Intent(CondicionesNegativasActivity.this, AddNegativeConditionActivity.class);
                        startActivity(intent);
                        break;
                    case CondicionesNegativasFragment.CALLBACK_EDIT_EXISTING_CONDITION:
                        intent = new Intent(CondicionesNegativasActivity.this, AddNegativeConditionActivity.class);
                        final ConditionNegativeToGroup objSent = (ConditionNegativeToGroup) feedback;
                        final Bundle bundle = new Bundle();
                        bundle.putBinder(EXTRA_CONDITION, new ObjectWrapperForBinder(objSent));
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

}
