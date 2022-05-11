package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import devs.mrp.coolyourturkey.R;

public class AppsTabFragment extends Fragment {

    private RecyclerView mRecyclerView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_single_recycler, container, false);

        mRecyclerView = v.findViewById(R.id.recyclerView);



        return v;
    }
}
