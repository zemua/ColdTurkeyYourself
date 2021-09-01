package devs.mrp.coolyourturkey.comun;

public interface MyObservableNegative<T> {

    public void addNegativeObserver(MyObserver<T> observer);

    public void doNegativeCallback(T feedback);

}
