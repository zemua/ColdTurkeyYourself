package devs.mrp.coolyourturkey.dtos.randomcheck;

import java.util.List;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.databaseroom.randomchecks.RandomCheck;

public class CheckFactory {

    public PositiveCheck importPositiveFrom(RandomCheck rc) throws Exception {
        if (rc.getType() != RandomCheck.CheckType.POSITIVE) {throw new Exception("Retrieved RandomCheck is the wrong type");}
        PositiveCheck pc = new PositiveCheckImpl();
        pc.setMultiplicador(rc.getMultiplicador());
        pc.setId(rc.getId());
        pc.setName(rc.getName());
        pc.setQuestion(rc.getQuestion());
        pc.setFrequency(rc.getFrequency());
        return pc;
    }

    public Check importNegativeFrom(RandomCheck rc) throws Exception {
        if (rc.getType() != RandomCheck.CheckType.NEGATIVE) {throw new Exception("Retrieved RandomCheck is the wrong type");}
        Check nc = new NegativeCheck();
        nc.setId(rc.getId());
        nc.setName(rc.getName());
        nc.setQuestion(rc.getQuestion());
        nc.setFrequency(rc.getFrequency());
        return nc;
    }

    public RandomCheck exportPositiveFrom(PositiveCheck pc) {
        RandomCheck rc = new RandomCheck();
        rc.setId(pc.getId());
        rc.setMultiplicador(pc.getMultiplicador());
        rc.setType(RandomCheck.CheckType.POSITIVE);
        rc.setName(pc.getName());
        rc.setQuestion(pc.getQuestion());
        rc.setFrequency(pc.getFrequency());
        return rc;
    }

    public RandomCheck existingPositiveFrom(PositiveCheck pc) throws Exception {
        if (pc.getId() == null) {throw new Exception("Existing entry must have an id");}
        RandomCheck rc = new RandomCheck();
        rc.setId(pc.getId());
        rc.setQuestion(pc.getQuestion());
        rc.setType(RandomCheck.CheckType.POSITIVE);
        rc.setName(pc.getName());
        rc.setMultiplicador(pc.getMultiplicador());
        rc.setFrequency(pc.getFrequency());
        return rc;
    }

    public RandomCheck exportNegativeFrom(Check c) {
        RandomCheck rc = new RandomCheck();
        rc.setId(c.getId());
        rc.setName(c.getName());
        rc.setQuestion(c.getQuestion());
        rc.setType(RandomCheck.CheckType.NEGATIVE);
        rc.setFrequency(c.getFrequency());
        return rc;
    }

    public RandomCheck existingNegativeFrom(Check c) throws Exception {
        if (c.getId() == null) {throw new Exception("Existing entry must have an id");}
        RandomCheck rc = new RandomCheck();
        rc.setId(c.getId());
        rc.setName(c.getName());
        rc.setType(RandomCheck.CheckType.NEGATIVE);
        rc.setQuestion(c.getQuestion());
        rc.setFrequency(c.getFrequency());
        return rc;
    }

    public PositiveCheck newPositive() {
        return new PositiveCheckImpl();
    }

    public Check newNegative() {
        return new NegativeCheck();
    }

    public List<PositiveCheck> positiveFrom(List<RandomCheck> checks) {
        return checks.stream().map(c -> {
            try {
                return importPositiveFrom(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
    }

    public List<Check> importNegativesFrom(List<RandomCheck> checks) {
        return checks.stream().map(c -> {
            try {
                return importNegativeFrom(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
    }

    public List<PositiveCheck> importPositivesFrom(List<RandomCheck> checks) {
        return checks.stream().map(c -> {
            try {
                return importPositiveFrom(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
    }
}
