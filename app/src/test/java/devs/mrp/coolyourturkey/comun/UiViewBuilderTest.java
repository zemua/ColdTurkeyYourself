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

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import devs.mrp.coolyourturkey.comun.impl.ViewDisablerImpl;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;
import devs.mrp.coolyourturkey.configuracion.modules.beans.PreferencesSwitchConfigurer;

@ExtendWith(MockitoExtension.class)
class UiViewBuilderTest {

    private static View parent;
    @Mock
    private Switch aSwitch;
    @Mock
    private View.OnClickListener clickListener;

    private static Stream<Arguments> providesTestData() {
        parent = mock(View.class);
        BiFunction<MisPreferencias, ClickListenerConfigurer, UiViewConfigurer> negativesBifunction = (p, d) -> new PreferencesSwitchConfigurer(p, d, parent, 123, PreferencesBooleanEnum.LOCKDOWN_NEGATIVE_BLOCK, Arrays.asList(()->false),Arrays.asList(()->true),new ViewDisablerImpl());
        BiFunction<MisPreferencias, ClickListenerConfigurer, UiViewConfigurer> negativesBifunction2 = (p, d) -> new PreferencesSwitchConfigurer(p, d, parent, 123, PreferencesBooleanEnum.LOCKDOWN_NEGATIVE_BLOCK, Arrays.asList(()->false),Arrays.asList(()->false),new ViewDisablerImpl());
        BiFunction<MisPreferencias, ClickListenerConfigurer, UiViewConfigurer> neutralDecreaseBifunction = (p, d) -> new PreferencesSwitchConfigurer(p, d, parent, 123, PreferencesBooleanEnum.LOCKDOWN_NEUTRAL_DECREASE, Arrays.asList(()->true),Arrays.asList(()->true),new ViewDisablerImpl());
        return Stream.of(
                Arguments.of(mock(MisPreferencias.class), mock(ClickListenerConfigurer.class), negativesBifunction, Boolean.TRUE, PreferencesBooleanEnum.LOCKDOWN_NEGATIVE_BLOCK),
                Arguments.of(mock(MisPreferencias.class), mock(ClickListenerConfigurer.class), negativesBifunction2, Boolean.TRUE, PreferencesBooleanEnum.LOCKDOWN_NEGATIVE_BLOCK),
                Arguments.of(mock(MisPreferencias.class), mock(ClickListenerConfigurer.class), neutralDecreaseBifunction, Boolean.TRUE, PreferencesBooleanEnum.LOCKDOWN_NEUTRAL_DECREASE)
        );
    }

    @ParameterizedTest
    @MethodSource("providesTestData")
    void testBuildElement(MisPreferencias preferencias,
                          ClickListenerConfigurer clickListenerFactoryProvider,
                          BiFunction<MisPreferencias, ClickListenerConfigurer, UiViewConfigurer> bifunc,
                          Boolean defaultChecked,
                          PreferencesBooleanEnum type) {
        UiViewConfigurer builder = bifunc.apply(preferencias, clickListenerFactoryProvider);
        when(parent.findViewById(123)).thenReturn(aSwitch);
        when(clickListenerFactoryProvider.getListener(type)).thenReturn(clickListener);
        when(preferencias.getBoolean(type, defaultChecked)).thenReturn(defaultChecked);

        builder.buildElement();

        verify(aSwitch, times(1)).setOnClickListener(clickListener);
        verify(preferencias, times(1)).getBoolean(type, defaultChecked);
        verify(aSwitch, times(1)).setChecked(defaultChecked);
    }

}