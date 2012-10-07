package ca.sam.games.littleTankWars;

import javax.microedition.khronos.opengles.GL10;

import ca.sam.games.framework.gl.Animation;
import ca.sam.games.framework.gl.Camera2D;
import ca.sam.games.framework.gl.SpriteBatcher;
import ca.sam.games.framework.gl.TextureRegion;
import ca.sam.games.framework.impl.GLGraphics;

/*
 * Classe permettant de faire le rendu concrets des 
 * objets du jeu, par l'accès d'un "batcher", qui doit
 * être utilisé comme suit : (begin) - dessin - (end)
 */
public class WorldRenderer {
    static final float FRUSTUM_WIDTH = 25;
    static final float FRUSTUM_HEIGHT = 15;
	
    GLGraphics glGraphics;
    World world;
    Camera2D cam;
    SpriteBatcher batcher;    
    
    // Constructor of the world renderer
    // Draws every game objets
    public WorldRenderer(GLGraphics glGraphics, SpriteBatcher batcher, World world) {
        this.glGraphics = glGraphics;
        this.world = world;
        this.cam = new Camera2D(glGraphics, FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        this.batcher = batcher;        
    }

    public void render() {
        cam.setViewportAndMatrices();
        renderBackground();
        renderObjects();        
    }
    
    public void renderBackground() {
       batcher.beginBatch(Assets.backgroundItems); 
       TextureRegion asset = null;
       
       // Tiles map rednering!
       for(int i = 0; i < World.WORLD_WIDTH; i++){
        	for(int j = 0; j < World.WORLD_HEIGHT; j++){
        		
        		if(world.level[i][j] == 1)
        			asset = Assets.grass1;       
        		else if(world.level[i][j] == 2)
        			asset = Assets.grass2;     
        		else if(world.level[i][j] == 3)
        			asset = Assets.rock;
        		else
        			asset = Assets.dirt;
       
        		batcher.drawSprite(i, j , 1.0f, 1.0f, asset);
        	}
       }
        
        batcher.endBatch();
    }
    
    public void renderObjects() {
        GL10 gl = glGraphics.getGL();
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        
        // Smoothing games objects
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        
        
        renderTank();
        renderAmmo();
        renderEnemies();
        renderPowerUp();
        
        gl.glDisable(GL10.GL_BLEND);
    }
    
    private void renderTank() {

    	/* Anti-blending */
    	GL10 gl = glGraphics.getGL();
    	gl.glColor4f(1, 1, 1, 1);
    	
    	batcher.beginBatch(Assets.gameItems);
    	
    	if(world.tank.position.x < World.WORLD_WIDTH - 3.2*4 && world.tank.position.x > 3.2*4)
    		cam.position.x = world.tank.position.x;
    	if(world.tank.position.y < World.WORLD_HEIGHT - 3.2*4 && world.tank.position.y > 3.2*4)
    		cam.position.y = world.tank.position.y;
    	
   // Draw according to different states
        if(world.tank.state == Tank.TANK_MOVE || world.tank.state == Tank.TANK_IDLE){
	        TextureRegion keyFrame = Assets.tankMove;
	        batcher.drawSprite(world.tank.position.x, world.tank.position.y, 2, 1.5f,world.tank.rotationAngle, keyFrame);
        }
        else if(world.tank.state == Tank.TANK_HIT){
        	batcher.drawSprite(world.tank.position.x, world.tank.position.y , 1.5f, 2, world.tank.rotationAngle + 90, Assets.tankBodyHit);
        }
        
        batcher.drawSprite(world.tank.position.x , 
        				   world.tank.position.y ,
        				   0.8f, 1.4f, world.tank.lastAmmoAngle + 90, Assets.tankTop);
        
        batcher.endBatch();
    }
   
    private void renderAmmo() {
    	
    	try {
    		
    		batcher.beginBatch(Assets.gameItems);
        	
            int len = world.AmmoArray.size();
            for(int i = 0; i < len; i++) {
                Ammo ammo = world.AmmoArray.get(i);         
                     
                if(world.tank.currentWeapon == Tank.TYPE_BASIC_BULLET || world.tank.currentWeapon == Tank.TYPE_THREESHOT)
                	batcher.drawSprite(ammo.position.x, ammo.position.y, 
                					   Ammo.BASIC_BULLET_WIDTH, Ammo.BASIC_BULLET_WIDTH, 
                					   ammo.rotationAngle + 90, Assets.basicAmmo);
                
                else if(world.tank.currentWeapon == Tank.TYPE_FLAMETHROWER)
                	batcher.drawSprite(ammo.position.x, ammo.position.y, 
                				       5.0f, 1.0f, 
                				       ammo.rotationAngle, Assets.laserAmmo);
            }
            
            batcher.endBatch();
            
		} catch (Exception e) {}
    }
    
    private void renderEnemies() {
    	
    	batcher.beginBatch(Assets.enemies);
    	
        int len = world.EnemyArray.size();
        for(int i = 0; i < len; i++) {
            Enemy enemy = world.EnemyArray.get(i);  
            
            if(enemy.enemyType == Enemy.Enemy_TYPE_BASIC){
            	TextureRegion keyFrame = Assets.enemyMove.getKeyFrame(enemy.stateTime, Animation.ANIMATION_LOOPING);
            	batcher.drawSprite(enemy.position.x, enemy.position.y, 1.4f, 1.4f,enemy.rotationAngle - 90, keyFrame);
            	
            } else if ( enemy.enemyType == Enemy.Enemy_TYPE_BLOB ) {
            	TextureRegion keyFrame = Assets.blobMove.getKeyFrame(enemy.stateTime, Animation.ANIMATION_LOOPING);
            	batcher.drawSprite(enemy.position.x, enemy.position.y, 1.4f, 1.4f, keyFrame);
            	
            }   else if ( enemy.enemyType == Enemy.Enemy_TYPE_BOSS ) {
            	TextureRegion keyFrame = Assets.enemyMove.getKeyFrame(enemy.stateTime, Animation.ANIMATION_LOOPING);
            	batcher.drawSprite(enemy.position.x, enemy.position.y, 5, 5, enemy.rotationAngle - 90, keyFrame);
            }
        }
       
        batcher.endBatch();

    }
  
    private void renderPowerUp() {
    	try {	
    		batcher.beginBatch(Assets.gameItems);
        	
            int len = world.PowerUpArray.size();
            for(int i = 0; i < len; i++) {
                PowerUp pup = world.PowerUpArray.get(i);
                batcher.drawSprite(pup.position.x, pup.position.y, PowerUp.PowerUp_BASIC_WIDTH, PowerUp.PowerUp_BASIC_HEIGHT,  Assets.threeShot);
            }
            
            batcher.endBatch();
    		
		} catch (Exception e) {}
    	
    }
}

