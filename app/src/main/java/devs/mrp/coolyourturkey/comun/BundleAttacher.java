package devs.mrp.coolyourturkey.comun;

public interface BundleAttacher {

    public void attach(String name, Object object);
    public Object read(String name, Class<?> type, Object defaultValue);
    public boolean contains(String name);

}
