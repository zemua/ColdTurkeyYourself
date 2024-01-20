package devs.mrp.coolyourturkey.grupos.reviewer;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.AppsTabFragment;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.ConditionsTabFragment;
import devs.mrp.coolyourturkey.grupos.GroupType;

public class ReviewerPagerAdapterNegative extends ReviewerPagerAdapter {

    private Context mContext;
    private Integer mGroupId;
    private String mGroupName;
    private boolean mGroupPreventClose;
    private boolean mGroupIgnoreBasedConditions;

    public ReviewerPagerAdapterNegative(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Context context, Integer groupId, String groupName, boolean preventClose, boolean ignoreBasedConditions) {
        super(fragmentManager, lifecycle);
        this.mContext = context;
        this.mGroupId = groupId;
        this.mGroupName = groupName;
        this.mGroupPreventClose = preventClose;
        this.mGroupIgnoreBasedConditions = ignoreBasedConditions;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AppsTabFragment(GroupType.NEGATIVE, mGroupId);
            case 1:
                return new ConditionsTabFragment(mGroupId, mGroupName, GroupType.NEGATIVE, mGroupPreventClose, mGroupIgnoreBasedConditions);
            default:
                return new AppsTabFragment(GroupType.NEGATIVE, mGroupId);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public String getPositionName(int position) {
        switch (position) {
            case 0:
                return mContext.getResources().getString(R.string.apps);
            case 1:
                return mContext.getResources().getString(R.string.condiciones);
            default:
                return "-";
        }
    }
}
