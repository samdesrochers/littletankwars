package ca.sam.games.littleTankWars;

import ca.sam.games.framework.DynamicGameObject;
public class Tank extends DynamicGameObject{

    public static final int TANK_MOVE = 0;
    public static final int TANK_IDLE = 1;
    public static final int TANK_BOOST = 2;
    public static final int TANK_HIT = 3; 
    public static final int TANK_DEAD = 4; 
    public static final float TANK_MOVE_VELOCITY = 6.5f;
    
    public static final float TANK_WIDTH = 1.5f;
    public static final float TANK_HEIGHT = 2.0f;
    public static final float TANK__TOP_WIDTH = 0.8f;
    public static final float TANK_TOP_HEIGHT = 1.4f;
    
	public static final int TYPE_BASIC_BULLET = 0;
	public static final int TYPE_THREESHOT = 1;
	public static final int TYPE_FLAMETHROWER = 2;
	
    int state;
    public int life;
    public float rotationAngle;
    public float stateTime;
    int currentWeapon;
    public float lastAmmoAngle;
    float powerUpTimer;

    // Super constructor creates a bounding box around the object
    public Tank(float x, float y) {
        super(x, y, TANK_WIDTH, TANK_HEIGHT);
        state = TANK_IDLE;
        currentWeapon = TYPE_BASIC_BULLET;
        rotationAngle = 0;
        lastAmmoAngle = 0;
        stateTime = 0; 
        life = 100;
        powerUpTimer = 0;
    }

    // Update method
    public void update(float deltaTime) {     

    	// Friction
        velocity.add(World.friction.x * deltaTime, World.friction.y * deltaTime );
        
        // Move bounds of the bounding rectangle
        bounds.lowerLeft.set(position).sub(bounds.width / 2, bounds.height / 2);
        
        // If tank is not moving
        if(state == TANK_IDLE){
        	velocity.x = 0; 
        	velocity.y = 0;
        }    
        
        // Check boundaries if tank is out of the world limits
        if(this.position.x > World.WORLD_WIDTH )
        	this.position.x = 0;
        if(this.position.x < 0)
        	this.position.x = World.WORLD_WIDTH;
        if(this.position.y > World.WORLD_HEIGHT)
        	this.position.y = 0;
        if(this.position.y < 0)
        	this.position.y = World.WORLD_HEIGHT;
        
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        
        powerUpTimer -= deltaTime;
        
        if(powerUpTimer <= 0){
        	currentWeapon = Tank.TYPE_BASIC_BULLET;
        	powerUpTimer = 0;
        }
        
        stateTime += deltaTime;
    }
    
    // Modify velocity
    public void updateVelocity(float speed){
		
		velocity.x = speed*android.util.FloatMath.cos((float)(rotationAngle/180*3.146f))/6;
		velocity.y = speed*android.util.FloatMath.sin((float)(rotationAngle/180*3.146f))/6;
		state = TANK_MOVE;
	}

    // Enemy was hit by the tank
    public void hitEnemy() {
    	velocity.set(0,0);
    	state = TANK_HIT;  
    	if(life == 0)
    		state = TANK_DEAD;
    	stateTime = 0;
    }
  
    // power up was hit by the tank
    public void hitPowerUp(int type) {
    	if(type == PowerUp.PowerUp_TYPE_THREESHOT){
    		currentWeapon = Tank.TYPE_THREESHOT;
    		powerUpTimer = 10.0f;
    	}
    }
}
