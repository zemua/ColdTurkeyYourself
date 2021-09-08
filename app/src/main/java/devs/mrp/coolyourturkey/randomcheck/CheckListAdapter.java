package devs.mrp.coolyourturkey.randomcheck;

import android.content.Context;
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
import devs.mrp.coolyourturkey.dtos.randomcheck.Check;

public class CheckListAdapter<T extends Check> extends RecyclerView.Adapter<CheckListAdapter.CheckListViewHolder<T>> implements MyObservable<T> {

    public static final String FEEDBACK_CHECK_SELECTED = "check selected";

    public static final String BACKGROUND_GREEN = "green";
    public static final String BACKGROUND_RED = "red";

    private List<MyObserver<T>> observers = new ArrayList<>();

    private List<? extends Check> mDataset;
    private Context mContext;
    private String mColor;

    public CheckListAdapter(Context c, String color) {
        mContext = c;
        mColor = color;
    }

    public static class CheckListViewHolder<S extends Check> extends RecyclerView.ViewHolder {
        public TextView textView;
        public S check;

        public CheckListViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.templateTextView);
        }
    }

    @NonNull
    @Override
    public CheckListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_simple_text, parent, false);
        CheckListViewHolder<T> vh = new CheckListViewHolder<>(v);

        setBackground(vh.textView);
        vh.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCallBack(FEEDBACK_CHECK_SELECTED, vh.check);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CheckListViewHolder holder, int position) {
        holder.check = mDataset.get(position);
        holder.textView.setText(holder.check.getName());
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

    private void setBackground(TextView view) {
        switch(mColor) {
            case BACKGROUND_GREEN:
                view.setBackgroundResource(R.drawable.green_rounded_corner_with_border);
                break;
            case BACKGROUND_RED:
                view.setBackgroundResource(R.drawable.red_rounded_corner_with_border);
                break;
        }
    }

    public void updateDataset(List<? extends Check> checks) {
        mDataset = checks;
        notifyDataSetChanged();
    }
}
