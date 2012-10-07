package ca.sam.games.littleTankWars;

import java.util.Random;

import ca.sam.games.framework.DynamicGameObject;
public class Enemy extends DynamicGameObject{

    public static final int Enemy_DEAD = 0;
    public static final int Enemy_ALIVE = 1;
    public static final int Enemy_BOOST = 2;  
    
    public static final int Enemy_TYPE_BASIC = 0;
    public static final int Enemy_TYPE_BLOB = 1;
    public static final int Enemy_TYPE_ROCKING = 2;
    public static final int Enemy_TYPE_BOSS = 10;
    
    public static final float Enemy_MOVE_VELOCITY = 3.2f;
    public static final float Enemy_BASIC_WIDTH = 1.0f;
    public static final float Enemy_BASIC_HEIGHT = 1.0f;
    public static final float Enemy_TOP_WIDTH = 1.0f;
    public static final float Enemy_TOP_HEIGHT = 1.0f;
    public static final float Enemy_BOSS_WIDTH = 4.0f;
    public static final float Enemy_BOSS_HEIGHT = 4.0f;
    
    public static final int Enemy_SCORE = 50;
    
    public int life;
    public int damage;
    public final Random rand;
    public float difficulty;
    
	int enemyType;
    int state;
    int score;
    float enemySpeed;
    public float rotationAngle;
    float stateTime;

    // Super constructor : creates a bounding box around the object
    public Enemy(float x, float y, int type, float diff) {
        super(x, y, Enemy_BASIC_WIDTH, Enemy_BASIC_HEIGHT);
        state = Enemy_ALIVE;
        rotationAngle = 0;
        enemyType = type;
        rand = new Random();
        life = 10;
        damage = 5;
        difficulty = diff;
        stateTime = rand.nextFloat();    
        initialize();
    }
    
    public void initialize(){
    	if(enemyType == Enemy_TYPE_BASIC){
    		score = Enemy_SCORE;
    		enemySpeed = Enemy_MOVE_VELOCITY + (difficulty / 4);
	        
    	} else if (enemyType == Enemy_TYPE_BLOB){
    		score = Enemy_SCORE+10;
    		enemySpeed = Enemy_MOVE_VELOCITY - 1.2f + difficulty;
    		life = 20;
    		damage = 10;
	        
    	} else if (enemyType == Enemy_TYPE_ROCKING){
    		score = Enemy_SCORE+30;
    		enemySpeed = Enemy_MOVE_VELOCITY + difficulty;
    		
    	} else if (enemyType == Enemy_TYPE_BOSS){
    		score = (int) (500 * (difficulty * 2));
    		life = (int) (100 + (difficulty * 50));
    		damage = (int) (15 + (difficulty * 5));
    		enemySpeed = Enemy_MOVE_VELOCITY + 2 + (difficulty / 3) ; 
    		this.bounds.width += Enemy_BOSS_WIDTH/2;
    		this.bounds.height += Enemy_BOSS_WIDTH/2;
    	}	
    }
    
    public void update(float deltaTime) {    
    	bounds.lowerLeft.set(position).sub(bounds.width / 2, bounds.height / 2);
    	
    	updateVelocity();
    	
        if(this.position.x >= World.WORLD_WIDTH )
        	this.position.x = 0;
        if(this.position.x <= 0)
        	this.position.x = World.WORLD_WIDTH;
        if(this.position.y >= World.WORLD_HEIGHT)
        	this.position.y = 0;
        if(this.position.y <= 0)
        	this.position.y = World.WORLD_HEIGHT;
        
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        
        stateTime += deltaTime;
        
        if(life <= 0)
        	state = Enemy_DEAD;
        
        if(life < 0 && enemyType == Enemy_TYPE_BOSS);
        	
    }
    
    public void updateVelocity(){
    		    	
    	if(enemyType == Enemy_TYPE_BASIC){
        	velocity.x = (float) (enemySpeed*Math.cos(rotationAngle/180*3.146f));
        	velocity.y = (float) (enemySpeed*Math.sin(rotationAngle/180*3.146f));
        	
    	} else if(enemyType == Enemy_TYPE_BLOB) { 
    		
        	velocity.x = (float) (enemySpeed*Math.cos(rotationAngle/180*3.146f));
        	velocity.y = (float)(Math.sin(stateTime) * (enemySpeed*Math.sin(rotationAngle/180*3.146f)));
	    	if((int)stateTime%4 > 2)
	    		velocity.y += (velocity.x * velocity.x)/4;
	    	else
	    		velocity.y -= (velocity.x * velocity.x)/4;
	    	
    	} else if (enemyType == Enemy_TYPE_ROCKING) {
    		int direction = 1;
	    	if( (int)stateTime%6 > 3 ){
	    		direction = -1;
	    	}
	    	else if ( (int)stateTime%6 < 3 ){
	    		direction = 1;
	    	}
    		velocity.x = (float) ( (int)(stateTime%12) * Math.cos(stateTime) * direction) ;
    		velocity.y = (float) ( (int)(stateTime%8) *  Math.sin(stateTime) * direction);
    		
    	} else if (enemyType == Enemy_TYPE_BOSS) {
        	velocity.x = (float) (enemySpeed*Math.cos(rotationAngle/180*3.146f));
        	velocity.y = (float) (enemySpeed*Math.sin(rotationAngle/180*3.146f));
    	}
    }
}
