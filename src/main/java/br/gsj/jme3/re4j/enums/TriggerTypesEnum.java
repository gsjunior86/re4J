/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package br.gsj.jme3.re4j.enums;

/**
 *
 * @author gsjunior
 */
public enum TriggerTypesEnum {
    
    SCENE("scene"),
    TEXT("text"),
    DOOR("door");
    
    private String type;
    
    TriggerTypesEnum(String type) {
        this.type = type;
    }
    
    public String getType() {
        return type;
    }
    
}
