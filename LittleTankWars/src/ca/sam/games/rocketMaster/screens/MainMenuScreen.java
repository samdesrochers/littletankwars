package ca.sam.games.rocketMaster.screens;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import ca.sam.games.framework.Game;
import ca.sam.games.framework.Input.TouchEvent;
import ca.sam.games.framework.gl.Camera2D;
import ca.sam.games.framework.gl.SpriteBatcher;
import ca.sam.games.framework.impl.GLScreen;
import ca.sam.games.framework.math.Vector2;
import ca.sam.games.rocketMaster.Assets;

public class MainMenuScreen extends GLScreen {
    Camera2D guiCam;
    SpriteBatcher batcher;
    Vector2 touchPoint;

    public MainMenuScreen(Game game) {
        super(game);
        
        // Main GL camera
        guiCam = new Camera2D(glGraphics, 800, 480);
        
        // Batcher that will draw every sprites
        batcher = new SpriteBatcher(glGraphics, 100);
        
        // Touch location vector
        touchPoint = new Vector2();
        
        
        // Load previous game settings (sound enabled on/off)
        //Settings.load(game.getFileIO());
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
            }
        }
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
        batcher.beginBatch(Assets.mainMenuItems);
        batcher.drawSprite(400, 240, 800, 480, Assets.mainMenuBackground);
        batcher.endBatch();      
        
        gl.glDisable(GL10.GL_BLEND);
    }
    
    @Override
    public void pause() {        
        //Settings.save(game.getFileIO());
    }

    @Override
    public void resume() {        
    }       

    @Override
    public void dispose() {        
    }
}