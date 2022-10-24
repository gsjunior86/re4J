/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gsj.re4j.main;

import br.gsj.re4j.anim.PlayerAnimation;
import br.gsj.re4j.control.SceneChangerControl;
import br.gsj.re4j.control.TextDisplayControl;
import static br.gsj.re4j.main.Main.FREE_CAMERA;
import br.gsj.re4j.utils.Utils;
import com.jme3.anim.AnimComposer;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.audio.Environment;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import com.jme3.ui.Picture;
import java.util.List;

/**
 *
 * @author gsjunior
 */
public class MainGameState extends AbstractAppState{
    
    

    private BulletAppState bulletAppState;
    private BetterCharacterControl playerControl;
    final private Node roomNode = new Node("Room Node");
    final private Node guiNode;
    private Spatial currentScene;
    private Spatial player;
    private Picture backgroundPicture;
    private CameraNode camNode;
    private PlayerAnimation playerAnim;
    private AnimComposer playerAnimComposer;
    private AudioNode backgroundMusic;
    private final float walkingSpeed = 4.5f;
    final private Vector3f playerWalkDirection = new Vector3f(0,0,0);
    private final Vector3f classicMoveDir = new Vector3f(0,0,1);
    private AssetManager assetManager ;
    private Application application;
    private InputManager inputManager;
    
    private Camera cam ;
    private final FlyByCamera flyCam;
    
    private final int screenWidth;
    private final int screenHeight;
    
  
    public MainGameState(int height, int width,FlyByCamera flyCam, Node guiNode){
        this.screenHeight = height;
        this.screenWidth = width;
        this.flyCam = flyCam;
        this.guiNode = guiNode;
    }
    
    public Node getRootNode(){
        return this.roomNode;
    }
    
    public Node getGuiNode(){
        return this.guiNode;
    }
    
    private void initializeState(){
        backgroundPicture = setupBackground("Textures/rooms/rpd/stairwell/stairwell_1.png");                        
        ViewPort pv = application.getRenderManager().createPreView("background", cam);
        pv.setClearFlags(true, true, true);
        pv.attachScene(backgroundPicture);
        pv.attachScene(roomNode);
        
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter();
        bloom.setEnabled(true);
        bloom.setBlurScale(3);
        bloom.setBloomIntensity(5);
        fpp.addFilter(bloom);
        pv.addProcessor(fpp);
        
        flyCam.setMoveSpeed(10);
        flyCam.setEnabled(false);
        if(FREE_CAMERA)
            flyCam.setEnabled(true);
        
        application.getViewPort().setClearFlags(false, true, true);
        setupScene();
        roomNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        
        backgroundMusic = new AudioNode(assetManager, "Sounds/background_music/MAIN07.SAP.wav",AudioData.DataType.Stream);
        backgroundMusic.setLooping(true);
        backgroundMusic.setPositional(false);
        backgroundMusic.setDirectional(false);
        backgroundMusic.play();
        application.getAudioRenderer().setEnvironment(new Environment ( new float[]{ 2, 1.9f, 1f, -1000, -454, 0, 0.40f, 0.83f, 1f, -1646, 0.002f, 0f, 0f, 0f, 53, 0.003f, 0f, 0f, 0f, 0.250f, 0f, 0.250f, 0f, -5f, 5000f, 250f, 0f, 0x3f} ));
        backgroundPicture.updateGeometricState();
        
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

  
    
    
    @Override
    public void update(float tpf) {
        super.update(tpf);

        roomNode.updateLogicalState(tpf);
        roomNode.updateGeometricState();
        guiNode.updateLogicalState(tpf);
        guiNode.updateGeometricState();
        traditionalPlayerMove();
       
        
        if(FREE_CAMERA){
            System.out.println("Location: " + cam.getLocation());
            System.out.println("Rotation: " + cam.getRotation());
        }
     
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();
        this.application = app;
        this.cam = application.getCamera();
        this.bulletAppState = this.application.getStateManager().getState(BulletAppState.class);
        initializeState();
    }
    
    

   
    
    
    public void traditionalPlayerMove(){
        Vector3f dir = classicMoveDir.clone().multLocal(walkingSpeed);
        
        playerWalkDirection.set(0,0,0);
        
        int g = 2;
        
        if (playerAnim.isForward()) {
            playerWalkDirection.addLocal(dir);
            playerWalkDirection.set(dir);
            playerControl.setViewDirection(classicMoveDir);
            playerControl.setWalkDirection(playerWalkDirection);
        }
        else if (playerAnim.isBackward()) {
            playerWalkDirection.addLocal(dir.negate());
            playerWalkDirection.set(dir.negate());
            playerControl.setViewDirection(classicMoveDir.negate());
            playerControl.setWalkDirection(playerWalkDirection);
        }
        else if(playerAnim.isRightRotate()){
            Quaternion pitch = new Quaternion();
            pitch.fromAngleAxis(FastMath.PI * g / 180, Vector3f.UNIT_Y);
            classicMoveDir.set(pitch.mult(classicMoveDir));
            playerControl.setViewDirection(classicMoveDir);
            
        }
        else if(playerAnim.isLeftRotate()){
           Quaternion pitch = new Quaternion();
            pitch.fromAngleAxis(-1* (FastMath.PI * g / 180), Vector3f.UNIT_Y);
            classicMoveDir.set(pitch.mult(classicMoveDir));
            playerControl.setViewDirection(classicMoveDir); 
        }else
            playerControl.setWalkDirection(playerWalkDirection);
        
        
        
    }
    
    private void spawnPlayer() {
        
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
        
        Node spawnPlayerNode = (Node) roomNode.getChild("player_spawn_1");
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
        playerAnim = new PlayerAnimation(playerAnimComposer, inputManager,assetManager,this);
        
        List<Spatial> frontNode = Utils.getSpatialsFromNode((Node) player, "front");
        for(Spatial node: frontNode){
             //System.out.println(node.getName());
             CollisionShape cs = CollisionShapeFactory.createBoxShape(node);
             GhostControl gc = new GhostControl(cs);
             node.addControl(gc);
             node.setCullHint(Spatial.CullHint.Always);
             //bulletAppState.getPhysicsSpace().addCollisionListener(gc);
             bulletAppState.getPhysicsSpace().add(node.getControl(GhostControl.class));
        }
        
        
    }
    
    
    private void setupScene() {
        
        Vector3f loc = new Vector3f(15.169064F, 6.051987F, 2.7805796F);
        Quaternion rot = new Quaternion(0.0077047423F, -0.82605225F, 0.011292024F, 0.5634277F);

        // set forward camera node that follows the character
        camNode = new CameraNode("CamNode", cam);
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);

        camNode.setLocalTranslation(loc);
        camNode.setLocalRotation(rot);
        
        if(!FREE_CAMERA)
            roomNode.attachChild(camNode);        
        
        currentScene = assetManager.loadModel("Scenes/rpd/stairwell.j3o");
        roomNode.attachChild(currentScene);
        
        spawnPlayer();
        
        List<Spatial> listFloors = Utils.getSpatialsFromNode(roomNode, "floor");
        List<Spatial> listWalls = Utils.getSpatialsFromNode(roomNode, "wall");
        
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
        
        List<Spatial> listTriggerScene = Utils.getSpatialsFromNode(roomNode, "trigger","scene");
        for(Spatial trigger: listTriggerScene){
             CollisionShape cs = CollisionShapeFactory.createBoxShape(trigger);   
             SceneChangerControl scc = new SceneChangerControl(trigger.getName(),camNode,
                     backgroundPicture,assetManager,cs);
             trigger.addControl(scc);
             trigger.setCullHint(Spatial.CullHint.Always);
             bulletAppState.getPhysicsSpace().addCollisionListener(scc);
             bulletAppState.getPhysicsSpace().add(trigger.getControl(GhostControl.class));
        }
        
        List<Spatial> listTriggerText = Utils.getSpatialsFromNode(roomNode, "trigger","text");
        for(Spatial trigger: listTriggerText){
             CollisionShape cs = CollisionShapeFactory.createBoxShape(trigger);   
             TextDisplayControl tdc =
                     new TextDisplayControl("This area is stil in development...",guiNode,roomNode,
                             assetManager.loadFont("Interface/Fonts/Inconsolata.fnt"),
                             playerAnim,cs,this);
             trigger.addControl(tdc);
             trigger.setCullHint(Spatial.CullHint.Always);
             bulletAppState.getPhysicsSpace().addCollisionListener(tdc);
             bulletAppState.getPhysicsSpace().add(trigger.getControl(GhostControl.class));
        }
        
        
                        
        

    }
    
    private Picture setupBackground(String img) {
        Picture p = new Picture("background");
        p.setImage(assetManager, img, false);
        p.setWidth(this.screenWidth);
        p.setHeight(this.screenHeight);
        p.setPosition(0, 0);
        p.setUserData("width", screenWidth);
        p.setUserData("height", screenHeight);
        p.setUserData("gui_layer", guiNode);
        p.setUserData("room_layer", roomNode);
        return p;
    }
    
}
