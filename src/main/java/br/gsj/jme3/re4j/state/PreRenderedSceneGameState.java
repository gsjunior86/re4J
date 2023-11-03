package br.gsj.jme3.re4j.state;

import br.gsj.jme3.re4j.anim.PlayerAnimation;
import br.gsj.jme3.re4j.control.DoorControl;
import br.gsj.jme3.re4j.control.SceneChangerControl;
import br.gsj.jme3.re4j.control.TextDisplayControl;
import br.gsj.jme3.re4j.enums.PlayerSFXEnum;
import br.gsj.jme3.re4j.map.MapElements;
import br.gsj.jme3.re4j.map.Scene;
import br.gsj.jme3.re4j.map.SpawnPoint;
import br.gsj.jme3.re4j.sfx.PlayerSFX;
import br.gsj.jme3.re4j.triggers.TriggerDoor;
import br.gsj.jme3.re4j.triggers.TriggerScene;
import br.gsj.jme3.re4j.triggers.TriggerText;
import br.gsj.jme3.re4j.enums.TriggerTypesEnum;
import br.gsj.jme3.re4j.helpers.NodesSpatialsHelper;
import br.gsj.jme3.re4j.helpers.SceneChangerHelper;
import com.jme3.anim.AnimComposer;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioData.DataType;
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
 * This is the main class responsible for orchestrating the camera changes,
 * player control movement and the management of all objects on scene.
 *
 *
 *
 * @author gsjunior
 */
public class PreRenderedSceneGameState extends AbstractAppState {

    protected BulletAppState bulletAppState;
    private BetterCharacterControl playerControl;
    private final Node roomNode = new Node("Room Node");
    private final Node guiNode;
    private Spatial currentScene;
    private Spatial player;
    private Picture backgroundPicture;
    private CameraNode camNode;
    private PlayerAnimation playerAnim;
    private boolean isControlsEnabled = true;
    private AnimComposer playerAnimComposer;
    private AudioNode backgroundMusic;
    private final float walkingSpeed = 5.5f;
    private Vector3f playerWalkDirection = new Vector3f(0, 0, 0);
    private Vector3f classicMoveDir = new Vector3f(0, 0, 1);
    private AssetManager assetManager;
    private Application application;
    private InputManager inputManager;
    private String mapXmlFile = "";
    private Camera cam;
    public final FlyByCamera flyCam;
    public static boolean FREE_CAMERA = false;
    private ViewPort preRenderedView;
    private ViewPort modelsView;
    private FilterPostProcessor fpp;
    private boolean changeMap = false;
    private float waitFadeTotalTime = 0;
    private  final float waitFadeTime = 1200;
    private final FadeFilter fade = new FadeFilter(waitFadeTime/1000);

    private AudioNode openDoor;
    private AudioNode closeDoor;

    private final int screenWidth;
    private final int screenHeight;
    private String spawnPoint;
    private String nextMap;

    private MapElements mapElements;

    public PreRenderedSceneGameState(int height, int width, FlyByCamera flyCam, Node guiNode, String mapXmlFile, String spawnPoint) {
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
        if (preRenderedView == null || modelsView == null) {
            preRenderedView = application.getRenderManager().createPreView("background", cam);
            modelsView = application.getRenderManager().createMainView("scene", cam);
        }
        initializeState(this.mapXmlFile);

    }

    /**
     * This method changes the current map
     *
     * @param destination - The next map
     * @param nextSpawnPoint - A valid spawnPoint that must exist in the next map
     *
     @author gsjunior
     */
    public void changeMap(String destination, String nextSpawnPoint) {
        changeMap = true;
        openDoor.play();
        this.spawnPoint = nextSpawnPoint;
        this.nextMap = destination;
        fade.fadeOut();
    }

    /**
     * Restart all the components when the map changes
     *
     * @author gsjunior
     */
    public void restart() {
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

        preRenderedView.detachScene(backgroundPicture);
        preRenderedView.removeProcessor(fpp);

        roomNode.detachAllChildren();
        guiNode.detachAllChildren();

        playerWalkDirection = new Vector3f(0, 0, 0);
        classicMoveDir = new Vector3f(0, 0, 1);
        //inputManager.clearMappings();
        //inputManager = application.getInputManager();

    }

    public Node getGuiNode() {
        return this.guiNode;
    }


    private void initializeState(String mapXmlFile) {
        fade.fadeIn();
        mapElements = new MapElements(mapXmlFile,application);

        Scene spawnScene = mapElements.getMapSpawnScene().get(spawnPoint);
        backgroundPicture = setupBackground(spawnScene.getNextScene());

        preRenderedView.setClearFlags(true, true, true);
        preRenderedView.attachScene(backgroundPicture);

        modelsView.attachScene(roomNode);
        modelsView.attachScene(guiNode);
        //mainView.setClearFlags(false, true, false);
        //mainView.setBackgroundColor(ColorRGBA.Blue);

        fpp = new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter();
        bloom.setEnabled(true);
        bloom.setBlurScale(3);
        bloom.setBloomIntensity(5);

        fpp.addFilter(fade);
        fpp.addFilter(bloom);
        preRenderedView.addProcessor(fpp);
        //mainView.addProcessor(fpp);

        flyCam.setMoveSpeed(10);
        flyCam.setEnabled(false);
        if (FREE_CAMERA) {
            flyCam.setEnabled(true);
        }

        application.getViewPort().setEnabled(false);
        application.getViewPort().setClearFlags(false, true, false);

        /*application.getViewPort().attachScene(roomNode);
        application.getViewPort().attachScene(guiNode);*/
        //application.getViewPort().addProcessor(fpp);

        setupScene(spawnScene);
        roomNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        backgroundPicture.updateGeometricState();

    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        if(changeMap){
            playerAnim.clearMappings();
            if(waitFadeTotalTime  >= waitFadeTime / 1000){
                changeMap = false;
                waitFadeTotalTime = 0;
                this.restart();
                closeDoor.play();
                initializeState(this.nextMap);
            }

            waitFadeTotalTime += tpf;
        }
        else{
            traditionalPlayerMove();
        }

        if (FREE_CAMERA) {
            System.out.println("Location: " + cam.getLocation());
            System.out.println("Rotation: " + cam.getRotation());
        }

        roomNode.updateLogicalState(tpf);
        roomNode.updateGeometricState();
        guiNode.updateLogicalState(tpf);
        guiNode.updateGeometricState();

    }

    public void traditionalPlayerMove() {
        Vector3f dir = classicMoveDir.clone().multLocal(walkingSpeed);

        playerWalkDirection.set(0, 0, 0);

        int g = 2;

        if (playerAnim.isLeftRotate()) {
            Quaternion pitch = new Quaternion();
            pitch.fromAngleAxis(FastMath.PI * g / 180, Vector3f.UNIT_Y);
            classicMoveDir.set(pitch.mult(classicMoveDir));
            playerControl.setViewDirection(classicMoveDir);

        } else if (playerAnim.isRightRotate()) {
            Quaternion pitch = new Quaternion();
            pitch.fromAngleAxis(-1 * (FastMath.PI * g / 180), Vector3f.UNIT_Y);
            classicMoveDir.set(pitch.mult(classicMoveDir));
            playerControl.setViewDirection(classicMoveDir);
        } else {
            playerControl.setWalkDirection(playerWalkDirection);
        }

        if (playerAnim.isForward()) {
            playerWalkDirection.addLocal(dir);
            playerWalkDirection.set(dir);
            playerControl.setViewDirection(classicMoveDir);
            playerControl.setWalkDirection(playerWalkDirection);
        } else if (playerAnim.isBackward()) {
            playerWalkDirection.addLocal(dir.negate());
            playerWalkDirection.set(dir.negate());
            playerControl.setViewDirection(classicMoveDir.negate());
            playerControl.setWalkDirection(playerWalkDirection);
        } else {
            playerControl.setWalkDirection(playerWalkDirection);
        }

    }

    private void spawnPlayer(int direction) {

        Node spawnPlayerNode = (Node) roomNode.getChild(spawnPoint);
        //spawnPlayerNode.getChild("player_claire").removeFromParent();
        player = spawnPlayerNode.getChild("player_leon");

        for (Entry<String, Scene> spawnPointentry : mapElements.getMapSpawnScene().entrySet()) {
            if (!spawnPointentry.getKey().equals(spawnPoint)) {
                roomNode.getChild(spawnPointentry.getKey()).removeFromParent();
            }
        }

        player.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        playerAnimComposer = player.getControl(AnimComposer.class);
        playerAnimComposer.setCurrentAction("breath");

        playerControl = new BetterCharacterControl(0.5f, 5f, 1f);
        playerControl.setGravity(new Vector3f(0, -1f, 0));

        player.addControl(playerControl);

        Quaternion pitch = new Quaternion();
        pitch.fromAngleAxis(-1 * (FastMath.PI * direction / 180), Vector3f.UNIT_Y);
        classicMoveDir.set(pitch.mult(classicMoveDir));
        playerControl.setViewDirection(classicMoveDir);

        playerControl.setViewDirection(classicMoveDir);

        bulletAppState.getPhysicsSpace().add(playerControl);
        bulletAppState.getPhysicsSpace().add(player);
        playerControl.setEnabled(true);

        playerAnim = new PlayerAnimation(playerAnimComposer, inputManager, assetManager, this,mapElements.getPlayerSFX());

        List<Spatial> frontNode = NodesSpatialsHelper.getSpatialsFromNode((Node) player, "front");
        for (Spatial node : frontNode) {
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


        camNode = new CameraNode("CamNode", cam);
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);

        if (!FREE_CAMERA) {
            roomNode.attachChild(camNode);
        }

        currentScene = assetManager.loadModel(mapElements.getCurrentMap());
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

        spawnPlayer(mapElements.getMapSpawnPoint().get(spawnPoint).getDirection());

        List<Spatial> listFloors = NodesSpatialsHelper.getSpatialsFromNode(roomNode, "floor");
        List<Spatial> listWalls = NodesSpatialsHelper.getSpatialsFromNode(roomNode, "wall");

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);

        for (Spatial floor : listFloors) {
            CollisionShape cs = CollisionShapeFactory.createBoxShape(floor);
            RigidBodyControl rbc = new RigidBodyControl(cs, 0.0f);
            floor.addControl(rbc);
            if (FREE_CAMERA) {
                floor.setMaterial(mat);
            } else {
                floor.setCullHint(Spatial.CullHint.Always);
            }

            bulletAppState.getPhysicsSpace().add(floor.getControl(RigidBodyControl.class));
        }

        for (Spatial wall : listWalls) {
            CollisionShape cs = CollisionShapeFactory.createBoxShape(wall);
            RigidBodyControl rbc = new RigidBodyControl(cs, 0.0f);
            wall.addControl(rbc);
            if (FREE_CAMERA) {
                wall.setMaterial(mat);
            } else {
                wall.setCullHint(Spatial.CullHint.Always);
            }
            bulletAppState.getPhysicsSpace().add(wall.getControl(RigidBodyControl.class));
        }

        for (Entry<String, TriggerScene> entry : mapElements.getMapTriggerScene().entrySet()) {
            Spatial trigger = NodesSpatialsHelper.getSpatialFromNode(roomNode, entry.getKey());

            TriggerScene triggerScene = mapElements.getMapTriggerScene().get(trigger.getName());
            trigger.setUserData("camLocation", triggerScene.getScene().getCamLocation());
            trigger.setUserData("camRotation", triggerScene.getScene().getCamRotation());
            trigger.setUserData("nextScene", triggerScene.getScene().getNextScene());

            CollisionShape cs = CollisionShapeFactory.createBoxShape(trigger);
            SceneChangerControl scc = new SceneChangerControl(trigger.getName(), camNode,
                    backgroundPicture, assetManager, cs);
            trigger.addControl(scc);
            trigger.setCullHint(Spatial.CullHint.Always);
            bulletAppState.getPhysicsSpace().addCollisionListener(scc);
            bulletAppState.getPhysicsSpace().add(trigger.getControl(GhostControl.class));

        }

        for (Entry<String, TriggerText> entry : mapElements.getMapTriggerText().entrySet()) {
            Spatial trigger = NodesSpatialsHelper.getSpatialFromNode(roomNode, entry.getKey());
            CollisionShape cs = CollisionShapeFactory.createBoxShape(trigger);
            TextDisplayControl tdc
                    = new TextDisplayControl(trigger.getName(),
                    mapElements.getMapTriggerText().get(trigger.getName()).getText(), guiNode, roomNode,
                    assetManager.loadFont("Interface/Fonts/FiraSansLight.fnt"),
                    playerAnim, cs, this);
            trigger.addControl(tdc);
            trigger.setCullHint(Spatial.CullHint.Always);
            bulletAppState.getPhysicsSpace().addCollisionListener(tdc);
            bulletAppState.getPhysicsSpace().add(trigger.getControl(GhostControl.class));
        }

        for (Entry<String, TriggerDoor> entry : mapElements.getMapTriggerDoor().entrySet()) {
            Spatial trigger = NodesSpatialsHelper.getSpatialFromNode(roomNode, entry.getKey());
            CollisionShape cs = CollisionShapeFactory.createBoxShape(trigger);
            TriggerDoor td = mapElements.getMapTriggerDoor().get(trigger.getName());
            DoorControl dc = new DoorControl(trigger.getName(),
                    td.getDestination(),
                    td.getSpawnPoint(),
                    td.getOpenSound(),
                    td.getCloseSound(),
                    roomNode, playerAnim, cs, this);
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

    public void setDoorSounds(DoorControl.DoorSound doorSound){
        this.openDoor =  new AudioNode(assetManager, doorSound.getOpenSound(),DataType.Stream);
        this.closeDoor =  new AudioNode(assetManager, doorSound.getCloseSound(),DataType.Stream);

    }


}
