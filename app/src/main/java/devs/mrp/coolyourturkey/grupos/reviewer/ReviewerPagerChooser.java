package devs.mrp.coolyourturkey.grupos.reviewer;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ReviewerPagerChooser {

    private ReviewerPagerAdapter adapter;

    public ReviewerPagerChooser(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Type type, Integer groupId, Context context, String groupName, boolean preventClose) {
        set(fragmentManager, lifecycle, type, context, groupId, groupName, preventClose);
    }

    public FragmentStateAdapter get() {
        return adapter;
    }

    public String getPositionName(int position) {
        return adapter.getPositionName(position);
    }

    private void set(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Type type, Context context, Integer groupId, String groupName, boolean preventClose) {
        switch (type) {
            case NEGATIVE:
                adapter = new ReviewerPagerAdapterNegative(fragmentManager, lifecycle, context, groupId, groupName, preventClose);
                break;
            case POSITIVE:
                adapter = new ReviewerPagerAdapterPositive(fragmentManager, lifecycle, context, groupId, groupName, preventClose);
                break;
            default:
                break;
        }
    }

    public enum Type {
        POSITIVE, NEGATIVE;
    }
}
