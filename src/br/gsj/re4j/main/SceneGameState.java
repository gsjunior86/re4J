/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gsj.re4j.main;

import br.gsj.re4j.anim.PlayerAnimation;
import br.gsj.re4j.control.DoorControl;
import br.gsj.re4j.control.SceneChangerControl;
import br.gsj.re4j.control.TextDisplayControl;
import static br.gsj.re4j.main.Main.FREE_CAMERA;
import br.gsj.re4j.map.Scene;
import br.gsj.re4j.map.SpawnPoint;
import br.gsj.re4j.triggers.TriggerDoor;
import br.gsj.re4j.triggers.TriggerScene;
import br.gsj.re4j.triggers.TriggerText;
import br.gsj.re4j.enums.TriggerTypes;
import br.gsj.re4j.helpers.NodesSpatialsHelper;
import br.gsj.re4j.helpers.SceneChangerHelper;
import com.jme3.anim.AnimComposer;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
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
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.FadeFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import com.jme3.ui.Picture;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author gsjunior
 */
public class SceneGameState extends AbstractAppState{
    
    

    protected BulletAppState bulletAppState;
    private BetterCharacterControl playerControl;
    private final Node roomNode = new Node("Room Node");
    private final Node guiNode;
    private Spatial currentScene;
    private Spatial player;
    private Picture backgroundPicture;
    private CameraNode camNode;
    private PlayerAnimation playerAnim;
    private AnimComposer playerAnimComposer;
    private AudioNode backgroundMusic;
    private final float walkingSpeed = 5.5f;
    private Vector3f playerWalkDirection = new Vector3f(0,0,0);
    private Vector3f classicMoveDir = new Vector3f(0,0,1);
    private AssetManager assetManager ;
    private Application application;
    private InputManager inputManager;
    private String mapXmlFile = "";
    private Camera cam ;
    public final FlyByCamera flyCam;
    
    private ViewPort pv;
    private  FilterPostProcessor fpp;
    private final FadeFilter fade = new FadeFilter(1);
    
    private final int screenWidth;
    private final int screenHeight;
    protected String spawnPoint;
    
    private final Map<String,TriggerScene> mapTriggerScene = new HashMap<>();
    private final Map<String,Scene> mapScenes = new HashMap<>();
    private final Map<String,Scene> mapSpawnScene = new HashMap<>();
    private final Map<String,SpawnPoint> mapSpawnPoint = new HashMap<>();
    private final Map<String,TriggerText> mapTriggerText =  new HashMap<>();
    private final Map<String,TriggerDoor> mapTriggerDoor = new HashMap<>();
    private String currentMap;
    
  
    public SceneGameState(int height, int width,FlyByCamera flyCam, Node guiNode, String mapXmlFile, String spawnPoint){
        this.screenHeight = height;
        this.screenWidth = width;
        this.flyCam = flyCam;
        this.guiNode = guiNode;
        this.spawnPoint = spawnPoint;
        this.mapXmlFile = mapXmlFile;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();
        this.application = app;
        this.cam = application.getCamera();
        this.bulletAppState = this.application.getStateManager().getState(BulletAppState.class);
        pv = application.getRenderManager().createPreView("background", cam);
        initializeState(this.mapXmlFile);
        
    }
    
    public void changeMap(String destination, String nextSpawnPoint){
        System.out.println("change map: " + destination + " | " + nextSpawnPoint);
        
        this.restart();
        pv = application.getRenderManager().createPreView("background", cam);
        //this.application.getViewPort().detachScene(this.roomNode);
        //this.application.getViewPort().attachScene(doorState.getRootNode());
        //this.stateManager.attach(doorState);
        //this.stateManager.detach(this);
        this.spawnPoint = nextSpawnPoint;
        initializeState(destination);
        
        
    }

    
    public void restart() {
        mapTriggerScene.clear();
        mapSpawnScene.clear();
        mapScenes.clear();
        mapTriggerText.clear();
        mapTriggerDoor.clear();
        mapSpawnPoint.clear();
        bulletAppState.getPhysicsSpace().removeAll(roomNode);
        bulletAppState.getPhysicsSpace().remove(playerControl);
        bulletAppState.getPhysicsSpace().remove(player);
        bulletAppState.getPhysicsSpace().destroy();
        bulletAppState.getPhysicsSpace().create();
        playerAnim.clearMappings();
        currentScene = null;
        player = null;
        playerControl = null;
        playerAnim = null;
        playerAnimComposer = null;
        //backgroundMusic.stop();
        //backgroundMusic = null;
        pv.detachScene(roomNode);
        pv.detachScene(backgroundPicture);
        pv.removeProcessor(fpp);
        
        roomNode.detachAllChildren();
        guiNode.detachAllChildren();
        
        
        playerWalkDirection = new Vector3f(0,0,0);
        classicMoveDir = new Vector3f(0,0,1);
        //inputManager.clearMappings();
        //inputManager = application.getInputManager();
        

        
    }
    
    
    
    public Node getRootNode(){
        return this.roomNode;
    }
    
    public Node getGuiNode(){
        return this.guiNode;
    }
    
    private void readMapDefinition(String mapDefinition){
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        
        File xmlFile = new File(this.getClass().getClassLoader().getResource(mapDefinition).getFile());
        try{
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
              // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(xmlFile);  
            doc.getDocumentElement().normalize();
            
            currentMap = doc.getDocumentElement().getAttribute("map");
            
            if(backgroundMusic != null){
                backgroundMusic.stop();
            }
            
            backgroundMusic = new AudioNode(assetManager,
                        doc.getDocumentElement().getAttribute("music"),
                        AudioData.DataType.Stream);
                backgroundMusic.setLooping(true);
                backgroundMusic.setPositional(false);
                backgroundMusic.setDirectional(false);
                backgroundMusic.play();
                application.getAudioRenderer().setEnvironment(new Environment ( new float[]{ 2, 1.9f, 1f, -1000, -454, 0, 0.40f, 0.83f, 1f, -1646, 0.002f, 0f, 0f, 0f, 53, 0.003f, 0f, 0f, 0f, 0.250f, 0f, 0.250f, 0f, -5f, 5000f, 250f, 0f, 0x3f} ));
                
            
            
            
            
            NodeList scenesTag = doc.getDocumentElement().getElementsByTagName("scenes");
            

//            NodeList scenes = doc.getDocumentElement().getElementsByTagName("triggers");
            
            for (int i = 0; i < scenesTag.getLength(); i++) {
                NodeList childList = scenesTag.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                   org.w3c.dom.Node childNode = childList.item(j);
                    if (childNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element element = (Element) childNode;
                    String name = element.getAttribute("name");
                                       
                     
                    Scene scene = new Scene(
                        name,
                        element.getElementsByTagName("camLocation").item(0).getTextContent(),
                        element.getElementsByTagName("camRotation").item(0).getTextContent(),
                        element.getElementsByTagName("nextScene").item(0).getTextContent()
                    );
                        mapScenes.put(name,scene);
                  }   
                }
            }
        
        
        
        NodeList triggersTag = doc.getDocumentElement().getElementsByTagName("triggers");
        
        for (int i = 0; i < triggersTag.getLength(); i++) {
                NodeList childList = triggersTag.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                   org.w3c.dom.Node childNode = childList.item(j);
                    if (childNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element element = (Element) childNode;
                    String name = element.getAttribute("name");
                    String type = element.getAttribute("type");
                    String scene = element.getAttribute("scene");
                                       
                     if(type.equalsIgnoreCase(TriggerTypes.SCENE.getType())){
                         Scene sceneMapped = mapScenes.get(scene);
                         TriggerScene triggerScene = new TriggerScene(
                        name,
                        sceneMapped
                        );
                        mapTriggerScene.put(name,triggerScene);
                     }
                     else if(type.equalsIgnoreCase(TriggerTypes.TEXT.getType())){
                         TriggerText triggerText = new TriggerText(name,
                                 element.getElementsByTagName("text").item(0).getTextContent());
                         mapTriggerText.put(name,triggerText);
                     }else if(type.equalsIgnoreCase(TriggerTypes.DOOR.getType())){
                         TriggerDoor triggerDoor = new TriggerDoor(name,
                                 element.getAttribute("destination"),
                                  element.getAttribute("spawn"));
                         mapTriggerDoor.put(name,triggerDoor);
                     }
                    
                }
            }
        
        }
         
        NodeList spawnsTag = doc.getDocumentElement().getElementsByTagName("spawns");
        
        for (int i = 0; i < spawnsTag.getLength(); i++) {
                NodeList childList = spawnsTag.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                   org.w3c.dom.Node childNode = childList.item(j);
                    if (childNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element element = (Element) childNode;
                    String name = element.getAttribute("name");
                    String sceneName = element.getAttribute("scene");
                    int direction = Integer.parseInt(element.getAttribute("direction"));
                    SpawnPoint spawnPoint = new SpawnPoint(name, sceneName,direction);
                    mapSpawnPoint.put(name, spawnPoint);
                    mapSpawnScene.put(name, mapScenes.get(sceneName));
                    
                }
            }
        
        }
                    
        
            
        } catch (ParserConfigurationException | SAXException | IOException e) {
          e.printStackTrace();
      }
        
    }

    
    private void initializeState(String mapXmlFile){
        
        readMapDefinition(mapXmlFile);
        
        Scene spawnScene = mapSpawnScene.get(spawnPoint);
        backgroundPicture = setupBackground(spawnScene.getNextScene());   
        
        
        
        pv.setClearFlags(true, true, true);
        pv.attachScene(backgroundPicture);
        pv.attachScene(roomNode);
        
        fpp = new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter();
        bloom.setEnabled(true);
        bloom.setBlurScale(3);
        bloom.setBloomIntensity(5);
        
        fpp.addFilter(fade);
        fpp.addFilter(bloom);
        pv.addProcessor(fpp);
        fade.fadeIn();
        
        flyCam.setMoveSpeed(10);
        flyCam.setEnabled(false);
        if(FREE_CAMERA)
            flyCam.setEnabled(true);
        
        application.getViewPort().setClearFlags(false, true, true);
        setupScene(spawnScene);
        roomNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        
        
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
        else if(playerAnim.isLeftRotate()){
            Quaternion pitch = new Quaternion();
            pitch.fromAngleAxis(FastMath.PI * g / 180, Vector3f.UNIT_Y);
            classicMoveDir.set(pitch.mult(classicMoveDir));
            playerControl.setViewDirection(classicMoveDir);
            
        }
        else if(playerAnim.isRightRotate()){
            Quaternion pitch = new Quaternion();
            pitch.fromAngleAxis(-1* (FastMath.PI * g / 180), Vector3f.UNIT_Y);
            classicMoveDir.set(pitch.mult(classicMoveDir));
            playerControl.setViewDirection(classicMoveDir); 
        }else
            playerControl.setWalkDirection(playerWalkDirection);
        
        
        
    }
    
    private void spawnPlayer(int direction) {
        
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
        
        Node spawnPlayerNode = (Node) roomNode.getChild(spawnPoint);
        //spawnPlayerNode.getChild("player_claire").removeFromParent();
        player = spawnPlayerNode.getChild("player_leon");
        
        for(Entry<String,Scene> spawnPointentry: mapSpawnScene.entrySet()){
            if(!spawnPointentry.getKey().equals(spawnPoint))
                roomNode.getChild(spawnPointentry.getKey()).removeFromParent();
        }
                
        player.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        
        
        playerAnimComposer = player.getControl(AnimComposer.class);
        playerAnimComposer.setCurrentAction("breath");
        
        playerControl  = new BetterCharacterControl(0.5f, 5f, 1f);
        playerControl.setGravity(new Vector3f(0,-1f,0));
        

        player.addControl(playerControl);
        
        Quaternion pitch = new Quaternion();
        pitch.fromAngleAxis(-1* (FastMath.PI * direction / 180), Vector3f.UNIT_Y);
        classicMoveDir.set(pitch.mult(classicMoveDir));
        playerControl.setViewDirection(classicMoveDir); 
        
        playerControl.setViewDirection(classicMoveDir);

        bulletAppState.getPhysicsSpace().add(playerControl);
        bulletAppState.getPhysicsSpace().add(player);
        playerControl.setEnabled(true);
        
        playerAnim = new PlayerAnimation(playerAnimComposer, inputManager,assetManager,this);
        
        
        List<Spatial> frontNode = NodesSpatialsHelper.getSpatialsFromNode((Node) player, "front");
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
    
    
    private void setupScene(Scene spawnScene) {
        
        String[] rotArray = spawnScene.getCamRotation().split(",");
        String[] locArray = spawnScene.getCamLocation().split(",");
            
            Vector3f loc = new Vector3f(
                    Float.parseFloat(locArray[0]),
                    Float.parseFloat(locArray[1]),
                    Float.parseFloat(locArray[2]));
            
            Quaternion rot;
            rot = new Quaternion(
                    Float.parseFloat(rotArray[0]),
                    Float.parseFloat(rotArray[1]),
                    Float.parseFloat(rotArray[2]),
                    Float.parseFloat(rotArray[3]));
        

        // set forward camera node that follows the character
        camNode = new CameraNode("CamNode", cam);
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        
        if(!FREE_CAMERA)
            roomNode.attachChild(camNode);        
        
        currentScene = assetManager.loadModel(currentMap);
        roomNode.attachChild(currentScene);
        
        SceneChangerHelper.checkAndInsertAlphaOnScene(
                assetManager,
                camNode,
                guiNode,
                roomNode,
                backgroundPicture,
                spawnScene.getNextScene(),
                screenWidth,
                screenHeight,
                loc,
                rot);
        
        spawnPlayer(mapSpawnPoint.get(spawnPoint).getDirection());
        
        List<Spatial> listFloors = NodesSpatialsHelper.getSpatialsFromNode(roomNode, "floor");
        List<Spatial> listWalls = NodesSpatialsHelper.getSpatialsFromNode(roomNode, "wall");
        
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
        
        for(Entry<String,TriggerScene> entry: mapTriggerScene.entrySet()){
            Spatial trigger = NodesSpatialsHelper.getSpatialFromNode(roomNode,entry.getKey());
            
            TriggerScene triggerScene = mapTriggerScene.get(trigger.getName());
                trigger.setUserData("camLocation", triggerScene.getScene().getCamLocation());
                trigger.setUserData("camRotation", triggerScene.getScene().getCamRotation());
                trigger.setUserData("nextScene", triggerScene.getScene().getNextScene());
                
                CollisionShape cs = CollisionShapeFactory.createBoxShape(trigger);   
                SceneChangerControl scc = new SceneChangerControl(trigger.getName(),camNode,
                        backgroundPicture,assetManager,cs);
                trigger.addControl(scc);
                trigger.setCullHint(Spatial.CullHint.Always);
                bulletAppState.getPhysicsSpace().addCollisionListener(scc);
                bulletAppState.getPhysicsSpace().add(trigger.getControl(GhostControl.class));
            
        }
               
        for(Entry<String,TriggerText> entry: mapTriggerText.entrySet()){
            Spatial trigger = NodesSpatialsHelper.getSpatialFromNode(roomNode,entry.getKey());
            CollisionShape cs = CollisionShapeFactory.createBoxShape(trigger);   
                TextDisplayControl tdc =
                        new TextDisplayControl(trigger.getName(),
                                mapTriggerText.get(trigger.getName()).getText(),guiNode,roomNode,
                                assetManager.loadFont("Interface/Fonts/FiraSansLight.fnt"),
                                playerAnim,cs,this);
                trigger.addControl(tdc);
                trigger.setCullHint(Spatial.CullHint.Always);
                bulletAppState.getPhysicsSpace().addCollisionListener(tdc);
                bulletAppState.getPhysicsSpace().add(trigger.getControl(GhostControl.class));  
        }
                
        for(Entry<String,TriggerDoor> entry: mapTriggerDoor.entrySet()){
            Spatial trigger = NodesSpatialsHelper.getSpatialFromNode(roomNode,entry.getKey());
            CollisionShape cs = CollisionShapeFactory.createBoxShape(trigger);   
                DoorControl dc =
                        new DoorControl(trigger.getName(),
                                mapTriggerDoor.get(trigger.getName()).getDestination(),
                               mapTriggerDoor.get(trigger.getName()).getSpawnPoint(),
                                roomNode,playerAnim,cs,this);
                trigger.addControl(dc);
                trigger.setCullHint(Spatial.CullHint.Always);
                bulletAppState.getPhysicsSpace().addCollisionListener(dc);
                bulletAppState.getPhysicsSpace().add(trigger.getControl(GhostControl.class));   
        }

    }
    
    private Picture setupBackground(String img) {
        Picture p = new Picture("background");
        //p.setImage(assetManager, img, true);
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
