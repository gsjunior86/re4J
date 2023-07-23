package br.gsj.jme3.re4j.map;

import br.gsj.jme3.re4j.enums.PlayerSFXEnum;
import br.gsj.jme3.re4j.enums.TriggerTypesEnum;
import br.gsj.jme3.re4j.sfx.PlayerSFX;
import br.gsj.jme3.re4j.triggers.TriggerDoor;
import br.gsj.jme3.re4j.triggers.TriggerScene;
import br.gsj.jme3.re4j.triggers.TriggerText;
import com.jme3.app.Application;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.audio.Environment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapElements {

    private final Map<String, TriggerScene> mapTriggerScene = new HashMap<String,TriggerScene>();
    private final Map<String, Scene> mapScenes = new HashMap<String, Scene>();
    private final Map<String, Scene> mapSpawnScene = new HashMap<String, Scene>();
    private final Map<String, SpawnPoint> mapSpawnPoint = new HashMap<String, SpawnPoint>();
    private final Map<String, TriggerText> mapTriggerText = new HashMap<String, TriggerText>();
    private final Map<String, TriggerDoor> mapTriggerDoor = new HashMap<String, TriggerDoor>();
    private PlayerSFX playerSFX;
    private String currentMap;
    private static AudioNode backgroundMusic;
    private Application application;

    public MapElements(String xmlFile, Application application){
        resetElements();
        this.application = application;
        readMapDefinition(xmlFile);
    }

    private void readMapDefinition(String mapDefinition) {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        File xmlFile = new File(this.getClass().getClassLoader().getResource(mapDefinition).getFile());

        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(xmlFile);
            doc.getDocumentElement().normalize();

            currentMap = doc.getDocumentElement().getAttribute("map");

            if (backgroundMusic != null) {
                backgroundMusic.stop();
            }

            backgroundMusic = new AudioNode(application.getAssetManager(),
                    doc.getDocumentElement().getAttribute("music"),
                    AudioData.DataType.Stream);
            backgroundMusic.setLooping(true);
            backgroundMusic.setPositional(false);
            backgroundMusic.setDirectional(false);
            backgroundMusic.play();
            application.getAudioRenderer().setEnvironment(new Environment(new float[]{2, 1.9f, 1f, -1000, -454, 0, 0.40f, 0.83f, 1f, -1646, 0.002f, 0f, 0f, 0f, 53, 0.003f, 0f, 0f, 0f, 0.250f, 0f, 0.250f, 0f, -5f, 5000f, 250f, 0f, 0x3f}));

            NodeList scenesTag = doc.getDocumentElement().getElementsByTagName("scenes");
            readScenes(scenesTag);

            NodeList sfxTag = doc.getDocumentElement().getElementsByTagName("sfx");
            readSFX(sfxTag);

            NodeList triggersTag = doc.getDocumentElement().getElementsByTagName("triggers");
            readTriggers(triggersTag);

            NodeList spawnsTag = doc.getDocumentElement().getElementsByTagName("spawns");
            readSpawnPoints(spawnsTag);


        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

    }

    private void readSFX(NodeList sfxTag){
        for (int i = 0; i < sfxTag.getLength(); i++) {
            NodeList childList= sfxTag.item(i).getChildNodes();
            String step_l = "";
            String step_r = "";
            for (int j = 0; j < childList.getLength(); j++) {
                org.w3c.dom.Node childNode = childList.item(j);
                if (childNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element element = (Element) childNode;
                    if(element.getAttribute("type").equals(PlayerSFXEnum.STEP_L.getStep()))
                        step_l = element.getAttribute("src");
                    else if(element.getAttribute("type").equals(PlayerSFXEnum.STEP_R.getStep()))
                        step_r = element.getAttribute("src");
                }

            }
            playerSFX = new PlayerSFX(step_l,step_r);
        }
    }

    private void readScenes(NodeList scenesTag){
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
                    mapScenes.put(name, scene);
                }
            }
        }
    }

    private void readTriggers(NodeList triggersTag){
        for (int i = 0; i < triggersTag.getLength(); i++) {
            NodeList childList = triggersTag.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                org.w3c.dom.Node childNode = childList.item(j);
                if (childNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element element = (Element) childNode;
                    String name = element.getAttribute("name");
                    String type = element.getAttribute("type");
                    String scene = element.getAttribute("scene");

                    if (type.equalsIgnoreCase(TriggerTypesEnum.SCENE.getType())) {
                        Scene sceneMapped = mapScenes.get(scene);
                        TriggerScene triggerScene = new TriggerScene(
                                name,
                                sceneMapped
                        );
                        mapTriggerScene.put(name, triggerScene);
                    } else if (type.equalsIgnoreCase(TriggerTypesEnum.TEXT.getType())) {
                        TriggerText triggerText = new TriggerText(name,
                                element.getElementsByTagName("text").item(0).getTextContent());
                        mapTriggerText.put(name, triggerText);
                    } else if (type.equalsIgnoreCase(TriggerTypesEnum.DOOR.getType())) {
                        TriggerDoor triggerDoor = new TriggerDoor(name,
                                element.getAttribute("destination"),
                                element.getAttribute("spawn"),
                                element.getAttribute("open_sound"),
                                element.getAttribute("close_sound"));
                        mapTriggerDoor.put(name, triggerDoor);
                    }

                }
            }

        }

    }
    private void readSpawnPoints(NodeList spawnsTag){
        for (int i = 0; i < spawnsTag.getLength(); i++) {
            NodeList childList = spawnsTag.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                org.w3c.dom.Node childNode = childList.item(j);
                if (childNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element element = (Element) childNode;
                    String name = element.getAttribute("name");
                    String sceneName = element.getAttribute("scene");
                    int direction = Integer.parseInt(element.getAttribute("direction"));
                    SpawnPoint spawnPoint = new SpawnPoint(name, sceneName, direction);
                    mapSpawnPoint.put(name, spawnPoint);
                    mapSpawnScene.put(name, mapScenes.get(sceneName));

                }
            }

        }
    }

    public PlayerSFX getPlayerSFX() {
        return playerSFX;
    }

    public Map<String, Scene> getMapSpawnScene() {
        return mapSpawnScene;
    }

    public Map<String, SpawnPoint> getMapSpawnPoint() {
        return mapSpawnPoint;
    }

    public AudioNode getBackgroundMusic() {
        return backgroundMusic;
    }

    public Map<String, TriggerDoor> getMapTriggerDoor() {
        return mapTriggerDoor;
    }

    public String getCurrentMap() {
        return currentMap;
    }

    public Map<String, TriggerScene> getMapTriggerScene() {
        return mapTriggerScene;
    }

    public Map<String, TriggerText> getMapTriggerText() {
        return mapTriggerText;
    }

    private void resetElements(){
        mapTriggerScene.clear();
        mapSpawnScene.clear();
        mapScenes.clear();
        mapTriggerText.clear();
        mapTriggerDoor.clear();
    }
}
