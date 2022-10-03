package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import android.app.Application;
import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition.GrupoCondition;
import devs.mrp.coolyourturkey.grupos.GroupType;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;

public class ConditionsAdapterHandlerImpl implements AdapterHandler<GrupoCondition> {

    private ConditionsAdapter conditionsAdapter;

    @Inject
    public ConditionsAdapterHandlerImpl(Context context, GroupType type, LifecycleOwner owner, Application app) {
        conditionsAdapter = new ConditionsAdapter(context, type, owner, app);
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

    @Override
    public void refresh() {
        conditionsAdapter.notifyDataSetChanged();
    }
}
