package devs.mrp.coolyourturkey.listados;

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

import androidx.recyclerview.widget.RecyclerView;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListada;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

import java.util.ArrayList;
import java.util.List;

public class AppsListAdapter extends RecyclerView.Adapter<AppsListAdapter.AppsListViewHolder> implements Feedbacker<AplicacionListada> {
    private String TAG = "APPSLISTADAPTER";

    public static final int FEEDBACK_INSERT = 0;
    public static final int FEEDBACK_REQUEST_CONFIRM = 1;

    private AppLister mDataset;
    private Context mContext;

    private String tipoActual; // comparar con campo de "AplicacionListada"
    public static final String POSITIVA = AplicacionListada.POSITIVA;
    public static final String NEGATIVA = AplicacionListada.NEGATIVA;
    public static final String NEUTRAL = AplicacionListada.NEUTRAL;

    ArrayList<FeedbackListener<AplicacionListada>> mFeedbackList = new ArrayList<>();
    List<AplicacionListada> listaAppsSetted;
    private boolean loaded = false;

    public AppsListAdapter(AppLister dataset, Context context, String tipo) {
        mDataset = dataset;
        mContext = context;
        tipoActual = tipo;
    }

    public AppsListAdapter(Context context, String tipo) {
        mContext = context;
        tipoActual = tipo;
    }

    @Override
    public void giveFeedback(int tipo, AplicacionListada feedback) {
        mFeedbackList.forEach((item) -> {
            item.giveFeedback(tipo, feedback);
        });
    }

    // para cuando pulsamos sobre un switch y necesitamos una referencia a su posición
    // para devolverlo a su posición más tarde
    public void giveFeedback(int tipo, AplicacionListada feedback, Integer posicion){
        mFeedbackList.forEach((item)->{
            item.giveFeedback(tipo, feedback, posicion);
        });
    }

    @Override
    public void addFeedbackListener(FeedbackListener<AplicacionListada> listener) {
        mFeedbackList.add(listener);
    }

    // reference to the views
    public static class AppsListViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public Switch switchView;
        public TextView textView;
        public String packageName;

        public AppsListViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.textView);
            imageView = v.findViewById(R.id.imageView);
            switchView = v.findViewById(R.id.switch1);
            packageName = null;
        }
    }

    // crear nuevas estructuras de los views (invoked by the layout manager)
    @Override
    public AppsListAdapter.AppsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Log.d(TAG, "onCreateViewHolder");
        // crear vista a partir de layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_apps_list_adapter_texto, parent, false);

        AppsListViewHolder vh = new AppsListViewHolder(v);

        vh.switchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean toggled = ((Switch) v).isChecked();
                AplicacionListada app;
                if (toggled && tipoActual.equals(NEGATIVA)) {
                    app = new AplicacionListada(vh.packageName, tipoActual);
                    giveFeedback(FEEDBACK_INSERT, app);
                } else if (!toggled && tipoActual.equals(POSITIVA)) {
                    app = new AplicacionListada(vh.packageName, mContext.getString(R.string.string_tipo_lista_ninguna));
                    giveFeedback(FEEDBACK_INSERT, app);
                } else if (toggled && tipoActual.equals(POSITIVA)){
                    // si intentamos activar una positiva pedir confirmación
                    app = new AplicacionListada(vh.packageName, tipoActual);
                    giveFeedback(FEEDBACK_REQUEST_CONFIRM, app, vh.getAdapterPosition());
                } else if (!toggled && tipoActual.equals(NEGATIVA)){
                    // si intentamos desactivar una negativa pedir confirmación
                    app = new AplicacionListada(vh.packageName, mContext.getString(R.string.string_tipo_lista_ninguna));
                    giveFeedback(FEEDBACK_REQUEST_CONFIRM, app, vh.getAdapterPosition());
                }
            }
        });

        //Log.d(TAG, "creando viewholder");
        return vh;
    }

    // darle contenidos a la vista del viewholder
    @Override
    public void onBindViewHolder(AppsListViewHolder holder, int posicion) {
        // obtener datos del dataset
        // y remplazar el contenido en esta posición
        try {
            String packageName = mDataset.getNombre(posicion);
            ApplicationInfo applicationInfo = mContext.getPackageManager().getApplicationInfo(packageName, 0);
            holder.imageView.setImageDrawable(mContext.getPackageManager().getApplicationIcon(applicationInfo));
            holder.textView.setText(mContext.getPackageManager().getApplicationLabel(applicationInfo));
            holder.packageName = packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        boolean estaEnOtraLista = false;
        // cambiar los switches siguiendo la db
        if (listaAppsSetted != null) {
            boolean seToggleo = false;
            for (AplicacionListada app : listaAppsSetted) {
                if (mDataset.getNombre(posicion).equals(app.getNombre())) {
                    if (tipoActual.equals(app.getLista())) {
                        if (!holder.switchView.isChecked()) {
                            holder.switchView.setChecked(true);
                        }
                        seToggleo = true;
                    } else {
                        if (holder.switchView.isChecked()) {
                            holder.switchView.setChecked(false);
                        }
                        if (!app.getLista().equals(NEUTRAL)){
                            estaEnOtraLista = true;
                        }
                        seToggleo = true;
                    }
                    break;
                }
            }
            if (!seToggleo && holder.switchView.isChecked()){
                holder.switchView.setChecked(false);
            }
        }

        ponerTextoSwitch(holder, estaEnOtraLista);
    }

    private void ponerTextoSwitch(AppsListViewHolder vh, boolean enOtraLista){
        if (enOtraLista){
            switch (tipoActual){
                case POSITIVA:
                    vh.switchView.setText(R.string.es_negativa);
                    break;
                case NEGATIVA:
                    vh.switchView.setText(R.string.es_positiva);
                    break;
            }
            vh.switchView.setEnabled(false);
            return;
        }
        vh.switchView.setEnabled(true);
        switch (tipoActual){
            case POSITIVA:
                vh.switchView.setText(R.string.es_positiva);
                break;
            case NEGATIVA:
                vh.switchView.setText(R.string.es_negativa);
                break;
        }
    }

    // dar tamaño del dataset
    @Override
    public int getItemCount() {
        if (mDataset == null) {
            return 0;
        }
        Log.d(TAG, "getItemCount");
        int s = mDataset.getList().size();
        Log.d(TAG, "tamaño del datalist es " + String.valueOf(s));
        return s;
    }

    public void changeToDataset(AppLister dset) {
        Log.d(TAG, "changeToDataset");
        mDataset = dset;
        this.notifyDataSetChanged();
    }

    public void fitToDb(List<AplicacionListada> aplicacionesListadas) {
        //Log.d(TAG, "fit_to_db");
        listaAppsSetted = aplicacionesListadas;
        if (!loaded) {
            loaded = true;
            //changeToDataset(mDataset);
            this.notifyDataSetChanged();
        }
    }

    public void desSwitchear(List<AplicacionListada> aplicacionesListadas){
        listaAppsSetted = aplicacionesListadas;
        this.notifyDataSetChanged();
    }

    public void resetLoaded() {
        loaded = false;
    }
}
