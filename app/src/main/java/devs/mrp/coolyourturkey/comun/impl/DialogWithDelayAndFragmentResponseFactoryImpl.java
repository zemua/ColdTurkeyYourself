package devs.mrp.coolyourturkey.comun.impl;

import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.comun.DialogWithDelayAndFragmentResponse;
import devs.mrp.coolyourturkey.comun.DialogWithDelayAndFragmentResponseFactory;
import devs.mrp.coolyourturkey.comun.DialogWithDelayShower;
import devs.mrp.coolyourturkey.comun.ObjectTransporter;

public class DialogWithDelayAndFragmentResponseFactoryImpl implements DialogWithDelayAndFragmentResponseFactory {

    @Override
    public DialogWithDelayShower getDialog(int iconResId, String title, String message, String requestKey) {
        return new DialogWithDelayAndFragmentResponse(iconResId, title, message, requestKey);
    }
}
