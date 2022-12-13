/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gsj.re4j.triggers;

import br.gsj.re4j.map.Scene;

/**
 *
 * @author gsjunior
 */
public class TriggerScene {
    
    private String name;
    private Scene scene;

    public TriggerScene(String name, Scene scene) {
        this.name = name;
        this.scene = scene;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }
    
    
  
    
        
}
