package devs.mrp.coolyourturkey.usagestats;

import android.app.usage.UsageStats;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.PermisosChecker;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListada;
import devs.mrp.coolyourturkey.listados.AppLister;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsAdapterDetail extends RecyclerView.Adapter<StatsAdapterDetail.StatsViewHolder> {

    private String TAT = "STATS_ADAPTER";

    private ArrayList<AplicacionListada> mAppPosNegList;
    private StatsHandler mStatsHandler;
    private Context mContext;
    private AppLister mAppsInstaladas;
    private ArrayList<UsageStats> mDetalleUsageStats;
    private Map<String, UsageStats> mUsageMap;

    private boolean dbLoaded = false;

    public static class StatsViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public TextView textTime;

        public StatsViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.textView2);
            imageView = v.findViewById(R.id.imageView2);
            textTime = v.findViewById(R.id.textViewTime);
        }
    }

    public static StatsAdapterDetail createDetailedStatsAdapter(Context context) {
        StatsAdapterDetail lStatsAdapter = new StatsAdapterDetail();
        lStatsAdapter.inicializaCosas(context);

        // pasar estadísticas a objeto útil
        lStatsAdapter.mDetalleUsageStats = lStatsAdapter.mStatsHandler.getDetailUsageStats();
        lStatsAdapter.mUsageMap = new HashMap<>();
        if (PermisosChecker.checkPermisoEstadisticas(context)) {
            lStatsAdapter.mDetalleUsageStats.forEach((stat) -> {
                lStatsAdapter.mUsageMap.put(stat.getPackageName(), stat);
            });
        }

        return lStatsAdapter;
    }

    private void inicializaCosas(Context context) {
        mStatsHandler = new StatsHandler(context); // para consultar los tiempos

        mContext = context;
        dbLoaded = false;
    }

    public StatsAdapterDetail inicializaInstalledList(Context context) {
        mAppsInstaladas = new AppLister(context);
        mAppsInstaladas.setSystemList(); // incluir apps del sistema
        return this;
    }

    @Override
    public StatsAdapterDetail.StatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_stats_adapter, parent, false);

        StatsAdapterDetail.StatsViewHolder vh = new StatsAdapterDetail.StatsViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(StatsAdapterDetail.StatsViewHolder holder, int posicion) {
        String lnombre = mAppPosNegList.get(posicion).getNombre();
        try {
            ApplicationInfo applicationInfo = mContext.getPackageManager().getApplicationInfo(lnombre, 0);
            CharSequence label = mContext.getPackageManager().getApplicationLabel(applicationInfo);
            holder.textView.setText(label);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        try {
            holder.imageView.setImageDrawable(mContext.getPackageManager().getApplicationIcon(lnombre));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        UsageStats lus = mUsageMap.get(lnombre);
        if (lus != null) {
            long lmilis = mUsageMap.get(lnombre).getTotalTimeInForeground();
            Long lhora = lmilis/(1000*60*60);
            Long lminuto = (lmilis%(1000*60*60))/(1000*60);
            Long lsegundo = (lmilis%(1000*60)/(1000));
            Formatter fm = new Formatter();
            fm.format("%02d:%02d:%02d", lhora, lminuto, lsegundo);
            holder.textTime.setText(fm.toString());
        }
        else {
            holder.textTime.setText("00:00:00");
        }
    }

    @Override
    public int getItemCount() {
        if (mAppPosNegList == null || mAppsInstaladas == null) {
            return 0;
        }
        int s = mAppPosNegList.size();
        return s;
    }

    public void fitToDb(List<AplicacionListada> appListadas) {
        if (dbLoaded == true) {
            return;
        }
        dbLoaded = true;
        mAppPosNegList = new ArrayList(appListadas);
        this.notifyDataSetChanged();
    }
}
