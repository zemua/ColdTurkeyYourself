package devs.mrp.coolyourturkey.grupospositivos;

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

public class GruposPositivosAdapter extends RecyclerView.Adapter<GruposPositivosAdapter.GruposPositivosViewHolder> implements Feedbacker<Integer> {

    private static String TAG = "GRUPOS_POSITIVOS_ADAPTER";

    public static final int FEEDBACK_ITEM_CLICKED = 0;

    private List<GrupoPositivo> mDataset;
    private Context mContext;

    private ArrayList<FeedbackListener<Integer>> mFeedbackListener = new ArrayList<>();

    public GruposPositivosAdapter(List<GrupoPositivo> dataset, Context context) {
        mDataset = dataset;
        mContext = context;
    }

    @NonNull
    @Override
    public GruposPositivosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_grupospositivos, parent, false);
        GruposPositivosViewHolder vh = new GruposPositivosViewHolder(v);

        vh.textView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                giveFeedback(FEEDBACK_ITEM_CLICKED, vh.id);
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull GruposPositivosViewHolder holder, int position) {
        holder.textView.setText(mDataset.get(position).getNombre());
        holder.id = mDataset.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void giveFeedback(int tipo, Integer feedback) {
        mFeedbackListener.forEach((item) -> {
            item.giveFeedback(tipo, feedback);
        });
    }

    @Override
    public void addFeedbackListener(FeedbackListener<Integer> listener) {
        mFeedbackListener.add(listener);
    }

    public static class GruposPositivosViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public Integer id;
        public GruposPositivosViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.nombre);
        }
    }

    public void updateDataset(List<GrupoPositivo> grupos) {
        Log.d(TAG, "received updated dataset size: " + grupos.size());
        mDataset = grupos;
        this.notifyDataSetChanged();
    }
}
