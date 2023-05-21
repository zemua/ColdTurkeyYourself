package devs.mrp.coolyourturkey.watchdog.actionchain;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Application;
import android.app.Notification;
import android.content.res.Resources;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;

import devs.mrp.coolyourturkey.comun.GenericTimedToaster;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;
import devs.mrp.coolyourturkey.configuracion.ToqueDeQuedaHandler;
import devs.mrp.coolyourturkey.databaseroom.contador.Contador;
import devs.mrp.coolyourturkey.watchdog.TimePusher;
import devs.mrp.coolyourturkey.watchdog.WatchDogData;
import devs.mrp.coolyourturkey.watchdog.WatchdogHandler;
import devs.mrp.coolyourturkey.watchdog.WatchdogService;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;

@ExtendWith(MockitoExtension.class)
class PositiveActionTest {

    private PositiveAction positiveAction;

    @Mock
    private TimePusher timePusher;
    @Mock
    private TimeLogHandler timeLogHandler;
    @Mock
    private Contador ultimoContador;
    @Mock
    private WatchdogHandler watchdogHandler;
    @Mock
    private Notification notification;
    @Mock
    private ToqueDeQuedaHandler toqueDeQuedaHandler;
    @Mock
    private MisPreferencias misPreferencias;
    @Mock
    private GenericTimedToaster genericTimedToaster;
    @Mock
    private WatchdogService watchdogService;
    @Mock
    private Application application;
    @Mock
    private Resources resources;
    @Spy
    private WatchDogDataTest data;

    @BeforeEach
    void setup() {
        positiveAction = new PositiveAction();

        data.setTimePusher(timePusher);
        data.setTimeLogHandler(timeLogHandler);
        data.setUltimoContador(ultimoContador);
        data.setPackageName("some package");
        data.setMilisTranscurridos(321L);
        data.setWatchDogHandler(watchdogHandler);
        data.setToquedeQuedaHandler(toqueDeQuedaHandler);
        data.setMisPreferencias(misPreferencias);
        data.setNow(987L);
        data.setProporcion(4L);
        data.setConditionToaster(genericTimedToaster);
    }

    @Test
    void testNormalConditions() {
        when(ultimoContador.getAcumulado()).thenReturn(456L);
        when(toqueDeQuedaHandler.isToqueDeQueda()).thenReturn(false);
        ArgumentCaptor<Consumer<Boolean>> captor = ArgumentCaptor.forClass(Consumer.class);

        positiveAction.handle(data);

        verify(timeLogHandler, times(1)).onAllConditionsMet(ArgumentMatchers.anyString(), captor.capture());
        captor.getValue().accept(true);

        verify(data, times(1)).setTiempoAcumulado(456L + 321L);
        verify(timePusher, times(1)).push(987L, 456L + 321L);
        verify(timeLogHandler, times(1)).insertTimeGoodApp("some package", 321L);
        verify(data, times(1)).setNeedToBlock(false);
    }

    @Test
    void testConditionsNotMet() {
        when(ultimoContador.getAcumulado()).thenReturn(456L);
        when(toqueDeQuedaHandler.isToqueDeQueda()).thenReturn(false);
        when(watchdogService.getApplication()).thenReturn(application);
        when(application.getResources()).thenReturn(resources);
        when(resources.getString(ArgumentMatchers.anyInt())).thenReturn("conditions not met");
        ArgumentCaptor<Consumer<Boolean>> captor = ArgumentCaptor.forClass(Consumer.class);

        positiveAction.handle(data);

        verify(timeLogHandler, times(1)).onAllConditionsMet(ArgumentMatchers.anyString(), captor.capture());
        captor.getValue().accept(false);

        verify(data, times(1)).setTiempoAcumulado(456L);
        verify(timePusher, times(0)).push(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        verify(timeLogHandler, times(1)).insertTimeGoodApp("some package", 321L);
        verify(data, times(1)).setNeedToBlock(false);
    }

    @Test
    void testToqueDeQuedaNoOptionsSelected() {
        when(ultimoContador.getAcumulado()).thenReturn(456L);
        when(toqueDeQuedaHandler.isToqueDeQueda()).thenReturn(true);
        when(misPreferencias.getBoolean(PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DECREASE, PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DECREASE.getDefaultState())).thenReturn(false);
        when(misPreferencias.getBoolean(PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DONT_SUM, PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DONT_SUM.getDefaultState())).thenReturn(false);
        ArgumentCaptor<Consumer<Boolean>> captor = ArgumentCaptor.forClass(Consumer.class);

        positiveAction.handle(data);

        verify(timeLogHandler, times(1)).onAllConditionsMet(ArgumentMatchers.anyString(), captor.capture());
        captor.getValue().accept(true);

        verify(data, times(1)).setTiempoAcumulado(456L + 321L);
        verify(timePusher, times(1)).push(987L, 456L + 321L);
        verify(timeLogHandler, times(1)).insertTimeGoodApp("some package", 321L);
        verify(data, times(1)).setNeedToBlock(false);
    }

    @Test
    void testToqueDeQuedaDontSum() {
        when(ultimoContador.getAcumulado()).thenReturn(456L);
        when(toqueDeQuedaHandler.isToqueDeQueda()).thenReturn(true);
        when(misPreferencias.getBoolean(PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DECREASE, PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DECREASE.getDefaultState())).thenReturn(false);
        when(misPreferencias.getBoolean(PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DONT_SUM, PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DONT_SUM.getDefaultState())).thenReturn(true);

        positiveAction.handle(data);

        verify(data, times(1)).setTiempoAcumulado(456L);
        verify(timePusher, times(0)).push(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        verify(timeLogHandler, times(1)).insertTimeGoodApp("some package", 321L);
        verify(data, times(1)).setNeedToBlock(false);
    }

    @Test
    void testToqueDeQuedaDecrease() {
        when(ultimoContador.getAcumulado()).thenReturn(456L);
        when(toqueDeQuedaHandler.isToqueDeQueda()).thenReturn(true);
        when(misPreferencias.getBoolean(PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DECREASE, PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DECREASE.getDefaultState())).thenReturn(true);

        positiveAction.handle(data);

        verify(data, times(1)).setTiempoAcumulado(456L - (321L*4));
        verify(timePusher, times(1)).push(987L, 456L - (321L*4));
        verify(timeLogHandler, times(1)).insertTimeGoodApp("some package", 321L);
        verify(data, times(1)).setNeedToBlock(false);
    }

    private class WatchDogDataTest extends WatchDogData {
        public WatchDogDataTest() {
            super(watchdogService);
        }
    }

}