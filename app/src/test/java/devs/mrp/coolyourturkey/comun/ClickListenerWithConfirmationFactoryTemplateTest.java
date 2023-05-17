package devs.mrp.coolyourturkey.comun;

import static org.junit.jupiter.api.Assertions.fail;
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

import java.util.Collections;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;

import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesEnum;
import devs.mrp.coolyourturkey.configuracion.modules.beans.ConfirmDeactivateSwitchListenerFactory;

@ExtendWith(MockitoExtension.class)
class ClickListenerWithConfirmationFactoryTemplateTest {

    @Mock
    private Switch view;

    private static Stream<Arguments> providesTestData() {
        BiFunction<MisPreferencias, DialogWithDelayPresenter, ClickListenerWithConfirmationFactoryTemplate> negativesBifunction = (p, d) -> new ConfirmDeactivateSwitchListenerFactory(p, d);
        BiFunction<MisPreferencias, DialogWithDelayPresenter, ClickListenerWithConfirmationFactoryTemplate> neutralDecreaseBifunction = (p, d) -> new ConfirmDeactivateSwitchListenerFactory(p, d);
        BiFunction<MisPreferencias, DialogWithDelayPresenter, ClickListenerWithConfirmationFactoryTemplate> disablerOnPositive = (p, d) -> new ConfirmDeactivateSwitchListenerFactory(p, d, Collections.emptyList());
        return Stream.of(
                Arguments.of(mock(MisPreferencias.class), mock(DialogWithDelayPresenter.class), negativesBifunction, Boolean.TRUE, PreferencesEnum.LOCKDOWN_NEGATIVE_BLOCK),
                Arguments.of(mock(MisPreferencias.class), mock(DialogWithDelayPresenter.class), neutralDecreaseBifunction, Boolean.TRUE, PreferencesEnum.LOCKDOWN_NEUTRAL_DECREASE),
                Arguments.of(mock(MisPreferencias.class), mock(DialogWithDelayPresenter.class), disablerOnPositive, Boolean.TRUE, PreferencesEnum.LOCKDOWN_POSITIVE_DONT_SUM)
        );
    }

    @ParameterizedTest
    @MethodSource("providesTestData")
    void testPositiveActionIsMapped(MisPreferencias preferencias,
                                    DialogWithDelayPresenter dialogWithDelayPresenter,
                                    BiFunction<MisPreferencias, DialogWithDelayPresenter, ClickListenerWithConfirmationFactoryTemplate> bifunction,
                                    Boolean isViewChecked,
                                    PreferencesEnum type) {
        ClickListenerWithConfirmationFactoryTemplate clickListenerFactory = bifunction.apply(preferencias, dialogWithDelayPresenter);
        View.OnClickListener listener = clickListenerFactory.getListener(type);
        when(view.isChecked()).thenReturn(isViewChecked);
        listener.onClick(view);
        verify(preferencias, times(1)).setBoolean(type, true);
    }

    @ParameterizedTest
    @MethodSource("providesTestData")
    void testDeactivateConfirmationIsTriggered(MisPreferencias preferencias,
                                               DialogWithDelayPresenter dialogWithDelayPresenter,
                                               BiFunction<MisPreferencias, DialogWithDelayPresenter, ClickListenerWithConfirmationFactoryTemplate> bifunction,
                                               Boolean isViewChecked,
                                               PreferencesEnum type) {
        ClickListenerWithConfirmationFactoryTemplate clickListenerFactory = bifunction.apply(preferencias, dialogWithDelayPresenter);
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
                                               BiFunction<MisPreferencias, DialogWithDelayPresenter, ClickListenerWithConfirmationFactoryTemplate> bifunction,
                                               Boolean isViewChecked,
                                               PreferencesEnum type) {
        ClickListenerWithConfirmationFactoryTemplate clickListenerFactory = bifunction.apply(preferencias, dialogWithDelayPresenter);
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
                       BiFunction<MisPreferencias, DialogWithDelayPresenter, ClickListenerWithConfirmationFactoryTemplate> bifunction,
                       Boolean isViewChecked,
                       PreferencesEnum type) {
        ClickListenerWithConfirmationFactoryTemplate clickListenerFactory = bifunction.apply(preferencias, dialogWithDelayPresenter);
        View.OnClickListener listener = clickListenerFactory.getListener(type);
        View genericView = mock(View.class);
        listener.onClick(genericView);
        verify(preferencias, times(0)).setBoolean(ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(dialogWithDelayPresenter, times(0)).showDialog(ArgumentMatchers.any());
    }

    @Test
    void testDisablerOnActivate() {
        fail("not yet implemented");
    }

    @Test
    void testEnablerOnDeactivate() {
        fail("not yet implemented");
    }

}