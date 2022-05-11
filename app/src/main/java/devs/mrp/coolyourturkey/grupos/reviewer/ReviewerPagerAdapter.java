package devs.mrp.coolyourturkey.grupos.reviewer;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ReviewerPagerAdapter extends FragmentStateAdapter {

    public ReviewerPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        initContext();
    }

    private void initContext() {
        // TODO init variables to build fragments with a chain handler
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // TODO
        return null;
    }

    @Override
    public int getItemCount() {
        // TODO
        return 0;
    }
}
