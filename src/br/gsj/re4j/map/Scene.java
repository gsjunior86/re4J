/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gsj.re4j.map;

/**
 *
 * @author gsjunior
 */
public class Scene {
    
    private String name;
    private String camLocation;
    private String camRotation;
    private String nextScene;

    public Scene(String name, String camLocation, String camRotation, String nextScene) {
        this.name = name;
        this.camLocation = camLocation;
        this.camRotation = camRotation;
        this.nextScene = nextScene;
    }
    
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCamLocation() {
        return camLocation;
    }

    public void setCamLocation(String camLocation) {
        this.camLocation = camLocation;
    }

    public String getCamRotation() {
        return camRotation;
    }

    public void setCamRotation(String camRotation) {
        this.camRotation = camRotation;
    }

    public String getNextScene() {
        return nextScene;
    }

    public void setNextScene(String nextScene) {
        this.nextScene = nextScene;
    }
    
}
