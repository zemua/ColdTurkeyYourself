package devs.mrp.coolyourturkey.plantillas;

public interface FeedbackListener<T> {
    public void giveFeedback(int tipo, T feedback, Object ... args);
}
