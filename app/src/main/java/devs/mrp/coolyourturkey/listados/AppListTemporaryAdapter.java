package devs.mrp.coolyourturkey.listados;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import devs.mrp.coolyourturkey.R;

public class AppListTemporaryAdapter extends RecyclerView.Adapter<AppsListAdapter.AppsListViewHolder>{
    @NonNull
    @Override
    public AppsListAdapter.AppsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AppsListAdapter.AppsListViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public static class AppsListViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public AppsListViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.imageView);
        }
    }
}
