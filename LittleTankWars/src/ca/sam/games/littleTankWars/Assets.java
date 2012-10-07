package ca.sam.games.littleTankWars;

import ca.sam.games.framework.gl.Animation;
import ca.sam.games.framework.Music;
import ca.sam.games.framework.Sound;
import ca.sam.games.framework.gl.Font;
import ca.sam.games.framework.gl.Texture;
import ca.sam.games.framework.gl.TextureRegion;
import ca.sam.games.framework.impl.GLGame;

public class Assets {
   
    
    public static Texture MainMenuBackground;
    public static Texture backgroundItems;
    public static Texture gameScreenItems;
    public static Texture enemies;
    public static Texture gameItems;
    public static Texture fontTex;  
    
    public static TextureRegion gameBackgroundRegion;
    public static TextureRegion MainMenuBackgroundRegion;
    public static TextureRegion menuBackRegion;
    public static TextureRegion clouds1;
    public static TextureRegion clouds2;
    public static TextureRegion gameoverRegion;
    public static TextureRegion highScoreRegion;
    public static TextureRegion grass1;
    public static TextureRegion grass2;
    public static TextureRegion rock;
    public static TextureRegion dirt;
    public static TextureRegion pauseButton;
    public static TextureRegion thumbStick;
    public static TextureRegion thumbStickBase;  
    public static TextureRegion tankTop;
    public static TextureRegion tankBodyHit;
    public static TextureRegion enemy;
    public static TextureRegion basicAmmo;
    public static TextureRegion laserAmmo;
    public static TextureRegion threeShot;
    public static TextureRegion tankMove;
    
    public static TextureRegion debug1;
    public static TextureRegion debug2;
    public static TextureRegion debug3;
    public static TextureRegion debug4;
    public static TextureRegion debug5;
    
    public static Animation enemyMove;
    public static Animation blobMove;
    
    public static Font font;   
	public static Music music;
	public static Sound hit;
	public static Sound gameOver;
	public static Sound select;
	public static Sound shoot;
	public static Sound enemyHit;
	public static Sound powerUp;
      
    
    public static void load(GLGame game) {
       	
    	MainMenuBackground = new Texture(game, "backgrounds.png");
    	MainMenuBackgroundRegion = new TextureRegion(MainMenuBackground, 0, 0, 800, 480);
    	gameoverRegion = new TextureRegion(MainMenuBackground, 0, 482, 800, 480);
    	highScoreRegion = new TextureRegion(MainMenuBackground, 802, 482, 800, 480);
    	clouds1 = new TextureRegion(MainMenuBackground, 0, 964, 800, 250);
    	clouds2 = new TextureRegion(MainMenuBackground, 802, 964 , 800, 400);
    	menuBackRegion = new TextureRegion(MainMenuBackground, 802, 0, 800, 480);
    	
    	/* Game Background Tiles */
        backgroundItems = new Texture(game, "backgroundItems.png");
        grass1 = new TextureRegion(backgroundItems, 1, 33, 32, 32);
        grass2 = new TextureRegion(backgroundItems, 1, 1, 32, 32);
        rock = new TextureRegion(backgroundItems, 33, 1, 32, 32 );
        dirt = new TextureRegion(backgroundItems, 66, 1, 32, 32 );
        
        /* GameScreen Items */
        gameScreenItems = new Texture(game, "gamescreenitems.png");
        thumbStickBase = new TextureRegion(gameScreenItems, 0, 0, 172, 168);
        thumbStick = new TextureRegion(gameScreenItems, 0, 168, 94, 88);
        pauseButton = new TextureRegion(gameScreenItems, 197, 9, 64, 64);
        
        /* Game Items */
        gameItems = new Texture(game, "gameitems.png");             
        tankBodyHit = new TextureRegion(gameItems, 0, 0, 64, 96);
        tankMove = new TextureRegion(gameItems, 0,92,90,66);
        tankTop = new TextureRegion(gameItems, 66, 0, 38, 72);
        basicAmmo = new TextureRegion(gameItems, 106, 0, 8, 8);
        threeShot = new TextureRegion(gameItems, 0, 158, 64, 64);
                
        /* Enemies */
        enemies = new Texture(game, "enemies.png");  
        enemyMove = new Animation(0.2f,
                	new TextureRegion(enemies, 0, 97, 55, 46),
                	new TextureRegion(enemies, 44, 50, 55, 46));
        
        blobMove = new Animation(0.25f,
        		new TextureRegion(enemies, 0, 1, 46, 56),
        		new TextureRegion(enemies, 46, 1, 50, 50),
        		new TextureRegion(enemies, 56, 96, 50, 44),
            	new TextureRegion(enemies, 46, 1, 50, 50));
        
    	fontTex = new Texture(game, "font3.png");
        font = new Font(fontTex, 0, 0, 16, 16, 20);
              
        music = game.getAudio().newMusic("music.ogg");
        music.setLooping(true);
        music.setVolume(0.5f);
        music.play();
        hit = game.getAudio().newSound("hit.ogg");
        gameOver = game.getAudio().newSound("gameover.ogg");
        select = game.getAudio().newSound("select.ogg");
        shoot = game.getAudio().newSound("shoot.ogg");
        enemyHit = game.getAudio().newSound("enemyhit.ogg");
        powerUp = game.getAudio().newSound("powerup.ogg");
    }       
    
    public static void reload() {
    	MainMenuBackground.reload();
    	backgroundItems.reload();
    	gameItems.reload();
    	gameScreenItems.reload();
        enemies.reload();
        fontTex.reload();
//        if(Settings.soundEnabled )
            music.play();
    }
    
    public static void playSound(Sound sound) {
        if(Settings.soundEnabled);
            sound.play(0.4f);
    	}
}

