package devs.mrp.coolyourturkey.grupospositivos;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.apptogroup.AppToGroup;
import devs.mrp.coolyourturkey.databaseroom.apptogroup.AppToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListada;
import devs.mrp.coolyourturkey.listados.AppLister;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

public class ReviewGroupAppsAdapter extends RecyclerView.Adapter<ReviewGroupAppsAdapter.ReviewGroupAppsViewHolder> implements Feedbacker<AppToGroup> {


    private String TAG = "REVIEW_GROUP_APPS_ADAPTER";

    public static final int FEEDBACK_SET_APPTOGROUP = 0;
    public static final int FEEDBACK_DEL_APPTOGROUP = 1;

    private List<FeedbackListener<AppToGroup>> listeners = new ArrayList<>();

    private AppLister mDataset;
    private Map<String, ApplicationInfo> mapDataset;
    private Context mContext;
    private List<AppToGroup> listaAppsSetted;
    private Map<String, AppToGroup> mapAppsSetted;
    private boolean loaded = false; // prevent switches' weird behavior on further group DB updates
    private Integer mThisGroupId;

    public ReviewGroupAppsAdapter(AppLister dataset, Context context, Integer thisGroupId){
        this.mDataset = dataset;
        setMapDataset(mDataset.getList());
        this.mContext = context;
        this.mThisGroupId = thisGroupId;
    }

    @NonNull
    @Override
    public ReviewGroupAppsAdapter.ReviewGroupAppsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_apps_list_adapter_texto, parent, false);
        ReviewGroupAppsAdapter.ReviewGroupAppsViewHolder vh = new ReviewGroupAppsAdapter.ReviewGroupAppsViewHolder(v);

        vh.switchView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "switch clicked");
                boolean toggled = ((Switch) v).isChecked();
                AppToGroup app = new AppToGroup(vh.packageName, getCurrentGroupId());
                if (!toggled && ifInThisGroup(app.getAppName())) {
                    app.setId(mapAppsSetted.get(app.getAppName()).getId()); // set the id of the AppToGroup to de-register by id
                    unRegisterFromDb(app);
                } else if (toggled && !ifInThisGroup(app.getAppName())) {
                    registerInDb(app);
                }
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewGroupAppsAdapter.ReviewGroupAppsViewHolder holder, int position) {
        try {
            String packageName = mDataset.getNombre(position);
            ApplicationInfo applicationInfo = mContext.getPackageManager().getApplicationInfo(packageName, 0);
            holder.imageView.setImageDrawable(mContext.getPackageManager().getApplicationIcon(applicationInfo));
            holder.textView.setText(mContext.getPackageManager().getApplicationLabel(applicationInfo));
            holder.packageName = packageName;
            holder.hposicion = position;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // set switches according to db
        if (listaAppsSetted != null) {
            String lnombre = mDataset.getNombre(position);
            if (mapDataset.containsKey(lnombre)) {
                if (ifInThisGroup(lnombre)) {
                    if (!holder.switchView.isChecked()) {
                        holder.switchView.setChecked(true);
                    }
                } else {
                    if (holder.switchView.isChecked()) {
                        holder.switchView.setChecked(false);
                    }
                    if (ifInOtherGroup(lnombre)) {
                        holder.switchView.setEnabled(false);
                    }
                }
            } else {
                if (holder.switchView.isChecked()) {
                    holder.switchView.setChecked(false);
                }
            }
        }
        setTextOfSwitch(holder);
    }

    @Override
    public int getItemCount() {
        return mDataset.getList().size();
    }

    @Override
    public void giveFeedback(int tipo, AppToGroup feedback) {
        listeners.forEach(listener -> {
            listener.giveFeedback(tipo, feedback);
        });
    }

    @Override
    public void addFeedbackListener(FeedbackListener<AppToGroup> listener) {
        listeners.add(listener);
    }

    public static class ReviewGroupAppsViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public Switch switchView;
        public TextView textView;
        public String packageName;
        public int hposicion;

        public ReviewGroupAppsViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
            switchView = itemView.findViewById(R.id.switch1);
            packageName = null;
            hposicion = -1;
        }
    }

    public void updateDataSet(List<AplicacionListada> positiveApps) {
        mDataset.setPositiveList(positiveApps);
        setMapDataset(mDataset.getList());
        this.notifyDataSetChanged();
        Log.d(TAG, "updateDataSet done");
    }

    public void firstGroupDbLoad(List<AppToGroup> appsToGroups) {
        listaAppsSetted = appsToGroups;
        setMapAppToGroup(listaAppsSetted);
        if (!loaded) {
            loaded = true;
            this.notifyDataSetChanged();
            Log.d(TAG, "firstGroupDbLoad done");
        }
    }

    private void setMapAppToGroup(List<AppToGroup> appsToGroups) {
        mapAppsSetted = appsToGroups.stream().collect(Collectors.toMap(AppToGroup::getAppName, appToGroup -> appToGroup));
    }

    private void setMapDataset(List<ApplicationInfo> appsListadas) {
        mapDataset = appsListadas.stream().collect(Collectors.toMap((app -> app.packageName), (app -> app)));
    }

    public void resetLoaded() { loaded = false; }

    private void setTextOfSwitch(ReviewGroupAppsViewHolder vh) {
        if (ifInOtherGroup(vh.packageName)){
            vh.switchView.setText(R.string.en_otro_grupo);
        }
    }

    private Integer getAppGroupId(String packageName){
        if (mapAppsSetted != null && mapAppsSetted.containsKey(packageName)) {
            return mapAppsSetted.get(packageName).getGroupId();
        }
        return -1;
    }

    private boolean ifInOtherGroup(String packageName){
        if (mapAppsSetted != null && mapAppsSetted.containsKey(packageName)) {
            return !mapAppsSetted.get(packageName).getGroupId().equals(mThisGroupId);
        }
        return false;
    }

    private boolean ifInThisGroup(String packageName){
        if (mapAppsSetted != null && mapAppsSetted.containsKey(packageName)) {
            return mapAppsSetted.get(packageName).getGroupId().equals(mThisGroupId);
        }
        return false;
    }

    private boolean ifInNoGroup(String packageName){
        return mapAppsSetted != null && !mapAppsSetted.containsKey(packageName);
    }

    private Integer getCurrentGroupId() {
        return mThisGroupId;
    }

    private void registerInDb(AppToGroup appToGroup){
        Log.d(TAG, "register in DB");
        giveFeedback(FEEDBACK_SET_APPTOGROUP, appToGroup);
        Log.d(TAG, "sent feedback set: " + appToGroup);
    }

    private void unRegisterFromDb(AppToGroup appToGroup){
        Log.d(TAG, "unregister from DB");
        giveFeedback(FEEDBACK_DEL_APPTOGROUP, appToGroup);
        Log.d(TAG, "sent feedback delete: " + appToGroup);
    }
}
