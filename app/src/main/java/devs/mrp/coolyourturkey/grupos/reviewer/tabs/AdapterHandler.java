package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;

public interface AdapterHandler<T> {
    public static final int FEEDBACK_ITEM_SELECTED = 0;

    public RecyclerView.Adapter getAdapter();
    public void setDataset(List<T> dataSet);
    public void setGrupos(Map<Integer, Grupo> grupos);
    public void addFeedbackListener(FeedbackListener<T> listener);
}
