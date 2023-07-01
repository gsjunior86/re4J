package br.gsj.jme3.re4j.sfx;

public class PlayerSFX {

    private String step_l;
    private String step_r;

    public PlayerSFX(String step_l, String step_r){
        this.step_l = step_l;
        this.step_r = step_r;
    }

    public String getStepL(){
        return this.step_l;
    }

    public String getStepR()
    {
        return this.step_r;
    }

}
