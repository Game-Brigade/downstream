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
	public static final float WHIRL_DEFAULT_ORBIT = 1.8f;  

	/** The range at which the player gets pulled into a whirlpool */
	public static final float WHIRL_DEFAULT_RANGE = 7;
	
	/** The direction the pool spins; 1 is ccw, -1 is cw */
	private float direction;
	
	private float rot = 0;
	private Vector2 angle;
	private float numOfRotations; 
	private Vector2 entry;

	public WhirlpoolModel(float x, float y, float dir) {
		super(x, y, WHIRL_DEFAULT_RANGE);
		direction = dir;
		numOfRotations = 0;
	}
	
	public WhirlpoolModel(float x, float y, float dir, Vector2 ang){
		this(x,y,dir);
		this.angle = ang;
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
				texture.getRegionHeight()/2, this.getX()*drawScale.x, this.getY()*drawScale.x, (float)(Math.PI*rot*direction),0.4f, 0.4f);
		rot+=0.02;
		//canvas.drawLeadingLine(this.getPosition(), angle, 2);
		
	}
	
	public void setEntry(Vector2 e){
		entry = e;
	}
	
	public void rotationPass(PlayerModel koi){
		if(numOfRotations == 5){
			//koi.setDead(true);
		}
		else{
			//if(koi.get)
		}
	}
	

}
