package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.CheckTimeBlock;
import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementToGroup;
import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementType;

public class ChecksAdapter extends AbstractSwitchesAdapter<ChecksAdapter.CheckViewHolder, Long, CheckTimeBlock> {

    public static final int FEEDBACK_SET_CHECKTOGROUP = 0;
    public static final int FEEDBACK_DEL_CHECKTOGROUP = 1;

    private List<CheckTimeBlock> mDataSet;
    private List<ElementToGroup> mSettedChecks;
    private Map<Long, ElementToGroup> mapSettedChecks;
    private Context mContext;

    public ChecksAdapter(Context context, Integer thisGroupId) {
        this.mContext = context;
        this.mGroupId = thisGroupId;
    }

    @NonNull
    @Override
    public ChecksAdapter.CheckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_list_adapter_texto_switch, parent, false);
        CheckViewHolder vh = new CheckViewHolder(v);

        vh.switchView.setOnClickListener((view) -> {
            Switch s = (Switch) view;
            ElementToGroup element = new ElementToGroup().withType(ElementType.CHECK).withName(vh.textView.getText().toString()).withGroupId(mGroupId).withToId(vh.checkId);
            if (!s.isChecked() && ifInThisGroup(element.getToId())) {
                element.setId(mapSettedChecks.get(element.getToId()).getId()); // set id of the ElementToGroup to de-register by id
                giveFeedback(FEEDBACK_DEL_CHECKTOGROUP, element);
            } else if (s.isChecked() && !ifInThisGroup(element.getToId())) {
                giveFeedback(FEEDBACK_SET_CHECKTOGROUP, element);
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ChecksAdapter.CheckViewHolder holder, int position) {
        if (mSettedChecks != null) {
            holder.textView.setText(mSettedChecks.get(position).getName());
            holder.checkId = mSettedChecks.get(position).getToId();
            setSwitchAccordingToDb(holder.switchView, holder.checkId);
        }
        setTextOfSwitch(holder.switchView, holder.checkId);
    }

    protected static class CheckViewHolder extends RecyclerView.ViewHolder {

        public Switch switchView;
        public TextView textView;
        public Long checkId;

        public CheckViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            switchView = itemView.findViewById(R.id.switch1);
            checkId = null;
        }
    }

    @Override
    protected Map<Long, ElementToGroup> mapSettedElements(List<ElementToGroup> checksToGroup) {
        return checksToGroup.stream().collect(Collectors.toMap(ElementToGroup::getToId, Function.identity()));
    }
}
