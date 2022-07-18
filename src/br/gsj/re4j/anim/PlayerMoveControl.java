/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gsj.re4j.anim;

import br.gsj.re4j.main.Main;
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


/**
 *
 * @author gsjunior
 */
public class PlayerMoveControl implements ActionListener{
    
    
    
    private final AnimComposer playerAnimComposer;
    private final InputManager inputManager;
    private final AssetManager assetManager;
    
    final private AudioNode step1;
    final private AudioNode step2;
     
    final private Action walk_l;
    final private Action walk_r;
    private boolean forward = false, backward = false,
            leftRotate = false, rightRotate = false;
    
    
    public PlayerMoveControl(AnimComposer playerAnimComposer, InputManager inputManager,
            AssetManager assetManager){
        
        this.playerAnimComposer = playerAnimComposer;
        this.inputManager = inputManager;
        this.assetManager = assetManager;
        
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
        inputManager.addMapping("Rotate Left",
                new KeyTrigger(KeyInput.KEY_A),
                new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Rotate Right",
                new KeyTrigger(KeyInput.KEY_D),
                new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Walk Forward",
                new KeyTrigger(KeyInput.KEY_W),
                new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Walk Backward",
                new KeyTrigger(KeyInput.KEY_S),
                new KeyTrigger(KeyInput.KEY_DOWN));


        inputManager.addListener(this, "Rotate Left");
        inputManager.addListener(this, "Rotate Right");
        inputManager.addListener(this, "Walk Forward");
        inputManager.addListener(this, "Walk Backward");
    }

    @Override
    public void onAction(String binding, boolean value, float tpf) {
        if(!Main.DEBUG_MODE){
            if(!value){  
                playerAnimComposer.setCurrentAction("breath");
                step1.stop();
                step2.stop();
            }else{
                               
                playerAnimComposer.setCurrentAction("walkCycle");

            }
            if (binding.equals("Rotate Left")) {
                if (value) {
                    leftRotate = true;
                } else {
                    leftRotate = false;
                }
            } else if (binding.equals("Rotate Right")) {
                if (value) {
                    rightRotate = true;
                } else {
                    rightRotate = false;
                }
            } else if (binding.equals("Walk Forward")) {
                if (value) {
                    forward = true;
                } else {
                    forward = false;
                }
            } else if (binding.equals("Walk Backward")) {
                if (value) {
                    backward = true;
                } else {
                    backward = false;
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
    
    
    
}
