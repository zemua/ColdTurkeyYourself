package devs.mrp.coolyourturkey.dtos.randomcheck;

import devs.mrp.coolyourturkey.comun.MyNombrable;

public abstract class Check implements MyNombrable {

    private Integer id;
    private String name;
    private String question;

    public void setId(Integer i) {
        id = i;
    }
    public Integer getId() {
        return id;
    }

    public void setName(String s) {
        name = s;
    }
    public String getName() {
        return name;
    }

    public void setQuestion(String s) {
        question = s;
    }
    public String getQuestion() {
        return question;
    }

}
