package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.databaseroom.grupocondition.GrupoCondition;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;

public class ConditionsAdapterHandlerImpl implements AdapterHandler<GrupoCondition> {

    private ConditionsAdapter conditionsAdapter;

    public ConditionsAdapterHandlerImpl(Context context) {
        conditionsAdapter = new ConditionsAdapter(context);
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return conditionsAdapter;
    }

    @Override
    public void setDataset(List<GrupoCondition> dataSet) {
        conditionsAdapter.setDataset(dataSet);
    }

    @Override
    public void setGrupos(Map<Integer, Grupo> grupos) {
        conditionsAdapter.setGrupos(grupos);
    }

    @Override
    public void addFeedbackListener(FeedbackListener<GrupoCondition> listener) {
        conditionsAdapter.addFeedbackListener(listener);
    }
}
