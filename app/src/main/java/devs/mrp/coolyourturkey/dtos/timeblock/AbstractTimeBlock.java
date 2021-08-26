package devs.mrp.coolyourturkey.dtos.timeblock;

import java.util.List;

import devs.mrp.coolyourturkey.dtos.randomcheck.Check;
import devs.mrp.coolyourturkey.dtos.randomcheck.PositiveCheck;

public abstract class AbstractTimeBlock {

    private Integer id;
    private String name;
    private Long fromTime;
    private Long toTime;
    private Long minimumLapse;
    private Long maximumLapse;
    private List<Integer> days;
    private List<PositiveCheck> positiveChecks;
    private List<Check> negativeChecks;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getFromTime() {
        return fromTime;
    }

    public void setFromTime(Long fromTime) {
        this.fromTime = fromTime;
    }

    public Long getToTime() {
        return toTime;
    }

    public void setToTime(Long toTime) {
        this.toTime = toTime;
    }

    public Long getMinimumLapse() {
        return minimumLapse;
    }

    public void setMinimumLapse(Long minimumLapse) {
        this.minimumLapse = minimumLapse;
    }

    public Long getMaximumLapse() {
        return maximumLapse;
    }

    public void setMaximumLapse(Long maximumLapse) {
        this.maximumLapse = maximumLapse;
    }

    public List<Integer> getDays() {
        return days;
    }

    public void setDays(List<Integer> days) {
        this.days = days;
    }

    public List<PositiveCheck> getPositiveChecks() {
        return positiveChecks;
    }

    public void setPositiveChecks(List<PositiveCheck> positiveChecks) {
        this.positiveChecks = positiveChecks;
    }

    public List<Check> getNegativeChecks() {
        return negativeChecks;
    }

    public void setNegativeChecks(List<Check> negativeChecks) {
        this.negativeChecks = negativeChecks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
