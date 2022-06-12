package devs.mrp.coolyourturkey.watchdog.groups;

public class ConditionCheckerFactoryOld {

    public static ConditionChecker getChecker() {
        return new ConditionCheckerImpl();
    }

}
