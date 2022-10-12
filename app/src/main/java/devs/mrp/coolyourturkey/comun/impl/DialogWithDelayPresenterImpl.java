package devs.mrp.coolyourturkey.comun.impl;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.DialogWithDelayAndFragmentResponse;
import devs.mrp.coolyourturkey.comun.DialogWithDelayAndFragmentResponseFactory;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.comun.DialogWithDelayShower;

public class DialogWithDelayPresenterImpl implements DialogWithDelayPresenter {

    private String resultKey = DialogWithDelayAndFragmentResponse.RESULT_KEY;

    private FragmentManager fragmentManager;
    private Fragment fragment;
    private DialogWithDelayAndFragmentResponseFactory dialogFactory;
    private int mDelaySeconds;
    private Object data = null;

    public DialogWithDelayPresenterImpl(Fragment fragment, DialogWithDelayAndFragmentResponseFactory factory, int delaySeconds) {
        fragmentManager = fragment.getActivity().getSupportFragmentManager();
        this.fragment = fragment;
        this.dialogFactory = factory;
        this.mDelaySeconds = delaySeconds;
    }

    @Override
    public void setListener(String requestKey, Consumer<Boolean> consumer) {
        FragmentResultListener fragmentResultListener = (key, bundle) -> consumer.accept(bundle.getBoolean(resultKey));
        fragmentManager.setFragmentResultListener(requestKey, fragment, fragmentResultListener);
    }

    @Override
    public void setListener(String requestKey, BiConsumer<Boolean, Object> consumer) {
        FragmentResultListener fragmentResultListener = (key, bundle) -> consumer.accept(bundle.getBoolean(resultKey), data);
        fragmentManager.setFragmentResultListener(requestKey, fragment, fragmentResultListener);
    }

    @Override
    public void showDialog(String requestKey, String titulo, String mensaje, int iconoResId) {
        if (titulo != null && mensaje != null && fragment.getContext().getDrawable(iconoResId) != null) {
            DialogWithDelayShower dwd = dialogFactory.getDialog(iconoResId, titulo, mensaje, requestKey);
            dwd.show(fragment.getChildFragmentManager(), null);
        }
    }

    @Override
    public void showDialog(String requestKey, String titulo, String mensaje) {
        showDialog(requestKey, titulo, mensaje, R.drawable.lock_question);
    }

    @Override
    public void showDialog(String requestKey) {
        showDialog(requestKey, fragment.getString(R.string.recapacita), fragment.getString(R.string.estas_seguro_que_debes_hacer_esto));
    }

    @Override
    public void showDialog(String requestKey, Object data) {
        this.data = data;
        showDialog(requestKey);
    }
}
