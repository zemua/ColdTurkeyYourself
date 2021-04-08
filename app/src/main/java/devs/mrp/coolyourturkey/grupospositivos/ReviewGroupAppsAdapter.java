package devs.mrp.coolyourturkey.grupospositivos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.apptogroup.AppToGroup;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListada;
import devs.mrp.coolyourturkey.listados.AppLister;
import devs.mrp.coolyourturkey.listados.AppsListAdapter;

public class ReviewGroupAppsAdapter extends RecyclerView.Adapter<ReviewGroupAppsAdapter.ReviewGroupAppsViewHolder> {


    private String TAG = "REVIEW_GROUP_APPS_ADAPTER";

    private AppLister mDataset;
    private Context mContext;
    private List<AppToGroup> listaAppsSetted;
    private boolean loaded = false; // prevent switches' weird behavior on further group DB updates

    public ReviewGroupAppsAdapter(AppLister dataset, Context context){
        this.mDataset = dataset;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ReviewGroupAppsAdapter.ReviewGroupAppsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_apps_list_adapter_texto, parent, false);
        ReviewGroupAppsAdapter.ReviewGroupAppsViewHolder vh = new ReviewGroupAppsAdapter.ReviewGroupAppsViewHolder(v);

        vh.switchView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                boolean toggled = ((Switch) v).isChecked();
                AppToGroup app = new AppToGroup(vh.packageName, getCurrentGroupId());
                if (toggled && ifInThisGroup(app.getAppName())) {
                    unRegisterFromDb(app);
                } else if (!toggled && ifInThisGroup(app.getAppName())) {
                    registerInDb(app);
                }
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewGroupAppsAdapter.ReviewGroupAppsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mDataset.getList().size();
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
        this.notifyDataSetChanged();
    }

    public void firstGroupAllocationDbLoad(List<AppToGroup> appsToGroups) {
        listaAppsSetted = appsToGroups;
        if (!loaded) {
            loaded = true;
            this.notifyDataSetChanged();
        }
    }

    public void resetLoaded() { loaded = false; }

    private Integer getAppGroupId(String packageName){
        // TODO
        return -1;
    }

    private boolean ifInOtherGroup(String packageName){
        // TODO
        return false;
    }

    private boolean ifInThisGroup(String packageName){
        // TODO
        return false;
    }

    private boolean ifInNoGroup(String packageName){
        // TODO
        return true;
    }

    private Integer getCurrentGroupId() {
        // TODO
        return -1;
    }

    private void registerInDb(AppToGroup appToGroup){
        // TODO
    }

    private void unRegisterFromDb(AppToGroup appToGroup){
        // TODO
    }
}
