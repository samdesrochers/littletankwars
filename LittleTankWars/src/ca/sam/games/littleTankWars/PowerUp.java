
package ca.sam.games.littleTankWars;

import ca.sam.games.framework.DynamicGameObject;
public class PowerUp extends DynamicGameObject{

    public static final int PowerUp_DEAD = 0;
    public static final int PowerUp_ALIVE = 1;
    
    public static final int PowerUp_TYPE_THREESHOT = 0;
    public static final int PowerUp_TYPE_BLOB = 1;
    
    public static final float PowerUp_BASIC_WIDTH = 2.0f;
    public static final float PowerUp_BASIC_HEIGHT = 2.0f;
    public static final float PowerUp_TOP_WIDTH = 2.0f;
    public static final float PowerUp_TOP_HEIGHT = 2.0f;
    
	int PowerUpType;
    int state;
    int score;
    float oscillationX;
    float oscillationY;
    int bonusScore;
    public float rotationAngle;
    float stateTime;

    // Super constructor creates a bounding box around the object
    public PowerUp(float x, float y, int type) {
        super(x, y, PowerUp_BASIC_WIDTH, PowerUp_BASIC_HEIGHT);
        state = PowerUp_ALIVE;
        PowerUpType = type;  
        
        if(type == PowerUp_TYPE_THREESHOT)
        	bonusScore = 500;
        else
        	bonusScore = 100;
    }
    
    
    public void update(float deltaTime) {    
    	bounds.lowerLeft.set(position).sub(bounds.width / 2, bounds.height / 2);
    	
        if(this.position.x >= World.WORLD_WIDTH )
        	this.position.x = 0;
        if(this.position.x <= 0)
        	this.position.x = World.WORLD_WIDTH;
        if(this.position.y >= World.WORLD_HEIGHT)
        	this.position.y = 1;
        if(this.position.y <= 0)
        	this.position.y = World.WORLD_HEIGHT;
        
        oscillationX = (float) (Math.cos(stateTime)*2f);
        oscillationY = (float) (Math.sin(stateTime)*2f);
        position.add(oscillationX*deltaTime, oscillationY*deltaTime);
        
        stateTime += deltaTime;
    }
}
