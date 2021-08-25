package devs.mrp.coolyourturkey.dtos.randomcheck;

public abstract class PositiveCheck extends Check{

    private Integer multiplicador;

    public void setMultiplicador(Integer i) {
        multiplicador = i;
    }

    public Integer getMultiplicador() {
        return multiplicador;
    }

}
