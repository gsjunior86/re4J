/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gsj.jme3.re4j.map;

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

    public String getCamLocation() {
        return camLocation;
    }

    public String getCamRotation() {
        return camRotation;
    }

    public String getNextScene() {
        return nextScene;
    }


}
