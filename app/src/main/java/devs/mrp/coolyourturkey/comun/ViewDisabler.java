package devs.mrp.coolyourturkey.comun;

import android.view.View;

import java.util.List;
import java.util.function.Supplier;

public interface ViewDisabler {

    public void addViewConditions(View view, List<Supplier<Boolean>> conditions);

    public void evaluateConditions();

}
