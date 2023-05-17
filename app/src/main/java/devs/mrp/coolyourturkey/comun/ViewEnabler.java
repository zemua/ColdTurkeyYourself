package devs.mrp.coolyourturkey.comun;

public interface ViewEnabler {

    // TODO store a List<List<Supplier<Boolean>>,View> in constructor, then iterate on the conditions for each view
    // TODO create a Supplier<ViewEnabler> bean to be injected in the fragment, add conditions, then call inside click listeners

    public void evaluateConditions();

}
