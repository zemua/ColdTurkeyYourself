package devs.mrp.coolyourturkey.dtos.timeblock.facade;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.List;

import devs.mrp.coolyourturkey.comun.MyObservable;
import devs.mrp.coolyourturkey.comun.MyObserver;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;

public interface ITimeBlockFacade {

    public void getById(Integer blockid, MyObserver<List<AbstractTimeBlock>> observer);

    public void getAll(MyObserver<List<AbstractTimeBlock>> observer);

}
