package devs.mrp.coolyourturkey.grupos.reviewer;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ReviewerPagerChooser {

    private ReviewerPagerAdapter adapter;

    public ReviewerPagerChooser(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Type type, Context context) {
        set(fragmentManager, lifecycle, type, context);
    }

    public FragmentStateAdapter get() {
        return adapter;
    }

    public String getPositionName(int position) {
        return adapter.getPositionName(position);
    }

    private void set(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Type type, Context context) {
        switch (type) {
            case NEGATIVE:
                adapter = new ReviewerPagerAdapterNegative(fragmentManager, lifecycle, context);
                break;
            case POSITIVE:
                adapter = new ReviewerPagerAdapterPositive(fragmentManager, lifecycle, context);
                break;
            default:
                break;
        }
    }

    public enum Type {
        POSITIVE, NEGATIVE;
    }
}
