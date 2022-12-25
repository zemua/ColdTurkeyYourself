package devs.mrp.coolyourturkey.watchdog.checkscheduling;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementToGroup;
import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementType;
import devs.mrp.coolyourturkey.grupos.conditionchecker.ConditionCheckerCommander;
import devs.mrp.coolyourturkey.grupos.conditionchecker.impl.ConditionCheckerFactory;

public class StaticRandomCheckWorkerChecker {

    private static final String TAG = "StaticConditionChecker";

    private static LifecycleOwner owner;
    private static Application app;

    private static Handler mainHandler = new Handler(Looper.getMainLooper());

    public static void setContext(Application a, LifecycleOwner o) {
        owner = o;
        app = a;
    }

    public LifecycleOwner getOwner() {
        return owner;
    }

    public Application getApp() {
        return app;
    }

    public static void onAllConditionsMet(int groupId, Consumer<Boolean> action) {
        ConditionCheckerCommander checker = ConditionCheckerFactory.getConditionChecker(app, owner);
        if (Objects.isNull(checker)) {
            Log.w(TAG, "ConditionChecker is null, returning as if all conditions met");
            action.accept(true);
            return;
        }
        mainHandler.post(() -> {
            checker.onAllConditionsMet(groupId, action);
        });
    }

    public static void onRandomCheckGroupId(int blockId, Consumer<Integer> action) {
        ElementToGroupRepository mElementRepo = ElementToGroupRepository.getRepo(app);
        LiveData<List<ElementToGroup>> assignationLiveData = mElementRepo.findElementOfTypeAndElementId(ElementType.CHECK, blockId);
        mainHandler.post(() -> {
            assignationLiveData.observe(owner, elements -> {
                assignationLiveData.removeObservers(owner);
                if (!elements.isEmpty()) {
                    ElementToGroup etg = elements.get(0);
                    action.accept(etg.getGroupId());
                } else {
                    action.accept(-1);
                }
            });
        });
    }

}
