package ca.sam.games.littleTankWars;


import ca.sam.games.framework.DynamicGameObject;

public class Ammo extends DynamicGameObject{
	
	public static final float BASIC_BULLET_WIDTH = 0.5f;
	public static final float BASIC_BULLET_HEIGHT = 0.5f;
	public static final int LASER_WIDTH = 2;
	public static final int LASER_HEIGHT = 2;
	
	public static final int ACTIVE = 1;
	public static final int NOT_ACTIVE = 0;
	
	public float rotationAngle = 0.0f;
	public int weaponType;
	public int weaponDamage;
	public int speed;
	public int state;
	public float stateTime = 0;
	
	public Ammo(float x, float y, float angle, int type) {
		super(x, y, BASIC_BULLET_WIDTH, BASIC_BULLET_HEIGHT);
		weaponType = type;
		rotationAngle = angle;
		state = ACTIVE;
        this.initialize();
    }
	
	private void initialize(){
		if(weaponType == Tank.TYPE_BASIC_BULLET || weaponType == Tank.TYPE_THREESHOT){
			weaponDamage = 15;
			speed= 25;
		}
		else if(weaponType == Tank.TYPE_FLAMETHROWER){
			weaponDamage = 20;
			this.bounds.width += LASER_WIDTH;
			this.bounds.height += LASER_HEIGHT;
			speed = 30;
		}
	}

	public void update(float deltaTime){
		
		bounds.lowerLeft.set(position).sub(bounds.width / 2, bounds.height / 2);
    	velocity.x = (float) (speed*Math.cos(rotationAngle/180*3.146f));
    	velocity.y = (float) (speed*Math.sin(rotationAngle/180*3.146f));
		position.add(velocity.x * deltaTime, velocity.y * deltaTime);
		
		if(weaponType == Tank.TYPE_BASIC_BULLET || weaponType == Tank.TYPE_THREESHOT){
			if(position.x > World.WORLD_WIDTH || position.x < 0 || position.y > World.WORLD_HEIGHT || position.y < 0 ){
				state = NOT_ACTIVE;
			}
		} else if (weaponType == Tank.TYPE_FLAMETHROWER) {
			if(stateTime > 0.8){
				state = NOT_ACTIVE;
				deltaTime = 0.0f;
			}			
		}
		
		if(weaponType == Tank.TYPE_THREESHOT){
			
		}
		stateTime += deltaTime;
	}
}
