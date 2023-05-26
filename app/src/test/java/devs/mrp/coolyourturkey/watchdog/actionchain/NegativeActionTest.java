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
import devs.mrp.coolyourturkey.databaseroom.grupo.ElementAndGroupFacade;
import devs.mrp.coolyourturkey.grupos.packagemapper.PackageConditionsChecker;
import devs.mrp.coolyourturkey.watchdog.ScreenBlock;
import devs.mrp.coolyourturkey.watchdog.TimePusher;
import devs.mrp.coolyourturkey.watchdog.WatchDogData;
import devs.mrp.coolyourturkey.watchdog.WatchdogHandler;
import devs.mrp.coolyourturkey.watchdog.WatchdogService;
import devs.mrp.coolyourturkey.watchdog.actionchain.impl.PointsUpdaterImpl;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;

@ExtendWith(MockitoExtension.class)
class NegativeActionTest {

    private NegativeAction negativeAction;

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
    @Mock
    private ElementAndGroupFacade elementAndGroupFacade;
    @Mock
    private PackageConditionsChecker packageConditionsChecker;
    @Mock
    private ScreenBlock screenBlock;
    @Spy
    private WatchDogDataTest data;

    @BeforeEach
    void setup() {
        negativeAction = new NegativeAction(new PointsUpdaterImpl());

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
        data.setTiempoImportado(0L);
        data.setElementAndGroupFacade(elementAndGroupFacade);
        data.setPackageConditionsChecker(packageConditionsChecker);
        data.setScreenBlock(screenBlock);
    }

    @Test
    void testNormalConditions() throws Exception {
        when(ultimoContador.getAcumulado()).thenReturn(99456L);
        when(toqueDeQuedaHandler.isToqueDeQueda()).thenReturn(false);
        ArgumentCaptor<Consumer<Boolean>> logCaptor = ArgumentCaptor.forClass(Consumer.class);
        ArgumentCaptor<Consumer<Boolean>> preventCloseCaptor = ArgumentCaptor.forClass(Consumer.class);
        when(misPreferencias.getMilisToast()).thenReturn(50L);

        negativeAction.handle(data);

        verify(elementAndGroupFacade, times(1)).onPreventClosing(ArgumentMatchers.eq("some package"), preventCloseCaptor.capture());
        preventCloseCaptor.getValue().accept(false);

        verify(packageConditionsChecker, times(1)).onAllConditionsMet(ArgumentMatchers.eq("some package"), logCaptor.capture());
        logCaptor.getValue().accept(true);

        verify(data, times(1)).setTiempoAcumulado(99456L - (321L*4));
        verify(timePusher, times(1)).push(987L, 99456L - (321L*4));
        verify(timeLogHandler, times(1)).insertTimeBadApp("some package", 321L);
        verify(data, times(1)).setNeedToBlock(false);
        verify(genericTimedToaster, times(0)).noticeMessage(ArgumentMatchers.anyString());
        verify(screenBlock, times(0)).go();
    }

    @Test
    void testTimeRunningLowSendsMessage() throws Exception {
        when(ultimoContador.getAcumulado()).thenReturn(322L*4);
        when(toqueDeQuedaHandler.isToqueDeQueda()).thenReturn(false);
        ArgumentCaptor<Consumer<Boolean>> logCaptor = ArgumentCaptor.forClass(Consumer.class);
        ArgumentCaptor<Consumer<Boolean>> preventCloseCaptor = ArgumentCaptor.forClass(Consumer.class);
        when(misPreferencias.getMilisToast()).thenReturn(50L);
        when(watchdogService.getString(ArgumentMatchers.anyInt())).thenReturn("message");

        negativeAction.handle(data);

        verify(elementAndGroupFacade, times(1)).onPreventClosing(ArgumentMatchers.eq("some package"), preventCloseCaptor.capture());
        preventCloseCaptor.getValue().accept(false);

        verify(packageConditionsChecker, times(1)).onAllConditionsMet(ArgumentMatchers.eq("some package"), logCaptor.capture());
        logCaptor.getValue().accept(true);

        verify(data, times(1)).setTiempoAcumulado((322L*4) - (321L*4));
        verify(timePusher, times(1)).push(987L, (322L*4) - (321L*4));
        verify(timeLogHandler, times(1)).insertTimeBadApp("some package", 321L);
        verify(data, times(1)).setNeedToBlock(false);
        verify(genericTimedToaster, times(1)).noticeMessage(ArgumentMatchers.anyString());
        verify(screenBlock, times(0)).go();
    }

    @Test
    void testNotEnoughTimeBecauseOfImportedTime() throws Exception {
        when(ultimoContador.getAcumulado()).thenReturn(99456L);
        data.setTiempoImportado(-99856L);
        when(toqueDeQuedaHandler.isToqueDeQueda()).thenReturn(false);
        ArgumentCaptor<Consumer<Boolean>> logCaptor = ArgumentCaptor.forClass(Consumer.class);
        ArgumentCaptor<Consumer<Boolean>> preventCloseCaptor = ArgumentCaptor.forClass(Consumer.class);
        when(misPreferencias.getMilisToast()).thenReturn(50L);
        when(watchdogService.getString(ArgumentMatchers.anyInt())).thenReturn("message");

        negativeAction.handle(data);

        verify(elementAndGroupFacade, times(1)).onPreventClosing(ArgumentMatchers.eq("some package"), preventCloseCaptor.capture());
        preventCloseCaptor.getValue().accept(false);

        verify(packageConditionsChecker, times(1)).onAllConditionsMet(ArgumentMatchers.eq("some package"), logCaptor.capture());
        logCaptor.getValue().accept(true);

        verify(data, times(1)).setTiempoAcumulado(99456L - (321L*4));
        verify(timePusher, times(1)).push(987L, 99456L - (321L*4));
        verify(timeLogHandler, times(1)).insertTimeBadApp("some package", 321L);
        verify(data, times(1)).setNeedToBlock(true);
        verify(genericTimedToaster, times(1)).noticeMessage(ArgumentMatchers.anyString());
        verify(screenBlock, times(1)).go();
    }

    @Test
    void testNotEnoughTimeBecauseOfIntrinsicTime() throws Exception {
        when(ultimoContador.getAcumulado()).thenReturn(-99456L);
        data.setTiempoImportado(99256L);
        when(toqueDeQuedaHandler.isToqueDeQueda()).thenReturn(false);
        ArgumentCaptor<Consumer<Boolean>> logCaptor = ArgumentCaptor.forClass(Consumer.class);
        ArgumentCaptor<Consumer<Boolean>> preventCloseCaptor = ArgumentCaptor.forClass(Consumer.class);
        when(misPreferencias.getMilisToast()).thenReturn(50L);
        when(watchdogService.getString(ArgumentMatchers.anyInt())).thenReturn("message");

        negativeAction.handle(data);

        verify(elementAndGroupFacade, times(1)).onPreventClosing(ArgumentMatchers.eq("some package"), preventCloseCaptor.capture());
        preventCloseCaptor.getValue().accept(false);

        verify(packageConditionsChecker, times(1)).onAllConditionsMet(ArgumentMatchers.eq("some package"), logCaptor.capture());
        logCaptor.getValue().accept(true);

        verify(data, times(1)).setTiempoAcumulado(-99456L - (321L*4));
        verify(timePusher, times(1)).push(987L, -99456L - (321L*4));
        verify(timeLogHandler, times(1)).insertTimeBadApp("some package", 321L);
        verify(data, times(1)).setNeedToBlock(true);
        verify(genericTimedToaster, times(1)).noticeMessage(ArgumentMatchers.anyString());
        verify(screenBlock, times(1)).go();
    }

    @Test
    void testConditionsNotMet() throws Exception {
        when(ultimoContador.getAcumulado()).thenReturn(99456L);
        when(toqueDeQuedaHandler.isToqueDeQueda()).thenReturn(false);
        ArgumentCaptor<Consumer<Boolean>> logCaptor = ArgumentCaptor.forClass(Consumer.class);
        ArgumentCaptor<Consumer<Boolean>> preventCloseCaptor = ArgumentCaptor.forClass(Consumer.class);
        when(misPreferencias.getMilisToast()).thenReturn(50L);

        negativeAction.handle(data);

        verify(elementAndGroupFacade, times(1)).onPreventClosing(ArgumentMatchers.eq("some package"), preventCloseCaptor.capture());
        preventCloseCaptor.getValue().accept(false);

        verify(packageConditionsChecker, times(1)).onAllConditionsMet(ArgumentMatchers.eq("some package"), logCaptor.capture());
        logCaptor.getValue().accept(false);

        verify(data, times(1)).setTiempoAcumulado(99456L - (321L*4));
        verify(timePusher, times(1)).push(987L, 99456L - (321L*4));
        verify(timeLogHandler, times(1)).insertTimeBadApp("some package", 321L);
        verify(data, times(1)).setNeedToBlock(true);
        verify(genericTimedToaster, times(0)).noticeMessage(ArgumentMatchers.anyString());
        verify(screenBlock, times(1)).go();
    }

    @Test
    void testConditionsNotMetButPreventBlocking() throws Exception {
        when(ultimoContador.getAcumulado()).thenReturn(99456L);
        ArgumentCaptor<Consumer<Boolean>> logCaptor = ArgumentCaptor.forClass(Consumer.class);
        ArgumentCaptor<Consumer<Boolean>> preventCloseCaptor = ArgumentCaptor.forClass(Consumer.class);
        when(misPreferencias.getMilisToast()).thenReturn(50L);

        negativeAction.handle(data);

        verify(elementAndGroupFacade, times(1)).onPreventClosing(ArgumentMatchers.eq("some package"), preventCloseCaptor.capture());
        preventCloseCaptor.getValue().accept(true);

        verify(packageConditionsChecker, times(0)).onAllConditionsMet(ArgumentMatchers.eq("some package"), logCaptor.capture());

        verify(data, times(1)).setTiempoAcumulado(99456L - (321L*4));
        verify(timePusher, times(1)).push(987L, 99456L - (321L*4));
        verify(timeLogHandler, times(1)).insertTimeBadApp("some package", 321L);
        verify(data, times(1)).setNeedToBlock(false);
        verify(genericTimedToaster, times(0)).noticeMessage(ArgumentMatchers.anyString());
        verify(screenBlock, times(0)).go();
    }

    @Test
    void testToqueDeQuedaNoOptionsSelected() throws Exception {
        when(ultimoContador.getAcumulado()).thenReturn(99456L);
        when(toqueDeQuedaHandler.isToqueDeQueda()).thenReturn(false);
        ArgumentCaptor<Consumer<Boolean>> logCaptor = ArgumentCaptor.forClass(Consumer.class);
        ArgumentCaptor<Consumer<Boolean>> preventCloseCaptor = ArgumentCaptor.forClass(Consumer.class);
        when(misPreferencias.getMilisToast()).thenReturn(50L);
        when(toqueDeQuedaHandler.isToqueDeQueda()).thenReturn(true);
        when(misPreferencias.getBoolean(PreferencesBooleanEnum.LOCKDOWN_NEGATIVE_BLOCK, true)).thenReturn(false);

        negativeAction.handle(data);

        verify(elementAndGroupFacade, times(1)).onPreventClosing(ArgumentMatchers.eq("some package"), preventCloseCaptor.capture());
        preventCloseCaptor.getValue().accept(false);

        verify(packageConditionsChecker, times(1)).onAllConditionsMet(ArgumentMatchers.eq("some package"), logCaptor.capture());
        logCaptor.getValue().accept(true);

        verify(data, times(1)).setTiempoAcumulado(99456L - (321L*4));
        verify(timePusher, times(1)).push(987L, 99456L - (321L*4));
        verify(timeLogHandler, times(1)).insertTimeBadApp("some package", 321L);
        verify(data, times(1)).setNeedToBlock(false);
        verify(genericTimedToaster, times(0)).noticeMessage(ArgumentMatchers.anyString());
        verify(screenBlock, times(0)).go();
    }

    @Test
    void testToqueDeQuedaBlocks() throws Exception {
        when(ultimoContador.getAcumulado()).thenReturn(99456L);
        when(toqueDeQuedaHandler.isToqueDeQueda()).thenReturn(false);
        ArgumentCaptor<Consumer<Boolean>> logCaptor = ArgumentCaptor.forClass(Consumer.class);
        ArgumentCaptor<Consumer<Boolean>> preventCloseCaptor = ArgumentCaptor.forClass(Consumer.class);
        when(misPreferencias.getMilisToast()).thenReturn(50L);
        when(toqueDeQuedaHandler.isToqueDeQueda()).thenReturn(true);
        when(misPreferencias.getBoolean(PreferencesBooleanEnum.LOCKDOWN_NEGATIVE_BLOCK, true)).thenReturn(true);

        negativeAction.handle(data);

        verify(elementAndGroupFacade, times(1)).onPreventClosing(ArgumentMatchers.eq("some package"), preventCloseCaptor.capture());
        preventCloseCaptor.getValue().accept(false);

        verify(packageConditionsChecker, times(0)).onAllConditionsMet(ArgumentMatchers.eq("some package"), logCaptor.capture());

        verify(data, times(1)).setTiempoAcumulado(99456L - (321L*4));
        verify(timePusher, times(1)).push(987L, 99456L - (321L*4));
        verify(timeLogHandler, times(1)).insertTimeBadApp("some package", 321L);
        verify(data, times(1)).setNeedToBlock(true);
        verify(genericTimedToaster, times(0)).noticeMessage(ArgumentMatchers.anyString());
        verify(screenBlock, times(1)).go();
    }

    private class WatchDogDataTest extends WatchDogData {
        public WatchDogDataTest() {
            super(watchdogService);
        }
    }

}