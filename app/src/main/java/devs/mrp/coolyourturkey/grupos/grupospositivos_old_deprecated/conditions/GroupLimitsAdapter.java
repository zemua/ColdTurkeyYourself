package devs.mrp.coolyourturkey.grupos.grupospositivos_old_deprecated.conditions;

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
import devs.mrp.coolyourturkey.databaseroom.deprecated.grouplimit.GroupLimit;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

public class GroupLimitsAdapter extends RecyclerView.Adapter<GroupLimitsAdapter.GroupLimitsViewHolder> implements Feedbacker<GroupLimit> {

    public static final int FEEDBACK_DELETE = 0;

    private List<FeedbackListener<GroupLimit>> feedbackListeners = new ArrayList<>();

    private List<GroupLimit> mDataSet;
    private Context mContext;

    public GroupLimitsAdapter(List<GroupLimit> dataSet, Context context) {
        mDataSet = dataSet;
        mContext = context;
    }

    public static class GroupLimitsViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public GroupLimit groupLimit;
        public GroupLimitsViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.templateTextView);
        }

    }

    @NonNull
    @Override
    public GroupLimitsAdapter.GroupLimitsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_simple_text, parent, false);
        GroupLimitsAdapter.GroupLimitsViewHolder vh = new GroupLimitsViewHolder(v);

        vh.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveFeedback(FEEDBACK_DELETE, vh.groupLimit);
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupLimitsAdapter.GroupLimitsViewHolder holder, int position) {
        StringBuilder builder = new StringBuilder();
        GroupLimit limit = mDataSet.get(position);
        builder
                .append(mContext.getResources().getString(R.string.limite_de)).append(" ")
                .append(limit.getMinutesLimit()/60).append(" ").append(mContext.getResources().getString(R.string.h_de_horas)).append(" ")
                .append(limit.getMinutesLimit()%60).append(" ").append(mContext.getResources().getString(R.string.m_de_minutos)).append(" ")
                .append(mContext.getResources().getString(R.string.en_los_ultimos)).append(" ")
                .append(limit.getOffsetDays()).append(" ")
                .append(mContext.getResources().getString(R.string.d_de_dias));

        if (!limit.getSolosicondiciones()) {
            builder
                    .append(" - ")
                    .append(mContext.getString(R.string.aunqye_no_se_cumplan_las_condiciones_para_sumar));
        }

        if (limit.getBlocking()) {
            builder
                    .append(" - ")
                    .append(mContext.getString(R.string.bloquea_la_aplicacion_al_cumplirse));
        }

        holder.groupLimit = limit;
        holder.textView.setText(builder.toString());
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public void giveFeedback(int tipo, GroupLimit feedback) {
        feedbackListeners.stream().forEach(listener -> {
            listener.giveFeedback(tipo, feedback);
        });
    }

    @Override
    public void addFeedbackListener(FeedbackListener<GroupLimit> listener) {
        feedbackListeners.add(listener);
    }

    public void updateDataSet(List<GroupLimit> dataSet) {
        mDataSet = dataSet;
        this.notifyDataSetChanged();
    }
}
