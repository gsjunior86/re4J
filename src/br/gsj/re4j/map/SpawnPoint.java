/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gsj.re4j.map;

/**
 *
 * @author gsjunior
 */
public class SpawnPoint {
    
    private String name;
    private String sceneName;
    private int direction;

    public SpawnPoint(String name, String sceneName,int direction) {
        this.name = name;
        this.sceneName = sceneName;
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }
    
    
    
    
}
