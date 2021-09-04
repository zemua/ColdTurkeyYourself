package devs.mrp.coolyourturkey.randomcheck.timeblocks.review;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MySelectableAndNombrable;

public class SelectablesAdapter<T extends MySelectableAndNombrable> extends RecyclerView.Adapter<SelectablesAdapter.SelectablesViewHolder> {

    private final String TAG = "SelectablesAdapter";

    private List<T> mDataset = new ArrayList<>();

    public static class SelectablesViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public Switch vhSwitch;
        public SelectablesViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.holderText);
            vhSwitch = itemView.findViewById(R.id.holderSwitch);
        }
    }

    @NonNull
    @Override
    public SelectablesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_text_and_switch, parent, false);
        SelectablesViewHolder vh = new SelectablesViewHolder(v);

        vh.vhSwitch.setOnClickListener(view -> {
            mDataset.get(vh.getAdapterPosition()).setSelected(((Switch)view).isChecked());
            //Log.d(TAG, "in position: " + vh.getAdapterPosition());
            //Log.d(TAG, "set to: " + ((Switch)view).isChecked());
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SelectablesViewHolder holder, int position) {
        holder.textView.setText(mDataset.get(position).getName());
        holder.vhSwitch.setChecked(mDataset.get(position).isSelected());
        //Log.d(TAG, "position " + position + " is selected: " + mDataset.get(position).isSelected());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void updateDataset(List<T> data) {
        mDataset = data;
        notifyDataSetChanged();
    }

    public List<T> getSelectedFromDataSet() {
        return mDataset.stream()
                //.peek(c -> Log.d(TAG, "filtering " + c.getName() + " " + c.isSelected()))
                .filter(c -> c.isSelected())
                .collect(Collectors.toList());
    }

    public List<T> getFullDataSet() {
        return mDataset;
    }

}
