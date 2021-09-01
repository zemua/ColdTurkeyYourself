package devs.mrp.coolyourturkey.randomcheck.timeblocks.lists;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MyObservable;
import devs.mrp.coolyourturkey.comun.MyObserver;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.CheckTimeBlock;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.TimeBlockWithChecks;

public class TimeBlockAdapter<T extends TimeBlockWithChecks> extends RecyclerView.Adapter<TimeBlockAdapter.TimeBlockViewHolder<T>> implements MyObservable<T> {

    public static final String FEEDBACK_CHECK_SELECTED = "check selected";

    private List<MyObserver<T>> observers = new ArrayList<>();

    private List<T> mDataset;

    public static class TimeBlockViewHolder<S extends TimeBlockWithChecks> extends RecyclerView.ViewHolder {
        public TextView textView;
        public S timeBlock;

        public TimeBlockViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.templateTextView);
        }
    }

    @NonNull
    @Override
    public TimeBlockAdapter.TimeBlockViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_simple_text, parent, false);
        TimeBlockViewHolder<T> vh = new TimeBlockViewHolder<>(v);

        vh.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCallBack(FEEDBACK_CHECK_SELECTED, vh.timeBlock);
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull TimeBlockAdapter.TimeBlockViewHolder<T> holder, int position) {
        holder.timeBlock = mDataset.get(position);
        holder.textView.setText(holder.timeBlock.getTimeBlock().getName());
    }

    @Override
    public int getItemCount() {
        if (mDataset == null) {
            return 0;
        }
        return mDataset.size();
    }

    @Override
    public void addObserver(MyObserver<T> observer) {
        observers.add(observer);
    }

    @Override
    public void doCallBack(String tipo, T feedback) {
        observers.stream().forEach(o -> {
            o.callback(tipo, feedback);
        });
    }

    public void updateDataset(List<T> timeBlocks) {
        mDataset = timeBlocks;
        notifyDataSetChanged();
    }
}
