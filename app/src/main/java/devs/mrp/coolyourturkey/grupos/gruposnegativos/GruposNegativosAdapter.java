package devs.mrp.coolyourturkey.grupos.gruposnegativos;

import android.app.Application;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.grupos.GruposAdapter;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;

public class GruposNegativosAdapter extends GruposAdapter {

    private static final String TAG = "GruposNegativosAdapter";

    public GruposNegativosAdapter(List<Grupo> dataset, Context context, TimeLogHandler timeLogHandler, LifecycleOwner owner) {
        super(dataset, context, timeLogHandler, owner);
    }

    @Override
    protected String getLoggerTag() {
        return TAG;
    }

    @Override
    protected void onItemClicked(View v, GruposViewHolder vh) {
        giveFeedback(FEEDBACK_ITEM_CLICKED, vh.getGrupo());
    }

    @Override
    protected void doOtherStuffOnBind(@NonNull GruposViewHolder holder, int position) {
        // TODO set background color of textfield depending on condition met
    }
}
