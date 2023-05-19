package devs.mrp.coolyourturkey.comun;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import android.widget.Switch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;
import devs.mrp.coolyourturkey.configuracion.modules.builder.ClickListenerConfigurerBuilder;
import devs.mrp.coolyourturkey.configuracion.modules.builder.impl.SwitchClickListenerBuilder;

@ExtendWith(MockitoExtension.class)
class UiViewConfigurerTest {

    private ClickListenerConfigurerBuilder<Switch, MisPreferencias, PreferencesBooleanEnum> clickListenerConfigurerBuilder;

    @Mock
    MisPreferencias preferencias;
    @Mock
    DialogWithDelayPresenter dialogWithDelayPresenter;
    @Mock
    Runnable onStateChangeAction;

    @BeforeEach
    void setup() {
        clickListenerConfigurerBuilder = new SwitchClickListenerBuilder();
        clickListenerConfigurerBuilder.preferencias(preferencias);
        clickListenerConfigurerBuilder.dialogWithDelayPresenter(dialogWithDelayPresenter);
    }

    @Test
    void testConfigureCorrectly() {
        ClickListenerConfigurer<Switch, PreferencesBooleanEnum> result = clickListenerConfigurerBuilder.build();
        assertNotNull(result);
    }

    @Test
    void testConfigureWithNullPreferences() {
        assertThrows(RuntimeException.class, () -> clickListenerConfigurerBuilder.preferencias(null).build());
    }

    @Test
    void testConfigureWithNullDialog() {
        assertThrows(RuntimeException.class, () -> clickListenerConfigurerBuilder.dialogWithDelayPresenter(null).build());
    }

    @Test
    void testKeepsChangeAction() throws NoSuchFieldException, IllegalAccessException {
        ClickListenerConfigurer<Switch, PreferencesBooleanEnum> result = clickListenerConfigurerBuilder.onStateChangeAction(onStateChangeAction).build();
        Field field = result.getClass().getDeclaredField("doOnChangeAction");
        field.setAccessible(true);
        Runnable value = (Runnable) field.get(result);
        assertEquals(onStateChangeAction, value);
    }

    @Test
    void createsEmptyChangeActionIfNotProvided() throws NoSuchFieldException, IllegalAccessException {
        ClickListenerConfigurer<Switch, PreferencesBooleanEnum> result = clickListenerConfigurerBuilder.onStateChangeAction(null).build();
        Field field = result.getClass().getDeclaredField("doOnChangeAction");
        field.setAccessible(true);
        Runnable value = (Runnable) field.get(result);
        assertNotNull(value);
    }

}