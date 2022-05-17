package devs.mrp.coolyourturkey.grupos.reviewer;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ReviewerPagerChooser {

    private ReviewerPagerAdapter adapter;

    public ReviewerPagerChooser(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Type type, Integer groupId, Context context) {
        set(fragmentManager, lifecycle, type, context, groupId);
    }

    public FragmentStateAdapter get() {
        return adapter;
    }

    public String getPositionName(int position) {
        return adapter.getPositionName(position);
    }

    private void set(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Type type, Context context, Integer groupId) {
        switch (type) {
            case NEGATIVE:
                adapter = new ReviewerPagerAdapterNegative(fragmentManager, lifecycle, context, groupId);
                break;
            case POSITIVE:
                adapter = new ReviewerPagerAdapterPositive(fragmentManager, lifecycle, context, groupId);
                break;
            default:
                break;
        }
    }

    public enum Type {
        POSITIVE, NEGATIVE;
    }
}