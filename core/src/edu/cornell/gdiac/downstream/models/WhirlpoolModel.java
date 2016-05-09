package edu.cornell.gdiac.downstream.models;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;

import edu.cornell.gdiac.downstream.GameCanvas;
import edu.cornell.gdiac.downstream.obstacle.WheelObstacle;

public class WhirlpoolModel extends WheelObstacle {
	
	/** Radius of this whirlpool, used for collision detection */
	private static final int WHIRL_DEFAULT_RADIUS = 1;

	/** The radius at which the player spins around a whirlpool */
	public static final float WHIRL_DEFAULT_ORBIT = 4f;  

	/** The range at which the player gets pulled into a whirlpool */
	public static final float WHIRL_DEFAULT_RANGE = 6;
	
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
		
		
	}
	
	public void setEntry(Vector2 e){
		entry = e;
	}
	
	public void rotationPass(PlayerModel koi){
		Vector2 fishVec = koi.getPosition().cpy().sub(this.getPosition()).nor();
		Vector2 angleVec = this.angle.cpy().sub(this.getPosition()).nor();
		
		float fishSlope = Math.abs((koi.getPosition().y - this.getPosition().y) / (koi.getPosition().x - this.getPosition().x));
		float angleSlope = Math.abs((this.angle.y - this.getPosition().y) / (this.angle.y - this.getPosition().x));
		
		//System.out.println(numOfRotations);
		if(fishVec.isCollinear(angleVec, .11f) && numOfRotations >= 8){
			koi.burst();
			koi.setExitingWhirlpool(true);
			numOfRotations = -1;
		}
		else if(fishVec.isCollinear(angleVec, .11f)){
			numOfRotations += 1;
		}
		else{}
		
		/*
		System.out.println(numOfRotations);
		if(fishSlope < angleSlope + .01 && fishSlope > angleSlope - .01 && numOfRotations >= 8){
			koi.burst();
			koi.setExitingWhirlpool(true);
			numOfRotations = -1;
		}
		else if(fishSlope < angleSlope + .11 && fishSlope > angleSlope - .11){
			numOfRotations += 0.25;
		}
		else{}
		*/
		
		/*
		if(fishVec.isOnLine(angleVec, .1f) && numOfRotations >= 6){
			System.out.println(numOfRotations);
			koi.burst();
			//koi.setWhirled(false);
			numOfRotations = -1;
		}
		else if(fishVec.isOnLine(angleVec, .1f)){
			numOfRotations += 0.5;
		}
		else{}
	*/
		
		/*
		if(numOfRotations >= 5 && fishVec.angleRad(angleVec) < .01){
			koi.burst();
			System.out.println("BURST");
			numOfRotations = -1;
		}
		else{
			if(fishVec.angleRad(angleVec) < .01){
				System.out.println("ROTATION");
				numOfRotations += 0.5;
			}
			System.out.println("Angle: " + fishVec.angleRad(angleVec));
		}
		*/
		
	}
	

}
