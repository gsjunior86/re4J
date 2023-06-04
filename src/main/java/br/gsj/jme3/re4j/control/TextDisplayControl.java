/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gsj.jme3.re4j.control;


import br.gsj.jme3.re4j.anim.PlayerAnimation;
import br.gsj.jme3.re4j.helpers.NodesSpatialsHelper;
import br.gsj.jme3.re4j.helpers.SceneChangerHelper;
import br.gsj.jme3.re4j.state.SceneGameState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;


/**
 *
 * @author gsjunior
 */
public class TextDisplayControl extends GhostControl implements PhysicsCollisionListener {
    
    private final String triggerName;
    private final Node guiNode;
    private final Node roomNode;
    private final BitmapText hudText;
    private final PlayerAnimation playerAL;
    private final SceneGameState gameState;
    private float lastFrameTime;
    private boolean isShow = false;

    private boolean isOverlapping;

    
    
    public TextDisplayControl(String triggerName,String text, Node guiNode,Node roomNode,
            BitmapFont guiFont, PlayerAnimation playerAL,
            CollisionShape cs, SceneGameState gameState){
        super(cs);
        this.triggerName = triggerName;
        this.guiNode = guiNode;
        this.roomNode = roomNode;
        this.playerAL = playerAL;
        this.gameState = gameState;
        BitmapText hudText = new BitmapText(guiFont);
        hudText.setName("TextOverlay");
        hudText.setSize(guiFont.getCharSet().getRenderedSize());      // font size
        hudText.setColor(ColorRGBA.White);                             // font color
        hudText.setText(text);             // the text
        hudText.setLocalTranslation(300, hudText.getLineHeight(), 0);
        this.hudText = hudText;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        if(isOverlapping && playerAL.isAction()){
            if(NodesSpatialsHelper.getMatchSpatialsFromNode(guiNode, "TextOverlay").isEmpty())
                guiNode.attachChild(hudText);
            isShow = true;
            gameState.setEnabled(false);
        }else{
            isShow = false;
        }

        roomNode.updateGeometricState();
        guiNode.updateGeometricState();
    }
    
    

    @Override
    public void collision(PhysicsCollisionEvent event) {
        this.isOverlapping = SceneChangerHelper.isOverlapping(this.getOverlappingObjects(),"front");
    }
    
}
