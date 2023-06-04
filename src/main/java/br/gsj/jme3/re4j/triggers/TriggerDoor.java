/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gsj.jme3.re4j.triggers;

/**
 *
 * @author gsjunior
 */
public class TriggerDoor {
    
    private String name;
    private String destination;
    private String spawnPoint;
    private String openSound;
    private String closeSound;

    public TriggerDoor(String name, String destination, String spawnPoint,
            String openSound, String closeSound) {
        this.name = name;
        this.destination = destination;
        this.spawnPoint = spawnPoint;
        this.openSound = openSound;
        this.closeSound = closeSound;
    }

    public String getOpenSound() {
        return openSound;
    }

    public String getCloseSound() {
        return closeSound;
    }

    public String getName() {
        return name;
    }

    public String getDestination() {
        return destination;
    }

    public String getSpawnPoint() {
        return spawnPoint;
    }
    
    
    
        
    
    
}
