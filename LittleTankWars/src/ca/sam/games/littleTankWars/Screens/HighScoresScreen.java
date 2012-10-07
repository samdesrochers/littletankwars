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

public class HighScoresScreen extends GLScreen {
    Camera2D guiCam;
    SpriteBatcher batcher;
    Rectangle backBounds;
    Vector2 touchPoint;
    String[] highScores;  
    float xOffset = 0;
    
    public HighScoresScreen(Game game) {
        super(game);
        
        guiCam = new Camera2D(glGraphics, 800, 480);
        backBounds = new Rectangle(0, 0, 800, 480);
        touchPoint = new Vector2();
        batcher = new SpriteBatcher(glGraphics, 100);
        highScores = new String[5];        
        for(int i = 0; i < 5; i++) {
            highScores[i] = (i + 1) + ". " + Settings.highscores[i];
            xOffset = Math.max(highScores[i].length() * Assets.font.glyphWidth, xOffset);
        }        
        xOffset = 400 - xOffset / 2 + Assets.font.glyphWidth / 2;
    }    

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            touchPoint.set(event.x, event.y);
            guiCam.touchToWorld(touchPoint);
            
            if(event.type == TouchEvent.TOUCH_UP) {
                if(OverlapTester.pointInRectangle(backBounds, touchPoint)) {
                    //Assets.playSound(Assets.clickSound);
                    game.setScreen(new MainMenuScreen(game));
                    return;
                }
            }
        }
    }

    @Override
    public void present(float deltaTime) {
        GL10 gl = glGraphics.getGL();        
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);
        guiCam.setViewportAndMatrices();
        
        gl.glEnable(GL10.GL_TEXTURE_2D);
        
        batcher.beginBatch(Assets.MainMenuBackground);
        batcher.drawSprite(400, 240, 800, 480, Assets.highScoreRegion);
        batcher.endBatch();
        
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        
        batcher.beginBatch(Assets.fontTex);
        //batcher.drawSprite(160, 360, 300, 33, Assets.highScoresRegion);
        
        float y = 145;
        for(int i = 4; i >= 0; i--) {
            Assets.font.drawText(batcher, highScores[i], xOffset, y);
            y += Assets.font.glyphHeight + 10;
        }
        
        //batcher.drawSprite(32, 32, 64, 64, Assets.arrow);
        batcher.endBatch();
        
        gl.glDisable(GL10.GL_BLEND);
    }
    
    @Override
    public void resume() {        
    }
    
    @Override
    public void pause() {        
    }

    @Override
    public void dispose() {
    }
}
