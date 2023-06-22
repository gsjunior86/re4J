/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gsj.jme3.re4j.anim;

import br.gsj.jme3.re4j.enums.InputMappingEnum;
import br.gsj.jme3.re4j.state.PreRenderedSceneGameState;
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
    
    private final AudioNode step1;
    private final AudioNode step2;
     
    private final Action walk_l;
    private final Action walk_r;
    
    private boolean forward = false, backward = false,
            leftRotate = false, rightRotate = false,action = false;
    
    private final PreRenderedSceneGameState sceneGameState;
    
    
    public PlayerAnimation(AnimComposer playerAnimComposer, InputManager inputManager,
            AssetManager assetManager, PreRenderedSceneGameState gameState){
        
        this.playerAnimComposer = playerAnimComposer;
        this.inputManager = inputManager;
        this.sceneGameState = gameState;
        
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
        
        inputManager.addMapping(InputMappingEnum.ROTATE_LEFT,
                new KeyTrigger(KeyInput.KEY_A),
                new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping(InputMappingEnum.ROTATE_RIGHT,
                new KeyTrigger(KeyInput.KEY_D),
                new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping(InputMappingEnum.WALK_FORWARD,
                new KeyTrigger(KeyInput.KEY_W),
                new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping(InputMappingEnum.WALK_BACKWARD,
                new KeyTrigger(KeyInput.KEY_S),
                new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping(InputMappingEnum.ACTION,
                new KeyTrigger(KeyInput.KEY_X));
        
        inputManager.addMapping(InputMappingEnum.PAUSE, new Trigger[]{
                new KeyTrigger(KeyInput.KEY_P),new KeyTrigger(KeyInput.KEY_PAUSE)});
        


        inputManager.addListener(this, InputMappingEnum.ROTATE_LEFT);
        inputManager.addListener(this, InputMappingEnum.ROTATE_RIGHT);
        inputManager.addListener(this, InputMappingEnum.WALK_FORWARD);
        inputManager.addListener(this, InputMappingEnum.WALK_BACKWARD);
        inputManager.addListener(this, InputMappingEnum.ACTION);
        inputManager.addListener(this, InputMappingEnum.PAUSE);
       
        
    }
    
    public void clearMappings(){
        inputManager.clearMappings();
        /*inputManager.deleteMapping(InputMapping.ROTATE_LEFT);
        inputManager.deleteMapping(InputMapping.ROTATE_RIGHT);
        inputManager.deleteMapping(InputMapping.WALK_FORWARD);
        inputManager.deleteMapping(InputMapping.WALK_BACKWARD);
        inputManager.deleteMapping(InputMapping.ACTION);
        inputManager.deleteMapping(InputMapping.PAUSE);*/
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
    public void onAction(String binding, boolean isKeyPressed, float tpf) {
        
        if(isKeyPressed){
            sceneGameState.getGuiNode().detachChildNamed("TextOverlay");
            sceneGameState.setEnabled(true);
        }

            
        if(!sceneGameState.FREE_CAMERA && isKeyPressed){
            switch(binding){
                case InputMappingEnum.ROTATE_LEFT:
                    playerAnimComposer.setCurrentAction("walkCycle");
                    leftRotate = true;
                    break;
                case InputMappingEnum.ROTATE_RIGHT:
                    playerAnimComposer.setCurrentAction("walkCycle");
                    rightRotate = true;
                    break;
                case InputMappingEnum.WALK_FORWARD:
                    playerAnimComposer.setCurrentAction("walkCycle");
                    forward = true;
                    break;
                case InputMappingEnum.WALK_BACKWARD:
                    playerAnimComposer.setCurrentAction("walkCycle");
                    backward = true;
                    break;
                case InputMappingEnum.ACTION:
                    action = true;
                    break;
            }
        } else {
            action = false;
            leftRotate = false;
            rightRotate = false;
            forward = false;
            backward = false;
            playerAnimComposer.setCurrentAction("breath");
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
