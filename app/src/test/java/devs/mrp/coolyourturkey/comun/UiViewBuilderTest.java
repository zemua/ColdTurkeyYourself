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
import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;
import devs.mrp.coolyourturkey.configuracion.modules.beans.ConfirmDeactivateSwitchViewBuilder;

@ExtendWith(MockitoExtension.class)
class UiViewBuilderTest {

    @Mock
    private View parent;
    @Mock
    private Switch aSwitch;
    @Mock
    private View.OnClickListener clickListener;

    private static Stream<Arguments> providesTestData() {
        BiFunction<MisPreferencias, ClickListenerWithConfirmationFactoryTemplate, UiViewBuilder> negativesBifunction = (p, d) -> new ConfirmDeactivateSwitchViewBuilder(p, d, true);
        BiFunction<MisPreferencias, ClickListenerWithConfirmationFactoryTemplate, UiViewBuilder> neutralDecreaseBifunction = (p, d) -> new ConfirmDeactivateSwitchViewBuilder(p, d, true);
        return Stream.of(
                Arguments.of(mock(MisPreferencias.class), mock(ClickListenerWithConfirmationFactoryTemplate.class), negativesBifunction, Boolean.TRUE, PreferencesBooleanEnum.LOCKDOWN_NEGATIVE_BLOCK),
                Arguments.of(mock(MisPreferencias.class), mock(ClickListenerWithConfirmationFactoryTemplate.class), neutralDecreaseBifunction, Boolean.TRUE, PreferencesBooleanEnum.LOCKDOWN_NEUTRAL_DECREASE)
        );
    }

    @ParameterizedTest
    @MethodSource("providesTestData")
    void testBuildElement(MisPreferencias preferencias,
                          ClickListenerWithConfirmationFactoryTemplate clickListenerFactoryProvider,
                          BiFunction<MisPreferencias, ClickListenerWithConfirmationFactoryTemplate, UiViewBuilder> bifunc,
                          Boolean defaultChecked,
                          PreferencesBooleanEnum type) {
        UiViewBuilder builder = bifunc.apply(preferencias, clickListenerFactoryProvider);
        when(parent.findViewById(123)).thenReturn(aSwitch);
        when(clickListenerFactoryProvider.getListener(type)).thenReturn(clickListener);
        when(preferencias.getBoolean(type, defaultChecked)).thenReturn(defaultChecked);

        builder.buildElement(parent, 123, type);

        verify(aSwitch, times(1)).setOnClickListener(clickListener);
        verify(preferencias, times(1)).getBoolean(type, defaultChecked);
        verify(aSwitch, times(1)).setChecked(defaultChecked);
    }

}