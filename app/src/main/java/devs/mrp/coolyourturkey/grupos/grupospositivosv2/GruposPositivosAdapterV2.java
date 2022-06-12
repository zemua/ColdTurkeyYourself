package devs.mrp.coolyourturkey.grupos.grupospositivosv2;

import android.app.Application;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import java.util.List;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.grupos.GroupType;
import devs.mrp.coolyourturkey.grupos.GruposAdapter;
import devs.mrp.coolyourturkey.grupos.conditionchecker.ConditionCheckerCommander;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;

public class GruposPositivosAdapterV2 extends GruposAdapter {

    private static final String TAG = "GruposPositivosAdapter-V2";
    private ConditionCheckerCommander checker;

    public GruposPositivosAdapterV2(List<Grupo> dataset, Context context, TimeLogHandler timeLogHandler, LifecycleOwner owner, Application app, ConditionCheckerCommander checker) {
        super(dataset, context, timeLogHandler, owner, app, GroupType.POSITIVE);
        this.checker = checker;
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
        checker.onAllConditionsMet(holder.getGrupo().getId(), booleanResult -> {
            if (booleanResult) {
                holder.getTextView().setBackgroundResource(R.drawable.green_rounded_corner_with_border);
            } else {
                holder.getTextView().setBackgroundResource(R.drawable.red_rounded_corner_with_border);
            }
        });
    }
}
