package devs.mrp.coolyourturkey.plantillas;

public interface FeedbackReceiver<U, T> {
    public void receiveFeedback(U feedbacker, int accion, T feedback, Object ... args);
}
