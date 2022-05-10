package devs.mrp.coolyourturkey.grupos;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;

public abstract class GruposAdapter extends RecyclerView.Adapter<GruposAdapter.GruposViewHolder> implements Feedbacker<Grupo> {

    public static final int FEEDBACK_ITEM_CLICKED = 0;

    protected abstract String getLoggerTag();

    protected List<Grupo> mDataset;
    protected Context mContext;
    protected TimeLogHandler mTimeLogHandler;

    private ArrayList<FeedbackListener<Grupo>> listeners = new ArrayList<>();

    public GruposAdapter(List<Grupo> dataset, Context context, TimeLogHandler timeLogHandler) {
        mDataset = dataset;
        mContext = context;
        mTimeLogHandler = timeLogHandler;
    }

    @NonNull
    @Override
    public GruposViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_simple_text, parent, false);
        GruposViewHolder vh = new GruposViewHolder(v);
        vh.textView.setOnClickListener((view) -> onItemClicked(view, vh));
        return vh;
    }

    protected abstract void onItemClicked(View v, GruposViewHolder vh);

    @Override
    public void onBindViewHolder(@NonNull GruposViewHolder holder, int position) {
        holder.grupo = mDataset.get(position);
        holder.textView.setText(mDataset.get(position).getNombre() + " (" + mTimeLogHandler.todayStringTimeOnNegativeGroup(mDataset.get(position)) + " " + mContext.getString(R.string.hoy) + ")");
        holder.id = mDataset.get(position).getId();
        doOtherStuffOnBind(holder, position);
    }

    protected abstract void doOtherStuffOnBind(@NonNull GruposViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void giveFeedback(int tipo, Grupo feedback) {
        listeners.forEach((item) -> item.giveFeedback(tipo, feedback));
    }

    @Override
    public void addFeedbackListener(FeedbackListener<Grupo> listener) {
        listeners.add(listener);
    }

    public static class GruposViewHolder extends RecyclerView.ViewHolder {
        protected TextView textView;
        protected Integer id;
        protected Grupo grupo;

        public GruposViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.templateTextView);
        }

        public TextView getTextView() {
            return textView;
        }

        public Integer getId() {
            return id;
        }

        public Grupo getGrupo() {
            return grupo;
        }
    }

    public void updateDataset(List<Grupo> grupos) {
        Log.d(getLoggerTag(), "received updated dataset size: " + grupos.size());
        mDataset = grupos;
        this.notifyDataSetChanged();
    }

}
