/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gsj.re4j.anim;

import br.gsj.re4j.enums.InputMapping;
import br.gsj.re4j.main.Main;
import br.gsj.re4j.main.SceneGameState;
import br.gsj.re4j.utils.Utils;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.tween.Tween;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.Action;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;


/**
 *
 * @author gsjunior
 */
public class PlayerAnimation implements ActionListener{
    
    
    
    private final AnimComposer playerAnimComposer;
    private final InputManager inputManager;
    private final AssetManager assetManager;
    
    private final AudioNode step1;
    private final AudioNode step2;
     
    private final Action walk_l;
    private final Action walk_r;
    
    private boolean forward = false, backward = false,
            leftRotate = false, rightRotate = false,action = false;
    
    private final SceneGameState gameState;
    
    
    public PlayerAnimation(AnimComposer playerAnimComposer, InputManager inputManager,
            AssetManager assetManager, SceneGameState gameState){
        
        this.playerAnimComposer = playerAnimComposer;
        this.inputManager = inputManager;
        this.assetManager = assetManager;
        this.gameState = gameState;
        
        step1  = new AudioNode(assetManager, "Sounds/sfx/FS01_00001.ogg",DataType.Buffer);
        step2  = new AudioNode(assetManager, "Sounds/sfx/FS01_00002.ogg",DataType.Buffer);
        
        
       //step1.setReverbEnabled(true);
       step1.setPositional(true);
       step1.setDirectional(true);
       
       //step2.setReverbEnabled(true);
       step2.setPositional(true);
       step2.setDirectional(true);
        //step1.setLooping(true);
        //step2.setLooping(true);
        
        walk_l = playerAnimComposer.action("walk_l");
        walk_r = playerAnimComposer.action("walk_r");
        Tween fullCycleTween = Tweens.sequence(Tweens.callMethod(this,"playStep2"),walk_l,Tweens.callMethod(this,"playStep1"), walk_r, Tweens.callMethod(this,"playStep2"));
        Action walkCycle = playerAnimComposer.actionSequence("walkCycle", fullCycleTween);
        
        initKeys();
    }
    
    public void playStep1(){
        step2.stop();
        step1.play();
    }

    public void playStep2(){
        step1.stop();
        step2.play();
    }    
    
    private void initKeys() {
        
        inputManager.addMapping(InputMapping.ROTATE_LEFT.getKeyPressed(),
                new KeyTrigger(KeyInput.KEY_A),
                new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping(InputMapping.ROTATE_RIGHT.getKeyPressed(),
                new KeyTrigger(KeyInput.KEY_D),
                new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping(InputMapping.WALK_FORWARD.getKeyPressed(),
                new KeyTrigger(KeyInput.KEY_W),
                new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping(InputMapping.WALK_BACKWARD.getKeyPressed(),
                new KeyTrigger(KeyInput.KEY_S),
                new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping(InputMapping.ACTION.getKeyPressed(),
                new KeyTrigger(KeyInput.KEY_X));
        
        inputManager.addMapping(InputMapping.PAUSE.getKeyPressed(), new Trigger[]{
                new KeyTrigger(KeyInput.KEY_P),new KeyTrigger(KeyInput.KEY_PAUSE)});
        


        inputManager.addListener(this, InputMapping.ROTATE_LEFT.getKeyPressed());
        inputManager.addListener(this, InputMapping.ROTATE_RIGHT.getKeyPressed());
        inputManager.addListener(this, InputMapping.WALK_FORWARD.getKeyPressed());
        inputManager.addListener(this, InputMapping.WALK_BACKWARD.getKeyPressed());
        inputManager.addListener(this, InputMapping.ACTION.getKeyPressed());
        inputManager.addListener(this, InputMapping.PAUSE.getKeyPressed());
       
        
    }
    
    public void clearMappings(){
        inputManager.deleteMapping(InputMapping.ROTATE_LEFT.getKeyPressed());
        inputManager.deleteMapping(InputMapping.ROTATE_RIGHT.getKeyPressed());
        inputManager.deleteMapping(InputMapping.WALK_FORWARD.getKeyPressed());
        inputManager.deleteMapping(InputMapping.WALK_BACKWARD.getKeyPressed());
        inputManager.deleteMapping(InputMapping.ACTION.getKeyPressed());
        inputManager.deleteMapping(InputMapping.PAUSE.getKeyPressed());
        inputManager.removeListener(this);
        action = false;

    }
    
    SceneGraphVisitor visitor = new SceneGraphVisitor() {

  @Override
  public void visit(Spatial spatial) {
    System.out.println(spatial.getName());
   
  }

};

    @Override
    public void onAction(String binding, boolean value, float tpf) {
        
        if(value){
            gameState.getGuiNode().detachChildNamed("TextOverlay");
            gameState.setEnabled(true);
        }
            
        if(!Main.FREE_CAMERA){
            if (binding.equals(InputMapping.ROTATE_LEFT.getKeyPressed())) {
                if (value) {
                    playerAnimComposer.setCurrentAction("walkCycle");
                    leftRotate = true;
                } else {
                     playerAnimComposer.setCurrentAction("breath");
                    leftRotate = false;
                }
            } else if (binding.equals(InputMapping.ROTATE_RIGHT.getKeyPressed())) {
                if (value) {
                    playerAnimComposer.setCurrentAction("walkCycle");
                    rightRotate = true;
                } else {
                    playerAnimComposer.setCurrentAction("breath");
                    rightRotate = false;
                }
            } else if (binding.equals(InputMapping.WALK_FORWARD.getKeyPressed())) {
                if (value) {
                    playerAnimComposer.setCurrentAction("walkCycle");
                    forward = true;
                } else {
                     playerAnimComposer.setCurrentAction("breath");
                    forward = false;
                }
            } else if (binding.equals(InputMapping.WALK_BACKWARD.getKeyPressed())) {
                if (value) {
                    playerAnimComposer.setCurrentAction("walkCycle");
                    backward = true;
                } else {
                     playerAnimComposer.setCurrentAction("breath");
                    backward = false;
                }
            }else if (binding.equals(InputMapping.ACTION.getKeyPressed())) {
                if (value) {
                    action = true;
                } else {
                    action = false;
                }
            }
            
            
        }
    }

    public boolean isForward() {
        return forward;
    }

    public boolean isBackward() {
        return backward;
    }

    public boolean isLeftRotate() {
        return leftRotate;
    }

    public boolean isRightRotate() {
        return rightRotate;
    }
    
    public boolean isAction() {
        return action;
    }
    
    
    
}
