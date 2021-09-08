package devs.mrp.coolyourturkey.comun;

public interface MyObservablePositive<T> {

    public void addPositiveObserver(MyObserver<T> observer);

    public void doPositiveCallback(T feedback);

}
