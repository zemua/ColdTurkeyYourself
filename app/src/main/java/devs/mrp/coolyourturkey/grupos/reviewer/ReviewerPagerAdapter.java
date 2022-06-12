package devs.mrp.coolyourturkey.grupos.reviewer;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public abstract class ReviewerPagerAdapter extends FragmentStateAdapter {
    public ReviewerPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public abstract String getPositionName(int position);
}
