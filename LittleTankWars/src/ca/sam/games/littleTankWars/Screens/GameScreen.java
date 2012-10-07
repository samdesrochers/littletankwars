package ca.sam.games.littleTankWars.Screens;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;
import ca.sam.games.framework.Game;
import ca.sam.games.framework.Input.TouchEvent;
import ca.sam.games.framework.gl.Camera2D;
import ca.sam.games.framework.gl.FPSCounter;
import ca.sam.games.framework.gl.SpriteBatcher;
import ca.sam.games.framework.impl.GLScreen;
import ca.sam.games.framework.math.OverlapTester;
import ca.sam.games.framework.math.Rectangle;
import ca.sam.games.framework.math.Vector2;
import ca.sam.games.littleTankWars.Assets;
import ca.sam.games.littleTankWars.Settings;
import ca.sam.games.littleTankWars.World;
import ca.sam.games.littleTankWars.WorldRenderer;
import ca.sam.games.littleTankWars.World.WorldListener;

public class GameScreen extends GLScreen {
	static final int LEFT_DPAD_CENTER = 108;
	static final int RIGHT_DPAD_CENTER_X = 692;
	static final int RIGHT_DPAD_CENTER_Y = LEFT_DPAD_CENTER;
    static final int GAME_READY = 0;    
    static final int GAME_RUNNING = 1;
    static final int GAME_PAUSED = 2;
    static final int GAME_LEVEL_END = 3;
    static final int GAME_OVER = 4;
  
    int state;
    Camera2D guiCam;
    Vector2 touchPoint;
    float velocity;
    float angle;
    SpriteBatcher batcher;  
    
    World world;
    WorldListener worldListener;
    WorldRenderer renderer;
    
    Rectangle backBounds;
    Rectangle pauseBounds;
    Rectangle leftDpad;
    Rectangle rightDpad;
    
    int lastScore;
    public int timeScore;
    float lastBulletTime;
    float startTime;
    float elapsedTime;
    int displayScore;
    float bulletCount;
    Vector2 leftThumbStickPos;
    Vector2 rightThumbStickPos;
    FPSCounter fpsCounter;
    
	boolean gameOverTouch = false;
    
    public GameScreen(Game game) {
        super(game);
        state = GAME_READY;
        guiCam = new Camera2D(glGraphics, 800, 480);
        touchPoint = new Vector2();
        batcher = new SpriteBatcher(glGraphics, 5000);
        worldListener = new WorldListener() {
            @Override
            public void hit() {            
                Assets.playSound(Assets.hit);
            }

			@Override
			public void bulletFire() {
				Assets.playSound(Assets.shoot);	
			}

			@Override
			public void gameOver() {
				Assets.playSound(Assets.gameOver);	
			}

			@Override
			public void enemyHit() {
				Assets.playSound(Assets.enemyHit);	
			}
			@Override
			public int getTime() {
				return (int)elapsedTime;
			}

			@Override
			public void pickUpPup() {
				Assets.playSound(Assets.powerUp);
				
			}
        };
        world = new World(worldListener);
        renderer = new WorldRenderer(glGraphics, batcher, world);
        rightThumbStickPos = new Vector2();
        leftThumbStickPos = new Vector2();
        pauseBounds = new Rectangle(56, 400 , 48, 48);
        leftDpad = new Rectangle(12, 12 , 196, 196);
        rightDpad = new Rectangle(604, 12 , 196, 196);
        backBounds = new Rectangle(20, 20, 120, 80);
        fpsCounter = new FPSCounter();
        lastBulletTime = 0;
        velocity = 0;
        startTime = System.currentTimeMillis();
        elapsedTime = 0;
        displayScore = 0;
        bulletCount = 0;
    }

	@Override
	// Main update method : calls other update states methods
	public void update(float deltaTime) {
	    if(deltaTime > 0.1f)
	        deltaTime = 0.1f;
	    
	    switch(state) {
	    case GAME_READY:
	        updateReady();
	        break;
	    case GAME_RUNNING:
	        updateRunning(deltaTime);
	        break;
	    case GAME_PAUSED:
	        updatePaused();
	        break;
	    case GAME_OVER:
	        updateGameOver();
	        break;
	    }
	}
	
	private void updateReady() {
    	leftThumbStickPos.x = LEFT_DPAD_CENTER;
    	leftThumbStickPos.y = LEFT_DPAD_CENTER+2;
    	rightThumbStickPos.x = RIGHT_DPAD_CENTER_X-2;
    	rightThumbStickPos.y = RIGHT_DPAD_CENTER_Y+2;
	    if(game.getInput().getTouchEvents().size() > 0) {
	        state = GAME_RUNNING;
	    }
	}
	
	private void updateRunning(float deltaTime) {
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
	        touchPoint.set(event.x, event.y);
	        guiCam.touchToWorld(touchPoint);
	        	        
	        if(event.type == TouchEvent.TOUCH_DRAGGED ||event.type == TouchEvent.TOUCH_DOWN){     
	        	// Left DPad intesects
		        if(OverlapTester.pointInRectangle(leftDpad, touchPoint)) {
		        	float speedX = touchPoint.x - LEFT_DPAD_CENTER;
		        	float speedY = touchPoint.y - LEFT_DPAD_CENTER;
		        	
		        	leftThumbStickPos.x = touchPoint.x;
		        	leftThumbStickPos.y = touchPoint.y;
		        	
		        	velocity = android.util.FloatMath.sqrt((float)(( speedX * speedX ) + ( speedY * speedY )));
		        	angle = (float) Math.atan((touchPoint.y - LEFT_DPAD_CENTER) / (touchPoint.x - LEFT_DPAD_CENTER)) * 180 / 3.146f ;
		        	
		        	// Cadran 2
		        	if(touchPoint.x < LEFT_DPAD_CENTER && touchPoint.y > LEFT_DPAD_CENTER)
		        		angle += 180;
		        	// Cadran 3
		        	else if(touchPoint.x < LEFT_DPAD_CENTER && touchPoint.y < LEFT_DPAD_CENTER)
		        		angle += 180;
		        	// Cadran 4
		        	else if(touchPoint.x > LEFT_DPAD_CENTER && touchPoint.y < LEFT_DPAD_CENTER)
		        		angle += 360;
		        	
		        	world.tank.rotationAngle = angle;

		        }		        
		        // Right DPad intersects
		        if(OverlapTester.pointInRectangle(rightDpad, touchPoint)) {		        	
		        	//if(lastBulletTime < 0.2f){
		        		
			        	rightThumbStickPos.x = touchPoint.x;
			        	rightThumbStickPos.y = touchPoint.y;
		        		
			        	angle = (float) Math.atan((touchPoint.y - RIGHT_DPAD_CENTER_Y) / (touchPoint.x - RIGHT_DPAD_CENTER_X)) * 180 / 3.146f ;	        	
			        	// Cadran 2
			        	if(touchPoint.x < RIGHT_DPAD_CENTER_X && touchPoint.y > RIGHT_DPAD_CENTER_Y)
			        		angle += 180;
			        	// Cadran 3
			        	else if(touchPoint.x < RIGHT_DPAD_CENTER_X && touchPoint.y < RIGHT_DPAD_CENTER_Y)
			        		angle += 180;
			        	// Cadran 4
			        	else if(touchPoint.x > RIGHT_DPAD_CENTER_X && touchPoint.y < RIGHT_DPAD_CENTER_Y)
			        		angle += 360;
			        	Log.d("Angle : ", " : " + angle );
			        	
			        	world.tank.lastAmmoAngle = angle;
			        	// Adds a new bullet of the tank's current weapon type
			        	world.addBullet(angle);
			        	bulletCount ++;
			       
		        }	
	        }
	        else if(event.type == TouchEvent.TOUCH_UP){
	        	velocity = 0;
	        	world.tank.stateTime = 0;
	        	world.lastBulletTime = 0;
	        	leftThumbStickPos.x = LEFT_DPAD_CENTER;
	        	leftThumbStickPos.y = LEFT_DPAD_CENTER;
	        	rightThumbStickPos.x = RIGHT_DPAD_CENTER_X;
	        	rightThumbStickPos.y = RIGHT_DPAD_CENTER_Y;
	        } 
	    }    
	    elapsedTime += deltaTime;
	    world.update(deltaTime, velocity);
	    timeScore = (int)elapsedTime*100;
	    lastScore = world.score;

	    if(world.state == World.WORLD_STATE_NEXT_LEVEL) {
	        state = GAME_LEVEL_END;        
	    }
	    if(world.state == World.WORLD_STATE_GAME_OVER) {
	        state = GAME_OVER;
	        Settings.addScore(timeScore + lastScore);
	        Settings.save(game.getFileIO());
	    }
	}
	
	private void updatePaused() {
		game.setScreen(new MainMenuScreen(game));
	}	
	
	private void updateGameOver() {
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int finalScore = (timeScore + lastScore);
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {      
	        TouchEvent event = touchEvents.get(i);
	        touchPoint.set(event.x, event.y);
	        guiCam.touchToWorld(touchPoint);
	        if(event.type == TouchEvent.TOUCH_UP){        	
		        if(gameOverTouch){
		        	if(OverlapTester.pointInRectangle(backBounds, touchPoint) && displayScore == finalScore){
		        		game.setScreen(new MainMenuScreen(game));
		        		return;
		        	}
		        }
		        gameOverTouch = true;
		        displayScore = finalScore;
	        }
	    }
	    if( displayScore < finalScore)
	    	displayScore += 100;
	}

	@Override
	public void present(float deltaTime) {
	    GL10 gl = glGraphics.getGL();
	    gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	    gl.glEnable(GL10.GL_TEXTURE_2D);
	    
	    renderer.render();
	    
	    guiCam.setViewportAndMatrices();
	    gl.glEnable(GL10.GL_BLEND);
	    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	    gl.glColor4f(1, 1, 1, 0.5f);
	    batcher.beginBatch(Assets.gameScreenItems);
	    switch(state) {
	    case GAME_READY:
	        presentReady();
	        break;
	    case GAME_RUNNING:
	        presentRunning();
	        break;
	    case GAME_PAUSED:
	        presentPaused();
	        break;
	    case GAME_LEVEL_END:
	        presentLevelEnd();
	        break;
	    case GAME_OVER:
	        presentGameOver();
	        break;
	    }
	    batcher.endBatch();
	    gl.glDisable(GL10.GL_BLEND);
	    fpsCounter.logFrame();
	}
	
	private void presentReady() {

        batcher.drawSprite(110, 110, 140, 140, Assets.thumbStickBase);
        batcher.drawSprite( 690, 110, 140, 140, Assets.thumbStickBase);
        

        batcher.drawSprite( rightThumbStickPos.x , rightThumbStickPos.y , 88, 88, Assets.thumbStick);
        batcher.drawSprite( leftThumbStickPos.x, leftThumbStickPos.y, 88, 88, Assets.thumbStick);
        batcher.endBatch(); 
        
        batcher.beginBatch(Assets.fontTex);      
        Assets.font.drawText(batcher, "TAP TO BEGIN!", 300, 350);
        Assets.font.drawText(batcher, "---LEVEL 1---", 300, 330);
        Assets.font.drawText(batcher, "ARMOR:" +world.tank.life+"%", 50, 440);
        Assets.font.drawText(batcher, "SCORE:", 50, 420); 
        batcher.endBatch();  
	}
	
	private void presentRunning() {

        batcher.drawSprite(110, 110, 140, 140, Assets.thumbStickBase);
        batcher.drawSprite( 690, 110, 140, 140, Assets.thumbStickBase);
        	

        batcher.drawSprite( rightThumbStickPos.x , rightThumbStickPos.y , 88, 88, Assets.thumbStick);
        batcher.drawSprite( leftThumbStickPos.x, leftThumbStickPos.y, 88, 88, Assets.thumbStick);
        batcher.endBatch(); 
        
        batcher.beginBatch(Assets.fontTex);      
        Assets.font.drawText(batcher, "ARMOR:" +world.tank.life+"%", 50, 440);
        Assets.font.drawText(batcher, "SCORE:" +world.score, 50, 420); 
        Assets.font.drawText(batcher, "TIME:" +(int) elapsedTime, 50, 400); 
        batcher.endBatch();   
	    
	}
	
	private void presentPaused() { 
		//GL10 gl = glGraphics.getGL();
		//gl.glDisable(gl.GL_BLEND);
		batcher.drawSprite(80 + 24, 400 + 24, 48, 48, Assets.pauseButton);
		
	    //Assets.font.drawText(batcher, scoreString, 16, 480-20);
	}
	
	private void presentLevelEnd() {
		batcher.beginBatch(Assets.MainMenuBackground);
	    batcher.drawSprite(400, 240, 800, 480, Assets.MainMenuBackgroundRegion);
	    batcher.endBatch();
	}
	
	private void presentGameOver() {
		
		GL10 gl = glGraphics.getGL();
	    gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	    gl.glColor4f(1, 1, 1, 0.4f);
		batcher.beginBatch(Assets.MainMenuBackground);
	    batcher.drawSprite(400, 240, 800, 480, Assets.gameoverRegion);
	    batcher.endBatch();
	    
	    batcher.beginBatch(Assets.fontTex);
	    Assets.font.drawText(batcher, "GAME  SCORE:" + lastScore, 280, 200 + 60);
	    Assets.font.drawText(batcher, "TIME  SCORE:" + timeScore, 280, 175 + 60);
	    Assets.font.drawText(batcher, "BULLETS FIRED:" + bulletCount, 280, 150 + 60);
	    Assets.font.drawText(batcher, "ENEMIES HIT:" + world.enemiesKilled, 280, 125 + 60);
	    Assets.font.drawText(batcher, "ACCURACY:" + Math.round((world.enemiesKilled / bulletCount)*100) + "%", 280, 100 + 60);
	    Assets.font.drawText(batcher, "***************", 280, 75 + 60);
	    Assets.font.drawText(batcher, "TOTAL SCORE:" + 
	    								Math.round(displayScore + 
	    								(world.enemiesKilled / bulletCount) * 
	    								world.enemiesKilled), 
	    								280, 50 + 60);
	    batcher.endBatch();
	}

    @Override
    public void pause() {
        //if(state == GAME_RUNNING)
            //state = GAME_RUNNING;
    }

    @Override
    public void resume() {        
    }

    @Override
    public void dispose() {       
    }
}

