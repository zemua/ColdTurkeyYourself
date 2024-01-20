package devs.mrp.coolyourturkey.grupos.reviewer;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.AppsTabFragment;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.ChecksTabFragment;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.ConditionsTabFragment;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.ExternalTabFragment;
import devs.mrp.coolyourturkey.grupos.GroupType;

public class ReviewerPagerAdapterPositive extends ReviewerPagerAdapter {

    private Context mContext;
    private Integer mGroupId;
    private String mGroupName;
    private boolean mGroupPreventClose;
    private boolean mGroupIgnoreBasedConditions;

    public ReviewerPagerAdapterPositive(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Context context, Integer groupId, String groupName, boolean preventClose, boolean ignoreBasedConditions) {
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
                return new AppsTabFragment(GroupType.POSITIVE, mGroupId);
            case 1:
                return new ChecksTabFragment(GroupType.POSITIVE, mGroupId);
            case 2:
                return new ExternalTabFragment(GroupType.POSITIVE, mGroupId);
            case 3:
                return new ConditionsTabFragment(mGroupId, mGroupName, GroupType.POSITIVE, mGroupPreventClose, mGroupIgnoreBasedConditions);
            default:
                return new AppsTabFragment(GroupType.POSITIVE, mGroupId);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    @Override
    public String getPositionName(int position) {
        switch (position) {
            case 0:
                return mContext.getResources().getString(R.string.apps);
            case 1:
                return mContext.getResources().getString(R.string.controles_positivos);
            case 2:
                return mContext.getResources().getString(R.string.externo);
            case 3:
                return mContext.getResources().getString(R.string.condiciones);
            default:
                return "-";
        }
    }
}
