package devs.mrp.coolyourturkey.watchdog.groups;

public class ConditionCheckerFactory {

    public static ConditionChecker getChecker() {
        return new ConditionCheckerImpl();
    }

}
