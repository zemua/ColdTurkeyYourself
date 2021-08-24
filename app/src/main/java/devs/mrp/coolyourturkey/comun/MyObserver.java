package devs.mrp.coolyourturkey.comun;

public interface MyObserver<T> {

    public void callback(String tipo, T feedback);

}
