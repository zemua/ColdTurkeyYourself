package devs.mrp.coolyourturkey.grupos.grupospositivos;

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
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivo;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;

public class GruposPositivosAdapter extends RecyclerView.Adapter<GruposPositivosAdapter.GruposPositivosViewHolder> implements Feedbacker<GrupoPositivo> {

    private static String TAG = "GRUPOS_POSITIVOS_ADAPTER";

    public static final int FEEDBACK_ITEM_CLICKED = 0;

    private List<GrupoPositivo> mDataset;
    private Context mContext;
    private TimeLogHandler mTimeLogHandler;

    private ArrayList<FeedbackListener<GrupoPositivo>> mFeedbackListener = new ArrayList<>();

    public GruposPositivosAdapter(List<GrupoPositivo> dataset, Context context, TimeLogHandler logger) {
        mDataset = dataset;
        mContext = context;
        mTimeLogHandler = logger;
    }

    @NonNull
    @Override
    public GruposPositivosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_simple_text, parent, false);
        GruposPositivosViewHolder vh = new GruposPositivosViewHolder(v);

        vh.textView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                giveFeedback(FEEDBACK_ITEM_CLICKED, vh.grupo);
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull GruposPositivosViewHolder holder, int position) {
        holder.grupo = mDataset.get(position);
        holder.textView.setText(mDataset.get(position).getNombre() + " (" + mTimeLogHandler.todayStringTimeOnGroup(mDataset.get(position)) + " " + mContext.getString(R.string.hoy) + ")");
        holder.id = mDataset.get(position).getId();
        setBackgroundOnConditionMet(holder, mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void giveFeedback(int tipo, GrupoPositivo feedback) {
        mFeedbackListener.forEach((item) -> {
            item.giveFeedback(tipo, feedback);
        });
    }

    @Override
    public void addFeedbackListener(FeedbackListener<GrupoPositivo> listener) {
        mFeedbackListener.add(listener);
    }

    public static class GruposPositivosViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public Integer id;
        public GrupoPositivo grupo;
        public GruposPositivosViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.templateTextView);
        }
    }

    public void updateDataset(List<GrupoPositivo> grupos) {
        Log.d(TAG, "received updated dataset size: " + grupos.size());
        mDataset = grupos;
        this.notifyDataSetChanged();
    }

    private void setBackgroundOnConditionMet(GruposPositivosAdapter.GruposPositivosViewHolder holder, GrupoPositivo grupo) {
        if (mTimeLogHandler.ifAllGroupConditionsMet(grupo.getId())) {
            holder.textView.setBackgroundResource(R.drawable.green_rounded_corner_with_border);
        } else {
            holder.textView.setBackgroundResource(R.drawable.red_rounded_corner_with_border);
        }
    }
}
