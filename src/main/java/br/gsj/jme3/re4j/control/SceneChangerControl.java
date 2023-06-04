/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gsj.jme3.re4j.control;



import br.gsj.jme3.re4j.helpers.SceneChangerHelper;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
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
    private final int SCREEN_WIDTH;
    private final int SCREEN_HEIGHT;
    private final Node guiNode;
    private final Node roomNode;
    
    
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
        SCREEN_WIDTH = (int) this.backgroundPicture.getUserData("width");
        SCREEN_HEIGHT = (int) this.backgroundPicture.getUserData("height");
        guiNode = (Node) this.backgroundPicture.getUserData("gui_layer");
        roomNode = (Node) this.backgroundPicture.getUserData("room_layer");
    }
        
    
    @Override
    public void collision(PhysicsCollisionEvent event) {
        if(event.getNodeA().getName().startsWith("player") && event.getNodeB().getName().equals(triggerName)){
            
            String backgroundFile = event.getNodeB().getUserData(SCENE_CHANGER).toString();
           

            String[] rotArray = event.getNodeB().getUserData("camRotation").toString().split(",");
            String[] locArray = event.getNodeB().getUserData("camLocation").toString().split(",");
            
            Vector3f loc = new Vector3f(
                    Float.parseFloat(locArray[0]),
                    Float.parseFloat(locArray[1]),
                    Float.parseFloat(locArray[2]));
            
            Quaternion rot;
            rot = new Quaternion(
                    Float.parseFloat(rotArray[0]),
                    Float.parseFloat(rotArray[1]),
                    Float.parseFloat(rotArray[2]),
                    Float.parseFloat(rotArray[3]));
            
            SceneChangerHelper.checkAndInsertAlphaOnScene(
                    assetManager,
                    camNode,
                    guiNode,
                    roomNode,
                    backgroundPicture,
                    backgroundFile,
                    SCREEN_WIDTH,
                    SCREEN_HEIGHT,
                    loc,
                    rot);
            
            
        }
            
        
    }
    
}
