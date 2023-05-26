package devs.mrp.coolyourturkey.comun;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;
import devs.mrp.coolyourturkey.configuracion.modules.beans.PreferencesSwitchConfigurer;

@ExtendWith(MockitoExtension.class)
class UiViewConfigurerTest {

    private UiViewConfigurer<Switch, PreferencesBooleanEnum> configurer;

    @Mock
    MisPreferencias prefs;
    @Mock
    ClickListenerConfigurer<Switch, PreferencesBooleanEnum> listenerFactory;
    @Mock
    View.OnClickListener listener;
    @Mock
    View parent;
    Integer resourceId = 123;
    PreferencesBooleanEnum identifier;
    List<Supplier<Boolean>> requiredFalseEnablers;
    List<Supplier<Boolean>> requiredTrueEnablers;
    @Mock
    ViewDisabler viewDisabler;
    @Mock
    Switch aSwitch;

    @BeforeEach
    void setup() {
        identifier = PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DECREASE;
        requiredTrueEnablers = Arrays.asList(() -> true, ()-> false);
        requiredFalseEnablers = Arrays.asList(()->false, ()->true, () -> false, ()-> false);
        configurer = new PreferencesSwitchConfigurer(prefs, listenerFactory, parent, resourceId, identifier, requiredFalseEnablers, requiredTrueEnablers, viewDisabler);
        when(parent.findViewById(123)).thenReturn(aSwitch);
        when(listenerFactory.getListener(PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DECREASE)).thenReturn(listener);
        when(prefs.getBoolean(PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DECREASE, PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DECREASE.getDefaultState())).thenReturn(true);
    }

    @Test
    void test() {
        Optional<Switch> viewOptional = configurer.buildElement();
        assertTrue(viewOptional.isPresent());

        ArgumentCaptor<List<Supplier<Boolean>>> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(viewDisabler, times(1)).addViewConditions(ArgumentMatchers.eq(aSwitch), listCaptor.capture());
        List<Supplier<Boolean>> list = listCaptor.getValue();
        assertEquals(6, list.size());
        // true enablers are kept as is
        assertTrue(list.get(0).get());
        assertFalse(list.get(1).get());
        // false enablers are inverted
        assertTrue(list.get(2).get());
        assertFalse(list.get(3).get());
        assertTrue(list.get(4).get());
        assertTrue(list.get(5).get());

        verify(aSwitch, times(1)).setOnClickListener(listener);
        verify(aSwitch, times(1)).setChecked(true);
    }

}