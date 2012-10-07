package ca.sam.games.littleTankWars;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ca.sam.games.framework.math.OverlapTester;
import ca.sam.games.framework.math.Vector2;

/*
 * Master class holding all game objects and regulating their interactions
 */

public class World {
	
	// Interface, mostly used to access sound effects
    public interface WorldListener {
          public void enemyHit();

          public void gameOver();

          public void hit();

          public void bulletFire();
          
          public void pickUpPup();

		  int getTime();
    }

    // World's size
    public static final float WORLD_WIDTH = 125/2 ;
    public static final float WORLD_HEIGHT = 75/2;
    
    public static final int WORLD_STATE_RUNNING = 0;
    public static final int WORLD_STATE_NEXT_LEVEL = 1;
    public static final int WORLD_STATE_GAME_OVER = 2;
    public static final int ENEMY_COUNT = 36;
    // public static final Vector2 gravity = new Vector2(0, -12);
    public static final Vector2 friction = new Vector2(-12, -12);

    public final Tank tank;
    public final List<Ammo> AmmoArray;
    public final List<Enemy> EnemyArray;
    public final List<PowerUp> PowerUpArray;
    public final WorldListener listener;
    public final Random rand;
    
    public float lastBulletTime;
    public float enemiesKilled;
    public float difficulty;
    
    public boolean isPowerUpAdding;
    public boolean bossSpawned = false;
    
    public int score;
    public int state;
    
    // Tiles map
    public int[][] level;

    public World(WorldListener listener) {
        this.tank = new Tank(WORLD_WIDTH/2, WORLD_HEIGHT/2);
        this.AmmoArray = new ArrayList<Ammo>();
        this.EnemyArray = new ArrayList<Enemy>();
        this.PowerUpArray = new ArrayList<PowerUp>();
        this.listener = listener;
        this.isPowerUpAdding = true;
        rand = new Random();
        
        this.score = 0;
        this.enemiesKilled = 0;
        this.lastBulletTime = 0;
        level = new int[(int) WORLD_WIDTH][(int) WORLD_HEIGHT];
        this.state = WORLD_STATE_RUNNING;
        for(int i = 0; i < WORLD_WIDTH; i++)
        	for(int j = 0; j < WORLD_HEIGHT; j++)
        		level[i][j] = 0;
        
        generateLevel();
    }

    // Populate the level (tiles map) and adds enemies
	private void generateLevel() {
			
		int tileStyle = 0;
		difficulty = 1.0f;
	    for(int i = 0; i < WORLD_WIDTH; i++)
	    	for(int j = 0; j < WORLD_HEIGHT; j++){
	    		
	    		if(rand.nextFloat() > 0.3)
	    			tileStyle = 1;
	    		else if(rand.nextFloat() <= 0.3 && rand.nextFloat() > 0.2)
	    			tileStyle = 2;
	    		else if(rand.nextFloat() <= 0.2 && rand.nextFloat() > 0.1)
	    			tileStyle = 3;
	    		else if(rand.nextFloat() <= 0.1 && rand.nextFloat() >= 0)
	    			tileStyle = 4;
	    		
	    		level[i][j] = tileStyle;
	    	}
	    
	    for (int i = 0; i < ENEMY_COUNT; i++){
	    	addEnemy();
	    }
	}
	
	public void update(float deltaTime, float speed) {
		updateTank(deltaTime, speed);
		updateAmmo(deltaTime);
		updateEnemies(deltaTime);
		updatePowerUp(deltaTime);
		checkCollisions();
		checkGameOver();
	}
	
	/*
	 * Update concret du Tank
	 */
	private void updateTank(float deltaTime, float speed) {
	    tank.updateVelocity(speed);
	    if(speed == 0)
	    	tank.state = Tank.TANK_IDLE;
	    tank.update(deltaTime);
	}
	
	private void updateAmmo(float deltaTime) {
		synchronized (AmmoArray) {
			for(int i = 0; i < AmmoArray.size(); i ++){
				AmmoArray.get(i).update(deltaTime);
				
				if( AmmoArray.get(i).state == 0 )
					AmmoArray.remove(i);
			}
		}
	}
	
	private void updatePowerUp(float deltaTime) {
		synchronized (PowerUpArray) {
			for(int i = 0; i < PowerUpArray.size(); i ++){
				PowerUpArray.get(i).update(deltaTime);
			}
		}
		
		// Add power up every 10 secs
		if( listener.getTime()%10 == 0 && isPowerUpAdding){
			addPowerUp(PowerUp.PowerUp_TYPE_THREESHOT);
			isPowerUpAdding = false;
		} else 	if( listener.getTime()%10 == 1){
			isPowerUpAdding = true;
		}
	}
	
	
	private void updateEnemies(float deltaTime) {
	    int len = EnemyArray.size();
	    for (int i = 0; i < len; i++) {
	        Enemy enemy = EnemyArray.get(i);
	    	float angle = (float) Math.atan((tank.position.y - enemy.position.y) / (tank.position.x - enemy.position.x)) * 180 / 3.146f ;
	    	
	    	// Cadran 2
	    	if(tank.position.x < enemy.position.x && tank.position.y > enemy.position.y)
	    		angle += 180;
	    	// Cadran 3
	    	else if(tank.position.x < enemy.position.x && tank.position.y < enemy.position.y)
	    		angle += 180;
	    	// Cadran 4
	    	else if(tank.position.x > enemy.position.x && tank.position.y < enemy.position.y)
	    		angle += 360;
	        enemy.rotationAngle = angle;
	        enemy.update(deltaTime);
	        
	        if(enemy.state == Enemy.Enemy_DEAD){
	        	
	        	if(enemy.enemyType == Enemy.Enemy_TYPE_BOSS){
	        		bossSpawned = false;
	        		difficulty += 0.5f;
	        	}
	        	
	        	EnemyArray.remove(enemy);
	        	i = EnemyArray.size();	
	        	addEnemy(); 
	        }
	    }
	}
	
	private void checkCollisions() {
	    checkEnemyCollisions();
	    checkAmmoCollisions();
	    checkPowerUpCollisions();
	}
	
	// Enemy - Tank collision
	private void checkEnemyCollisions() {
	    int len = EnemyArray.size();
	    synchronized (EnemyArray) {
	    	for (int i = 0; i < len; i++) {
		        Enemy enemy = EnemyArray.get(i);
		        if (OverlapTester.overlapRectangles(enemy.bounds, tank.bounds)) {
		        	
		        	if(enemy.enemyType != Enemy.Enemy_TYPE_BOSS)
		        		EnemyArray.remove(enemy);
		        	
		        	len = EnemyArray.size();
		        	tank.life -= enemy.damage;
		            tank.hitEnemy();
		            listener.hit();
		            addEnemy();
		        }
		    }
		}   
	}
	
	// Enemy - Ammo collision
	private void checkAmmoCollisions() {
	    int elen = EnemyArray.size();
	    int alen = AmmoArray.size();
	    synchronized (EnemyArray) {
		    for (int i = 0; i < elen; i++) {
		        Enemy enemy = EnemyArray.get(i);
		        synchronized (enemy) {
		        	for (int j = 0; j < alen; j++){
			        	Ammo ammo = AmmoArray.get(j);
				        if (OverlapTester.overlapRectangles(ammo.bounds, enemy.bounds)) {
				            AmmoArray.remove(ammo);
				            alen = AmmoArray.size();
				            score += enemy.score;
				            enemy.life -= ammo.weaponDamage; 
				            listener.enemyHit();
				            enemiesKilled++;
				        }
			        }
				} 
		    }
		}
	
	}
	
	// Powerup - Tank collision
	private void checkPowerUpCollisions() {
	    int len = PowerUpArray.size();
	    synchronized (PowerUpArray) {
	    	for (int i = 0; i < len; i++) {
		        PowerUp pup = PowerUpArray.get(i);
		        if (OverlapTester.overlapRectangles(pup.bounds, tank.bounds)) {
		        	tank.hitPowerUp(pup.PowerUpType);
		        	score += pup.score;
		        	PowerUpArray.remove(pup);
		        	len = PowerUpArray.size();
		            listener.pickUpPup();
		            addEnemy();
		        }
		    }
		}   
	}
	
	private void checkGameOver() {
	           	  	
	    	if (tank.life <=  0) {
	            state = WORLD_STATE_GAME_OVER;
	            listener.gameOver();
	        }
	    }
	
	private void addEnemy(){
		
		int type = 0;
		
		if(rand.nextFloat() > 0.5)
			type = Enemy.Enemy_TYPE_BASIC;
		else if(rand.nextFloat() <= 0.5 && rand.nextFloat() > 0.2)
			type = Enemy.Enemy_TYPE_BLOB;
		else if(rand.nextFloat() <= 0.2 && rand.nextFloat() >= 0)
			type = Enemy.Enemy_TYPE_ROCKING;
	
		if( EnemyArray.size() < (ENEMY_COUNT/2) * difficulty && !bossSpawned){
			for(int i = 0; i < difficulty; ++i ){
				
				/* Enemy positioning algorithm */
				float directionX = rand.nextFloat();
				if(directionX > 0.5)
					directionX = -1;
				else
					directionX = 1;
				float directionY = rand.nextFloat();
				if(directionY > 0.5)
					directionY = -1;
				else
					directionY = 1;
		    	int posX = rand.nextInt( (int) WORLD_WIDTH );
		    	int posY = rand.nextInt( (int) WORLD_HEIGHT );
		    	
		    	if(posX < tank.position.x + WorldRenderer.FRUSTUM_WIDTH/2 && 
		    	   posX > tank.position.x - WorldRenderer.FRUSTUM_WIDTH/2){
		    		posX += directionX * WorldRenderer.FRUSTUM_WIDTH;
		    	}
		    	if (posY < tank.position.y + WorldRenderer.FRUSTUM_HEIGHT/2 && 
		    	    posY > tank.position.y - WorldRenderer.FRUSTUM_HEIGHT/2){
		    		posY += directionY * WorldRenderer.FRUSTUM_HEIGHT;
		    	}
		    	
		    	/* Boss spawning algorithm */
		    	if(listener.getTime() % 30 == 0 && listener.getTime() > 25 && !bossSpawned) {
		    		bossSpawned = true;
		    		type = Enemy.Enemy_TYPE_BOSS;
		    	}
		    		
		    	EnemyArray.add( new Enemy( posX , posY , type, difficulty) );
			}
		}
	}
	
	public void addBullet(float angle){
		synchronized (AmmoArray) {
			
			// Condition to regulate the bullets being fired
			if(lastBulletTime <= 0.0f){
				if(tank.currentWeapon == Tank.TYPE_BASIC_BULLET){
				AmmoArray.add(new Ammo(tank.position.x + (float)(Math.cos(angle/180*3.146)), 
											   tank.position.y + (float)(Math.sin(angle/180*3.146)),
											   angle,
											   tank.currentWeapon));
				
				} else if (tank.currentWeapon == Tank.TYPE_THREESHOT) {
					AmmoArray.add(new Ammo(tank.position.x + (float)(Math.cos(angle/180*3.146)), 
							   tank.position.y + (float)(Math.sin(angle/180*3.146)),
							   angle + 5,
							   tank.currentWeapon));
					AmmoArray.add(new Ammo(tank.position.x + (float)(Math.cos((angle)/180*3.146)), 
							   tank.position.y + (float)(Math.sin(angle/180*3.146)),
							   angle,
							   tank.currentWeapon));
					AmmoArray.add(new Ammo(tank.position.x + (float)(Math.cos(angle)/180*3.146), 
							   tank.position.y + (float)(Math.sin(angle/180*3.146)),
							   angle - 5,
							   tank.currentWeapon));
					
				}
				lastBulletTime = 0.10f;
				listener.bulletFire();
			}
			else
				lastBulletTime -= 0.025;
		}
	}
	
	public void addPowerUp(int type){
	    	synchronized (PowerUpArray) {
	    		
				float directionX = rand.nextFloat();
				if(directionX > 0.5) {
					directionX = -1;
				}
				else {
					directionX = 1;
				}
				
				float directionY = rand.nextFloat();
				if(directionY > 0.5) {
					directionY = -1;
				}
				else {
					directionY = 1;
				}
				
		    	int posX = rand.nextInt( (int) WORLD_WIDTH );
		    	int posY = rand.nextInt( (int) WORLD_HEIGHT );
		    	
		    	if(posX < tank.position.x + WorldRenderer.FRUSTUM_WIDTH/2 && 
		    	   posX > tank.position.x - WorldRenderer.FRUSTUM_WIDTH/2){
		    		posX += directionX * WorldRenderer.FRUSTUM_WIDTH;
		    	}
		    	if (posY < tank.position.y + WorldRenderer.FRUSTUM_HEIGHT/2 && 
		    	    posY > tank.position.y - WorldRenderer.FRUSTUM_HEIGHT/2){
		    		posY += directionY * WorldRenderer.FRUSTUM_HEIGHT;
		    	}
	    		
	    		if(type == PowerUp.PowerUp_TYPE_THREESHOT){
	    			PowerUpArray.add( new PowerUp(posX, posY, type) );
	    		}
	    	}
	    }
	}
