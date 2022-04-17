/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package re4j.main;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import com.jme3.ui.Picture;
import java.util.List;
import re4j.utils.Utils;

/**
 *
 * @author gsjunior
 */
public class Main extends SimpleApplication implements ActionListener {

    private boolean forward = false, backward = false,
            leftRotate = false, rightRotate = false;

    private BulletAppState bulletAppState;
    private BetterCharacterControl playerControl;

    private Spatial currentScene;
    private Spatial sceneMesh;
    private Spatial player;

    private CameraNode camNode;

    final private Vector3f playerWalkDirection = new Vector3f(0,0,0);
    final private Vector3f lastDir = new Vector3f();


    private static final float MOVE_SPEED = 0.05F;

    public static void main(String[] args) {
        Main app = new Main();
        app.start(); // start the game

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
    public void simpleUpdate(float tpf) {
        //System.out.println(playerControl.getViewDirection());
        Vector3f camDir = cam.getDirection().clone().multLocal(1f);
        Vector3f camLeft = cam.getLeft().clone().multLocal(1f);
        camDir.y = 0;
        camLeft.y = 0;

        playerWalkDirection.set(0, 0, 0);
        

        if (forward) {
            playerWalkDirection.addLocal(camDir);
            playerControl.setViewDirection(playerWalkDirection);
        }
        if (backward) {
            playerWalkDirection.addLocal(camDir.negate());
            playerControl.setViewDirection(playerWalkDirection);
        }
        
        if(rightRotate){
            playerWalkDirection.addLocal(camLeft.negate());
            playerControl.setViewDirection(playerWalkDirection);            
        }
        if(leftRotate){
           playerWalkDirection.addLocal(camLeft);
           playerControl.setViewDirection(playerWalkDirection);
        }

      playerControl.setWalkDirection(playerWalkDirection);  
      System.out.println(playerControl.getViewDirection());

    }

    @Override
    public void update() {
        super.update();
        
        /*System.out.println("Direction: " + cam.getDirection());
        System.out.println("Location: " + cam.getLocation());
        System.out.println("Rotation: " + cam.getRotation());*/
    }

    @Override
    public void onAction(String binding, boolean value, float tpf) {
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

    private void createCharacter() {


        /*Node playerModel = (Node) assetManager.loadModel("Models/chars/leon/leon.j3o");
        playerModel.setLocalScale(new Vector3f(0.085f, 0.085f, 0.085f));
        playerModel.setLocalTranslation(-6.429195f, .02f, -2.45331f);*/
        Vector3f loc = new Vector3f(11.167176F, 4.200334F, 1.5761515F);
        Quaternion rot = new Quaternion(0.012892349F, -0.7998087F, 0.017185071F, 0.5998704F);

        // set forward camera node that follows the character
        camNode = new CameraNode("CamNode", cam);
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);

        camNode.setLocalTranslation(loc);
        camNode.setLocalRotation(rot);

        player = rootNode.getChild("claire");
        
        
        //rootNode.getChild("claire").removeFromParent();
        
        //playerControl = new CharacterControl(level_shape, 1);
        playerControl  = new BetterCharacterControl(0.5f, 5f, 1f);
        playerControl.setGravity(new Vector3f(0,-1f,0));
        //playerControl.setPhysicsLocation(player.getWorldTranslation());

        player.addControl(playerControl);

        bulletAppState.getPhysicsSpace().add(playerControl);
        bulletAppState.getPhysicsSpace().add(player);
        playerControl.setEnabled(true);

        rootNode.attachChild(camNode);
    }

    private void createScene() {
        currentScene = assetManager.loadModel("Scenes/Room1.j3o");

        rootNode.attachChild(currentScene);
        sceneMesh = rootNode.getChild("floor");
        List<Spatial> listWalls = Utils.getSpatialsFromNode(rootNode, "wall_");

        bulletAppState.getPhysicsSpace().add(sceneMesh.getControl(RigidBodyControl.class));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        sceneMesh.setMaterial(mat);

        for (Spatial wall : listWalls) {
            System.out.println(wall.getName());
            wall.setMaterial(mat);
            bulletAppState.getPhysicsSpace().add(wall.getControl(RigidBodyControl.class));
        }

    }

    private Picture setupBackground() {
        Picture p = new Picture("background");
        p.setImage(assetManager, "Textures/rooms/re1.png", false);
        p.setWidth(settings.getWidth());
        p.setHeight(settings.getHeight());
        p.setPosition(0, 0);

        ViewPort pv = renderManager.createPreView("background", cam);
        pv.setClearFlags(true, true, true);
        pv.attachScene(p);
        pv.attachScene(rootNode);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);

        BloomFilter bloom = new BloomFilter();
        fpp.addFilter(bloom);
        pv.addProcessor(fpp);
        return p;
    }

    @Override
    public void simpleInitApp() {

        bulletAppState = new BulletAppState();
        //bulletAppState.setDebugEnabled(true);
        stateManager.attach(bulletAppState);

        createScene();
        createCharacter();

        Picture p = setupBackground();

        initKeys();

        flyCam.setMoveSpeed(10);
        flyCam.setEnabled(false);

        // You must add a light to make the model visible
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -3.0f));
        rootNode.addLight(sun);

        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);

        viewPort.setClearFlags(false, true, true);

        p.updateGeometricState();

    }
}
