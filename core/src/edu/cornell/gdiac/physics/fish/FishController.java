/*
 * FishController.java
 *
 * Author: Walker M. White && Dashiell Brown
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.physics.fish;

import java.util.ArrayList;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.*;
import edu.cornell.gdiac.physics.obstacle.*;

/**
 * Gameplay specific controller for Downstream.
 *
 * You will notice that asset loading is not done with static methods this time.  
 * Instance asset loading makes it easier to process our game modes in a loop, which 
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public class FishController extends WorldController implements ContactListener {
	/** Reference to the fish texture */
	private static final String KOI_TEXTURE = "fish/fish3.png";
	/** The reference for the tether textures  */
	private static final String TETHER_TEXTURE = "fish/lilypad.png";
	/** Reference to the enemy image assets */
	private static final String ENEMY_TEXTURE = "fish/enemy.png";

	/** The asset for the collision sound */
	private static final String  COLLISION_SOUND = "fish/bump.mp3";
	/** The asset for the main afterburner sound */
	private static final String  MAIN_FIRE_SOUND = "fish/afterburner.mp3";
	/** The asset for the right afterburner sound */
	private static final String  RGHT_FIRE_SOUND = "fish/sideburner-right.mp3";
	/** The asset for the left afterburner sound */
	private static final String  LEFT_FIRE_SOUND = "fish/sideburner-left.mp3";
	
	/** Texture assets for the koi */
	private TextureRegion koiTexture;
	/** Texture assets for the lilypads */
	private TextureRegion tetherTexture;
	/** Texture assets for the enemy fish */
	private TextureRegion enemyTexture;

	/** Texture filmstrip for the main afterburner */
	private FilmStrip mainTexture;
	/** Texture filmstrip for the main afterburner */
	private FilmStrip leftTexture;
	/** Texture filmstrip for the main afterburner */
	private FilmStrip rightTexture;
	
	/** Track asset loading from all instances and subclasses */
	private AssetState fishAssetState = AssetState.EMPTY;
	
	private boolean tethered;
	/**
	 * Preloads the assets for this controller.
	 *
	 * To make the game modes more for-loop friendly, we opted for nonstatic loaders
	 * this time.  However, we still want the assets themselves to be static.  So
	 * we have an AssetState that determines the current loading state.  If the
	 * assets are already loaded, this method will do nothing.
	 * 
	 * @param manager Reference to global asset manager.
	 */
	public void preLoadContent(AssetManager manager) {
		if (fishAssetState != AssetState.EMPTY) {
			return;
		}
		
		fishAssetState = AssetState.LOADING;

		
		manager.load(ENEMY_TEXTURE, Texture.class);
		assets.add(ENEMY_TEXTURE);
		
		// Ship textures
		manager.load(KOI_TEXTURE, Texture.class);
		assets.add(KOI_TEXTURE);
		
		manager.load(TETHER_TEXTURE, Texture.class);
		assets.add(TETHER_TEXTURE);
		
		// Ship sounds
		manager.load(MAIN_FIRE_SOUND, Sound.class);
		assets.add(MAIN_FIRE_SOUND);
		manager.load(LEFT_FIRE_SOUND, Sound.class);
		assets.add(LEFT_FIRE_SOUND);
		manager.load(RGHT_FIRE_SOUND, Sound.class);
		assets.add(RGHT_FIRE_SOUND);
		manager.load(COLLISION_SOUND, Sound.class);
		assets.add(COLLISION_SOUND);

		super.preLoadContent(manager);
	}

	/**
	 * Loads the assets for this controller.
	 *
	 * To make the game modes more for-loop friendly, we opted for nonstatic loaders
	 * this time.  However, we still want the assets themselves to be static.  So
	 * we have an AssetState that determines the current loading state.  If the
	 * assets are already loaded, this method will do nothing.
	 * 
	 * @param manager Reference to global asset manager.
	 */
	public void loadContent(AssetManager manager) {
		if (fishAssetState != AssetState.LOADING) {
			return;
		}

		enemyTexture = createTexture(manager,ENEMY_TEXTURE,false);
		koiTexture = createTexture(manager,KOI_TEXTURE,false);
		tetherTexture = createTexture(manager,TETHER_TEXTURE,false);
		
		SoundController sounds = SoundController.getInstance();
		sounds.allocate(manager,MAIN_FIRE_SOUND);
		sounds.allocate(manager,LEFT_FIRE_SOUND);
		sounds.allocate(manager,RGHT_FIRE_SOUND);
		sounds.allocate(manager,COLLISION_SOUND);
		
		super.loadContent(manager);
		fishAssetState = AssetState.COMPLETE;
	}
	
	// Physics constants for initialization
	/** Density of non-enemy objects */
	private static final float BASIC_DENSITY   = 0.0f;
	/** Density of the enemy objects */
	private static final float ENEMY_DENSITY   = 1.0f;
	/** Friction of non-enemy objects */
	private static final float BASIC_FRICTION  = 0.1f;
	/** Friction of the enemy objects */
	private static final float ENEMY_FRICTION  = 0.3f;
	/** Collision restitution for all objects */
	private static final float BASIC_RESTITUTION = 0.1f;
	/** Threshold for generating sound on collision */
	private static final float SOUND_THRESHOLD = 1.0f;
	
	private static final float TETHER_DENSITY = ENEMY_DENSITY;
	private static final float TETHER_FRICTION = ENEMY_FRICTION;
	private static final float TETHER_RESTITUTION = BASIC_RESTITUTION;

	// Since these appear only once, we do not care about the magic numbers.
	// In an actual game, this information would go in a data file.
	// Wall vertices
	private static final float[][] LAND = {{}};

	private static final float[] WALL1 = { 0.0f, 18.0f, 16.0f, 18.0f, 16.0f, 17.0f,
										   8.0f, 15.0f,  1.0f, 17.0f,  2.0f,  7.0f,
										   3.0f,  5.0f,  3.0f,  1.0f, 16.0f,  1.0f,
										  16.0f,  0.0f,  0.0f,  0.0f};
	private static final float[] WALL2 = {32.0f, 18.0f, 32.0f,  0.0f, 16.0f,  0.0f,
										  16.0f,  1.0f, 31.0f,  1.0f, 30.0f, 10.0f,
										  31.0f, 16.0f, 16.0f, 17.0f, 16.0f, 18.0f};
	private static final float[] WALL3 = { 4.0f, 10.5f,  8.0f, 10.5f,
            							   8.0f,  9.5f,  4.0f,  9.5f};
	
	private static final float[] WALLX = { 0.0f, 0.0f, 32.0f, 0.0f,
										   16.0f, 32.0f, 0.0f, 0.0f, 16.0f};

	// The positions of the crate pyramid
//	private static final float[] BOXES = { 14.5f, 14.25f,
//            							   13.0f, 12.00f, 16.0f, 12.00f,
//            							   11.5f,  9.75f, 14.5f,  9.75f, 17.5f, 9.75f,
//            							   13.0f,  7.50f, 16.0f,  7.50f,
//            							   11.5f,  5.25f, 14.5f,  5.25f, 17.5f, 5.25f,
//            							   10.0f,  3.00f, 13.0f,  3.00f, 16.0f, 3.00f, 19.0f, 3.0f};
	private static final float[] BOXES = {};
	
	private ArrayList<TetherModel> tethers = new ArrayList<TetherModel>();

	// Other game objects
	/** The initial koi position */
	private static Vector2 KOI_POS = new Vector2(24, 4);
	/** The goal door position */
	private static Vector2 GOAL_POS = new Vector2( 6, 12);

	// Physics objects for the game
	/** Reference to the goalDoor (for collision detection) */
	private BoxObstacle goalDoor;
	/** Reference to the player avatar */
	private PlayerFishModel koi;
	
	private EnemyFish eFish;

	/**
	 * Creates and initialize a new instance of Downstream
	 *
	 * The game has no  gravity and deafault settings
	 */
	public FishController() {
		setDebug(false);
		setComplete(false);
		setFailure(false);
		world.setContactListener(this);
		tethered = false;
	}
	
	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {
		Vector2 gravity = new Vector2(world.getGravity() );
		
		for(Obstacle obj : objects) {
			obj.deactivatePhysics(world);
		}
		objects.clear();
		addQueue.clear();
		world.dispose();
		
		world = new World(gravity,false);
		world.setContactListener(this);
		setComplete(false);
		setFailure(false);
		populateLevel();
	}

	/**
	 * Lays out the game geography.
	 */
	private void populateLevel() {
		// Add level goal
		float dwidth  = goalTile.getRegionWidth()/scale.x;
		float dheight = goalTile.getRegionHeight()/scale.y;
		
		boolean sensorTethers = true;
		
		float rad = tetherTexture.getRegionWidth()/2;

		
		TetherModel tether = new TetherModel(12, 2, dwidth, dheight);
		tether.setBodyType(BodyDef.BodyType.StaticBody);
		tether.setName("tether"+ 1);
		tether.setDensity(TETHER_DENSITY);
		tether.setFriction(TETHER_FRICTION);
		tether.setRestitution(TETHER_RESTITUTION);
		tether.setSensor(sensorTethers);
		tether.setDrawScale(scale);
		tether.setTexture(tetherTexture);
		addObject(tether);
		tethers.add(tether);
		
		tether = new TetherModel(6, 12, dwidth, dheight);
		tether.setBodyType(BodyDef.BodyType.StaticBody);
		tether.setName("tether"+ 2);
		tether.setDensity(TETHER_DENSITY);
		tether.setFriction(TETHER_FRICTION);
		tether.setRestitution(TETHER_RESTITUTION);
		tether.setSensor(sensorTethers);
		tether.setDrawScale(scale);
		tether.setTexture(tetherTexture);
		addObject(tether);
		tethers.add(tether);
		
		tether = new TetherModel(28, 10, dwidth, dheight);
		tether.setBodyType(BodyDef.BodyType.StaticBody);
		tether.setName("tether"+ 3);
		tether.setDensity(TETHER_DENSITY);
		tether.setFriction(TETHER_FRICTION);
		tether.setRestitution(TETHER_RESTITUTION);
		tether.setSensor(sensorTethers);
		tether.setDrawScale(scale);
		tether.setTexture(tetherTexture);
		addObject(tether);
		tethers.add(tether);
		
		tether = new TetherModel(16, 14, dwidth, dheight);
		tether.setBodyType(BodyDef.BodyType.StaticBody);
		tether.setName("tether"+ 4);
		tether.setDensity(TETHER_DENSITY);
		tether.setFriction(TETHER_FRICTION);
		tether.setRestitution(TETHER_RESTITUTION);
		tether.setSensor(sensorTethers);
		tether.setDrawScale(scale);
		tether.setTexture(tetherTexture);
		addObject(tether);
		tethers.add(tether);
		

		TextureRegion texture = enemyTexture;
		dwidth  = texture.getRegionWidth()/scale.x;
		dheight = texture.getRegionHeight()/scale.y;
		eFish = new EnemyFish(20, 0, dwidth, dheight);
		eFish.setDensity(ENEMY_DENSITY);
		eFish.setFriction(ENEMY_FRICTION);
		eFish.setRestitution(BASIC_RESTITUTION);
		eFish.setName("enemy");
		eFish.setDrawScale(scale);
		eFish.setTexture(texture);
		eFish.setAngle((float) (Math.PI/2));
		eFish.setBodyType(BodyDef.BodyType.StaticBody);
		eFish.setGoal(0, 0);
		addObject(eFish);
		
		
//		tether = new TetherModel(1, 6, dwidth, dheight);
//		tether.setBodyType(BodyDef.BodyType.StaticBody);
//		tether.setDensity(0.0f);
//		tether.setFriction(0.0f);
//		tether.setRestitution(0.0f);
//		tether.setSensor(true);
//		tether.setDrawScale(scale);
//		tether.setTexture(goalTile);
//		addObject(tether);
//		tethers.add(tether);
		
		// Create ground pieces
//		PolygonObstacle obj;
//		obj = new PolygonObstacle(WALL1, 0, 0);
//		obj.setBodyType(BodyDef.BodyType.StaticBody);
//		obj.setDensity(BASIC_DENSITY);
//		obj.setFriction(BASIC_FRICTION);
//		obj.setRestitution(BASIC_RESTITUTION);
//		obj.setDrawScale(scale);
//		obj.setTexture(earthTile);
//		obj.setName("wall1");
//		addObject(obj);
//
//		obj = new PolygonObstacle(WALL2, 0, 0);
//		obj.setBodyType(BodyDef.BodyType.StaticBody);
//		obj.setDensity(BASIC_DENSITY);
//		obj.setFriction(BASIC_FRICTION);
//		obj.setRestitution(BASIC_RESTITUTION);
//		obj.setDrawScale(scale);
//		obj.setTexture(earthTile);
//		obj.setName("wall2");
//		addObject(obj);


		// Create the fish avatar
		dwidth  = koiTexture.getRegionWidth()/scale.x;
		dheight = koiTexture.getRegionHeight()/scale.y;
		koi = new PlayerFishModel(KOI_POS.x, KOI_POS.y, dwidth, dheight);
		koi.setDrawScale(scale);
		koi.setName("koi");
		koi.setTexture(koiTexture);
	  
		addObject(koi);
	}

	/**
	 * The core gameplay loop of this world.
	 *
	 * This method contains the specific update code for this mini-game. It does
	 * not handle collisions, as those are managed by the parent class WorldController.
	 * This method is called after input is read, but before collisions are resolved.
	 * The very last thing that it should do is apply forces to the appropriate objects.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	public void update(float dt) {

		float thrust = koi.getThrust();
		InputController input = InputController.getInstance();
		koi.setFX(thrust * input.getHorizontal());
		koi.setFY(thrust * input.getVertical());
		koi.applyForce();
		koi.setLinearVelocity(koi.getLinearVelocity().setLength(8));
		
		if (input.didLaunch()) tethered = !tethered;
		
		TetherModel closestTether = tethers.get(0);
		float closestDistance = tethers.get(0).getPosition().sub(koi.getPosition()).len2();
		for (TetherModel tether : tethers) {
			float newDistance = tether.getPosition().sub(koi.getPosition()).len2();
			if (newDistance < closestDistance) {
				closestDistance = newDistance;
				closestTether = tether;
			}
		}
//		if (tethered &&
		if (input.space && 
			koi.getPosition().sub(koi.getInitialTangentPoint(closestTether)).len2() < .1) {
			koi.applyTetherForce(closestTether);			
		}
		
		float angV = 3f;
		float radius = closestTether.getPosition().dst(koi.getPosition());
		float tetherSpeed = angV*radius;
		
		float MAX_SPEED = 7f;
		float MIN_SPEED = 6f;
		
		int motionType = 0;
		
//		if (fish.getLinearVelocity().len2() != 0) {
//			switch(motionType){
//			case 0:
//				koi.setLinearVelocity(koi.getLinearVelocity().setLength(MAX_SPEED));
//				break;
//			case 1:
//				if (koi.getLinearVelocity().len() <= MAX_SPEED - 1 && input.accel){
//					koi.setLinearVelocity(koi.getLinearVelocity().setLength(koi.getLinearVelocity().len()+1));
//				}
//				if (koi.getLinearVelocity().len() >= MIN_SPEED + 1 && input.deccel){
//					koi.setLinearVelocity(koi.getLinearVelocity().setLength(koi.getLinearVelocity().len()-1));
//				}
//				break;
//			case 2:
//				koi.setLinearVelocity(koi.getLinearVelocity().setLength(tetherSpeed));
//				break;
//			}
//		}
		
		
		
		eFish.moveTowardsGoal();
		eFish.patrol(20, 0, 20, 18);
		eFish.getGoal();
		
	    SoundController.getInstance().update();
	}
	
	/// CONTACT LISTENER METHODS
	/**
	 * Callback method for the start of a collision
	 *
	 * This method is called when we first get a collision between two objects.  We use 
	 * this method to test if it is the "right" kind of collision.  In particular, we
	 * use it to test if we made it to the win door.
	 *
	 * @param contact The two bodies that collided
	 */
	public void beginContact(Contact contact) {
		Body body1 = contact.getFixtureA().getBody();
		Body body2 = contact.getFixtureB().getBody();

		if( (body1.getUserData() == koi   && body2.getUserData() == goalDoor) ||
			(body1.getUserData() == goalDoor && body2.getUserData() == koi)) {
			setComplete(true);
		}
	}
	
	/**
	 * Callback method for the start of a collision
	 *
	 * This method is called when two objects cease to touch.  We do not use it.
	 */ 
	public void endContact(Contact contact) {}
	
	private Vector2 cache = new Vector2();
	
	/** Unused ContactListener method */
	public void postSolve(Contact contact, ContactImpulse impulse) {}

	/**
	 * Handles any modifications necessary before collision resolution
	 *
	 * This method is called just before Box2D resolves a collision.  We use this method
	 * to implement sound on contact, using the algorithms outlined similar to those in
	 * Ian Parberry's "Introduction to Game Physics with Box2D".  
	 * 
	 * However, we cannot use the proper algorithms, because LibGDX does not implement 
	 * b2GetPointStates from Box2D.  The danger with our approximation is that we may
	 * get a collision over multiple frames (instead of detecting the first frame), and
	 * so play a sound repeatedly.  Fortunately, the cooldown hack in SoundController
	 * prevents this from happening.
	 *
	 * @param  contact  	The two bodies that collided
	 * @param  oldManfold  	The collision manifold before contact
	 */

	public void preSolve(Contact contact, Manifold oldManifold) {
		float speed = 0;

		// Use Ian Parberry's method to compute a speed threshold
		Body body1 = contact.getFixtureA().getBody();
		Body body2 = contact.getFixtureB().getBody();
		WorldManifold worldManifold = contact.getWorldManifold();
		Vector2 wp = worldManifold.getPoints()[0];
		cache.set(body1.getLinearVelocityFromWorldPoint(wp));
		cache.sub(body2.getLinearVelocityFromWorldPoint(wp));
		speed = cache.dot(worldManifold.getNormal());
		    
		// Play a sound if above threshold
		if (speed > SOUND_THRESHOLD) {
			String s1 = ((Obstacle)body1.getUserData()).getName();
			String s2 = ((Obstacle)body2.getUserData()).getName();
			if (s1.equals("koi") || s1.startsWith("enemy") || s1.startsWith("tether")) {
				SoundController.getInstance().play(s1, COLLISION_SOUND, false, 0.5f);
			}
			if (s2.equals("koi") || s2.startsWith("enemy") || s2.startsWith("tether")) {
				SoundController.getInstance().play(s2, COLLISION_SOUND, false, 0.5f);
			}
		}
		
	}
}