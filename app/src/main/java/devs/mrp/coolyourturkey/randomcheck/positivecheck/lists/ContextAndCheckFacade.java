package devs.mrp.coolyourturkey.randomcheck.positivecheck.lists;

import android.content.Context;

import devs.mrp.coolyourturkey.dtos.randomcheck.PositiveCheck;

public class ContextAndCheckFacade {
    private Context context;
    private PositiveCheck check;

    public ContextAndCheckFacade(Context c, PositiveCheck pc) {
        context = c;
        check = pc;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public PositiveCheck getCheck() {
        return check;
    }

    public void setCheck(PositiveCheck check) {
        this.check = check;
    }
}
