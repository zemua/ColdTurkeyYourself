package devs.mrp.coolyourturkey.randomcheck.timeblocks.pickerchain;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.comun.TimePickerFragment;
import devs.mrp.coolyourturkey.randomcheck.timeblocks.review.TimeBlocksFragment;

public class PickerMaxHandler extends ChainHandler<Intent> {

    private static final String TAG = "PickerMaxHandler";

    private View v;
    private TimeBlocksFragment fr;

    public PickerMaxHandler(View vies, TimeBlocksFragment fr) {
        v = vies;
        this.fr = fr;
    }

    @Override
    protected boolean canHandle(String tipo) {
        return tipo.equals(String.valueOf(TimeBlocksFragment.REQUEST_CODE_MAX_TIME));
    }

    @Override
    protected void handle(Intent data) {
        Log.d(TAG, "inside handle");
        if (data != null) {
            Log.d(TAG, "data is not null");
            Button b = v.findViewById(R.id.buttonControlMax);
            String txt = data.getStringExtra(TimePickerFragment.EXTRA_REPLY_STRING);
            long hora = data.getIntExtra(TimePickerFragment.EXTRA_REPLY_HORA, -1);
            long minuto = data.getIntExtra(TimePickerFragment.EXTRA_REPLY_MINUTO, -1);
            if (txt != null) {
                Log.d(TAG, "txt is not null: " + txt);
                b.setText(txt);
            }
            if (hora != -1 && minuto != -1) {
                Log.d(TAG, "hora y minuto != -1 => " + hora + " " + minuto);
                fr.setMaxH((int)hora);
                fr.setMaxM((int)minuto);
            }
        }
    }
}
