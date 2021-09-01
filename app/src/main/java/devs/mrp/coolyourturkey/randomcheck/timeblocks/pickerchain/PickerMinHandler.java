package devs.mrp.coolyourturkey.randomcheck.timeblocks.pickerchain;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.comun.TimePickerFragment;
import devs.mrp.coolyourturkey.randomcheck.timeblocks.review.TimeBlocksFragment;

public class PickerMinHandler extends ChainHandler<Intent> {
    private View v;
    private TimeBlocksFragment fr;

    public PickerMinHandler(View vies, TimeBlocksFragment fr) {
        v = vies;
        this.fr = fr;
    }

    @Override
    protected boolean canHandle(String tipo) {
        return tipo.equals(String.valueOf(TimeBlocksFragment.REQUEST_CODE_MIN_TIME));
    }

    @Override
    protected void handle(Intent data) {
        if (data != null) {
            Button b = v.findViewById(R.id.buttonControlMin);
            String txt = data.getStringExtra(TimePickerFragment.EXTRA_REPLY_STRING);
            long hora = data.getIntExtra(TimePickerFragment.EXTRA_REPLY_HORA, -1);
            long minuto = data.getIntExtra(TimePickerFragment.EXTRA_REPLY_MINUTO, -1);
            if (txt != null) {
                b.setText(txt);
            }
            if (hora != -1 && minuto != -1) {
                fr.setMinH((int)hora);
                fr.setMinM((int)minuto);
            }
        }
    }
}
