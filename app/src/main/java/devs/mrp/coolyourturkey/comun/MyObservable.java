package devs.mrp.coolyourturkey.comun;

public interface MyObservable<T> {

    public void addObserver(MyObserver<T> observer);

    public void doCallBack(String tipo, T feedback);

}
