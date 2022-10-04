package devs.mrp.coolyourturkey.comun.impl;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

import java.util.function.Consumer;

import devs.mrp.coolyourturkey.comun.DialogWithDelayAndFragmentResponse;
import devs.mrp.coolyourturkey.comun.DialogWithDelayAndFragmentResponseFactory;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.comun.DialogWithDelayShower;

public class DialogWithDelayPresenterImpl implements DialogWithDelayPresenter {

    private String resultKey = DialogWithDelayAndFragmentResponse.RESULT_KEY;

    private FragmentManager fragmentManager;
    private Fragment fragment;
    private DialogWithDelayAndFragmentResponseFactory dialogFactory;

    public DialogWithDelayPresenterImpl(Fragment fragment, DialogWithDelayAndFragmentResponseFactory factory) {
        fragmentManager = fragment.getActivity().getSupportFragmentManager();
        this.fragment = fragment;
        this.dialogFactory = factory;
    }

    @Override
    public void setListener(String requestKey, Consumer<Boolean> consumer) {
        FragmentResultListener fragmentResultListener = (key, bundle) -> consumer.accept(bundle.getBoolean(resultKey));
        fragmentManager.setFragmentResultListener(requestKey, fragment.getViewLifecycleOwner(), fragmentResultListener);
    }

    @Override
    public void showDialog(String requestKey, String titulo, String mensaje, int iconoResId) {
        if (titulo != null && mensaje != null && fragment.getContext().getDrawable(iconoResId) != null) {
            DialogWithDelayShower dwd = dialogFactory.getDialog(iconoResId, titulo, mensaje, fragmentManager, requestKey);
            dwd.show(fragmentManager, null);
        }
    }
}
