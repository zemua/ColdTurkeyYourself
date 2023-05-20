package devs.mrp.coolyourturkey.comun;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import android.view.View;
import android.widget.Switch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;

import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;
import devs.mrp.coolyourturkey.configuracion.modules.beans.PreferencesSwitchListenerConfigurer;

@ExtendWith(MockitoExtension.class)
class ClickListenerConfigurerTest {

    private ClickListenerConfigurer<Switch, PreferencesBooleanEnum> configurer;

    @Mock
    MisPreferencias preferencias;
    @Mock
    DialogWithDelayPresenter dialogWithDelayPresenter;
    @Mock
    Runnable doOnChangeAction;

    @BeforeEach
    void setup() {
        configurer = new PreferencesSwitchListenerConfigurer(preferencias, dialogWithDelayPresenter, doOnChangeAction);
    }

    @Test
    void testNegativeAction() {
        View.OnClickListener result = configurer.getListener(PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DECREASE);
        Switch aSwitch = mock(Switch.class);
        when(aSwitch.isChecked()).thenReturn(false);
        result.onClick(aSwitch);
        ArgumentCaptor<Consumer<Boolean>> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(dialogWithDelayPresenter, times(1)).setListener(ArgumentMatchers.eq(PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DECREASE.getValue()),captor.capture());
        Consumer<Boolean> consumer = captor.getValue();
        verifyNoInteractions(doOnChangeAction);
        verifyNoInteractions(preferencias);
        verify(aSwitch, never()).setChecked(ArgumentMatchers.anyBoolean());
        consumer.accept(false);
        verifyNoInteractions(doOnChangeAction);
        verifyNoInteractions(preferencias);
        verify(aSwitch, times(1)).setChecked(true);
        verify(aSwitch, times(0)).setChecked(false);
        consumer.accept(true);
        verify(doOnChangeAction, times(1)).run();
        verify(preferencias, times(1)).setBoolean(PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DECREASE, false);
        verify(aSwitch, times(1)).setChecked(false);
    }

    @Test
    void testPositiveAction() {
        View.OnClickListener result = configurer.getListener(PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DECREASE);
        Switch aSwitch = mock(Switch.class);
        when(aSwitch.isChecked()).thenReturn(true);
        result.onClick(aSwitch);
        verify(doOnChangeAction, times(1)).run();
        verify(preferencias, times(1)).setBoolean(PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DECREASE, true);
        verify(aSwitch, times(0)).setChecked(ArgumentMatchers.anyBoolean());
    }

}