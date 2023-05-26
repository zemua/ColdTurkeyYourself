package devs.mrp.coolyourturkey.configuracion;

public enum PreferencesBooleanEnum {
    LOCKDOWN_NEGATIVE_BLOCK("lockdown.negative.block", true),
    LOCKDOWN_NEUTRAL_DECREASE("lockdown.neutral.decrease", true),
    LOCKDOWN_POSITIVE_DONT_SUM("lockdown.positive.dont.sum", true),
    LOCKDOWN_POSITIVE_DECREASE("lockdown.positive.decrease", true);

    private String value;
    private boolean defaultState;

    PreferencesBooleanEnum(String value, boolean defaultState) {
        this.value = value;
        this.defaultState = defaultState;
    }

    public String getValue() {
        return this.value;
    }

    public boolean getDefaultState() {
        return this.defaultState;
    }

}
