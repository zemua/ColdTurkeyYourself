package devs.mrp.coolyourturkey.comun;

@FunctionalInterface
public interface MyObserver<T> {

    public void callback(String tipo, T feedback);

}
