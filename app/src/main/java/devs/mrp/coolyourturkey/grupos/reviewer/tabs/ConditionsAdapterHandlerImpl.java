package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.databaseroom.grupocondition.GrupoCondition;

public class ConditionsAdapterHandlerImpl implements AdapterHandler<GrupoCondition> {

    private ConditionsAdapter conditionsAdapter;

    public ConditionsAdapterHandlerImpl(Context context) {
        conditionsAdapter = new ConditionsAdapter(context);
    }

    @Override
    public RecyclerView.Adapter<RecyclerView.ViewHolder> getAdapter() {
        return null;
    }

    @Override
    public void setDataset(List<GrupoCondition> dataSet) {

    }

    @Override
    public void setGrupos(Map<Integer, Grupo> grupos) {

    }
}
