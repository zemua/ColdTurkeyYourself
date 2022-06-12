package devs.mrp.coolyourturkey.grupos.conditionchecker.impl;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LifecycleOwner;

import devs.mrp.coolyourturkey.comun.Notificador;
import devs.mrp.coolyourturkey.comun.TimeBounded;
import devs.mrp.coolyourturkey.comun.impl.TimeBoundedImpl;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.databaseroom.grupo.GrupoRepository;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition.GrupoConditionRepository;
import devs.mrp.coolyourturkey.grupos.conditionchecker.ChangeChecker;
import devs.mrp.coolyourturkey.grupos.conditionchecker.ConditionCheckerCommander;

public class ChangeCheckerFactory {
    public static ChangeChecker getChangeNotifier(Application app, LifecycleOwner owner) {
        Application application = app;
        LifecycleOwner lifecycleOwner = owner;
        ConditionCheckerCommander checker = ConditionCheckerFactory.getConditionChecker(app, owner);
        GrupoConditionRepository conditionRepository = GrupoConditionRepository.getRepo(app);
        GrupoRepository grupoRepository = GrupoRepository.getRepo(app);
        TimeBounded timeBounded = new TimeBoundedImpl();
        MisPreferencias prefs = new MisPreferencias(app);
        Notificador notificador = new Notificador(app, app);
        Handler mainHandler = new Handler(Looper.getMainLooper());
        return new ChangeNotifier(app, owner, checker, conditionRepository, grupoRepository, timeBounded, prefs, notificador, mainHandler);
    }
}
