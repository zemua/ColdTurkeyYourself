package devs.mrp.coolyourturkey.comun;

public interface IntentAttacher {

    public void attach(String name, Object object);
    public Object read(String name, Class<?> type, Object defaultValue);
    public boolean isRead();

}
