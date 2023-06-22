package br.gsj.jme3.re4j.enums;

public enum PlayerSFXEnum {

    STEP_L("step_l"),

    STEP_R("step_l");

    private String step;

    PlayerSFXEnum(String type) {
        this.step = type;
    }

    public String getStep() {
        return step;
    }

}
