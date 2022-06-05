package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementToGroup;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

public abstract class AbstractAdapter<VH extends RecyclerView.ViewHolder, ID, DATA> extends RecyclerView.Adapter<VH> implements Feedbacker<ElementToGroup> {

    private List<FeedbackListener<ElementToGroup>> listeners = new ArrayList<>();
    protected Map<ID, ElementToGroup> mapSettedElements;
    protected List<ElementToGroup> mSettedElements;
    protected List<DATA> mDataSet;
    protected Integer mGroupId;
    protected boolean loaded;
    protected Context mContext;

    AbstractAdapter(Context context, Integer groupId) {
        this.mContext = context;
        this.mGroupId = groupId;
    }

    @Override
    public void giveFeedback(int tipo, ElementToGroup feedback) {
        listeners.forEach(l -> l.giveFeedback(tipo, feedback));
    }

    @Override
    public void addFeedbackListener(FeedbackListener<ElementToGroup> listener) {
        listeners.add(listener);
    }

    public void loopedGroupDbLoad(List<ElementToGroup> checksToGroup) {
        mSettedElements = checksToGroup;
        mapSettedElements = mapSettedElements(mSettedElements);
        loaded = true;
    }

    public void firstGroupDbLoad(List<ElementToGroup> checksToGroup) {
        boolean preloaded = this.loaded;
        loopedGroupDbLoad(checksToGroup);
        if (!preloaded) {
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        if (mDataSet == null) {
            return 0;
        }
        return mDataSet.size();
    }

    public void resetLoaded() {
        loaded = false;
    }

    public void updateDataSet(List<DATA> checks) {
        this.mDataSet = checks;
        this.notifyDataSetChanged();
    }

    protected abstract Map<ID, ElementToGroup> mapSettedElements(List<ElementToGroup> elementsToGroup);

    protected boolean ifInOtherGroup(ID toId) {
        if (mapSettedElements != null && mapSettedElements.containsKey(toId)) {
            return !mapSettedElements.get(toId).getGroupId().equals(mGroupId);
        }
        return false;
    }

    protected boolean ifInThisGroup(ID toId) {
        if (mapSettedElements != null && mapSettedElements.containsKey(toId)) {
            return Optional.ofNullable(mapSettedElements.get(toId).getGroupId()).map(gid -> gid.equals(mGroupId)).orElse(false);
        }
        return false;
    }

}
