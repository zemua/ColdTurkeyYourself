package devs.mrp.coolyourturkey.comun;

import android.view.View;

import java.util.Optional;

public interface UiViewBuilder<T extends View> {

    public Optional<T> buildElement(View parent, int resourceId);

}
