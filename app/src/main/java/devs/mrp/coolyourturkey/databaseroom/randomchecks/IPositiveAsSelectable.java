package devs.mrp.coolyourturkey.databaseroom.randomchecks;

import java.util.List;

import devs.mrp.coolyourturkey.comun.MyObservable;
import devs.mrp.coolyourturkey.dtos.randomcheck.APositiveCheckSelectable;

public interface IPositiveAsSelectable extends MyObservable<List<APositiveCheckSelectable>> {

    public abstract void getPositiveSelectables(String tagForCallback);

}
