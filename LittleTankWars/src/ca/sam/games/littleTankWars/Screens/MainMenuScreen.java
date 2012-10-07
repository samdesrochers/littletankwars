package ca.sam.games.littleTankWars.Screens;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import ca.sam.games.framework.Game;
import ca.sam.games.framework.Input.TouchEvent;
import ca.sam.games.framework.gl.Camera2D;
import ca.sam.games.framework.gl.SpriteBatcher;
import ca.sam.games.framework.impl.GLScreen;
import ca.sam.games.framework.math.OverlapTester;
import ca.sam.games.framework.math.Rectangle;
import ca.sam.games.framework.math.Vector2;
import ca.sam.games.littleTankWars.Assets;
import ca.sam.games.littleTankWars.Settings;

public class MainMenuScreen extends GLScreen {
    Camera2D guiCam;
    SpriteBatcher batcher;
    Rectangle soundBounds;
    Rectangle playBounds;
    Rectangle highscoresBounds;
    Rectangle helpBounds;
    Vector2 touchPoint;
    float posCloudX1;
    float posCloudX2;

    public MainMenuScreen(Game game) {
        super(game);
        
        // Main GL camera
        guiCam = new Camera2D(glGraphics, 800, 480);
        
        // Batcher that will draw every sprites
        batcher = new SpriteBatcher(glGraphics, 100);
        
        // Bounding Rectangle around "play button"
        playBounds = new Rectangle(80, 120, 280, 100);
        
        // Bounding Rectangle around "Highscores button"
        highscoresBounds = new Rectangle(500, 70, 180, 50);
        touchPoint = new Vector2();
        
        // Parallaxing backgrounds
        posCloudX1 = 400;
        posCloudX2 = 400;
        
        // Load previous game settings (sound enabled on/off)
        Settings.load(game.getFileIO());
    }       

    @Override
    public void update(float deltaTime) {
    	
    	// Acquire all of touch events
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
        	
        	// Cycle through every touch events
            TouchEvent event = touchEvents.get(i);
            
            // Assign touch point after conversion to our World coordinates
            touchPoint.set(event.x, event.y);
            guiCam.touchToWorld(touchPoint);
            
            // Detect touch on specific bounding rects
            if(event.type == TouchEvent.TOUCH_UP) {
                if(OverlapTester.pointInRectangle(playBounds, touchPoint)) {
                	Assets.playSound(Assets.select);
                    game.setScreen(new GameScreen(game));
                    return;
                }
                if(OverlapTester.pointInRectangle(highscoresBounds, touchPoint)) {
                	Assets.playSound(Assets.select);
                    game.setScreen(new HighScoresScreen(game));
                    return;
                }    
            }
        }
        
        // Parallaxing backgrounds
        posCloudX1 -= deltaTime*20;
        posCloudX2 += deltaTime*20;
        if(posCloudX1 < 0)	
        	posCloudX1 = 800;
        if(posCloudX2 > 800)
        	posCloudX2 = 0;
    }

    @Override
    // Draw method - draws the present assets
    public void present(float deltaTime) {
    	GL10 gl = glGraphics.getGL();
	    gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	    gl.glEnable(GL10.GL_TEXTURE_2D); 
	    guiCam.setViewportAndMatrices();
	    
	    gl.glEnable(GL10.GL_BLEND);
	    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glColor4f(1, 1, 1, 1);     
	    
        // Select the assets batch and draw them
        batcher.beginBatch(Assets.MainMenuBackground);
        batcher.drawSprite(400, 240, 800, 480, Assets.menuBackRegion);
        batcher.drawSprite(posCloudX1, 380, 1600, 400, Assets.clouds1);
        batcher.drawSprite(posCloudX2, 280, 1600, 400, Assets.clouds2);
        batcher.drawSprite(400, 240, 800, 480, Assets.MainMenuBackgroundRegion);
        batcher.endBatch();      
        
        gl.glDisable(GL10.GL_BLEND);
    }
    
    @Override
    public void pause() {        
        Settings.save(game.getFileIO());
    }

    @Override
    public void resume() {        
    }       

    @Override
    public void dispose() {        
    }
}