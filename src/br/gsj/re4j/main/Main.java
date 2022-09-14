/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gsj.re4j.main;

import br.gsj.re4j.anim.PlayerMoveControl;
import br.gsj.re4j.physics.SceneChangerControl;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.util.CollisionShapeFactory;
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
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.audio.Environment;

import com.jme3.material.Material;

import com.jme3.math.FastMath;

import com.jme3.renderer.queue.RenderQueue.ShadowMode;

import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import java.util.Random;

/**
 *
 * @author gsjunior
 */
public class Main extends SimpleApplication{
    
    
    public static boolean FREE_CAMERA = false;
    public static boolean DEBUG_PHYSICS = true;

    private BulletAppState bulletAppState;
    private BetterCharacterControl playerControl;

    private Spatial currentScene;
    private Spatial player;
    
    
    
    private Picture backgroundPicture;
    private Picture layerPic;
    private CameraNode camNode;
    
    private PlayerMoveControl playerMoveControl;

    private AnimComposer playerAnimComposer;

    
    private AudioNode backgroundMusic;
    
    
    private float walkingSpeed = 4.5f;
    final private Vector3f playerWalkDirection = new Vector3f(0,0,0);
    private Vector3f classicMoveDir = new Vector3f(0,0,1);



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

    public void traditionalPlayerMove(){
        Vector3f dir = classicMoveDir.clone().multLocal(walkingSpeed);
        
        playerWalkDirection.set(0,0,0);
        
        int g = 2;
        
        if (playerMoveControl.isForward()) {
            playerWalkDirection.addLocal(dir);
            playerWalkDirection.set(dir);
            playerControl.setViewDirection(classicMoveDir);
            playerControl.setWalkDirection(playerWalkDirection);
        }
        else if (playerMoveControl.isBackward()) {
            playerWalkDirection.addLocal(dir.negate());
            playerWalkDirection.set(dir.negate());
            playerControl.setViewDirection(classicMoveDir.negate());
            playerControl.setWalkDirection(playerWalkDirection);
        }
        else if(playerMoveControl.isRightRotate()){
            Quaternion pitch = new Quaternion();
            pitch.fromAngleAxis(FastMath.PI * g / 180, Vector3f.UNIT_Y);
            classicMoveDir.set(pitch.mult(classicMoveDir));
            playerControl.setViewDirection(classicMoveDir);
            
        }
        else if(playerMoveControl.isLeftRotate()){
           Quaternion pitch = new Quaternion();
            pitch.fromAngleAxis(-1* (FastMath.PI * g / 180), Vector3f.UNIT_Y);
            classicMoveDir.set(pitch.mult(classicMoveDir));
            playerControl.setViewDirection(classicMoveDir); 
        }else
            playerControl.setWalkDirection(playerWalkDirection);
        
        
        
    }
    
    public void relativeCamPlayerMove(){
        //System.out.println(playerControl.getViewDirection());
        Vector3f camDir = cam.getDirection().clone().multLocal(walkingSpeed);
        Vector3f camLeft = cam.getLeft().clone().multLocal(walkingSpeed);
        camDir.y = 0;
        camLeft.y = 0;

        playerWalkDirection.set(0, 0, 0);
        
        if (playerMoveControl.isForward()) {
            playerWalkDirection.addLocal(camDir);
            playerControl.setViewDirection(playerWalkDirection);
        }
        if (playerMoveControl.isBackward()) {
            playerWalkDirection.addLocal(camDir.negate());
            playerControl.setViewDirection(playerWalkDirection);
        }
        
        if(playerMoveControl.isRightRotate()){
            playerWalkDirection.addLocal(camLeft.negate());
            playerControl.setViewDirection(playerWalkDirection);            
        }
        if(playerMoveControl.isLeftRotate()){
           playerWalkDirection.addLocal(camLeft);
           playerControl.setViewDirection(playerWalkDirection);
        }
      playerControl.setWalkDirection(playerWalkDirection);
    }
    

    @Override
    public void simpleUpdate(float tpf) {
              
       traditionalPlayerMove();
       //relativeCamPlayerMove();
      
    }

    @Override
    public void update() {
       
              
        super.update();
        
        if(FREE_CAMERA){
            System.out.println("Location: " + cam.getLocation());
            System.out.println("Rotation: " + cam.getRotation());
        }
    }


    private void createCharacter() {
        
        //rootNode.getChild("claire").removeFromParent();

        /*String charSelection = Utils.charSelection();
        Node spawnPlayerNode = (Node) rootNode.getChild("player_spawn_1");
        if(charSelection.equals("Leon")){
            spawnPlayerNode.getChild("player_claire").removeFromParent();
            player = spawnPlayerNode.getChild("player_leon");
        }else if(charSelection.equals("Claire")){
            spawnPlayerNode.getChild("player_leon").removeFromParent();
            player = spawnPlayerNode.getChild("player_claire");
        }*/
        
        Node spawnPlayerNode = (Node) rootNode.getChild("player_spawn_1");
        spawnPlayerNode.getChild("player_claire").removeFromParent();
        player = spawnPlayerNode.getChild("player_leon");
                
        player.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        
        playerAnimComposer = player.getControl(AnimComposer.class);
        playerAnimComposer.setCurrentAction("breath");
        
        playerControl  = new BetterCharacterControl(0.5f, 5f, 1f);
        playerControl.setGravity(new Vector3f(0,-1f,0));
        

        player.addControl(playerControl);
        
        playerControl.setViewDirection(classicMoveDir);

        bulletAppState.getPhysicsSpace().add(playerControl);
        bulletAppState.getPhysicsSpace().add(player);
        playerControl.setEnabled(true);
        playerMoveControl = new PlayerMoveControl(playerAnimComposer, inputManager,assetManager);
        
    }
    

    private void createScene() {
        
        Vector3f loc = new Vector3f(15.169064F, 6.051987F, 2.7805796F);
        Quaternion rot = new Quaternion(0.0077047423F, -0.82605225F, 0.011292024F, 0.5634277F);

        // set forward camera node that follows the character
        camNode = new CameraNode("CamNode", cam);
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);

        camNode.setLocalTranslation(loc);
        camNode.setLocalRotation(rot);
        
        if(!FREE_CAMERA)
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
            if(FREE_CAMERA){
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
            if(FREE_CAMERA)
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
    
    /*private void setupLightAndShadow(){
        
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
        
    }*/

    private Picture setupBackground(String img) {
        Picture p = new Picture("background"+ new Random().nextInt());
        p.setImage(assetManager, img, false);
        p.setWidth(settings.getWidth());
        p.setHeight(settings.getHeight());
        p.setPosition(0, 0);
        p.setUserData("width", settings.getWidth());
        p.setUserData("height", settings.getHeight());
        p.setUserData("gui_layer", guiNode);
        return p;
    }

    @Override
    public void simpleInitApp() {

        bulletAppState = new BulletAppState();
        bulletAppState.setDebugEnabled(DEBUG_PHYSICS);
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
        
       
        //initKeys();

        flyCam.setMoveSpeed(10);
        flyCam.setEnabled(false);
        
        if(FREE_CAMERA)
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
        
        backgroundMusic = new AudioNode(assetManager, "Sounds/background_music/MAIN07.SAP.wav",AudioData.DataType.Stream);
        backgroundMusic.setLooping(true);
        backgroundMusic.setPositional(false);
        backgroundMusic.setDirectional(false);
        backgroundMusic.play();
        audioRenderer.setEnvironment(new Environment ( new float[]{ 2, 1.9f, 1f, -1000, -454, 0, 0.40f, 0.83f, 1f, -1646, 0.002f, 0f, 0f, 0f, 53, 0.003f, 0f, 0f, 0f, 0.250f, 0f, 0.250f, 0f, -5f, 5000f, 250f, 0f, 0x3f} ));
        
       // viewPort.addProcessor(fpp);
        
        
        
        
        backgroundPicture.updateGeometricState();
        //p2.updateGeometricState();
        

    }

}
