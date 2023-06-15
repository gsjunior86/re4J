/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import br.gsj.jme3.re4j.state.SceneGameState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.system.AppSettings;

/**
 *
 * @author gsjunior
 */
public class Main extends SimpleApplication {
    
    
    public static boolean FREE_CAMERA = false;
    public static boolean DEBUG_PHYSICS = false;

    private BulletAppState bulletAppState;
    private SceneGameState currentGameState;


    public static void main(String[] args) {
        Main app = new Main();
        
        app.setShowSettings(false);
        
        AppSettings settings = new AppSettings(true);
        settings.setFullscreen(false);
        settings.setGammaCorrection(true);
        settings.put("Width", 1280);
        settings.put("Height", 720);
        settings.put("VSync", true);
        settings.put("Samples", 16);

        app.setSettings(settings);
        app.setDisplayStatView(true);
        
        app.start(); // start the game// start the game

    }


    @Override
    public void initialize() {
        super.initialize();
        currentGameState = new SceneGameState(settings.getHeight(),settings.getWidth(),
                flyCam,guiNode,"Scenes/definitions/rpd/stairwell.xml","player_spawn_2");
        currentGameState.setEnabled(true);
        bulletAppState = new BulletAppState();
        bulletAppState.setDebugEnabled(DEBUG_PHYSICS);
        stateManager.attach(currentGameState);
        stateManager.attach(bulletAppState);

        float fov = 50;
        float aspect = (float)cam.getWidth() / (float)cam.getHeight();
        
                
        cam.setFrustumPerspective(fov, aspect, 0.001f, cam.getFrustumFar());


    }

    @Override
    public void simpleInitApp() {
        
    }


    

   

    
}
