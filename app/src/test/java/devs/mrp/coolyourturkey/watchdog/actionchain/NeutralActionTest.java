package devs.mrp.coolyourturkey.watchdog.actionchain;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

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
class NeutralActionTest {

    private NeutralAction neutralAction;

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
    @Spy
    private WatchDogDataTest data;

    @BeforeEach
    void setup() {
        neutralAction = new NeutralAction();

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
    }

    @Test
    void testNormalConditions() throws Exception {
        when(ultimoContador.getAcumulado()).thenReturn(456L);
        when(watchdogHandler.getNotificacionNeutra(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(notification);
        when(toqueDeQuedaHandler.isToqueDeQueda()).thenReturn(false);

        neutralAction.handle(data);
        verify(data, times(1)).setTiempoAcumulado(456L);
        verify(timePusher, times(0)).push(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        verify(timeLogHandler, times(1)).insertTimeNeutralApp("some package", 321L);
        verify(data, times(1)).setNeedToBlock(false);
    }

    @Test
    void testToqueDeQuedaButNotDecreasingOption() throws Exception {
        when(ultimoContador.getAcumulado()).thenReturn(456L);
        when(watchdogHandler.getNotificacionNeutra(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(notification);
        when(toqueDeQuedaHandler.isToqueDeQueda()).thenReturn(true);
        when(misPreferencias.getBoolean(PreferencesBooleanEnum.LOCKDOWN_NEUTRAL_DECREASE, PreferencesBooleanEnum.LOCKDOWN_NEUTRAL_DECREASE.getDefaultState())).thenReturn(false);

        neutralAction.handle(data);
        verify(data, times(1)).setTiempoAcumulado(456L);
        verify(timePusher, times(0)).push(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        verify(timeLogHandler, times(1)).insertTimeNeutralApp("some package", 321L);
        verify(data, times(1)).setNeedToBlock(false);
    }

    @Test
    void testToqueDeQuedaAndDecreasingPoints() throws Exception {
        when(ultimoContador.getAcumulado()).thenReturn(456L);
        when(watchdogHandler.getNotificacionNeutra(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(notification);
        when(toqueDeQuedaHandler.isToqueDeQueda()).thenReturn(true);
        when(misPreferencias.getBoolean(PreferencesBooleanEnum.LOCKDOWN_NEUTRAL_DECREASE, PreferencesBooleanEnum.LOCKDOWN_NEUTRAL_DECREASE.getDefaultState())).thenReturn(true);

        neutralAction.handle(data);
        verify(data, times(1)).setTiempoAcumulado(456L - (321L*4));
        verify(timePusher, times(1)).push(987L, 456L - (321L*4));
        verify(timeLogHandler, times(1)).insertTimeNeutralApp("some package", 321L);
        verify(data, times(1)).setNeedToBlock(false);
    }

    private class WatchDogDataTest extends WatchDogData {
        public WatchDogDataTest() {
            super(mock(WatchdogService.class));
        }
    }

}