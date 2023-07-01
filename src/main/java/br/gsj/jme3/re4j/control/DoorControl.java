/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gsj.jme3.re4j.control;


import br.gsj.jme3.re4j.anim.PlayerAnimation;
import br.gsj.jme3.re4j.helpers.SceneChangerHelper;
import br.gsj.jme3.re4j.state.PreRenderedSceneGameState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.scene.Node;


/**
 *
 * @author gsjunior
 */
public class DoorControl extends GhostControl implements PhysicsCollisionListener {
    
    private final String triggerName;
    private final String destination;
    private final String spawnPoint;
    private final DoorSound doorSound;
    private final Node roomNode;
    private final PlayerAnimation playerAL;
    private PreRenderedSceneGameState gameState;
    private boolean isOverlapping;

    
    
    public DoorControl(String triggerName,
            String destination,
            String spawnPoint,
            String openSound,
            String closeSound,
            Node roomNode,
            PlayerAnimation playerAL,
            CollisionShape cs, PreRenderedSceneGameState gameState){
        super(cs);
        this.destination = destination;
        this.spawnPoint = spawnPoint;
        this.triggerName = triggerName;
        this.roomNode = roomNode;
        this.playerAL = playerAL;
        this.gameState = gameState;
        this.doorSound = new DoorSound(openSound, closeSound);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        if(isOverlapping && playerAL.isAction()){

            this.gameState.setDoorSounds(doorSound);
            this.gameState.changeMap(destination, spawnPoint);

        }

        roomNode.updateGeometricState();

    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        this.isOverlapping = SceneChangerHelper.isOverlapping(this.getOverlappingObjects(),"front");

    }
    
    public class DoorSound{
        
        private final String openSound;
        private final String closeSound;
        
        public DoorSound(String openSound, String closeSound){
            this.openSound = openSound;
            this.closeSound = closeSound;
        }

        public String getOpenSound() {
            return openSound;
        }

        public String getCloseSound() {
            return closeSound;
        }
        
        
        
    }
    
    
}



