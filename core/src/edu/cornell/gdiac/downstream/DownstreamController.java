/*
 * FishController.java
 *
 * Author: Walker M. White && Dashiell Brown
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.downstream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.downstream.*;
import edu.cornell.gdiac.downstream.obstacle.*;
import edu.cornell.gdiac.downstream.models.*;
import edu.cornell.gdiac.downstream.models.TetherModel.TetherType;

/**
 * Gameplay specific controller for Downstream.
 *
 * You will notice that asset loading is not done with static methods this time.  
 * Instance asset loading makes it easier to process our game modes in a loop, which 
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public class DownstreamController extends WorldController implements ContactListener {
	/** Reference to the fish texture */
	private static final String KOI_TEXTURE = "koi/koi.png";
	/** The reference for the tether textures  */
	private static final String LILY_TEXTURE = "tethers/lilypad.png";
	/** Reference to the enemy image assets */
	private static final String ENEMY_TEXTURE = "enemy/enemy.png";
	/** Reference to the Lantern asset image*/
	private static final String LANTERN_TEXTURE = "tethers/notlit.png";
	/** Reference to the Lightin Texture image */
	private static final String LIGHTING_TEXTURE = "tethers/aura.png";
	/** Reference to the 4-sided land texture */
	private static final String LAND_4SIDE_TEXTURE = "terrain/land.png";
	/** Reference to the left land texture */
	private static final String LEFT_LAND_TEXTURE = "terrain/left-border.png";
	/** Reference to the right land texture */
	private static final String RIGHT_LAND_TEXTURE = "terrain/right-border.png";
	/** Reference to the top land texture */
	private static final String TOP_LAND_TEXTURE = "terrain/top-border.png";
	/** Reference to the bottom land texture */
	private static final String BOTTOM_LAND_TEXTURE = "terrain/bottom-border.png";
	/** Reference to the lotus texture */
	private static final String LOTUS_TEXTURE= null;

	/** Reference to the land texture */
	private static String EARTH_FILE = "terrain/earthtile.png";

	/** Reference to the whirlpool texture */
	private static final String WHIRLPOOL_TEXTURE = "terrain/whirlpool.png";
	/** Reference to the flipped whirlpool texture */
	private static final String WHIRLPOOL_FLIP_TEXTURE = "terrain/whirlpool_flip.png";


	/** The assets sounds */
	private static final String CLICK_SOUND = "SOUNDS/menu_click.wav";
	private static final String LIGHTING_SOUND = "SOUNDS/lighting_1.mp3";
	private static final String DEATH_SOUND = "SOUNDS/fish_death.wav";
	private static final String BACKGROUND_SOUND = "SOUNDS/background_sound.mp3";


	/** Texture assets for the koi */
	private TextureRegion koiTexture;
	/** Texture assets for the lilypads */
	private TextureRegion lilyTexture;
	/** Texture assets for the enemy fish */
	private TextureRegion enemyTexture;
	/** Texture assets for lantern */
	private TextureRegion lanternTexture;
	/** Texture assets for light */
	private TextureRegion lightingTexture;
	/** Texture assets for the land */
	private TextureRegion land4Texture;
	private TextureRegion leftLandTexture;
	private TextureRegion rightLandTexture;
	private TextureRegion topLandTexture;
	private TextureRegion bottomLandTexture;

	/** The texture for walls and platforms */
	private TextureRegion earthTile;

	/** Texture assets for whirlpools */
	private TextureRegion whirlpoolTexture;
	private TextureRegion whirlpoolFlipTexture;




	/** Track asset loading from all instances and subclasses */
	private AssetState fishAssetState = AssetState.EMPTY;

	private boolean tethered;
	private boolean whirled;

	private float PLAYER_LINEAR_VELOCITY = 4f;

	private boolean enableSlow = false;
	private boolean enableLeadingLine = false;
	private boolean enableTetherRadius = true;
	
	
	//animations
	
	float stateTime;  
    float relativeTime = 0;
    
	Animation                      	lilyAnimation;          // #3
    Texture                         lilySheet;              // #4
    TextureRegion[]                 lilyFrames;             // #5
    SpriteBatch                     lilyspriteBatch;            // #6
    TextureRegion                   lilycurrentFrame;           // #7

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

		manager.load(LILY_TEXTURE, Texture.class);
		assets.add(LILY_TEXTURE);

		manager.load(LANTERN_TEXTURE, Texture.class);
		assets.add(LANTERN_TEXTURE);

		manager.load(LIGHTING_TEXTURE, Texture.class);
		assets.add(LIGHTING_TEXTURE);


//		manager.load(LAND_4SIDE_TEXTURE, Texture.class);
//		assets.add(LAND_4SIDE_TEXTURE);

//		manager.load(LEFT_LAND_TEXTURE, Texture.class);
//		assets.add(LEFT_LAND_TEXTURE);
//
//		manager.load(RIGHT_LAND_TEXTURE, Texture.class);
//		assets.add(RIGHT_LAND_TEXTURE);
//
//		manager.load(TOP_LAND_TEXTURE, Texture.class);
//		assets.add(TOP_LAND_TEXTURE);
//
//		manager.load(BOTTOM_LAND_TEXTURE, Texture.class);
//		assets.add(BOTTOM_LAND_TEXTURE);
		
		manager.load(EARTH_FILE,Texture.class);
		assets.add(EARTH_FILE);


		//sounds
		//manager.load(MAIN_FIRE_SOUND, Sound.class);
		//assets.add(MAIN_FIRE_SOUND);


		manager.load(LAND_4SIDE_TEXTURE, Texture.class);
		assets.add(LAND_4SIDE_TEXTURE);

		manager.load(LEFT_LAND_TEXTURE, Texture.class);
		assets.add(LEFT_LAND_TEXTURE);

		manager.load(RIGHT_LAND_TEXTURE, Texture.class);
		assets.add(RIGHT_LAND_TEXTURE);

		manager.load(TOP_LAND_TEXTURE, Texture.class);
		assets.add(TOP_LAND_TEXTURE);

		manager.load(BOTTOM_LAND_TEXTURE, Texture.class);
		assets.add(BOTTOM_LAND_TEXTURE);
		
		manager.load(WHIRLPOOL_TEXTURE, Texture.class);
		assets.add(WHIRLPOOL_TEXTURE);
		
		manager.load(WHIRLPOOL_FLIP_TEXTURE, Texture.class);
		assets.add(WHIRLPOOL_FLIP_TEXTURE);
/*
		manager.load(CLICK_SOUND, Sound.class);
		assets.add(CLICK_SOUND);
		
		manager.load(LIGHTING_SOUND, Sound.class);
		assets.add(LIGHTING_SOUND);
		
		manager.load(DEATH_SOUND, Sound.class);
		assets.add(DEATH_SOUND);
		*/
	
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
		int cols = 11;
		int rows = 1;
		//animations
		lilySheet = new Texture(Gdx.files.internal("tethers/lotus_strip.png"));
		    
	      	//walkSheet = new Texture(Gdx.files.internal("koi/unnamed.png")); // #9
	    TextureRegion[][] tmplily = TextureRegion.split(lilySheet, lilySheet.getWidth()/cols, lilySheet.getHeight()/rows);              // #10
	    lilyFrames = new TextureRegion[11 * 1];
	    int index = 0;
	    for (int i = 0; i < 1; i++) {
	        for (int j = 0; j < 11; j++) {
	                 lilyFrames[index++] = tmplily[i][j];
	        }
	    }
	    lilyAnimation = new Animation(.2f, lilyFrames);      // #11
	    lilyspriteBatch = new SpriteBatch();                // #12
		

		enemyTexture = createTexture(manager,ENEMY_TEXTURE,false);
		koiTexture = createTexture(manager,KOI_TEXTURE,false);
		lilyTexture = lilyFrames[0];
		lanternTexture = createTexture(manager, LANTERN_TEXTURE, false);
		lightingTexture = createTexture(manager, LIGHTING_TEXTURE, false);

		earthTile = createTexture(manager,EARTH_FILE,true);


		land4Texture = createTexture(manager,LAND_4SIDE_TEXTURE,false);
		leftLandTexture = createTexture(manager,LEFT_LAND_TEXTURE,false);
		rightLandTexture = createTexture(manager,RIGHT_LAND_TEXTURE,false);
		topLandTexture = createTexture(manager,TOP_LAND_TEXTURE,false);
		bottomLandTexture = createTexture(manager,BOTTOM_LAND_TEXTURE,false);
		whirlpoolTexture = createTexture(manager,WHIRLPOOL_TEXTURE,false);
		whirlpoolFlipTexture = createTexture(manager,WHIRLPOOL_FLIP_TEXTURE,false);
/*
		SoundController sounds = SoundController.getInstance();
		sounds.allocate(manager,CLICK_SOUND);
		sounds.allocate(manager, LIGHTING_SOUND);
		sounds.allocate(manager, DEATH_SOUND);
		*/


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




	private ArrayList<TetherModel> tethers = new ArrayList<TetherModel>();
	private ArrayList<TetherModel> lanterns = new ArrayList<TetherModel>();
	private ArrayList<EnemyModel> enemies = new ArrayList<EnemyModel>();
	private ArrayList<WhirlpoolModel> wpools = new ArrayList<WhirlpoolModel>();
	private double rot = 0;

	// Other game objects
	/** The initial koi position */
	private static Vector2 KOI_POS = new Vector2(-2, 6);
	/** The goal door position */
	private static Vector2 GOAL_POS = new Vector2( 6, 12);

	// Physics objects for the game
	/** Reference to the goalDoor (for collision detection) */
	private BoxObstacle goalDoor;
	/** Reference to the player avatar */
	private PlayerModel koi;

	private EnemyModel eFish;
	
	private CameraController cameraController;

	/**
	 * Creates and initialize a new instance of Downstream
	 *
	 * The game has no  gravity and deafault settings
	 */
	public DownstreamController() {
		setDebug(false);
		setComplete(false);
		setFailure(false);
		world.setContactListener(this);
		tethered = false;
		whirled = false;
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
		enemies.clear();
		lanterns.clear();
		tethers.clear();
		objects.clear();
		addQueue.clear();
		world.dispose();
		tethered = true;
		whirled = false;

		world = new World(gravity,false);
		world.setContactListener(this);
		setComplete(false);
		setFailure(false);
		populateLevel();
		canvas.setCameraPosition(koi.getPosition().cpy().scl(scale));
	}

	/**
	 * Lays out the game geography.
	 */
	private void populateLevel() {
		
		LevelEditor.Level level = LevelEditor.loadFromJson();
		
		// Add level goal
//		System.out.println(Arrays.toString(Thread.currentThread().getStackTrace()));
		cameraController = new CameraController(canvas.getCamera());
		
		float dwidth;
		float dheight;
		float rad = lilyTexture.getRegionWidth()/scale.x/2;
		float wrad = whirlpoolTexture.getRegionWidth()/scale.x/2;

		boolean sensorTethers = true;


		for (Vector2 lilypad : level.lilypads) {
			TetherModel lily = new TetherModel(lilypad.x, lilypad.y, rad);
			lily.setBodyType(BodyDef.BodyType.StaticBody);
			lily.setName("lily"+ 1);
			lily.setDensity(TETHER_DENSITY);
			lily.setFriction(TETHER_FRICTION);
			lily.setRestitution(TETHER_RESTITUTION);
			lily.setSensor(sensorTethers);
			lily.setDrawScale(scale);
			lily.setTexture(lilyTexture);
			addObject(lily);
			tethers.add(lily);
		}

		boolean sensorPools = true;
		
		/*
		WhirlpoolModel pool = new WhirlpoolModel(-2, -5);
		pool.setBodyType(BodyDef.BodyType.StaticBody);
		pool.setName("whirlpool" + 1);
		pool.setDensity(TETHER_DENSITY);
		pool.setRestitution(TETHER_RESTITUTION);
		pool.setSensor(sensorPools);
		pool.setDrawScale(scale);
		pool.setTexture(whirlpoolFlipTexture);
		addObject(pool);
		wpools.add(pool);
		
		*/

		
		for (Vector2 lotus : level.lotuses) {
			TetherModel lantern = new TetherModel(lotus.x, lotus.y, rad, true);
			lantern.setBodyType(BodyDef.BodyType.StaticBody);
			lantern.setName("lotus"+ 1);
			lantern.setDensity(TETHER_DENSITY);
			lantern.setFriction(TETHER_FRICTION);
			lantern.setRestitution(TETHER_RESTITUTION);
			lantern.setSensor(sensorTethers);
			lantern.setDrawScale(scale);
			lantern.setTexture(lanternTexture);
			lantern.setlightingTexture(lightingTexture);
			lantern.setRotation(0);
			addObject(lantern);
			tethers.add(lantern);
			lanterns.add(lantern);
		}
		
		for (ArrayList<Float> wall : level.walls) {
			PolygonObstacle obj;
			float[] wallFloat = new float[wall.size()];
			for (int i = 0; i < wall.size(); i++) wallFloat[i] = wall.get(i);
			obj = new PolygonObstacle(wallFloat, 0, 0);
			obj.setBodyType(BodyDef.BodyType.StaticBody);
			obj.setDensity(BASIC_DENSITY);
			obj.setFriction(BASIC_FRICTION);
			obj.setRestitution(BASIC_RESTITUTION);
			obj.setDrawScale(scale);
			obj.setTexture(earthTile);
			obj.setName("wall1");
			addObject(obj);
		}
		
		for (Map.Entry<String,ArrayList<Vector2>> entry : level.enemies.entrySet()) {
			Vector2 enemyPos = vectorOfString(entry.getKey());
			ArrayList<Vector2> enemyPath = entry.getValue();
//			for (Vector2 vector : enemyPath) {vector.x /= scale.x; vector.y /= scale.y;}
			System.out.println(enemyPath);
			TextureRegion etexture = enemyTexture;
			dwidth  = etexture.getRegionWidth()/scale.x;
			dheight = etexture.getRegionHeight()/scale.y;
			eFish = new EnemyModel(enemyPos.x, enemyPos.y, dwidth, dheight, enemyPath);
			eFish.setDensity(ENEMY_DENSITY);
			eFish.setFriction(ENEMY_FRICTION);
			eFish.setRestitution(BASIC_RESTITUTION);
			eFish.setName("enemy");
			eFish.setDrawScale(scale);
			eFish.setTexture(etexture);
			eFish.setAngle((float) (Math.PI/2));
			eFish.setBodyType(BodyDef.BodyType.StaticBody);
			eFish.setGoal(0, 0);
			addObject(eFish);
			enemies.add(eFish);
		}
		


		// Create the fish avatar

		dwidth  = koiTexture.getRegionWidth()/scale.x;
		dheight = koiTexture.getRegionHeight()/scale.y;
		koi = new PlayerModel(level.player.x, level.player.y, dwidth, dheight);
		koi.setDrawScale(scale);
		koi.setName("koi");
		koi.setTexture(koiTexture);
		koi.setTethered(false);

		koi.setWhirled(false);

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

		
		for (EnemyModel enemy : enemies) {
//			enemy.patrol();
//			enemy.moveTowardsGoal();
		}


		// unused. was testing using "s" to slow down
		//		if (enableSlow && input.slow) koi.setLinearVelocity(koi.getLinearVelocity().setLength(4));


		if (input.didTether()) {
			tethered = !tethered; 
			koi.setTethered(false);
			System.out.println("USA USA USA");
			cameraController.resetCameraVelocity();
		}
		if (!tethered) {
			koi.setLinearVelocity(koi.getLinearVelocity().setLength(PLAYER_LINEAR_VELOCITY*2));
		}
		if (tethered) {
			koi.setLinearVelocity(koi.getLinearVelocity().setLength(PLAYER_LINEAR_VELOCITY*1.5f));
		}

		TetherModel closestTether = getClosestTether();

		// ENEMY PATROL CODE
		for (EnemyModel enemy : enemies) {
			enemy.patrol();
			enemy.moveTowardsGoal();
		}

		//check to see if closest tether is just attached or has been previously attached
		if (tethered & closestTether.getEntry().x == 0f & closestTether.isLantern()){
			//if just attached, define it as such
			Vector2 ent = new Vector2(closestTether.getX(), closestTether.getY());
			closestTether.setEntry(ent);

		}
		//checks to see if the fish is within reasonable circulating distance. It will pass the if statment many times
		if (tethered){
			//this is because the fish moves to quickly to get an exact range, so we must find it within .5 distance
			if ((closestTether.getEntry().x + .5 > koi.getPosition().x) && (closestTether.getEntry().x -.5 < koi.getPosition().x && closestTether.isLantern())){
				//because of the range, we only want the first instance, so we only check if it has not been previously checked in the last frame. 
				if (closestTether.set == false){
					//System.out.println(closestTether.getRotations());
					closestTether.updateRotations();
				}
				closestTether.set = true;
			}
			else{
				closestTether.set = false;
			}
		}
		else {
			closestTether.set = false;
		}

		boolean camera_zoom = true;

		// if koi is tethered: move slowly to tether, reset camera speed
		// if koi is not tethered: accelerate to fish up to maximum speed
		
		if (koi.isTethered() || tethered && 
				koi.getPosition().sub(koi.getInitialTangentPoint(closestTether.getPosition())).len2() < .01) {
			if (!koi.isTethered()) {
//				System.out.println("PENIS");
				koi.refreshTetherForce(closestTether.getPosition(), closestTether.getOrbitRadius());
			}
			koi.applyTetherForce(closestTether.getPosition(), closestTether.getOrbitRadius());
			cameraController.moveCameraTowards(closestTether.getPosition().cpy().scl(scale));
			if (camera_zoom) cameraController.zoomOut();
			koi.setTethered(true);	
		} else {
			cameraController.moveCameraTowards(koi.getPosition().cpy().scl(scale));
			if (camera_zoom) cameraController.zoomIn();
		}
		/*
		WhirlpoolModel closestWhirlpool = getClosestWhirl();
		
		if (koi.getPosition().sub(koi.getInitialTangentPoint(closestWhirlpool.getPosition())).len2() < .01){
			if (!koi.isWhirled()) {
				koi.refreshWhirlForce(closestWhirlpool.getPosition(), closestWhirlpool.getOrbitRadius());
			}
			koi.applyWhirlForce(closestWhirlpool.getPosition(), closestWhirlpool.getOrbitRadius());
			cameraController.moveCameraTowards(closestWhirlpool.getPosition().cpy().scl(scale));
			if (camera_zoom) cameraController.zoomOut();
			koi.setWhirled(true);
		}
		
*/
		koi.resolveDirection();



		float angV = 3f;
		float radius = closestTether.getPosition().dst(koi.getPosition());
		float tetherSpeed = angV*radius;

		float MAX_SPEED = 7f;
		float MIN_SPEED = 6f;

		int motionType = 0;
		
		//animation
		stateTime += Gdx.graphics.getDeltaTime();           // #15
		lilycurrentFrame = lilyAnimation.getKeyFrame(stateTime, true);
		
		for (int i = 0; i < tethers.size(); i++){
			if (tethers.get(i).getTetherType() == TetherType.Lilypad){
				tethers.get(i).setTexture(lilycurrentFrame);
			}
		}

		SoundController.getInstance().update();
	}

	private TetherModel getClosestTether() {
		TetherModel closestTether = tethers.get(0);
		float closestDistance = tethers.get(0).getPosition().sub(koi.getPosition()).len2();
		for (TetherModel tether : tethers) {
			float newDistance = tether.getPosition().sub(koi.getPosition()).len2();
			if (newDistance < closestDistance) {
				closestDistance = newDistance;
				closestTether = tether;
			}
		}
		return closestTether;
	}
	
	
	private WhirlpoolModel getClosestWhirl() {
		WhirlpoolModel closestWhirl = wpools.get(0);
		float closestDistance = wpools.get(0).getPosition().sub(koi.getPosition()).len2();
		for(WhirlpoolModel w: wpools){
			float newDistance = w.getPosition().sub(koi.getPosition()).len2();
			if(newDistance < closestDistance){
				closestDistance = newDistance;
				closestWhirl = w;
			}
		}
		return closestWhirl;
	}

	public void draw(float delta) {
		super.draw(delta);

		if (enableLeadingLine) {
			Vector2 farOff = koi.getPosition().cpy();
			farOff.add(koi.getLinearVelocity().cpy().scl(1000));
			canvas.drawLeadingLine(koi.getPosition().cpy(), farOff);
//			System.out.println(koi.getPosition());
//			System.out.println(farOff);
		}
		if (enableTetherRadius) {
			Vector2 closestTether = getClosestTether().getPosition().cpy().scl(scale);
			Vector2 initialTangent = koi.getInitialTangentPoint(getClosestTether().getPosition()).scl(scale);
			float radius = closestTether.dst(initialTangent);
			canvas.drawTetherCircle(closestTether, TetherModel.TETHER_DEFAULT_RANGE*scale.x);
			//canvas.drawTetherCircle(koi.cent.cpy().scl(scale), koi.pull.len()/2*scale.len());
		}
		

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
		String s1 = ((Obstacle)body1.getUserData()).getName();
		String s2 = ((Obstacle)body2.getUserData()).getName();

		if( (body1.getUserData() == koi && body2.getUserData() == eFish) 
				|| (body2.getUserData() == koi && body1.getUserData() == eFish))  {
			setFailure(true);
		}

		if( (body2.getUserData() == koi && (s1.startsWith("lily") || s1.startsWith("lantern")))) {
			TetherModel t = (TetherModel) body1.getUserData();
		}
		
		if( (body2.getUserData() == koi && s1.startsWith("whirlpool"))){
			WhirlpoolModel w = (WhirlpoolModel)body1.getUserData();
		}

	}
	
	private static Vector2 vectorOfString(String s) {
		int comma = s.indexOf(",");
		int openParens = s.indexOf("(");
		int closeParens = s.indexOf(")");
		String xstr = s.substring(openParens+1,comma);
		String ystr = s.substring(comma+1,closeParens);
		float x = Float.parseFloat(xstr);
		float y = Float.parseFloat(ystr);
		return new Vector2(x,y);
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

		/*
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
		 */
	}


}