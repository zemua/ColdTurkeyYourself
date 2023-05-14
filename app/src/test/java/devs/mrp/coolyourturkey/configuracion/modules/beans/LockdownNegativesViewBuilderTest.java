package devs.mrp.coolyourturkey.configuracion.modules.beans;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.View;
import android.widget.Switch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import devs.mrp.coolyourturkey.comun.ClickListenerWithConfirmationFactoryTemplate;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesEnum;

@ExtendWith(MockitoExtension.class)
class LockdownNegativesViewBuilderTest {

    private LockdownNegativesViewBuilder builder;

    @Mock
    private View parent;
    @Mock
    private Switch aSwitch;
    @Mock
    private ClickListenerWithConfirmationFactoryTemplate<Switch> clickListenerFactoryProvider;
    @Mock
    private View.OnClickListener clickListener;
    @Mock
    private MisPreferencias preferencias;

    @BeforeEach
    void setup() {
        builder = new LockdownNegativesViewBuilder(preferencias, clickListenerFactoryProvider);
    }

    @Test
    void testBuildElement() {
        when(parent.findViewById(123)).thenReturn(aSwitch);
        when(clickListenerFactoryProvider.getListener()).thenReturn(clickListener);
        when(preferencias.getBoolean(PreferencesEnum.LOCKDOWN_NEGATIVE_BLOCK, true)).thenReturn(true);

        builder.buildElement(parent, 123);

        verify(aSwitch, times(1)).setOnClickListener(clickListener);
        verify(preferencias, times(1)).getBoolean(PreferencesEnum.LOCKDOWN_NEGATIVE_BLOCK, true);
        verify(aSwitch, times(1)).setChecked(true);
    }

}