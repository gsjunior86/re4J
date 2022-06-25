/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gsj.re4j.physics;



import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.ui.Picture;

/**
 *
 * @author gsjunior
 */
public class SceneChangerControl extends GhostControl implements PhysicsCollisionListener{
    
    private final String triggerName;
    private final Picture backgroundPicture;
    private final String SCENE_CHANGER = "nextScene";
    private final AssetManager assetManager;
    private final CameraNode camNode;
    
    
    public SceneChangerControl(String triggerName,
            CameraNode camNode,
            Picture backgroundPicture,
            AssetManager assetManager,
            CollisionShape cs){
        super(cs);
        
        this.triggerName = triggerName;
        this.camNode = camNode;
        this.backgroundPicture = backgroundPicture;
        this.assetManager = assetManager;
    }
        
    
    @Override
    public void collision(PhysicsCollisionEvent event) {
        if(event.getNodeA().getName().startsWith("player") && event.getNodeB().getName().equals(triggerName)){
            
            this.backgroundPicture.setImage(assetManager,
                    event.getNodeB().getUserData(SCENE_CHANGER).toString(),
                    true);
            
            String[] rotArray = event.getNodeB().getUserData("camRotation").toString().split(",");
            String[] locArray = event.getNodeB().getUserData("camLocation").toString().split(",");
            
            Vector3f loc = new Vector3f(
                    Float.valueOf(locArray[0]),
                    Float.valueOf(locArray[1]),
                    Float.valueOf(locArray[2]));
            
            Quaternion rot = new Quaternion(
                    Float.valueOf(rotArray[0]),
                    Float.valueOf(rotArray[1]),
                    Float.valueOf(rotArray[2]),
                    Float.valueOf(rotArray[3]));
            
            camNode.setLocalRotation(rot);
            camNode.setLocalTranslation(loc);
                    
        }
            
        
    }
    
}
