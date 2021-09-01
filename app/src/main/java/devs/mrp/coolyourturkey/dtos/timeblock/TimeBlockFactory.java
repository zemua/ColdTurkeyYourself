package devs.mrp.coolyourturkey.dtos.timeblock;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.CheckTimeBlock;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.TimeBlockWithChecks;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.RandomCheck;
import devs.mrp.coolyourturkey.dtos.randomcheck.Check;
import devs.mrp.coolyourturkey.dtos.randomcheck.CheckFactory;
import devs.mrp.coolyourturkey.dtos.randomcheck.PositiveCheck;

public class TimeBlockFactory {

    private CheckFactory factory = new CheckFactory();

    public AbstractTimeBlock getNew() {
        return new TimeBlock();
    }

    public AbstractTimeBlock importFrom(TimeBlockWithChecks tb) {
        TimeBlock block = new TimeBlock();
        block.setId(tb.getTimeBlock().getBlockid());
        block.setName(tb.getTimeBlock().getName());
        block.setFromTime(tb.getTimeBlock().getFromtime());
        block.setToTime(tb.getTimeBlock().getTotime());
        block.setMinimumLapse(tb.getTimeBlock().getMinlapse());
        block.setMaximumLapse(tb.getTimeBlock().getMaxlapse());
        block.setDays(transformDays(tb.getTimeBlock()));
        block.setPositiveChecks(extractPositives(tb.getChecks()));
        block.setNegativeChecks(extractNegatives(tb.getChecks()));
        return block;
    }

    public TimeBlockWithChecks exportFrom(AbstractTimeBlock tb){
        TimeBlockWithChecks obj = new TimeBlockWithChecks();
        obj.setChecks(sendAllChecks(tb));
        obj.setTimeBlock(new CheckTimeBlock());
        obj.getTimeBlock().setBlockid(tb.getId());
        obj.getTimeBlock().setName(tb.getName());
        obj.getTimeBlock().setFromtime(tb.getFromTime());
        obj.getTimeBlock().setTotime(tb.getToTime());
        obj.getTimeBlock().setMinlapse(tb.getMinimumLapse());
        obj.getTimeBlock().setMaxlapse(tb.getMaximumLapse());
        setDaysToSend(tb, obj);
        return obj;
    }

    private List<Integer> transformDays(CheckTimeBlock tb) {
        List<Integer> days = new ArrayList<>();
        if (tb.isMonday()) {days.add(0);}
        if (tb.isTuesday()) {days.add(1);}
        if (tb.isWednesday()) {days.add(2);}
        if (tb.isThursday()) {days.add(3);}
        if (tb.isFriday()) {days.add(4);}
        if (tb.isSaturday()) {days.add(5);}
        if (tb.isSunday()) {days.add(6);}
        return days;
    }

    private List<PositiveCheck> extractPositives(List<RandomCheck> rc) {
        return rc.stream()
                .filter(c -> c.getType().equals(RandomCheck.CheckType.POSITIVE))
                .map(c -> {
                    try {
                        return factory.importPositiveFrom(c);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }

    private List<Check> extractNegatives(List<RandomCheck> rc) {
        return rc.stream()
                .filter(c -> c.getType().equals(RandomCheck.CheckType.NEGATIVE))
                .map(c -> {
                    try {
                        return factory.importNegativeFrom(c);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }

    private List<RandomCheck> sendAllChecks(AbstractTimeBlock atb) {
        List<RandomCheck> list = new ArrayList<>();
        list.addAll(atb.getPositiveChecks().stream().map(c -> factory.exportPositiveFrom(c)).collect(Collectors.toList()));
        list.addAll(atb.getNegativeChecks().stream().map(c -> factory.exportNegativeFrom(c)).collect(Collectors.toList()));
        return list;
    }

    private void setDaysToSend(AbstractTimeBlock atb, TimeBlockWithChecks obj) {
        obj.getTimeBlock().setMonday(atb.getDays().contains(0));
        obj.getTimeBlock().setTuesday(atb.getDays().contains(1));
        obj.getTimeBlock().setWednesday(atb.getDays().contains(2));
        obj.getTimeBlock().setThursday(atb.getDays().contains(3));
        obj.getTimeBlock().setFriday(atb.getDays().contains(4));
        obj.getTimeBlock().setSaturday(atb.getDays().contains(5));
        obj.getTimeBlock().setSunday(atb.getDays().contains(6));
    }

}
