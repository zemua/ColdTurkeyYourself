package devs.mrp.coolyourturkey.grupos.reviewer;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.AppsTabFragment;

public class ReviewerPagerAdapter extends FragmentStateAdapter {

    private Type type;
    private Context mContext;

    public ReviewerPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Type type, Context context) {
        super(fragmentManager, lifecycle);
        this.type = type;
        this.mContext = context;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AppsTabFragment(appsType());
            case 1:
                // TODO return new ChecksTabFragment();
            case 2:
                // TODO return new ExternalsTabFragment();
            case 3:
                // TODO return new ConditionsTabFragment();
            default:
                return new AppsTabFragment(appsType());
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public enum Type {
        POSITIVE, NEGATIVE;
    }

    private AppsTabFragment.Type appsType() {
        switch (type) {
            case POSITIVE:
                return AppsTabFragment.Type.POSITIVE;
            case NEGATIVE:
                return AppsTabFragment.Type.NEGATIVE;
            default:
                return null;
        }
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
