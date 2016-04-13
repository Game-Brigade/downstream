package edu.cornell.gdiac.downstream.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.BodyDef;

import edu.cornell.gdiac.downstream.GameCanvas;
import edu.cornell.gdiac.downstream.obstacle.WheelObstacle;

public class WhirlpoolModel extends WheelObstacle {
	
	/** Radius of this whirlpool, used for collision detection */
	private static final int WHIRL_DEFAULT_RADIUS = 1;

	/** The radius at which the player spins around a whirlpool */
	public static final float WHIRL_DEFAULT_ORBIT = 1.5f;  

	/** The range at which the player gets pulled into a whirlpool */
	private static final int WHIRL_DEFAULT_RANGE = 3;
	
	private float rot = 0;

	public WhirlpoolModel(float x, float y) {
		super(x, y, WHIRL_DEFAULT_RANGE);
		setBodyType(BodyDef.BodyType.StaticBody);
		
	}
	
	public float getRadius() {
		return WHIRL_DEFAULT_RADIUS;
	}

	public float getOrbitRadius() {
		return WHIRL_DEFAULT_ORBIT;
	}

	public float getSensorRadius() {
		return WHIRL_DEFAULT_RANGE;
	}
	
	public void draw(GameCanvas canvas){
		canvas.draw(texture, Color.WHITE, texture.getRegionWidth()/2, 
				texture.getRegionHeight()/2, this.getX()*drawScale.x, this.getY()*drawScale.x, (float)(Math.PI*rot),0.5f, 0.5f);
		
		rot+=0.01;
	}
	

}
