package devs.mrp.coolyourturkey.grupospositivos;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewGroupAppsAdapter extends RecyclerView.Adapter<ReviewGroupAppsAdapter.ReviewGroupAppsViewHolder> {




    @NonNull
    @Override
    public ReviewGroupAppsAdapter.ReviewGroupAppsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewGroupAppsAdapter.ReviewGroupAppsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ReviewGroupAppsViewHolder extends RecyclerView.ViewHolder {

        public ReviewGroupAppsViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
