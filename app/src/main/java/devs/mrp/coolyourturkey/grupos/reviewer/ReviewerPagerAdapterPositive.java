package devs.mrp.coolyourturkey.grupos.reviewer;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.AppsTabFragment;

public class ReviewerPagerAdapterPositive extends ReviewerPagerAdapter {

    private Context mContext;

    public ReviewerPagerAdapterPositive(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Context context) {
        super(fragmentManager, lifecycle);
        this.mContext = context;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AppsTabFragment(AppsTabFragment.Type.POSITIVE);
            case 1:
                // TODO return new ChecksTabFragment();
            case 2:
                // TODO return new ExternalsTabFragment();
            case 3:
                // TODO return new ConditionsTabFragment();
            default:
                return new AppsTabFragment(AppsTabFragment.Type.POSITIVE);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

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
