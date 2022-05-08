package devs.mrp.coolyourturkey.grupos.gruposnegativos;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.List;

import devs.mrp.coolyourturkey.grupos.GrupoDto;
import devs.mrp.coolyourturkey.grupos.GruposAdapter;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;

public class GruposNegativosAdapter extends GruposAdapter {

    public GruposNegativosAdapter(List<GrupoDto> dataset, Context context, TimeLogHandler timeLogHandler) {
        super(dataset, context, timeLogHandler);
    }

    @Override
    protected String getTag() {
        return "GruposNegativosAdapter";
    }

    @Override
    protected void onItemClicked(View v, GruposViewHolder vh) {
        // TODO giveFeedback
    }

    @Override
    protected void doOtherStuffOnBind(@NonNull GruposViewHolder holder, int position) {
        // TODO set background color of textfield depending on condition met
    }
}
