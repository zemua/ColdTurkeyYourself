package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.AppsViewHolder> implements Feedbacker<Object> { // TODO

    @NonNull
    @Override
    public AppsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AppsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public void giveFeedback(int tipo, Object feedback) {

    }

    @Override
    public void addFeedbackListener(FeedbackListener<Object> listener) {

    }

    protected static class AppsViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public Switch switchView;
        public TextView textView;
        public String packageName;

        public AppsViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
            switchView = itemView.findViewById(R.id.switch1);
            packageName = null;
        }
    }

}
