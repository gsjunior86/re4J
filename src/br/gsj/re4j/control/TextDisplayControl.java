/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gsj.re4j.control;

import br.gsj.re4j.anim.PlayerAnimation;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;


/**
 *
 * @author gsjunior
 */
public class TextDisplayControl extends RigidBodyControl implements PhysicsCollisionListener {
    
    private final Node guiNode;
    private final BitmapText hudText;
    private final PlayerAnimation playerAL;
    
    
    public TextDisplayControl(String text, Node guiNode,
            BitmapFont guiFont, PlayerAnimation playerAL,
            CollisionShape cs){
        super(cs,0);
        this.guiNode = guiNode;
        this.playerAL = playerAL;
        BitmapText hudText = new BitmapText(guiFont, false);
        hudText.setSize(guiFont.getCharSet().getRenderedSize());      // font size
        hudText.setColor(ColorRGBA.White);                             // font color
        hudText.setText(text);             // the text
        hudText.setLocalTranslation(300, hudText.getLineHeight(), 0);
        this.hudText = hudText;
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        //System.out.println(event.getNodeA().getName() + " | " + event.getNodeB().getName());
        if(event.getNodeA().getName().startsWith("front") && playerAL.isAction()){
            guiNode.attachChild(hudText);
        }else{
            guiNode.detachChild(hudText);
        }
    }
    
}
