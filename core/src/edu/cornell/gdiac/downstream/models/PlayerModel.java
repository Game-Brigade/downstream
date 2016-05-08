
/*
 * Tether.java
 *
 * A tether object. A player enters it's radius and begins orbitting
 *
 * Author: Dashiell Brown
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.downstream.models;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.downstream.obstacle.*;
import edu.cornell.gdiac.downstream.GameCanvas;

public class PlayerModel extends BoxObstacle {

	/** Fist move and need to be affected by forces */
	private static final BodyDef.BodyType PLAYER_FISH_BODY_TYPE = BodyDef.BodyType.DynamicBody;

	/** The density of the player */
	private static final float DEFAULT_DENSITY  =  1.0f;
	/** The friction of the player */
	private static final float DEFAULT_FRICTION = 0.1f;
	/** The restitution of the player */
	private static final float DEFAULT_RESTITUTION = 0.4f;
	/** The thrust factor to convert player input into thrust */
	private static final float DEFAULT_THRUST = 30.0f;

	
	/** Cache object for transforming the force according the object angle */
	public Affine2 affineCache = new Affine2();
	/** Cache object for left afterburner origin */
	public Vector2 leftOrigin = new Vector2();
	/** Cache object for right afterburner origin */
	public Vector2 rghtOrigin = new Vector2();
	
	public Vector2 initPos;

	private Vector2 cachedPos = new Vector2(0, 0);
	
	private int health;

	private Vector2 force;
	
	public static final Vector2 NE = (new Vector2(1,-1)).nor();
	
	private boolean isTethered;
	
	private boolean isWhirled;

	public Vector2 pull;

	private float fishAlpha = .7f;
	private Color fishColor = new Color(255, 255, 255, 1);
	
	public Vector2 cent;

	private Vector2 dest;

	private boolean attemptingTether;

	public boolean bursting;
	
	private float energy;
	
	private boolean curved;
	
	private boolean left;
	
	private boolean cachedLeft = false;

	private boolean pastTanTether;
	
	private boolean dead;

	private float speed;
	
	public TextureRegion ArrowTexture;

	/** Create a new player at x,y. */
	public PlayerModel(float x, float y, float width, float height) {
		super(x, y, width, height);
		initPos = getPosition();
		setBodyType(PLAYER_FISH_BODY_TYPE);
		setDensity(DEFAULT_DENSITY);
		setDensity(DEFAULT_DENSITY);
		setFriction(DEFAULT_FRICTION);
		setRestitution(DEFAULT_RESTITUTION);
		setGravityScale(0);
		setName("player");
		force = new Vector2();
		health = 1;
		isTethered = false;
		attemptingTether = true;
		isWhirled = false;
		setLinearVelocity(NE);
		speed = 1;
		pull = Vector2.Zero;
		cent = Vector2.Zero;
		dest = Vector2.Zero;
	}
	

	public boolean isAlive() {
		return health > 0;
	}

	public boolean isAttemptingTether() {
		return attemptingTether;
	}
	
	public void setAttemptingTether(boolean b) {
		attemptingTether = b;
	}
	
	public void applyTetherForce(Vector2 tetherPos, float rad) {
		force = calculateTetherForce(tetherPos,rad);
		body.applyForceToCenter(force, true);
	}
	
	public void applyWhirlForce(Vector2 whirlPos, float rad){
		body.applyForceToCenter(calculateWhirlForce(whirlPos,rad), true);
	}
	

	public void setCurved(boolean b){
		curved = b;
	}


	public void refreshTetherForce(Vector2 tetherPos, float rad){
		pull = tetherPos.cpy().sub(getPosition());
		pull.setLength(pull.len() + rad);
		dest = getPosition().cpy().add(pull);
		cent = getPosition().cpy().add(pull.cpy().scl(0.5f));
	}
	
	public void refreshWhirlForce(Vector2 whirlPos, float rad){
		pull = whirlPos.cpy().sub(getPosition());
		pull.setLength(pull.len() + rad);
		dest = getPosition().cpy().add(pull);
		cent = getPosition().cpy().add(pull.cpy().scl(0.5f));
	}
	
	public Vector2 calculateTetherForce(Vector2 tetherPos, float rad){
		if(cent.isZero() || dest.isZero() || pull.isZero()){
			return cent;
		}
		if(isTethered()){
			// TRUE CIRCLE
			if(getPosition().sub(dest).len2() < .01){
				pastTanTether = false;
				attemptingTether = false;
				dest = getPosition();
				
				// set force direction
				pull = tetherPos.sub(getPosition());
				
				// set force magnitude
			    float forceMagnitude = (float) (getMass() * getLinearVelocity().len2() / rad);
			    return pull.setLength(forceMagnitude);
			} 
			
			// CORRECTIVE CIRCLE
			else{
			    float forceMagnitude = (float) (getMass() * getLinearVelocity().len2() / (pull.len()/2));
			    return cent.cpy().sub(getPosition()).setLength(forceMagnitude);			
			}
		}
		else{
			if(isAttemptingTether()){
			    float forceMagnitude = (float) (getMass() * getLinearVelocity().len2() / (pull.len()/2));
			    return cent.cpy().sub(getPosition()).setLength(forceMagnitude);			
			}
			return Vector2.Zero;
		}
	}
	

	
	public Vector2 calculateWhirlForce(Vector2 whirlPos, float rad){
		if(cent.isZero() || dest.isZero() || pull.isZero()){
			return cent;
		}
		if(isWhirled()){
			// TRUE CIRCLE
			if(getPosition().sub(dest).len2() < .01){
				//pastTanTether = false;
				//attemptingTether = false;
				dest = getPosition();
				
				// set force direction
				pull = whirlPos.sub(getPosition());
				
				// set force magnitude
			    float forceMagnitude = (float) (1.9*getMass() * getLinearVelocity().len2() / rad);
			    return pull.setLength(forceMagnitude);
			} 
			
			// CORRECTIVE CIRCLE
			else{
			    float forceMagnitude = (float) (1.9*getMass() * getLinearVelocity().len2() / (pull.len()/2));
			    return cent.cpy().sub(getPosition()).setLength(forceMagnitude);			
			}
		}
		return Vector2.Zero;
		
	}
	
	public void passAdjust(Vector2 tetherPos){
		Vector2 perp = tetherPos.cpy().sub(getInitialTangentPoint(tetherPos)).scl(.5f);
		float rad = perp.len();
		cent = getPosition().add(getLinearVelocity().setLength(rad/2)).add(perp);
		
		dest = getPosition().add(perp.cpy().scl(2)).add(getLinearVelocity().setLength(rad));
		pull = dest.cpy().sub(getPosition());
		cent = getPosition().cpy().add(pull.cpy().scl(0.5f));
		
		pastTanTether = true;
		
	}
	
	public void passAdjustWhirl(Vector2 whirlPos){
		Vector2 perp = whirlPos.cpy().sub(getInitialTangentPoint(whirlPos)).scl(.5f);
		float rad = perp.len();
		cent = getPosition().add(getLinearVelocity().setLength(rad/2)).add(perp);
		
		dest = getPosition().add(perp.cpy().scl(2)).add(getLinearVelocity().setLength(rad));
		pull = dest.cpy().sub(getPosition());
		cent = getPosition().cpy().add(pull.cpy().scl(0.5f));
		
		
	}
	
	private void setForce(Vector2 scl) {
		force = scl;
	}

	public Vector2 calculateTetherForce(Vector2 tetherPos){
	    Vector2 direction = tetherPos.sub(getPosition());
	    float radius = 2;
	    float forceMagnitude = (float) (getMass() * getLinearVelocity().len2() / radius);
	    return direction.setLength(forceMagnitude);
	  }
	

	public Vector2 getInitialTangentPoint(Vector2 tetherPos) {
		if (getVX() == 0) setVX(.00001f);
		if (getVY() == 0) setVY(.00001f);
		float slope = getVY() / getVX();
		float xtan = (slope * getX() - getY() + tetherPos.x / slope + tetherPos.y) / (slope + 1 / slope);
		float ytan = slope * xtan - slope * getX() + getY();
		return new Vector2(xtan, ytan);
	}
	
	public Vector2 timeToIntersect(Vector2 target) {
		return new Vector2(target.x - getX() / getLinearVelocity().x,
						   target.y - getY() / getLinearVelocity().y);
	}
	
	public boolean willIntersect(Vector2 target) {
		Vector2 time = timeToIntersect(target);
		//return time.x > -0.009 && time.y > -0.009;
		return getLinearVelocity().isCollinear(target.sub(getPosition()), .09f);
	}
	
	public boolean willIntersectTether(Vector2 tether, int tetherRange) {
		Vector2 initialTangent = getInitialTangentPoint(tether);
		Vector2 difference = new Vector2(tether.x - getX(), tether.y - getY());
		boolean timeIsPositive = Math.signum(difference.x) == Math.signum(getVX()) && 
								 Math.signum(difference.y) == Math.signum(getVY());
		if (initialTangent.dst2(tether) > tetherRange*tetherRange || !timeIsPositive) return false;
		return true;
	}
	
	public boolean pastTangent(Vector2 target){
		return !getLinearVelocity().isZero() &&
				getLinearVelocity().hasOppositeDirection(target.cpy().sub(getPosition()));
	}
	
	public boolean isTethered() {
		return isTethered;
	}
	
	public void setTethered(boolean newState) {
		isTethered = newState;
	}
	
	public boolean isWhirled(){
		return isWhirled;
	}
	
	public void setWhirled(boolean newState){
		isWhirled = newState;
	}
	
	public boolean left(TetherModel t){
		boolean b;
		
		if (cachedPos.x - .5 > t.getX()){
			if (this.getPosition().x > t.getX() && this.getPosition().y > cachedPos.y){
				b = true;
			}
			else{ b = false;}
			cachedLeft = b;
		}
		else{
			b = cachedLeft;
		}
		
		if (cachedPos.x + .5 < t.getX()){
			if (this.getPosition().x < t.getX() && this.getPosition().y < cachedPos.y){
				b = true;
			}
			else{b = false;}
			cachedLeft = b;
		}
		else{
			b = cachedLeft;
		}
		
		cachedPos = getPosition().cpy();
		left = b;
		return b;

	}
	
	public boolean left(WhirlpoolModel t){
		boolean b;
		
		if (cachedPos.x - .5 > t.getX()){
			if (this.getPosition().x > t.getX() && this.getPosition().y > cachedPos.y){
				b = true;
			}
			else{ b = false;}
			cachedLeft = b;
		}
		else{
			b = cachedLeft;
		}
		
		if (cachedPos.x + .5 < t.getX()){
			if (this.getPosition().x < t.getX() && this.getPosition().y < cachedPos.y){
				b = true;
			}
			else{b = false;}
			cachedLeft = b;
		}
		else{
			b = cachedLeft;
		}
		
		cachedPos = getPosition().cpy();
		left = b;
		return b;

	}
	
	/**
	 * Draws the physics object.
	 *
	 * @param canvas Drawing context
	 */
	public void draw(GameCanvas canvas) {
		//		canvas.drawLeadingLine(body.getPosition(), new Vector2(0,0));
		if (true){
			if(isTethered()){

				Vector2 farOff = new Vector2(getX(), getY());
				farOff.add(this.getLinearVelocity().cpy().nor().scl(.4f));

				canvas.draw(ArrowTexture, Color.WHITE ,origin.x,origin.y,farOff.x*drawScale.x,farOff.y*drawScale.x,getAngle() + 2.2f, .6f, .6f);
				//canvas.draw(texture, farOff.x, farOff.y);
			}
		}
		if(!dead || true){
			//super.draw(canvas);  
			//		canvas.drawLeadingLine(body.getPosition(), new Vector2(0,0));
			if (texture != null) {
				fishColor.set(255, 255, 255, fishAlpha);
				//DO NOT USE COLORS DIRECTRLY!!!!!!!!
				if (!curved){canvas.draw(texture, fishColor ,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.x,getAngle() + 2.2f, .28f, .28f);}
				else{
					if(left){
						if (curved)canvas.draw(texture, fishColor,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.x, getAngle() + 2.6f, .3f, .3f);
					}
					else{
						if (curved)canvas.draw(texture, fishColor,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.x, getAngle() + 3.7f, .3f, .3f);
					}
				}


				//canvas.draw(texture,Color.WHITE.mul(1, 1, 1, fishAlpha),origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.x,getAngle(),1,1);
			}
		}

	}

	
	/**
	 * Returns the force applied to this rocket.
	 * 
	 * This method returns a reference to the force vector, allowing it to be modified.
	 * Remember to modify the input values by the thrust amount before assigning
	 * the value to force.
	 *
	 * @return the force applied to this rocket.
	 */
	public Vector2 getForce() {
		return force;
	}

	/**
	 * Returns the x-component of the force applied to this rocket.
	 * 
	 * Remember to modify the input values by the thrust amount before assigning
	 * the value to force.
	 *
	 * @return the x-component of the force applied to this rocket.
	 */
	public float getFX() {
		return force.x;
	}

	/**
	 * Sets the x-component of the force applied to this rocket.
	 * 
	 * Remember to modify the input values by the thrust amount before assigning
	 * the value to force.
	 *
	 * @param value the x-component of the force applied to this rocket.
	 */
	public void setFX(float value) {
		force.x = value;
	}

	/**
	 * Returns the y-component of the force applied to this rocket.
	 * 
	 * Remember to modify the input values by the thrust amount before assigning
	 * the value to force.
	 *
	 * @return the y-component of the force applied to this rocket.
	 */
	public float getFY() {
		return force.y;
	}

	/**
	 * Sets the x-component of the force applied to this rocket.
	 * 
	 * Remember to modify the input values by the thrust amount before assigning
	 * the value to force.
	 *
	 * @param value the x-component of the force applied to this rocket.
	 */
	public void setFY(float value) {
		force.y = value;
	}
	
	/**
	 * Returns the amount of thrust that this rocket has.
	 *
	 * Multiply this value times the horizontal and vertical values in the
	 * input controller to get the force.
	 *
	 * @return the amount of thrust that this rocket has.
	 */
	public float getThrust() {
		return DEFAULT_THRUST;
	}
	
	/**
	 * Creates the physics Body(s) for this object, adding them to the world.
	 *
	 * This method overrides the base method to keep your ship from spinning.
	 *
	 * @param world Box2D world to store body
	 *
	 * @return true if object allocation succeeded
	 */
	public boolean activatePhysics(World world) {
		// Get the box body from our parent class
		if (!super.activatePhysics(world)) {
			return false;
		}
		
		//#region INSERT CODE HERE
		// Insert code here to prevent the body from rotating
		
		setFixedRotation(true);
		
		//#endregion
		
		return true;
	}
	
	
	/**
	 * Applies the force to the body of this ship
	 *
	 * This method should be called after the force attribute is set.
	 */
	public void applyForce() {
		if (!isActive()) {
			return;
		}
		
		// Orient the force with rotation.
		affineCache.setToRotationRad(getAngle());
		affineCache.applyTo(force);
		
		//#region INSERT CODE HERE
		// Apply force to the rocket BODY, not the rocket
		
		body.applyForceToCenter(force,true);
		
		//#endregion
	}

	public void resolveDirection() {
		setAngle((float) Math.atan2(getVY(),getVX()));
		
	}
	
	public void burst(){
		if (energy >= 2){
			bursting = true;
			setTethered(false);
			setAttemptingTether(false);
		}
		
	}
	
	
	public void updateRestore(){
		if (energy < 2 && !bursting){
			energy = energy + .02f;
		}
		else if (bursting && energy >= 0){
			this.setLinearVelocity(this.getLinearVelocity().setLength(4f*9*speed));
			energy = energy - .07f;
		}
		else if (energy <= 0){
			bursting = false;
		}
		else if (energy >= 2){
			energy = 2;
		}
		
	}


	public void updateSpeed(float v) {
		if(!this.isDead()){
		if (isTethered()) {
			setLinearVelocity(getLinearVelocity().setLength(v*1.5f*speed));
		} else{
			setLinearVelocity(getLinearVelocity().setLength(v*2*speed));
		}
		}
	}

	public void setPastTangentTethering(boolean b){
		pastTanTether = b;
	}
	
	
	public boolean isPastTangentTethering() {
		return pastTanTether;
	}

	public void free(){
		setAttemptingTether(false);
		setTethered(false);
	}

	public void setDead(boolean b){
		dead = b;
	}
	
	public boolean isDead(){
		return dead;
	}

	public void getVectors() {
//		System.out.println("Cent: "+ cent);		
//		System.out.println("Dest: "+ dest);		
//		System.out.println("Pull: "+ pull);		
	}

	public void die(){
		if (fishAlpha > 0) fishAlpha = fishAlpha - .01f;
	}
	
	public void restoreAlpha(){
		fishAlpha = .7f;
	}
	
	public float getEnergy(){
		return energy;
	}
	
	public void scaleSpeed(float s){
		speed = s;
	}
	
}