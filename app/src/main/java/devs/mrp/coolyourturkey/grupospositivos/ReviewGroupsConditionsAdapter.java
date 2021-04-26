package devs.mrp.coolyourturkey.grupospositivos;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.apptogroup.AppToGroup;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup.ConditionToGroup;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivo;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivoRepository;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;

public class ReviewGroupsConditionsAdapter extends RecyclerView.Adapter<ReviewGroupsConditionsAdapter.ReviewGroupsConditionsViewHolder> implements Feedbacker<ConditionToGroup> {

    // TODO show current state of conditions (current time spent) and highlight with colors (green/red) depending on met/not met
    // ifConditionMet(groupId, conditionId) to highlight color
    // getTimeCountedOnGroupCondition(groupId, conditionId) to check the time spent on the group of this condition (if group)

    public static final int FEEDBACK_CONDITION_SELECTED = 0;

    private List<ConditionToGroup> mDataset;
    private Context mContext;
    private Map<Integer, GrupoPositivo> mGrupos; // Integer = id del grupo
    private TimeLogHandler mTimeLogHandler;

    private List<FeedbackListener<ConditionToGroup>> mListeners = new ArrayList<>();

    public ReviewGroupsConditionsAdapter(Context context, TimeLogHandler logger) {
        mContext = context;
        mDataset = new ArrayList<>();
        mGrupos = new HashMap<>();
        mTimeLogHandler = logger;
    }

    @NonNull
    @Override
    public ReviewGroupsConditionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_simple_text, parent, false);
        ReviewGroupsConditionsViewHolder vh = new ReviewGroupsConditionsViewHolder(v);

        vh.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveFeedback(FEEDBACK_CONDITION_SELECTED, vh.conditionToGroup);
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewGroupsConditionsViewHolder holder, int position) {
        holder.conditionToGroup = mDataset.get(position);
        String description = concatenateConditionText(mDataset.get(position));
        holder.textView.setText(description);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void giveFeedback(int tipo, ConditionToGroup feedback) {
        mListeners.forEach(listener -> {
            listener.giveFeedback(tipo, feedback);
        });
    }

    @Override
    public void addFeedbackListener(FeedbackListener<ConditionToGroup> listener) {
        mListeners.add(listener);
    }

    public static class ReviewGroupsConditionsViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ConditionToGroup conditionToGroup;

        ReviewGroupsConditionsViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.templateTextView);
        }
    }

    private Integer getLoggerMinutes(ConditionToGroup condition) {
        Long millis = mTimeLogHandler.getTimeCountedOnGroupCondition(condition);
        Long minutes = millis / 60 / 1000;
        return minutes.intValue();
    }

    private String concatenateConditionText(ConditionToGroup condition) {
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

    public void setDataset(List<ConditionToGroup> dataSet) {
        mDataset = dataSet;
        notifyDataSetChanged();
    }

    public void setGrupos(Map<Integer, GrupoPositivo> grupos) {
        mGrupos = grupos;
        notifyDataSetChanged();
    }

}
