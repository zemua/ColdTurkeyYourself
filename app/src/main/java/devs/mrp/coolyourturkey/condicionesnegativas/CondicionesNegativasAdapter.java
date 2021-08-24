package devs.mrp.coolyourturkey.condicionesnegativas;

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
import devs.mrp.coolyourturkey.databaseroom.conditionnegativetogroup.ConditionNegativeToGroup;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivo;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

public class CondicionesNegativasAdapter extends RecyclerView.Adapter<CondicionesNegativasAdapter.CondicionesNegativasViewHolder> implements Feedbacker<ConditionNegativeToGroup> {

    public static final int FEEDBACK_CONDITION_SELECTED = 0;

    private List<FeedbackListener<ConditionNegativeToGroup>> listeners = new ArrayList<>();

    private Context mContext;
    private List<ConditionNegativeToGroup> mDataset;
    private Map<Integer, GrupoPositivo> mGrupos;
    private NegativeConditionTimeChecker mTimeLogHandler;

    public CondicionesNegativasAdapter(Context context, NegativeConditionTimeChecker logger) {
        mContext = context;
        mDataset = new ArrayList<>();
        mGrupos = new HashMap<>();
        mTimeLogHandler = logger;
    }

    @NonNull
    @Override
    public CondicionesNegativasAdapter.CondicionesNegativasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_simple_text, parent, false);
        CondicionesNegativasViewHolder vh = new CondicionesNegativasViewHolder(v);

        vh.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveFeedback(FEEDBACK_CONDITION_SELECTED, vh.condition);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CondicionesNegativasAdapter.CondicionesNegativasViewHolder holder, int position) {
        holder.condition = mDataset.get(position);
        String description = concatenateConditionText(mDataset.get(position));
        holder.textView.setText(description);
        setBackgroundOnConditionMet(holder, mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        if (mDataset == null) {
            return 0;
        }
        return mDataset.size();
    }

    @Override
    public void giveFeedback(int tipo, ConditionNegativeToGroup feedback) {
        listeners.stream().forEach(l -> {
            l.giveFeedback(tipo, feedback);
        });
    }

    @Override
    public void addFeedbackListener(FeedbackListener<ConditionNegativeToGroup> listener) {
        listeners.add(listener);
    }

    public static class CondicionesNegativasViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public ConditionNegativeToGroup condition;

        public CondicionesNegativasViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.templateTextView);
        }
    }

    private String concatenateConditionText(ConditionNegativeToGroup condition) {
        StringBuilder s = new StringBuilder();
        switch(condition.getType()){
            case GROUP:
                s.append(mContext.getResources().getString(R.string.si_el_grupo));
                s.append(" ");
                if (mGrupos.containsKey(condition.getConditionalgroupid())){
                    s.append(mGrupos.get(condition.getConditionalgroupid()).getNombre());
                }
                break;
            case FILE:
                s.append(mContext.getResources().getString(R.string.si_el_archivo));
                s.append(" ");
                int len = 15; // length of the substring to attach
                if (condition.getFiletarget().length() < len){
                    s.append(condition.getFiletarget());
                } else {
                    s.append("...").append(condition.getFiletarget().substring(condition.getFiletarget().length()-len-1, condition.getFiletarget().length()-1));
                }
                break;
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
        s.append(getLoggerMinutes(condition)/60);
        s.append(" ");
        s.append(mContext.getResources().getString(R.string.h_de_horas));
        s.append(" ");
        s.append(getLoggerMinutes(condition)%60);
        s.append(" ");
        s.append(mContext.getResources().getString(R.string.m_de_minutos));
        return s.toString();
    }

    private Integer getLoggerMinutes(ConditionNegativeToGroup condition) {
        Long millis = mTimeLogHandler.getTimeCountedOnCondition(condition);
        Long minutes = millis / 60 / 1000;
        return minutes.intValue();
    }

    private void setBackgroundOnConditionMet(CondicionesNegativasAdapter.CondicionesNegativasViewHolder holder, ConditionNegativeToGroup condition) {
        if (mTimeLogHandler.ifConditionMet(condition)) {
            holder.textView.setBackgroundResource(R.drawable.green_rounded_corner_with_border);
        } else {
            holder.textView.setBackgroundResource(R.drawable.red_rounded_corner_with_border);
        }
    }

    public void setDataset(List<ConditionNegativeToGroup> dataSet) {
        mDataset = dataSet;
        notifyDataSetChanged();
    }

    public void setGrupos(Map<Integer, GrupoPositivo> grupos) {
        mGrupos = grupos;
        notifyDataSetChanged();
    }
}
