package ca.sam.games.rocketMaster;

import ca.sam.games.framework.Sound;
import ca.sam.games.framework.gl.Texture;
import ca.sam.games.framework.gl.TextureRegion;
import ca.sam.games.framework.impl.GLGame;

public class Assets {
   
    
    public static Texture mainMenuItems;
    public static TextureRegion mainMenuBackground;
    
    public static void load(GLGame game) {
       	
    	mainMenuItems = new Texture(game, "backgrounds.png");
    	mainMenuBackground = new TextureRegion(mainMenuItems, 0, 0, 800, 480);

    }       
    
    public static void reload() {
    	mainMenuItems.reload();

    	//if(Settings.soundEnabled )
           // music.play();
    }
    
    public static void playSound(Sound sound) {
        //if(Settings.soundEnabled);
         //   sound.play(0.4f);
    }
}

