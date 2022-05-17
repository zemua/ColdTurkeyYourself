package devs.mrp.coolyourturkey.grupos.reviewer;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.AppsTabFragment;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.ChecksTabFragment;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.ExternalTabFragment;

public class ReviewerPagerAdapterPositive extends ReviewerPagerAdapter {

    private Context mContext;
    private Integer mGroupId;

    public ReviewerPagerAdapterPositive(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Context context, Integer groupId) {
        super(fragmentManager, lifecycle);
        this.mContext = context;
        this.mGroupId = groupId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AppsTabFragment(AppsTabFragment.Type.POSITIVE, mGroupId);
            case 1:
                return new ChecksTabFragment(ChecksTabFragment.Type.POSITIVE, mGroupId);
            case 2:
                return new ExternalTabFragment(ExternalTabFragment.Type.POSITIVE, mGroupId);
            case 3:
                // TODO return new ConditionsTabFragment();
            default:
                return new AppsTabFragment(AppsTabFragment.Type.POSITIVE, mGroupId);
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
