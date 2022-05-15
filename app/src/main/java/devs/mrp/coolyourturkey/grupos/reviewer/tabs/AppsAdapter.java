package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.apptogroup.AppToGroup;
import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementToGroup;
import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementType;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListada;
import devs.mrp.coolyourturkey.listados.AppLister;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.AppsViewHolder> implements Feedbacker<Object> { // TODO

    public static final int FEEDBACK_SET_APPTOGROUP = 0;
    public static final int FEEDBACK_DEL_APPTOGROUP = 1;

    private AppLister mDataset;
    private Map<String, ApplicationInfo> mapDataset;
    private Context mContext;
    private Integer mThisGroupId;
    private List<ElementToGroup> listaAppsSetted;
    private Map<String, ElementToGroup> mapAppsSetted;
    private boolean loaded = false; // prevent switches' weird behavior on further group DB updates

    public AppsAdapter(AppLister dataset, Context context, Integer thisGroupId) {
        this.mDataset = dataset;
        this.mapDataset = mapDataset(dataset.getList());
        this.mContext = context;
        this.mThisGroupId = thisGroupId;
    }

    @NonNull
    @Override
    public AppsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_apps_list_adapter_texto, parent, false);
        AppsAdapter.AppsViewHolder vh = new AppsViewHolder(v);

        vh.switchView.setOnClickListener((view) -> {
            Switch s = (Switch) view;
            ElementToGroup element = new ElementToGroup().withType(ElementType.APP).withName(vh.packageName).withGroupId(mThisGroupId);
            if (!s.isChecked() && ifInThisGroup(element.getName())) {
                element.setId(mapAppsSetted.get(element.getName()).getId()); // set the id of the ElementToGroup to de-register by id
                unRegisterFromDb(element);
            } else if (s.isChecked() && !ifInThisGroup(element.getName())) {
                registerInDb(element);
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AppsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public void giveFeedback(int tipo, Object feedback) {

    }

    @Override
    public void addFeedbackListener(FeedbackListener<Object> listener) {

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

    public void updateDataset(List<AplicacionListada> apps) {
        if (mDataset != null) {
            mDataset.setPositiveList(apps);
            mapDataset = mapDataset(mDataset.getList());
            this.notifyDataSetChanged();
        }
    }

    public void firstGroupDbLoad(List<ElementToGroup> appsToGroup) {
        listaAppsSetted = appsToGroup;
        mapAppsSetted = mapAppToGroup(listaAppsSetted);
        if (!loaded) {
            loaded = true;
            this.notifyDataSetChanged();
        }
    }

    private Map<String, ElementToGroup> mapAppToGroup(List<ElementToGroup> appsToGroup) {
        return appsToGroup.stream().collect(Collectors.toMap(ElementToGroup::getName, Function.identity()));
    }

    private Map<String, ApplicationInfo> mapDataset(List<ApplicationInfo> apps) {
        return apps.stream().collect(Collectors.toMap(app -> app.packageName, app -> app));
    }

    public void resetLoaded() {
        loaded = false;
    }

    private void setTextOfSwitch(AppsViewHolder vh) {
        if (ifInOtherGroup(vh.packageName)) {
            vh.switchView.setText(R.string.en_otro_grupo);
        } else {
            vh.switchView.setText(R.string.switch_en_esta_lista);
        }
    }

    private boolean ifInOtherGroup(String packageName) {
        if (mapAppsSetted != null && mapAppsSetted.containsKey(packageName)) {
            return !mapAppsSetted.get(packageName).getGroupId().equals(mThisGroupId);
        }
        return false;
    }

    private boolean ifInThisGroup(String packageName) {
        if (mapAppsSetted != null && mapAppsSetted.containsKey(packageName)) {
            return mapAppsSetted.get(packageName).getGroupId().equals(mThisGroupId);
        }
        return false;
    }

    private void registerInDb(ElementToGroup element) {
        giveFeedback(FEEDBACK_SET_APPTOGROUP, element);
    }

    private void unRegisterFromDb(ElementToGroup element) {
        giveFeedback(FEEDBACK_DEL_APPTOGROUP, element);
    }

}
