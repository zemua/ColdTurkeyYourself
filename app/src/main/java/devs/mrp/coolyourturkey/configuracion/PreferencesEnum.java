package devs.mrp.coolyourturkey.configuracion;

public enum PreferencesEnum {
    LOCKDOWN_NEGATIVE_BLOCK("lockdown.negative.block");

    private String value;

    PreferencesEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
