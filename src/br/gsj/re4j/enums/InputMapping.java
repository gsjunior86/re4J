/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gsj.re4j.enums;

/**
 *
 * @author gsjunior
 */
public enum InputMapping {
    
    ROTATE_LEFT("Rotate Left"),
    ROTATE_RIGHT("Rotate Right"),
    WALK_FORWARD("Walk Forward"),
    WALK_BACKWARD("Walk Backward"),
    ACTION("Action"),
    PAUSE("Pause");
    
    private String keyPressed;
    
    InputMapping(String keyPressed) {
        this.keyPressed = keyPressed;
    }
    
    public String getKeyPressed() {
        return keyPressed;
    }
    
}
