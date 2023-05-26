package devs.mrp.coolyourturkey.watchdog.actionchain.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import devs.mrp.coolyourturkey.databaseroom.contador.Contador;
import devs.mrp.coolyourturkey.watchdog.TimePusherInterface;
import devs.mrp.coolyourturkey.watchdog.WatchDogData;
import devs.mrp.coolyourturkey.watchdog.WatchdogService;
import devs.mrp.coolyourturkey.watchdog.actionchain.PointsUpdater;

@ExtendWith(MockitoExtension.class)
class PointsUpdaterImplTest {

    private PointsUpdater pointsUpdater;

    @Spy
    private WatchDogDataTest data;

    @Mock
    private TimePusherInterface timePusher;
    @Mock
    private Contador ultimoContador;
    @Mock
    private WatchdogService watchdogService;

    @BeforeEach
    void setup() {
        pointsUpdater = new PointsUpdaterImpl();

        data.setTimePusher(timePusher);
        data.setUltimoContador(ultimoContador);
        data.setMilisTranscurridos(123L);
        data.setNow(333L);
        data.setProporcion(4);

        when(ultimoContador.getAcumulado()).thenReturn(555L);
    }

    @Test
    void decreasePoints() {
        pointsUpdater.decreasePoints(data);

        verify(data, times(1)).setTiempoAcumulado(555 - (123L * 4));
        verify(timePusher, times(1)).push(333L, 555 - (123L * 4));
    }

    @Test
    void increasePoints() {
        pointsUpdater.increasePoints(data);

        verify(data, times(1)).setTiempoAcumulado(555 + 123L);
        verify(timePusher, times(1)).push(333L, 555 + 123L);
    }

    @Test
    void keepPoints() {
        pointsUpdater.keepPoints(data);

        verify(data, times(1)).setTiempoAcumulado(555L);
        verify(timePusher, times(0)).push(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
    }

    private class WatchDogDataTest extends WatchDogData {
        public WatchDogDataTest() {
            super(watchdogService);
        }
    }

}