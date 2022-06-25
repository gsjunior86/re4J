/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gsj.re4j.main;

import br.gsj.re4j.physics.SceneChangerControl;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import com.jme3.ui.Picture;
import java.util.List;
import br.gsj.re4j.utils.Utils;
import com.jme3.anim.AnimComposer;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.PointLightShadowRenderer;
import com.jme3.system.AppSettings;

/**
 *
 * @author gsjunior
 */
public class Main extends SimpleApplication implements ActionListener{
    
    
    private boolean debugMode = false;

    private boolean forward = false, backward = false,
            leftRotate = false, rightRotate = false;

    private BulletAppState bulletAppState;
    private BetterCharacterControl playerControl;

    private Spatial currentScene;
    private Spatial player;
    
    private Picture backgroundPicture;
    private CameraNode camNode;

    private AnimComposer playerAnimComposer;
    
    public static final int SHADOWMAP_SIZE = 2048;
    
    final private Vector3f playerWalkDirection = new Vector3f(0,0,0);
    final private Vector3f lastDir = new Vector3f();
    
    

    private static final float MOVE_SPEED = 0.05F;

    public static void main(String[] args) {
        Main app = new Main();
        
        app.setShowSettings(false);
        
        
        AppSettings settings = new AppSettings(true);
        settings.setFullscreen(false);
        settings.put("Width", 1280);
        settings.put("Height", 720);
        settings.put("VSync", true);
        settings.put("Samples", 4);
        
        

        app.setSettings(settings);
        app.setDisplayStatView(false);
        
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
        Vector3f camDir = cam.getDirection().clone().multLocal(3.5f);
        Vector3f camLeft = cam.getLeft().clone().multLocal(3.5f);
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
      
    }

    @Override
    public void update() {
       
              
        super.update();
        
        if(debugMode){
            System.out.println("Location: " + cam.getLocation());
            System.out.println("Rotation: " + cam.getRotation());
        }
    }

    @Override
    public void onAction(String binding, boolean value, float tpf) {
        if(!debugMode){
            if(!value)
                playerAnimComposer.setCurrentAction("breath");
            else
                playerAnimComposer.setCurrentAction("walk");
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

    private void createCharacter() {
        
        //rootNode.getChild("claire").removeFromParent();

        String charSelection = Utils.charSelection();
        Node spawnPlayerNode = (Node) rootNode.getChild("player_spawn_1");
        if(charSelection.equals("Leon")){
            spawnPlayerNode.getChild("player_claire").removeFromParent();
            player = spawnPlayerNode.getChild("player_leon");
        }else if(charSelection.equals("Claire")){
            spawnPlayerNode.getChild("player_leon").removeFromParent();
            player = spawnPlayerNode.getChild("player_claire");
        }
                
        player.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        
        playerAnimComposer = player.getControl(AnimComposer.class);
        playerAnimComposer.setCurrentAction("breath");
        
        playerControl  = new BetterCharacterControl(0.5f, 5f, 1f);
        playerControl.setGravity(new Vector3f(0,-1f,0));
        

        player.addControl(playerControl);

        bulletAppState.getPhysicsSpace().add(playerControl);
        bulletAppState.getPhysicsSpace().add(player);
        playerControl.setEnabled(true);

        
    }
    

    private void createScene() {
        
        Vector3f loc = new Vector3f(15.169064F, 6.051987F, 2.7805796F);
        Quaternion rot = new Quaternion(0.0077047423F, -0.82605225F, 0.011292024F, 0.5634277F);

        // set forward camera node that follows the character
        camNode = new CameraNode("CamNode", cam);
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);

        camNode.setLocalTranslation(loc);
        camNode.setLocalRotation(rot);
        
        if(!debugMode)
            rootNode.attachChild(camNode);        
        
        currentScene = assetManager.loadModel("Scenes/rpd/stairwell.j3o");

        rootNode.attachChild(currentScene);
        List<Spatial> listFloors = Utils.getSpatialsFromNode(rootNode, "floor_");
        List<Spatial> listWalls = Utils.getSpatialsFromNode(rootNode, "wall_");
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        
        
        
        
        
        for (Spatial floor : listFloors){
            CollisionShape cs = CollisionShapeFactory.createBoxShape(floor);
            RigidBodyControl rbc = new RigidBodyControl(cs,0.0f);
            floor.addControl(rbc);
            if(debugMode){
                floor.setMaterial(mat);            
            }else{
                floor.setCullHint(Spatial.CullHint.Always);
            }
                
            bulletAppState.getPhysicsSpace().add(floor.getControl(RigidBodyControl.class));
        }
        

        for (Spatial wall : listWalls) {
            CollisionShape cs = CollisionShapeFactory.createBoxShape(wall);
            RigidBodyControl rbc = new RigidBodyControl(cs,0.0f);
            wall.addControl(rbc);
            if(debugMode)
                wall.setMaterial(mat);
            else
                wall.setCullHint(Spatial.CullHint.Always);
            bulletAppState.getPhysicsSpace().add(wall.getControl(RigidBodyControl.class));
        }
        
        List<Spatial> listTriggers = Utils.getSpatialsFromNode(rootNode, "trigger_");
        for(Spatial trigger: listTriggers){
             CollisionShape cs = CollisionShapeFactory.createBoxShape(trigger);   
             SceneChangerControl scc = new SceneChangerControl(trigger.getName(),camNode,
                     backgroundPicture,assetManager,cs);
             trigger.addControl(scc);
             trigger.setCullHint(Spatial.CullHint.Always);
             bulletAppState.getPhysicsSpace().addCollisionListener(scc);
             bulletAppState.getPhysicsSpace().add(trigger.getControl(GhostControl.class));
        }        
                        
        

    }
    
    private void setupLightAndShadow(){
        
        int lightCount = currentScene.getLocalLightList().size();
        
        for(int i=0; i < lightCount;i++){
            
        PointLightShadowRenderer plsr = new PointLightShadowRenderer(assetManager, SHADOWMAP_SIZE);
        plsr.setLight((PointLight) currentScene.getLocalLightList().get(i));
        plsr.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
        plsr.setShadowZExtend(20);
        plsr.setShadowZFadeLength(1);
        plsr.setShadowIntensity(0.9f);
        //plsr.displayDebug();
        viewPort.addProcessor(plsr);
            
        }
        
    }

    private Picture setupBackground(String img) {
        Picture p = new Picture("background");
        p.setImage(assetManager, img, false);
        p.setWidth(settings.getWidth());
        p.setHeight(settings.getHeight());
        p.setPosition(0, 0);
        return p;
    }

    @Override
    public void simpleInitApp() {

        bulletAppState = new BulletAppState();
        //bulletAppState.setDebugEnabled(true);
        stateManager.attach(bulletAppState);
        
        backgroundPicture = setupBackground("Textures/rooms/rpd/stairwell/stairwell_1.png");
        
        ViewPort pv = renderManager.createPreView("background", cam);
        pv.setClearFlags(true, true, true);
        pv.attachScene(backgroundPicture);
        pv.attachScene(rootNode);
        
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        
        BloomFilter bloom = new BloomFilter();
        bloom.setEnabled(true);
        bloom.setBlurScale(3);
        bloom.setBloomIntensity(5);
        fpp.addFilter(bloom);
        pv.addProcessor(fpp);
        
       
        initKeys();

        flyCam.setMoveSpeed(10);
        flyCam.setEnabled(false);
        
        if(debugMode)
            flyCam.setEnabled(true);
        // You must add a light to make the model visible
        /*DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -3.0f));
        rootNode.addLight(sun);*/

      
        viewPort.setClearFlags(false, true, true);
        createScene();
        createCharacter();
        //setupLightAndShadow();
        rootNode.setShadowMode(ShadowMode.CastAndReceive);
        
        
        
       // viewPort.addProcessor(fpp);
        
        
        
        
        backgroundPicture.updateGeometricState();

    }

}
