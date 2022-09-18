/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gsj.re4j.control;



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
import java.io.File;

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
    }
        
    
    @Override
    public void collision(PhysicsCollisionEvent event) {
        if(event.getNodeA().getName().startsWith("player") && event.getNodeB().getName().equals(triggerName)){
            
            String backgroundFile = event.getNodeB().getUserData(SCENE_CHANGER).toString();
            
            
            String alphaName =
                    backgroundFile.substring(0,backgroundFile.lastIndexOf("/"))
                            .concat("/"+
                    backgroundFile.substring(backgroundFile.lastIndexOf("/")+1,backgroundFile.lastIndexOf(".")).concat("_a.png"));            
            
            guiNode.detachAllChildren();
            Picture alphaLayer = new Picture("AlphaLayer");        
            alphaLayer.setWidth(SCREEN_WIDTH);
            alphaLayer.setHeight(SCREEN_HEIGHT);
            Material mat0 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            try{
                mat0.setTexture("ColorMap", assetManager.loadTexture(alphaName));
                mat0.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
                alphaLayer.setMaterial(mat0);
                guiNode.attachChild(alphaLayer);
            }catch(com.jme3.asset.AssetNotFoundException ex){
                    System.out.println("Could not find: " + alphaName);
                    alphaLayer = null;
                    mat0 = null;
            }
            
           
            
            this.backgroundPicture.setImage(assetManager,
                    backgroundFile,
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
