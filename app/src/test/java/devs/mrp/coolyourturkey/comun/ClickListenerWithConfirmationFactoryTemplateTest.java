package devs.mrp.coolyourturkey.comun;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import android.view.View;
import android.widget.Switch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;

import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;
import devs.mrp.coolyourturkey.configuracion.modules.beans.PreferencesSwitchListenerConfigurer;

@ExtendWith(MockitoExtension.class)
class ClickListenerWithConfirmationFactoryTemplateTest {

    @Mock
    private Switch view;

    private static Stream<Arguments> providesTestData() {
        BiFunction<MisPreferencias, DialogWithDelayPresenter, ClickListenerConfigurer> negativesBifunction = (p, d) -> new PreferencesSwitchListenerConfigurer(p, d, () -> {}, s->!s.isChecked());
        BiFunction<MisPreferencias, DialogWithDelayPresenter, ClickListenerConfigurer> neutralDecreaseBifunction = (p, d) -> new PreferencesSwitchListenerConfigurer(p, d, () -> {}, s->!s.isChecked());
        BiFunction<MisPreferencias, DialogWithDelayPresenter, ClickListenerConfigurer> disablerOnPositive = (p, d) -> new PreferencesSwitchListenerConfigurer(p, d, () -> {}, s->!s.isChecked());
        return Stream.of(
                Arguments.of(mock(MisPreferencias.class), mock(DialogWithDelayPresenter.class), negativesBifunction, Boolean.TRUE, PreferencesBooleanEnum.LOCKDOWN_NEGATIVE_BLOCK),
                Arguments.of(mock(MisPreferencias.class), mock(DialogWithDelayPresenter.class), neutralDecreaseBifunction, Boolean.TRUE, PreferencesBooleanEnum.LOCKDOWN_NEUTRAL_DECREASE),
                Arguments.of(mock(MisPreferencias.class), mock(DialogWithDelayPresenter.class), disablerOnPositive, Boolean.TRUE, PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DONT_SUM)
        );
    }

    @ParameterizedTest
    @MethodSource("providesTestData")
    void testPositiveActionIsMapped(MisPreferencias preferencias,
                                    DialogWithDelayPresenter dialogWithDelayPresenter,
                                    BiFunction<MisPreferencias, DialogWithDelayPresenter, ClickListenerConfigurer> bifunction,
                                    Boolean isViewChecked,
                                    PreferencesBooleanEnum type) {
        ClickListenerConfigurer clickListenerFactory = bifunction.apply(preferencias, dialogWithDelayPresenter);
        View.OnClickListener listener = clickListenerFactory.getListener(type);
        when(view.isChecked()).thenReturn(isViewChecked);
        listener.onClick(view);
        verify(preferencias, times(1)).setBoolean(type, true);
    }

    @ParameterizedTest
    @MethodSource("providesTestData")
    void testDeactivateConfirmationIsTriggered(MisPreferencias preferencias,
                                               DialogWithDelayPresenter dialogWithDelayPresenter,
                                               BiFunction<MisPreferencias, DialogWithDelayPresenter, ClickListenerConfigurer> bifunction,
                                               Boolean isViewChecked,
                                               PreferencesBooleanEnum type) {
        ClickListenerConfigurer clickListenerFactory = bifunction.apply(preferencias, dialogWithDelayPresenter);
        View.OnClickListener listener = clickListenerFactory.getListener(type);
        when(view.isChecked()).thenReturn(!isViewChecked);
        listener.onClick(view);
        verify(preferencias, times(0)).setBoolean(ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(dialogWithDelayPresenter, times(1)).showDialog(type.getValue());

        ArgumentCaptor<Consumer<Boolean>> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(dialogWithDelayPresenter, times(1)).setListener(ArgumentMatchers.eq(type.getValue()), consumerCaptor.capture());

        Consumer<Boolean> consumer = consumerCaptor.getValue();
        consumer.accept(true);
        verify(preferencias, times(1)).setBoolean(type, !isViewChecked);
        verify(view, times(1)).setChecked(!isViewChecked);
    }

    @ParameterizedTest
    @MethodSource("providesTestData")
    void testDeactivateCancelledSwitchGoesBack(MisPreferencias preferencias,
                                               DialogWithDelayPresenter dialogWithDelayPresenter,
                                               BiFunction<MisPreferencias, DialogWithDelayPresenter, ClickListenerConfigurer> bifunction,
                                               Boolean isViewChecked,
                                               PreferencesBooleanEnum type) {
        ClickListenerConfigurer clickListenerFactory = bifunction.apply(preferencias, dialogWithDelayPresenter);
        View.OnClickListener listener = clickListenerFactory.getListener(type);
        when(view.isChecked()).thenReturn(!isViewChecked);
        listener.onClick(view);
        verify(preferencias, times(0)).setBoolean(ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(dialogWithDelayPresenter, times(1)).showDialog(type.getValue());

        ArgumentCaptor<Consumer<Boolean>> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(dialogWithDelayPresenter, times(1)).setListener(ArgumentMatchers.eq(type.getValue()), consumerCaptor.capture());

        Consumer<Boolean> consumer = consumerCaptor.getValue();
        consumer.accept(false);
        verify(preferencias, times(0)).setBoolean(ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(view, times(1)).setChecked(isViewChecked);
    }

    @ParameterizedTest
    @MethodSource("providesTestData")
    void wrongViewType(MisPreferencias preferencias,
                       DialogWithDelayPresenter dialogWithDelayPresenter,
                       BiFunction<MisPreferencias, DialogWithDelayPresenter, ClickListenerConfigurer> bifunction,
                       Boolean isViewChecked,
                       PreferencesBooleanEnum type) {
        ClickListenerConfigurer clickListenerFactory = bifunction.apply(preferencias, dialogWithDelayPresenter);
        View.OnClickListener listener = clickListenerFactory.getListener(type);
        View genericView = mock(View.class);
        listener.onClick(genericView);
        verify(preferencias, times(0)).setBoolean(ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(dialogWithDelayPresenter, times(0)).showDialog(ArgumentMatchers.any());
    }

    @Test
    void testActionOnDeactivate() {
        MisPreferencias preferencias = mock(MisPreferencias.class);
        DialogWithDelayPresenter dialogWithDelayPresenter = mock(DialogWithDelayPresenter.class);
        View v1 = mock(View.class);
        View v2 = mock(View.class);
        Runnable action1 = () -> {v1.setEnabled(true);v2.setEnabled(true);};

        ClickListenerConfigurer clickListenerFactory = new PreferencesSwitchListenerConfigurer(preferencias, dialogWithDelayPresenter, action1, s->!s.isChecked());
        View.OnClickListener listener = clickListenerFactory.getListener(PreferencesBooleanEnum.LOCKDOWN_NEGATIVE_BLOCK);
        when(view.isChecked()).thenReturn(false);
        listener.onClick(view);

        ArgumentCaptor<Consumer<Boolean>> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(dialogWithDelayPresenter, times(1)).setListener(ArgumentMatchers.any(), consumerCaptor.capture());
        Consumer<Boolean> consumer = consumerCaptor.getValue();

        verify(v1, times(0)).setEnabled(true);
        verify(v2, times(0)).setEnabled(true);

        consumer.accept(true);

        verify(v1, times(1)).setEnabled(true);
        verify(v2, times(1)).setEnabled(true);
    }

    @Test
    void testActionOnActivate() {
        MisPreferencias preferencias = mock(MisPreferencias.class);
        DialogWithDelayPresenter dialogWithDelayPresenter = mock(DialogWithDelayPresenter.class);
        View v1 = mock(View.class);
        View v2 = mock(View.class);
        Runnable action = () -> {v1.setEnabled(false);v2.setEnabled(false);};

        ClickListenerConfigurer clickListenerFactory = new PreferencesSwitchListenerConfigurer(preferencias, dialogWithDelayPresenter, action, s->!s.isChecked());
        View.OnClickListener listener = clickListenerFactory.getListener(PreferencesBooleanEnum.LOCKDOWN_NEGATIVE_BLOCK);
        when(view.isChecked()).thenReturn(true);

        verify(v1, times(0)).setEnabled(true);
        verify(v2, times(0)).setEnabled(true);

        listener.onClick(view);

        verify(v1, times(1)).setEnabled(false);
        verify(v2, times(1)).setEnabled(false);
    }

}