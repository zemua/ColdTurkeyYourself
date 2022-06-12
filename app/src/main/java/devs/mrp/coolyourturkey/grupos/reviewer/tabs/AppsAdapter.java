package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementToGroup;
import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementType;

public class AppsAdapter extends AbstractSwitchesAdapter<AppsAdapter.AppsViewHolder, String, ApplicationInfo> {

    public static final int FEEDBACK_SET_APPTOGROUP = 0;
    public static final int FEEDBACK_DEL_APPTOGROUP = 1;

    public AppsAdapter(Context context, Integer thisGroupId) {
        super(context, thisGroupId);
    }

    @NonNull
    @Override
    public AppsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_apps_list_adapter_texto, parent, false);
        AppsAdapter.AppsViewHolder vh = new AppsViewHolder(v);

        vh.switchView.setOnClickListener((view) -> {
            Switch s = (Switch) view;
            ElementToGroup element = new ElementToGroup().withType(ElementType.APP).withName(vh.packageName).withGroupId(mGroupId).withToId(-1L);
            if (!s.isChecked() && ifInThisGroup(element.getName())) {
                element.setId(mapSettedElements.get(element.getName()).getId()); // set the id of the ElementToGroup to de-register by id
                giveFeedback(FEEDBACK_DEL_APPTOGROUP, element);
            } else if (s.isChecked() && !ifInThisGroup(element.getName())) {
                giveFeedback(FEEDBACK_SET_APPTOGROUP, element);
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AppsViewHolder holder, int position) {
        try {
            String packageName = mDataSet.get(position).packageName;
            PackageManager packageManager = mContext.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            holder.imageView.setImageDrawable(packageManager.getApplicationIcon(applicationInfo));
            holder.textView.setText(packageManager.getApplicationLabel(applicationInfo));
            holder.packageName = packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        setSwitchAccordingToDb(holder.switchView, mDataSet.get(position).packageName);
        setTextOfSwitch(holder.switchView, holder.packageName);
    }

    @Override
    protected Map<String, ElementToGroup> mapSettedElements(List<ElementToGroup> appsToGroup) {
        Map<String, ElementToGroup> map = new HashMap<>(); // avoid problems with repeated keys
        appsToGroup.stream().forEach(element -> map.put(element.getName(), element));
        return map;
    }

    protected static class AppsViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public Switch switchView;
        public TextView textView;
        public String packageName;

        public AppsViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
            switchView = itemView.findViewById(R.id.switch1);
            packageName = null;
        }
    }

}
