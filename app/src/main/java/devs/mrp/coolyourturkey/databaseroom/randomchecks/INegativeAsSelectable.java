package devs.mrp.coolyourturkey.databaseroom.randomchecks;

import java.util.List;

import devs.mrp.coolyourturkey.comun.MyObservable;
import devs.mrp.coolyourturkey.dtos.randomcheck.ANegativeCheckSelectable;

public interface INegativeAsSelectable extends MyObservable<List<ANegativeCheckSelectable>> {

    public abstract void getNegativeSelectables(String tagForCallbak);

}
