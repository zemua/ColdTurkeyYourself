package devs.mrp.coolyourturkey.plantillas;

public interface Feedbacker<T> {
    public void giveFeedback(int tipo, T feedback);
    public void addFeedbackListener(FeedbackListener<T> listener);
}
