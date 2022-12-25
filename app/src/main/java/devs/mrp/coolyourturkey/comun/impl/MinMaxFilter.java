package devs.mrp.coolyourturkey.comun.impl;

import android.text.InputFilter;
import android.text.Spanned;

public class MinMaxFilter implements InputFilter {

    private int min, max;

    public MinMaxFilter(int minValue, int maxValue) {
        this.min = minValue;
        this.max = maxValue;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt (dest.toString() + source.toString()) ;
            if (isInRange(min, max, input)) {
                return null;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace() ;
        }
        return "" ;
    }

    private boolean isInRange ( int a , int b , int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a ;
    }
}
