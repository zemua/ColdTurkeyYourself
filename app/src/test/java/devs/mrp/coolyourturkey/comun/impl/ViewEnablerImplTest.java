package devs.mrp.coolyourturkey.comun.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import devs.mrp.coolyourturkey.comun.ViewDisabler;

class ViewEnablerImplTest {

    private ViewDisabler viewDisabler;
    private View v1;
    private View v2;

    @BeforeEach
    void setup() {
        viewDisabler = new ViewDisablerImpl();
        v1 = mock(Switch.class);
        v2 = mock(Button.class);
    }

    @Test
    void testEvaluateConditionsEnable() {
        viewDisabler.addViewConditions(v1, Arrays.asList(()->true, ()->true));
        viewDisabler.addViewConditions(v2, Arrays.asList(()->true, ()->true));
        viewDisabler.evaluateConditions();
        verify(v1, times(1)).setEnabled(true);
        verify(v2, times(1)).setEnabled(true);
        verify(v1, times(0)).setEnabled(false);
        verify(v2, times(0)).setEnabled(false);
    }

    @Test
    void testEvaluateConditionsDisable() {
        viewDisabler.addViewConditions(v1, Arrays.asList(()->false, ()->true));
        viewDisabler.addViewConditions(v2, Arrays.asList(()->true, ()->false));
        viewDisabler.evaluateConditions();
        verify(v1, times(1)).setEnabled(false);
        verify(v2, times(1)).setEnabled(false);
        verify(v1, times(0)).setEnabled(true);
        verify(v2, times(0)).setEnabled(true);
    }

}