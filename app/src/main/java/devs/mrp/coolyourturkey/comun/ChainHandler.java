package devs.mrp.coolyourturkey.comun;

public abstract class ChainHandler<T> {
    protected ChainHandler<T> mNextHandler;

    public void setNextHandler(ChainHandler handler) {
        mNextHandler = handler;
    }

    protected abstract boolean canHandle(String tipo);

    public void receiveRequest(String tipo, T data) {
        if (!canHandle(tipo)) {
            if (mNextHandler != null) {
                mNextHandler.receiveRequest(tipo, data);
            }
            return;
        }
        handle(data);
    }

    protected abstract void handle(T data);
}
