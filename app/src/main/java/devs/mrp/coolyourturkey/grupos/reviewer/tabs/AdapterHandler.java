package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.databaseroom.grupocondition.GrupoCondition;

public interface AdapterHandler<T> {
    public RecyclerView.Adapter<RecyclerView.ViewHolder> getAdapter();
    public void setDataset(List<T> dataSet);
    public void setGrupos(Map<Integer, Grupo> grupos);
}
