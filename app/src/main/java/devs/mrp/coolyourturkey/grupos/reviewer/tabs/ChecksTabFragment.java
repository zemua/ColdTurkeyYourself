package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import android.content.Context;
import android.os.Handler;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

public class ChecksTabFragment extends Fragment {

    private static final String KEY_BUNDLE_ID_ACTUAL = "key.bundle.id.actual";

    private RecyclerView mRecyclerView;
    private Handler mainHandler;
    private Context mContext;
    private ViewModelProvider.Factory viewModelFactory;
    private Integer mGroupId;

    public enum Type {
        POSITIVE, NEGATIVE;
    }

}
