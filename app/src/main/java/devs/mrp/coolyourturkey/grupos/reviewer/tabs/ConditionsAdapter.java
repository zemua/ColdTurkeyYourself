package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.databaseroom.grupocondition.GrupoCondition;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

public class ConditionsAdapter extends RecyclerView.Adapter<ConditionsAdapter.ConditionViewHolder> implements Feedbacker<GrupoCondition> {

    public static final int FEEDBACK_CONDITION_SELECTED = 0;

    private List<GrupoCondition> mDataSet;
    private List<FeedbackListener<GrupoCondition>> listeners = new ArrayList<>();

    private Context mContext;
    private Map<Integer, Grupo> mGrupos;

    public ConditionsAdapter(Context context) {
        this.mContext = context;
        this.mDataSet = new ArrayList<>();
        this.mGrupos = new HashMap<>();
    }

    @NonNull
    @Override
    public ConditionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_simple_text, parent, false);
        ConditionViewHolder vh = new ConditionViewHolder(v);

        vh.textView.setOnClickListener((view) -> giveFeedback(FEEDBACK_CONDITION_SELECTED, vh.grupoCondition));

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ConditionViewHolder holder, int position) {
        holder.grupoCondition = mDataSet.get(position);
        String description = concatenateConditionText(mDataSet.get(position));
        holder.textView.setText(description);
        setBackgroundOnConditionMet(holder, mDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public void giveFeedback(int tipo, GrupoCondition feedback) {
        listeners.forEach(l -> l.giveFeedback(tipo, feedback));
    }

    @Override
    public void addFeedbackListener(FeedbackListener<GrupoCondition> listener) {
        listeners.add(listener);
    }

    protected static class ConditionViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public GrupoCondition grupoCondition;

        ConditionViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.templateTextView);
        }
    }

    private String concatenateConditionText(GrupoCondition condition) {
        StringBuilder s = new StringBuilder();
        s.append(mContext.getResources().getString(R.string.si_el_grupo));
        s.append(" ");
                if (mGrupos.containsKey(condition.getConditionalgroupid())){
                    s.append(mGrupos.get(condition.getConditionalgroupid()).getNombre());
                }

        s.append(" ");
        s.append(mContext.getResources().getString(R.string.ha_usado));
        s.append(" ");
        s.append(String.valueOf(condition.getConditionalminutes()/60));
        s.append(" ").append(mContext.getResources().getString(R.string.h_de_horas)).append(" ");
        s.append(String.valueOf(condition.getConditionalminutes()%60)).append(" ");
        s.append(mContext.getResources().getString(R.string.m_de_minutos)).append(" ");
        s.append(mContext.getResources().getString(R.string.en_los_ultimos)).append(" ");
        s.append(String.valueOf(condition.getFromlastndays())).append(" ");
        s.append(mContext.getResources().getString(R.string.d_de_dias));
        s.append(". ");
        s.append(mContext.getResources().getString(R.string.actualmente_ha_usado));
        s.append(" ");
        s.append(11);//s.append(getLoggerMinutes(condition)/60); // TODO get total time spent on the group
        s.append(" ");
        s.append(mContext.getResources().getString(R.string.h_de_horas));
        s.append(" ");
        s.append(11);//s.append(getLoggerMinutes(condition)%60); // TODO get total time spent on the group
        s.append(" ");
        s.append(mContext.getResources().getString(R.string.m_de_minutos));
        return s.toString();
    }

    private void setBackgroundOnConditionMet(ConditionViewHolder holder, GrupoCondition condition) {
        //if (mTimeLogHandler.ifConditionMet(condition)) {
        if (true) { // TODO check if conditions met for these groups
            holder.textView.setBackgroundResource(R.drawable.green_rounded_corner_with_border);
        } else {
            holder.textView.setBackgroundResource(R.drawable.red_rounded_corner_with_border);
        }
    }

    public void setDataset(List<GrupoCondition> dataSet) {
        mDataSet = dataSet;
        notifyDataSetChanged();
    }

    public void setGrupos(Map<Integer, Grupo> grupos) {
        mGrupos = grupos;
        notifyDataSetChanged();
    }
}
