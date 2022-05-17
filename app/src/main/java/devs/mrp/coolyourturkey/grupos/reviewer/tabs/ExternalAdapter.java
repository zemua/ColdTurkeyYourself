package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementToGroup;
import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementType;

public class ExternalAdapter extends AbstractAdapter<ExternalAdapter.ExternalViewHolder, String, String> {

    private static final int DISPLAY_NAME_LENGTH = 25;

    public static final int FEEDBACK_DEL_FILETOGROUP = 0;

    public ExternalAdapter(Context context, Integer groupId) {
        super(context, groupId);
    }

    @Override
    protected Map<String, ElementToGroup> mapSettedElements(List<ElementToGroup> elementsToGroup) {
        Map<String, ElementToGroup> map = new HashMap<>();
        elementsToGroup.stream().forEach(element -> map.put(element.getName(), element));
        return map;
    }

    @NonNull
    @Override
    public ExternalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_list_adapter_texto_button, parent, false);
        ExternalViewHolder vh = new ExternalViewHolder(v);

        vh.button.setOnClickListener((view) -> {
            ElementToGroup element = new ElementToGroup()
                    .withType(ElementType.FILE)
                    .withName(vh.filePath)
                    .withGroupId(mGroupId)
                    .withToId(-1L);
            element.setId(mapSettedElements.get(element.getName()).getId());
            giveFeedback(FEEDBACK_DEL_FILETOGROUP, element);
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ExternalViewHolder holder, int position) {
        String file = mDataSet.get(position);
        holder.filePath = file;
        holder.textView.setText(file.length() <= DISPLAY_NAME_LENGTH ? file : file.substring(file.length()-DISPLAY_NAME_LENGTH));
        holder.button.setText(R.string.borrar);
    }

    protected static class ExternalViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public String filePath;
        public Button button;

        public ExternalViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            button = itemView.findViewById(R.id.button);
        }
    }
}
