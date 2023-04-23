/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gsj.re4j.main;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
/**
 *
 * @author gsjunior
 */
public class DoorLoadingGameState extends AbstractAppState{
    
        private AssetManager assetManager ;
        private Node rootNode = new Node("Root Node");
        


    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); 
        this.assetManager = app.getAssetManager();
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         Box b = new Box(2, 2, 2); // create a 1x1x1 box shape
         Geometry geom = new Geometry("Box", b);
         mat.setColor("Color", ColorRGBA.Blue);   // set color of material to blue
         geom.setMaterial(mat);                   // set the cube's material
         rootNode.attachChild(geom); 
    }

    @Override
    public void update(float tpf) {
        super.update(tpf); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        rootNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
    }
    
    
    
    
     public Node getRootNode(){
        return this.rootNode;
    }
    
    
}
