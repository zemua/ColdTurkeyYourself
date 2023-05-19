package devs.mrp.coolyourturkey.configuracion.modules.builder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.view.View;
import android.widget.Switch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import devs.mrp.coolyourturkey.comun.ClickListenerConfigurer;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.comun.ViewDisabler;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;
import devs.mrp.coolyourturkey.configuracion.modules.builder.impl.PreferencesSwitchConfigurerBuilder;

@ExtendWith(MockitoExtension.class)
class ViewConfigurerBuilderTest {

    private ViewConfigurerBuilder configurerBuilder;

    @Mock
    MisPreferencias preferencias;
    @Mock
    ClickListenerConfigurerBuilder<Switch, MisPreferencias, PreferencesBooleanEnum> clickListenerFactoryBuilder;
    @Mock
    ClickListenerConfigurer<Switch, PreferencesBooleanEnum> clickListenerFactory;
    @Mock
    DialogWithDelayPresenter dialogWithDelayPresenter;
    @Mock
    View parent;
    @Mock
    Switch aSwitch;
    @Mock
    Switch targetSwitch;

    @BeforeEach
    void setup() {
        configurerBuilder = new PreferencesSwitchConfigurerBuilder(preferencias, clickListenerFactoryBuilder, dialogWithDelayPresenter);
    }

    @Test
    void configureCorreclty() {
        when(clickListenerFactoryBuilder.dialogWithDelayPresenter(ArgumentMatchers.any())).thenReturn(clickListenerFactoryBuilder);
        when(clickListenerFactoryBuilder.preferencias(ArgumentMatchers.any())).thenReturn(clickListenerFactoryBuilder);
        when(clickListenerFactoryBuilder.onStateChangeAction(ArgumentMatchers.any())).thenReturn(clickListenerFactoryBuilder);
        when(clickListenerFactoryBuilder.build()).thenReturn(clickListenerFactory);
        when(clickListenerFactory.getListener(PreferencesBooleanEnum.LOCKDOWN_NEUTRAL_DECREASE)).thenReturn(aView -> {targetSwitch.setEnabled(false);});
        when(parent.findViewById(ArgumentMatchers.anyInt())).thenReturn(aSwitch);
        Optional<Switch> sw = configurerBuilder
                .parentElement(parent)
                .repositoryIdentifier(PreferencesBooleanEnum.LOCKDOWN_NEUTRAL_DECREASE)
                .viewResourceId(123)
                .configure()
                .buildElement();
        assertTrue(sw.isPresent());
    }

    @Test
    void configureWithNulls() {
        assertThrows(RuntimeException.class, () ->configurerBuilder.configure().buildElement());
    }

    @Test
    void misconfiguredDisabler() {
        when(clickListenerFactoryBuilder.dialogWithDelayPresenter(ArgumentMatchers.any())).thenReturn(clickListenerFactoryBuilder);
        when(clickListenerFactoryBuilder.preferencias(ArgumentMatchers.any())).thenReturn(clickListenerFactoryBuilder);
        when(clickListenerFactoryBuilder.onStateChangeAction(ArgumentMatchers.any())).thenReturn(clickListenerFactoryBuilder);
        when(clickListenerFactoryBuilder.build()).thenReturn(clickListenerFactory);
        when(clickListenerFactory.getListener(PreferencesBooleanEnum.LOCKDOWN_NEUTRAL_DECREASE)).thenReturn(aView -> {targetSwitch.setEnabled(false);});
        when(parent.findViewById(ArgumentMatchers.anyInt())).thenReturn(aSwitch);
        Optional<Switch> sw = configurerBuilder
                .parentElement(parent)
                .repositoryIdentifier(PreferencesBooleanEnum.LOCKDOWN_NEUTRAL_DECREASE)
                .viewResourceId(123)
                .configure()
                .buildElement();
        assertTrue(sw.isPresent());
        configurerBuilder.addRequiredTrueEnablers(() -> true);
        assertThrows(RuntimeException.class, () -> configurerBuilder.configure().buildElement());
        configurerBuilder.viewDisabler(mock(ViewDisabler.class));
        Optional<Switch> sw2 = configurerBuilder.configure().buildElement();
        assertTrue(sw2.isPresent());
    }
}