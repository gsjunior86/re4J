/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gsj.jme3.re4j.map;

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

    public String getName() {
        return name;
    }


    
    
    
}
