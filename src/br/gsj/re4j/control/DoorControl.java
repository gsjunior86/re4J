/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gsj.re4j.control;

import br.gsj.re4j.anim.PlayerAnimation;
import br.gsj.re4j.main.SceneGameState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;


/**
 *
 * @author gsjunior
 */
public class DoorControl extends GhostControl implements PhysicsCollisionListener {
    
    private final String triggerName;
    private final String destination;
    private final String spawnPoint;
    private final Node roomNode;
    private final PlayerAnimation playerAL;
    private SceneGameState gameState;
    
    
    public DoorControl(String triggerName,
            String destination,
            String spawnPoint,
            Node roomNode,
            PlayerAnimation playerAL,
            CollisionShape cs, SceneGameState gameState){
        super(cs);
        this.destination = destination;
        this.spawnPoint = spawnPoint;
        this.triggerName = triggerName;
        this.roomNode = roomNode;
        this.playerAL = playerAL;
        this.gameState = gameState;
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        //System.out.println(event.getNodeA().getName() + " | " + event.getNodeB().getName());
        if(event.getNodeA().getName().startsWith("front") && playerAL.isAction()
                && event.getNodeB().getName().equals(this.triggerName)){
            
            this.gameState.changeMap(destination, spawnPoint);
            
        }
        
        /*if(!gameState.isEnabled())
            guiNode.attachChild(hudText);*/
            
        
        roomNode.updateGeometricState();
    }
    
}
