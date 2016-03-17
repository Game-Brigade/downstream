/*
 * Tether.java
 *
 * A tether object. A player enters it's radius and begins orbitting
 *
 * Author: Dashiell Brown
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.physics.fish;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.*;
import edu.cornell.gdiac.physics.obstacle.*;
import edu.cornell.gdiac.physics.rocket.RocketModel;

public class PlayerFishModel extends RocketModel {

	/** Fist move and need to be affected by forces */
	private static final BodyDef.BodyType PLAYER_FISH_BODY_TYPE = BodyDef.BodyType.DynamicBody;

	/** The density of the player */
	private static final float PLAYER_DEFAULT_DENSITY  =  1.0f;
	/** The friction of the player */
	private static final float PLAYER_DEFAULT_FRICTION = 0.1f;
	/** The restitution of the player */
	private static final float PLAYER_DEFAULT_RESTITUTION = 0.4f;
	/** The thrust factor to convert player input into thrust */
	private static final float PLAYER_DEFAULT_THRUST = 30.0f;

	private int health;

	private Vector2 force;

	/** Create a new player at x,y. */
	public PlayerFishModel(float x, float y, float width, float height) {
		super(x, y, width, height);
		setBodyType(PLAYER_FISH_BODY_TYPE);
		setDensity(PLAYER_DEFAULT_DENSITY);
		setDensity(PLAYER_DEFAULT_DENSITY);
		setFriction(PLAYER_DEFAULT_FRICTION);
		setRestitution(PLAYER_DEFAULT_RESTITUTION);
		setGravityScale(0);
		setName("player");
		force = new Vector2();
		health = 1;
	}

	public boolean isAlive() {
		return health > 0;
	}

	public void applyTetherForce(Vector2 tetherForce) {
		body.applyForceToCenter(tetherForce, true);
	}

	public void applyTetherForce(TetherModel tether) {
		applyTetherForce(tether.calculateAttractiveForce(this));
	}

	public Vector2 getInitialTangentPoint(Vector2 tether) {
		if (getVX() == 0) setVX(.00001f);
		if (getVY() == 0) setVY(.00001f);
		float slope = getVY() / getVX();
		float xtan = (slope * getX() - getY() + tether.x / slope + tether.y) / (slope + 1 / slope);
		float ytan = slope * xtan - slope * getX() + getY();
		return new Vector2(xtan, ytan);
	}
	
	public Vector2 timeToIntersect(Vector2 target) {
		return new Vector2(target.x - getX() / getLinearVelocity().x,
						   target.y - getY() / getLinearVelocity().y);
	}
	
	public boolean willIntersect(Vector2 target) {
		Vector2 time = timeToIntersect(target);
		return time.x > -0.009 && time.y > -0.009;
	}

	public void draw(GameCanvas canvas) {
		super.draw(canvas);
		
//		canvas.drawLeadingLine(body.getPosition(), new Vector2(0,0));
	}
	
}