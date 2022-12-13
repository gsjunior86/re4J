/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gsj.re4j.triggers;

/**
 *
 * @author gsjunior
 */
public class TriggerDoor {
    
    private String name;
    private String destination;
    private String spawnPoint;

    public TriggerDoor(String name, String destination, String spawnPoint) {
        this.name = name;
        this.destination = destination;
        this.spawnPoint = spawnPoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(String spawnPoint) {
        this.spawnPoint = spawnPoint;
    }
    
    
    
        
    
    
}
