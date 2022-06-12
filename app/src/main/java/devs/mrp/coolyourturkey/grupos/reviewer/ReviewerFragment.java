package devs.mrp.coolyourturkey.grupos.reviewer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.FeedbackerFragment;
import devs.mrp.coolyourturkey.databaseroom.grupo.GrupoType;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.ReviewerFeedbackCodes;

public class ReviewerFragment extends FeedbackerFragment<Intent> {

    private static final String KEY_BUNDLE_GROUP_ID = "key.bundle.group.id";
    private static final String KEY_BUNDLE_GROUP_NAME = "key.bundle.group.name";
    private static final String KEY_BUNDLE_GROUP_TYPE = "key.bundle.group.type";

    private ViewModelProvider.Factory viewModelFactory;

    private int mGroupId;
    private String mGroupName;
    private GrupoType mGroupType;
    private Context mContext;
    private Handler mainHandler;

    private TextView groupNameTextView;
    private TabLayout tabLayout;
    private Button buttonExpTxt;
    private Button buttonDelete;
    private ViewPager2 viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication());

        mContext = getActivity();
        mainHandler = new Handler(mContext.getMainLooper());

        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            mGroupId = savedInstanceState.getInt(KEY_BUNDLE_GROUP_ID);
            mGroupName = savedInstanceState.getString(KEY_BUNDLE_GROUP_NAME);
            mGroupType = GrupoType.valueOf(savedInstanceState.getString(KEY_BUNDLE_GROUP_TYPE));
        }

        View v = inflater.inflate(R.layout.fragment_reviewer, container, false);

        groupNameTextView = v.findViewById(R.id.groupNameText);
        tabLayout = v.findViewById(R.id.tabLayout);
        buttonExpTxt = v.findViewById(R.id.buttonExp);
        buttonDelete = v.findViewById(R.id.buttonDelete);
        viewPager = v.findViewById(R.id.groupViewPager);

        ReviewerPagerChooser chooser = new ReviewerPagerChooser(getActivity().getSupportFragmentManager(), getLifecycle(), pagerType(), mGroupId, mContext, mGroupName);
        FragmentStateAdapter adapter = chooser.get();
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(chooser.getPositionName(position))).attach();
        buttonDelete.setOnClickListener(view -> giveFeedback(ReviewerFeedbackCodes.DELETE, null));
        buttonExpTxt.setOnClickListener(view -> giveFeedback(ReviewerFeedbackCodes.SYNC, null));

        return v;
    }

    public void setGroupId(int mGroupId) {
        this.mGroupId = mGroupId;
    }

    public void setGroupName(String mGroupName) {
        this.mGroupName = mGroupName;
    }

    public void setGroupType(GrupoType mGroupType) {
        this.mGroupType = mGroupType;
    }

    private ReviewerPagerChooser.Type pagerType() {
        switch (this.mGroupType) {
            case NEGATIVE:
                return ReviewerPagerChooser.Type.NEGATIVE;
            case POSITIVE:
                return ReviewerPagerChooser.Type.POSITIVE;
            default:
                return null;
        }
    }
}
