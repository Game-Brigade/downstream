package edu.cornell.gdiac.downstream.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;

import edu.cornell.gdiac.downstream.GameCanvas;
import edu.cornell.gdiac.downstream.obstacle.WheelObstacle;

public class WhirlpoolModel extends WheelObstacle {
	
	/** Radius of this whirlpool, used for collision detection */
	private static final int WHIRL_DEFAULT_RADIUS = 1;

	/** The radius at which the player spins around a whirlpool */
	public static final float WHIRL_DEFAULT_ORBIT = 1.5f;  

	/** The range at which the player gets pulled into a whirlpool */
	public static final float WHIRL_DEFAULT_RANGE = 1.5f;
	
	/** The direction the pool spins; 1 is ccw, -1 is cw */
	private float direction;
	
	/** Used to increment rotation for drawing */
	private float rot = 0;
	
	/** Keep track of how many times koi has rotated around pool */
	private float rotations; 
	
	/** Entry point into pool */
	private Vector2 entry;
	
	/** Exit angle of pool */
	private float exit;

	public WhirlpoolModel(float x, float y) {
		super(x, y, WHIRL_DEFAULT_RANGE);
	}
	
	public float getDirection(){
		return direction;
	}
	
	public void setDirection(float dir){
		direction = dir;
	}
	
	public void setExit(float angle){
		exit = angle;
	}
	
	public float getExit(){
		return exit;
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
	
	public void setRotations(float r){
		rotations = r;
	}
	public void updateRotations(){
		rotations = rotations + .5f;
	}

	public float getRotations(){
		return rotations;
	}
	
	
	
	public void draw(GameCanvas canvas){
		canvas.draw(texture, Color.WHITE, texture.getRegionWidth()/2, 
				texture.getRegionHeight()/2, this.getX()*drawScale.x, this.getY()*drawScale.x, (float)(Math.PI*rot*direction),0.4f, 0.4f);
		rot+=0.02;
		
	}
	
	public void setEntry(Vector2 e){
		entry = e;
	}
	
	
	

}
