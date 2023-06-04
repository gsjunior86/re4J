/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gsj.jme3.re4j.helpers;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.ui.Picture;

import java.util.List;

/**
 *
 * @author gsjunior
 */
public class SceneChangerHelper {

    public static void checkAndInsertAlphaOnScene(
            final AssetManager assetManager,
            final CameraNode camNode,
            final Node guiNode,
            final Node roomNode,
            final Picture backgroundPicture,
            final String backgroundFile,
            final float SCREEN_WIDTH,
            final float SCREEN_HEIGHT,
            final Vector3f loc,
            final Quaternion rot
            
            ) {

        String alphaName
                = backgroundFile.substring(0, backgroundFile.lastIndexOf("/"))
                        .concat("/"
                                + backgroundFile.substring(backgroundFile.lastIndexOf("/") + 1, backgroundFile.lastIndexOf(".")).concat("_a.png"));

        Picture alphaLayer = new Picture("AlphaLayer");
        alphaLayer.setWidth(SCREEN_WIDTH);
        alphaLayer.setHeight(SCREEN_HEIGHT);
        Material mat0 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        try {
            mat0.setTexture("ColorMap", assetManager.loadTexture(alphaName));
            mat0.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            alphaLayer.setMaterial(mat0);
            if (NodesSpatialsHelper.getMatchSpatialsFromNode(guiNode, "AlphaLayer").isEmpty()) {
                guiNode.attachChild(alphaLayer);
            }

        } catch (com.jme3.asset.AssetNotFoundException ex) {

            guiNode.detachChildNamed("AlphaLayer");
            alphaLayer = null;
            mat0 = null;
        }

        backgroundPicture.setImage(assetManager,
                backgroundFile,
                true);
        
        camNode.setLocalRotation(rot);
        camNode.setLocalTranslation(loc);
        roomNode.updateGeometricState();
        guiNode.updateGeometricState();

    }


    public static boolean isOverlapping(List<PhysicsCollisionObject> listPCO, String objName){

        for(PhysicsCollisionObject obj: listPCO){
            Spatial spatial = (Spatial) obj.getUserObject();
            if(spatial.getName().equals(objName))
                return true;
        }

        return false;
    }

}
