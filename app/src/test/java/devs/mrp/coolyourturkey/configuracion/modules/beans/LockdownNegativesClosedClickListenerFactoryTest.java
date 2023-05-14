package devs.mrp.coolyourturkey.configuracion.modules.beans;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;

import devs.mrp.coolyourturkey.comun.ClickListenerWithConfirmationFactoryTemplate;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesEnum;

@ExtendWith(MockitoExtension.class)
class LockdownNegativesClosedClickListenerFactoryTest {

    private ClickListenerWithConfirmationFactoryTemplate clickListenerFactory;
    private MisPreferencias preferencias;
    @Mock
    private Switch view;
    private DialogWithDelayPresenter dialogWithDelayPresenter;

    @BeforeEach
    void setup() {
        preferencias = mock(MisPreferencias.class);
        dialogWithDelayPresenter = mock(DialogWithDelayPresenter.class);
        clickListenerFactory = new LockdownNegativesClosedClickListenerFactory(preferencias, dialogWithDelayPresenter);
    }

    @Test
    void testActivatePropertyIsMapped() {
        View.OnClickListener listener = clickListenerFactory.getListener();
        when(view.isChecked()).thenReturn(true);
        listener.onClick(view);
        verify(preferencias, times(1)).setBoolean(PreferencesEnum.LOCKDOWN_NEGATIVE_BLOCK, true);
    }

    @Test
    void testDeactivateConfirmationIsTriggered() {
        View.OnClickListener listener = clickListenerFactory.getListener();
        when(view.isChecked()).thenReturn(false);
        listener.onClick(view);
        verify(preferencias, times(0)).setBoolean(ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(dialogWithDelayPresenter, times(1)).showDialog(PreferencesEnum.LOCKDOWN_NEGATIVE_BLOCK.getValue());

        ArgumentCaptor<Consumer<Boolean>> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(dialogWithDelayPresenter, times(1)).setListener(ArgumentMatchers.eq(PreferencesEnum.LOCKDOWN_NEGATIVE_BLOCK.getValue()), consumerCaptor.capture());

        Consumer<Boolean> consumer = consumerCaptor.getValue();
        consumer.accept(true);
        verify(preferencias, times(1)).setBoolean(PreferencesEnum.LOCKDOWN_NEGATIVE_BLOCK, false);
        verify(view, times(1)).setChecked(false);
    }

    @Test
    void wrongViewType() {
        View.OnClickListener listener = clickListenerFactory.getListener();
        Button button = mock(Button.class);
        listener.onClick(button);
        verify(preferencias, times(0)).setBoolean(PreferencesEnum.LOCKDOWN_NEGATIVE_BLOCK, true);
    }

}