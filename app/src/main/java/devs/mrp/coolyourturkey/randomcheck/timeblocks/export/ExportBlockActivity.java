package devs.mrp.coolyourturkey.randomcheck.timeblocks.export;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.R;

public class ExportBlockActivity extends AppCompatActivity {
    
    public static final String EXTRA_BLOCK_ID = "extra.block.id";
    public static final String EXTRA_BLOCK_NAME = "extra.block.name";

    private Fragment fragment;
    private Integer mBlockId;
    private String mBlockName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlefragment);

        Intent intent = getIntent();
        mBlockId = intent.getIntExtra(EXTRA_BLOCK_ID, -1);
        mBlockName = intent.getStringExtra(EXTRA_BLOCK_NAME);

        FragmentManager fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = new ExportBlockFragment(mBlockId, mBlockName);
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }

        ((ExportBlockFragment)fragment).addObserver((tipo, feedback) -> {
            if (tipo.equals(ExportBlockFragment.FEEDBACK_DONE)) {
                finish();
            }
        });
    }
    
}
