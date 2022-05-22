package devs.mrp.coolyourturkey.grupos.reviewer;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.AppsTabFragment;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.GroupType;

public class ReviewerPagerAdapterNegative extends ReviewerPagerAdapter {

    private Context mContext;
    private Integer mGroupId;

    public ReviewerPagerAdapterNegative(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Context context, Integer groupId) {
        super(fragmentManager, lifecycle);
        this.mContext = context;
        this.mGroupId = groupId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AppsTabFragment(GroupType.NEGATIVE, mGroupId);
            case 1:
                // TODO return new ConditionsTabFragment();
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
