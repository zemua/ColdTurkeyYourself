package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementToGroup;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

public class ChecksAdapter extends RecyclerView.Adapter<ChecksAdapter.CheckViewHolder> implements Feedbacker<ElementToGroup> {

    private Context mContext;
    private Integer mGroupId;

    public ChecksAdapter(Context context, Integer thisGroupId) {
        this.mContext = context;
        this.mGroupId = thisGroupId;
    }

    @NonNull
    @Override
    public ChecksAdapter.CheckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ChecksAdapter.CheckViewHolder holder, int position) {

    }

    protected static class CheckViewHolder extends RecyclerView.ViewHolder {

        public Switch switchView;
        public TextView textView;
        public Integer checkId;

        public CheckViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            switchView = itemView.findViewById(R.id.switch1);
            checkId = null;
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public void giveFeedback(int tipo, ElementToGroup feedback) {

    }

    @Override
    public void addFeedbackListener(FeedbackListener<ElementToGroup> listener) {

    }
}
