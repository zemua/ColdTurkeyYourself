package devs.mrp.coolyourturkey.comun;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.View;
import android.widget.Switch;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.BiFunction;
import java.util.stream.Stream;

import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesEnum;
import devs.mrp.coolyourturkey.configuracion.modules.beans.LockdownNegativesViewBuilder;
import devs.mrp.coolyourturkey.configuracion.modules.beans.LockdownNeutralDecreaseViewBuilder;

@ExtendWith(MockitoExtension.class)
class UiViewBuilderTest {

    @Mock
    private View parent;
    @Mock
    private Switch aSwitch;
    @Mock
    private View.OnClickListener clickListener;

    private static Stream<Arguments> providesTestData() {
        BiFunction<MisPreferencias, ClickListenerWithConfirmationFactoryTemplate, UiViewBuilder> negativesClosed = (p, d) -> new LockdownNegativesViewBuilder(p, d);
        BiFunction<MisPreferencias, ClickListenerWithConfirmationFactoryTemplate, UiViewBuilder> neutralDecrease = (p, d) -> new LockdownNeutralDecreaseViewBuilder(p, d);
        return Stream.of(
                Arguments.of(mock(MisPreferencias.class), mock(ClickListenerWithConfirmationFactoryTemplate.class), negativesClosed, Boolean.TRUE, PreferencesEnum.LOCKDOWN_NEGATIVE_BLOCK),
                Arguments.of(mock(MisPreferencias.class), mock(ClickListenerWithConfirmationFactoryTemplate.class), neutralDecrease, Boolean.TRUE, PreferencesEnum.LOCKDOWN_NEUTRAL_DECREASE)
        );
    }

    @ParameterizedTest
    @MethodSource("providesTestData")
    void testBuildElement(MisPreferencias preferencias,
                          ClickListenerWithConfirmationFactoryTemplate clickListenerFactoryProvider,
                          BiFunction<MisPreferencias, ClickListenerWithConfirmationFactoryTemplate, UiViewBuilder> bifunc,
                          Boolean defaultChecked,
                          PreferencesEnum type) {
        UiViewBuilder builder = bifunc.apply(preferencias, clickListenerFactoryProvider);
        when(parent.findViewById(123)).thenReturn(aSwitch);
        when(clickListenerFactoryProvider.getListener()).thenReturn(clickListener);
        when(preferencias.getBoolean(type, defaultChecked)).thenReturn(defaultChecked);

        builder.buildElement(parent, 123);

        verify(aSwitch, times(1)).setOnClickListener(clickListener);
        verify(preferencias, times(1)).getBoolean(type, defaultChecked);
        verify(aSwitch, times(1)).setChecked(defaultChecked);
    }

}