/*
 * FishController.java
 *
 * Author: Walker M. White && Dashiell Brown && Omar Abdelaziz
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.downstream;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
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
public class DownstreamController extends WorldController implements ContactListener, Screen, InputProcessor{
	//Texture References//
	/** Reference to the fish texture */
	private static final String KOI_TEXTURE = "koi/koi.png";
	/** Reference to the lilypad texture  */
	private static final String LILY_TEXTURE = "tethers/lilypad.png";
	/** Reference to the enemy texture */
	private static final String ENEMY_TEXTURE = "enemy/enemy.png";
	/** Reference to the lantern texture */
	private static final String LANTERN_TEXTURE = "tethers/notlit.png";
	/** Reference to the lighting texture */
	private static final String LIGHTING_TEXTURE = "tethers/aura.png";
	private static final String SHADOW_TEXTURE = "terrain/shadow.png";
	private static final String GOAL_TEXTURE = "terrain/goal.png";

	/** Reference to the repeating land texture */

	private static final String EARTH_FILE = "terrain/repeat tile.png";
	private static final String EARTH_FILE_N = "terrain/Grass_Night.jpg";
	private static final String EARTH_FILE_D = "terrain/Grass_Day.jpg";
	private static final String EARTH_FILE_S = "terrain/Grass_Sunset.jpg";
	private static final String ROCK_FILE_N = "terrain/Rock_Night.png";
	private static final String ROCK_FILE_D = "terrain/Rock_Day.png";
	private static final String ROCK_FILE_S = "terrain/Rock_Sunset.png";

	/** Reference to the whirlpool texture */
	private static final String WHIRLPOOL_TEXTURE = "terrain/whirlpool.png";
	/** Reference to the flipped whirlpool texture */
	private static final String WHIRLPOOL_FLIP_TEXTURE = "terrain/whirlpool_flip.png";
	/** HUD textures */
	private static final String ENERGYBAR_TEXTURE = "MENUS/UI_bar.png";
	private static final String UI_FLOWER = "MENUS/UI_lotus.png";
	private static final String OVERLAY = "terrain/texture.jpg";

	//TextureRegions//
	/** Texture assets for the koi */
	private TextureRegion koiTexture;
	/** Texture assets for lilypads */
	private TextureRegion lilyTexture;
	/** Texture assets for enemy fish */
	private TextureRegion enemyTexture;
	/** Texture assets for lanterns */
	private TextureRegion lanternTexture;
	/** Texture assets for light */
	private TextureRegion lightingTexture;
	private TextureRegion shadowTexture;
	private TextureRegion goalTexture;
	/** Texture assets for walls and platforms */
	private TextureRegion earthTile;

	private TextureRegion earthTileDay;
	private TextureRegion earthTileNight;
	private TextureRegion earthTileSunset;

	private TextureRegion rockDay;
	private TextureRegion rockNight;
	private TextureRegion rockSunset;

	/** Texture assets for whirlpools */
	private TextureRegion whirlpoolTexture;
	private TextureRegion whirlpoolFlipTexture;
	/** Texture assets for HUD */
	private TextureRegion energyBarTexture;
	private TextureRegion UILotusTexture;

	/** The HUD */
	public HUDitems HUD;

	//Game States//
	/** Track asset loading from all instances and subclasses */
	private AssetState fishAssetState = AssetState.EMPTY;
	/** Pause menu and button states */
	public PauseMenuMode pauseMenu;
	private int backState;
	private int resumeState;
	private int restartState;
	private int optionsState;
	private boolean wasPaused;
	private boolean paused;
	private boolean dead;
	private boolean whirled;


	private TetherModel checkpoint;



	private float PLAYER_LINEAR_VELOCITY = 6f;
	private boolean enableSlow = false;
	private boolean enableLeadingLine = false;
	private boolean enableTetherRadius = true;

	//Sounds//
	/** References to sounds */
	private static final String LIGHTING_SOUND = "SOUNDS/lighting_1.mp3";
	private Music deathSound;



	private Animation lilyAnimation; // #3
	private Texture lilySheet; // #4
	private TextureRegion[] lilyFrames; // #5
	private SpriteBatch lilyspriteBatch; // #6
	private TextureRegion lilycurrentFrame; // #7

	private Animation closedFlowerAnimation; // #3
	private Texture closedFlowerSheet; // #4
	private TextureRegion[] closedFlowerFrames; // #5
	private SpriteBatch closedFlowerspriteBatch; // #6
	private TextureRegion closedFlowercurrentFrame; // #7

	private Animation openFlowerAnimation; // #3
	private Texture openFlowerSheet; // #4
	private TextureRegion[] openFlowerFrames; // #5
	private SpriteBatch openFlowerspriteBatch; // #6
	private TextureRegion openFlowercurrentFrame; // #7

	private Animation openingFlowerAnimation; // #3
	private Texture openingFlowerSheet; // #4
	private TextureRegion[] openingFlowerFrames; // #5
	private SpriteBatch openingFlowerspriteBatch; // #6
	private TextureRegion openingFlowercurrentFrame; // #7

	private Animation closingFlowerAnimation; // #3
	private Texture closingFlowerSheet; // #4
	private TextureRegion[] closingFlowerFrames; // #5
	private SpriteBatch closingFlowerspriteBatch; // #6
	private TextureRegion closingFlowercurrentFrame; // #7

	private Animation koiSAnimation; // #3
	private Texture koiSSheet; // #4
	private TextureRegion[] koiSFrames; // #5
	private SpriteBatch koiSspriteBatch; // #6
	private TextureRegion koiScurrentFrame; // #7

	private Animation koiCAnimation; // #3
	private Texture koiCSheet; // #4
	private TextureRegion[] koiCFrames; // #5
	private SpriteBatch koiCspriteBatch; // #6
	private TextureRegion koiCcurrentFrame; // #7

	private Animation koiCAnimationFlipped;
	private TextureRegion[]	koiCFramesFlipped;
	private TextureRegion KoiCcurrentFrameFlipped;

	// Physics constants for initialization //
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
	private static final float TETHER_DENSITY = ENEMY_DENSITY;
	private static final float TETHER_FRICTION = ENEMY_FRICTION;
	private static final float TETHER_RESTITUTION = BASIC_RESTITUTION;

	// Important game objects, lists, and controllers //
	private ArrayList<TetherModel> tethers = new ArrayList<TetherModel>();
	private ArrayList<TetherModel> lanterns = new ArrayList<TetherModel>();
	private Stack<TetherModel> litlanterns = new Stack<TetherModel>();
	private ArrayList<ShadowModel> shadows = new ArrayList<ShadowModel>();
	private ArrayList<EnemyModel> enemies = new ArrayList<EnemyModel>();
	private ArrayList<WhirlpoolModel> wpools = new ArrayList<WhirlpoolModel>();
	private ArrayList<ArrayList<Float>> walls = new ArrayList<ArrayList<Float>>();
	private PlayerModel koi;
	private BoxObstacle goalDoor;
	private EnemyModel eFish;
	private CameraController cameraController;
	private CollisionController collisionController;
	private TetherModel closestTether;
	private WhirlpoolModel closestWhirlpool;
	private int litLotusCount;
	private int level = -1;
	private static final int RESPAWN_TIME = 100;
	private int respawnTimer = RESPAWN_TIME;
	private TetherModel checkpoint0;


	private double rot = 0;


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

		manager.load(KOI_TEXTURE, Texture.class);
		assets.add(KOI_TEXTURE);

		manager.load(ENEMY_TEXTURE, Texture.class);
		assets.add(ENEMY_TEXTURE);

		manager.load(LILY_TEXTURE, Texture.class);
		assets.add(LILY_TEXTURE);

		manager.load(LANTERN_TEXTURE, Texture.class);
		assets.add(LANTERN_TEXTURE);

		manager.load(LIGHTING_TEXTURE, Texture.class);
		assets.add(LIGHTING_TEXTURE);

		manager.load(SHADOW_TEXTURE, Texture.class);
		assets.add(SHADOW_TEXTURE);

		manager.load(GOAL_TEXTURE, Texture.class);
		assets.add(GOAL_TEXTURE);

		manager.load(EARTH_FILE,Texture.class);
		assets.add(EARTH_FILE);

		manager.load(EARTH_FILE_D,Texture.class);
		assets.add(EARTH_FILE_D);
		manager.load(EARTH_FILE_N,Texture.class);
		assets.add(EARTH_FILE_N);
		manager.load(EARTH_FILE_S,Texture.class);
		assets.add(EARTH_FILE_S);

		manager.load(ROCK_FILE_D,Texture.class);
		assets.add(ROCK_FILE_D);
		manager.load(ROCK_FILE_N,Texture.class);
		assets.add(ROCK_FILE_N);
		manager.load(ROCK_FILE_S,Texture.class);
		assets.add(ROCK_FILE_S);

		manager.load(WHIRLPOOL_TEXTURE, Texture.class);
		assets.add(WHIRLPOOL_TEXTURE);

		manager.load(WHIRLPOOL_FLIP_TEXTURE, Texture.class);
		assets.add(WHIRLPOOL_FLIP_TEXTURE);

		manager.load(ENERGYBAR_TEXTURE, Texture.class);
		assets.add(ENERGYBAR_TEXTURE);

		manager.load(UI_FLOWER, Texture.class);
		assets.add(UI_FLOWER);

		manager.load(OVERLAY, Texture.class);
		assets.add(OVERLAY);

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

		//animationDef
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

		cols = 11;
		rows = 1;

		closedFlowerSheet = new Texture(Gdx.files.internal("tethers/flowerclosed_spritesheet.png"));

		//walkSheet = new Texture(Gdx.files.internal("koi/unnamed.png")); // #9
		TextureRegion[][] tmpclosed = TextureRegion.split(closedFlowerSheet, closedFlowerSheet.getWidth()/cols, closedFlowerSheet.getHeight()/rows);              // #10
		closedFlowerFrames = new TextureRegion[cols * rows];
		index = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				closedFlowerFrames[index++] = tmpclosed[i][j];
			}
		}
		closedFlowerAnimation = new Animation(.2f, closedFlowerFrames);  
		closedFlowerspriteBatch = new SpriteBatch(); 

		openFlowerSheet = new Texture(Gdx.files.internal("tethers/floweropen_spritesheet.png"));
		TextureRegion[][] tmpOpen = TextureRegion.split(openFlowerSheet, openFlowerSheet.getWidth()/cols, openFlowerSheet.getHeight()/rows);              // #10
		openFlowerFrames = new TextureRegion[cols * rows];
		index = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				openFlowerFrames[index++] = tmpOpen[i][j];
			}
		}
		openFlowerAnimation = new Animation(.2f, openFlowerFrames);  
		openFlowerspriteBatch = new SpriteBatch(); 

		cols = 8; 

		openingFlowerSheet = new Texture(Gdx.files.internal("tethers/flower_opening_spritesheet.png"));
		TextureRegion[][] tmpOpening = TextureRegion.split(openingFlowerSheet, openingFlowerSheet.getWidth()/cols, openingFlowerSheet.getHeight()/rows);              // #10
		openingFlowerFrames = new TextureRegion[cols * rows];
		index = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				openingFlowerFrames[index++] = tmpOpening[i][j];
			}
		}
		openingFlowerAnimation = new Animation(.5f, openingFlowerFrames); 
		openingFlowerspriteBatch = new SpriteBatch(); 


		closingFlowerSheet = new Texture(Gdx.files.internal("tethers/flower_closing_spritesheet.png"));
		TextureRegion[][] tmpClosing = TextureRegion.split(closingFlowerSheet, closingFlowerSheet.getWidth()/cols, closingFlowerSheet.getHeight()/rows);              // #10
		closingFlowerFrames = new TextureRegion[cols * rows];
		index = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				closingFlowerFrames[index++] = tmpClosing[i][j];
			}
		}
		closingFlowerAnimation = new Animation(.5f, closingFlowerFrames); 
		closingFlowerspriteBatch = new SpriteBatch(); 


		cols = 12;
		koiSSheet = new Texture(Gdx.files.internal("koi/Straight_Koi.png"));
		TextureRegion[][] tmpkoiS = TextureRegion.split(koiSSheet, koiSSheet.getWidth()/cols, koiSSheet.getHeight()/rows);              // #10
		koiSFrames = new TextureRegion[cols * rows];
		index = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				koiSFrames[index++] = tmpkoiS[i][j];
			}
		}
		koiSAnimation = new Animation(.05f, koiSFrames); 
		koiSspriteBatch = new SpriteBatch(); 

		cols = 31;
		//remeber kiddies, animate both directions
		koiCSheet = new Texture(Gdx.files.internal("koi/curved_koi.png"));
		TextureRegion[][] tmpkoiC = TextureRegion.split(koiCSheet, koiCSheet.getWidth()/cols, koiCSheet.getHeight()/rows);              // #10
		TextureRegion[][] tmpkoiCFlipped = TextureRegion.split(koiCSheet, koiCSheet.getWidth()/cols, koiCSheet.getHeight()/rows);              // #10

		koiCFrames = new TextureRegion[cols * rows];
		koiCFramesFlipped = new TextureRegion[cols * rows];
		index = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				//tmpkoiC[i][j].flip(false, true);
				koiCFrames[index] = tmpkoiC[i][j];
				tmpkoiCFlipped[i][j].flip(false, true);
				koiCFramesFlipped[index++] = tmpkoiCFlipped[i][j];
			}
		}
		koiCAnimation = new Animation(.05f, koiCFrames); 
		koiCAnimationFlipped = new Animation(.05f, koiCFramesFlipped);
		koiCspriteBatch = new SpriteBatch(); 



		energyBarTexture = createTexture(manager, ENERGYBAR_TEXTURE, false);
		enemyTexture = createTexture(manager,ENEMY_TEXTURE,false);
		//koiTexture = koiSFrames[0];
		koiTexture = createTexture(manager, KOI_TEXTURE, false);
		lilyTexture = lilyFrames[0];
		lanternTexture = closedFlowerFrames[0];
		lightingTexture = createTexture(manager, LIGHTING_TEXTURE, false);
		shadowTexture = createTexture(manager, SHADOW_TEXTURE, false);
		goalTexture = createTexture(manager, GOAL_TEXTURE, false);
		UILotusTexture = createTexture(manager, UI_FLOWER, false);

		earthTile = createTexture(manager,EARTH_FILE_N,true);
		earthTileDay = createTexture(manager,EARTH_FILE_D, true);
		earthTileNight = createTexture(manager,EARTH_FILE_N, true);
		earthTileSunset = createTexture(manager,EARTH_FILE_S, true);

		whirlpoolTexture = createTexture(manager,WHIRLPOOL_TEXTURE,false);
		whirlpoolFlipTexture = createTexture(manager,WHIRLPOOL_FLIP_TEXTURE,false);

		deathSound = Gdx.audio.newMusic(Gdx.files.internal(LIGHTING_SOUND));
		deathSound.setLooping(false);

		super.loadContent(manager);
		fishAssetState = AssetState.COMPLETE;
	}


	private float stateTime;  
	private float relativeTime = 0;

	/**
	 * Creates and initialize a new instance of Downstream
	 *
	 * The game has no  gravity and default settings
	 */
	public DownstreamController() {
		setDebug(false);
		setComplete(false);
		setFailure(false);
		world.setContactListener(this);
		dead = false;
		whirled = false;
		world.setGravity(Vector2.Zero);
		paused = false;
		wasPaused = false;
	}

	public DownstreamController(int level) {
		this();
		this.level = level;
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
		litlanterns.clear();
		tethers.clear();
		shadows.clear();
		wpools.clear();
		objects.clear();
		addQueue.clear();
		world.dispose();

		dead = false;
		whirled = false;
		paused = false;
		wasPaused = false;
		pauseMenu = new PauseMenuMode(canvas);
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

		int NDS = new Random().nextInt(3);
		//follow convention chicos
		// 0 is day 1 is night 2 is sunset
		setDayTime(NDS);


		LevelEditor.Level level;
		if (this.level != -1) {
			level = LevelEditor.loadFromJson(this.level);
		} else {
			level = LevelEditor.loadFromJson();
		}

		cameraController = new CameraController(canvas.getCamera());

		float dwidth;
		float dheight;
		float rad = lilyTexture.getRegionWidth()/scale.x/2;

		boolean sensorTethers = true;
		boolean sensorPools = true;


		if (!level.wpools.isEmpty()) {
			for (Vector2 whirlpool : level.wpools) {
				WhirlpoolModel pool = new WhirlpoolModel(whirlpool.x, whirlpool.y,1);
				pool.setBodyType(BodyDef.BodyType.StaticBody);
				pool.setName("whirlpool" + 1);
				pool.setDensity(TETHER_DENSITY);
				pool.setFriction(TETHER_FRICTION);
				pool.setRestitution(TETHER_RESTITUTION);
				pool.setSensor(sensorPools);
				pool.setDrawScale(scale);
				pool.setTexture(whirlpoolFlipTexture);
				addObject(pool);
				wpools.add(pool);
			}
		}



		for (Map.Entry<String,ArrayList<Vector2>> entry : level.enemiesLevel.entrySet()) {
			Vector2 enemyPos = vectorOfString(entry.getKey());
			ArrayList<Vector2> enemyPath = entry.getValue();
			//			for (Vector2 vector : enemyPath) {vector.x /= scale.x; vector.y /= scale.y;}
			//			System.out.println(enemyPath);
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

		//Create goal tile
		Vector2 goalPos = level.goal.get(0);
		Vector2 shadowDest = level.goal.get(1);

		cache = shadowDest.cpy().sub(goalPos).nor().scl(-3);
		dwidth  = goalTexture.getRegionWidth()/scale.x;
		dheight = goalTexture.getRegionHeight()/scale.y;
		BoxObstacle goalTile = new BoxObstacle(goalPos.x+cache.x, goalPos.y+cache.y, dwidth, dheight);
		goalTile.setName("goal");
		goalTile.setDrawScale(scale);
		goalTile.setTexture(goalTexture);
		goalTile.setSensor(true);
		goalTile.setAngle((float) Math.atan2(goalPos.y-shadowDest.y,goalPos.x-shadowDest.x));
		addObject(goalTile);

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
			if (NDS == 0){
				obj.setTexture(earthTileDay);
			}
			if (NDS == 1){
				obj.setTexture(earthTileNight);
			}
			if (NDS == 2){
				obj.setTexture(earthTileSunset);
			}
			//obj.setTexture(earthTile);
			obj.setName("wall1");
			ArrayList<Float> scaledWall = new ArrayList<Float>();
			for (Float f : wall) scaledWall.add(f*scale.x);
			walls.add(scaledWall);
			addObject(obj);
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

		//create shadow gate
		dwidth = shadowTexture.getRegionWidth()/scale.x;
		dheight = shadowTexture.getRegionHeight()/scale.y;
		ShadowModel shadow = new ShadowModel(goalPos.x, goalPos.y, dwidth/4, dheight, shadowDest);
		shadow.setName("shadow");
		shadow.setDrawScale(scale);
		shadow.setTexture(shadowTexture);
		shadow.setSensor(true);
		shadow.setAngle((float) Math.atan2(goalPos.y-shadowDest.y,goalPos.x-shadowDest.x));
		shadows.add(shadow);
		addObject(shadow);

		//Setup checkpoint and collisioncontroller
		collisionController = new CollisionController(koi);
		checkpoint0 = getClosestTetherTo(koi.initPos);
		checkpoint = checkpoint0;


		float width = Math.abs(level.map.get(0).x - level.map.get(1).x);
		float height = Math.abs(level.map.get(0).y - level.map.get(1).y);
		Vector2 center = new Vector2((level.map.get(0).x + level.map.get(1).x)/2,
				(level.map.get(0).y + level.map.get(1).y)/2);
		cameraController.zoomStart(width, height, center, koi.getPosition().cpy().scl(scale));

		HUD = new HUDitems(lanterns.size(), UILotusTexture, energyBarTexture, displayFont);
		addHUD(HUD);


	}

	// Respawns fish once it collides with a lethal object. 
	// The player is transported to the last checkpoint or initial start state if no lotuses have been lit
	private void respawn(){
		if(respawnTimer <= 0){
			collisionController.clear();
			collisionController.initStart(checkpoint);
			koi.setPosition(checkpoint.getPosition().add(koi.NE.cpy().rotate90(1).nor().scl(TetherModel.TETHER_DEFAULT_ORBIT)));
			koi.setTethered(true);
			koi.setLinearVelocity(koi.NE);
			koi.setDead(false);
			respawnTimer = RESPAWN_TIME;
			return;
		} 
		else if(respawnTimer <= RESPAWN_TIME/2){
			cameraController.moveCameraTowards(checkpoint.getPosition().scl(scale));
			cameraController.resetCameraVelocity(); 
			respawnTimer--;
		}
		else if(respawnTimer == RESPAWN_TIME){
			koi.setAttemptingTether(false);
			koi.setTethered(true);
			koi.setLinearVelocity(Vector2.Zero);
			collisionController.clear();
		}
		respawnTimer--;
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
		if(collisionController.didWin()){
			setComplete(true);
		}
		if(koi.isDead()){
			deathSound.play();
			respawn();
		} else{
			//ZOOM IN TO PLAYER AT START OF LEVEL
			if (!cameraController.isZoomedToPlayer()) {
				cameraController.zoomToPlayer();
				return;
			}

			//CHECKPOINT CODE
			checkpoint = checkpoint0;
			for(TetherModel t : lanterns){
				if(t.lit){
					if(!litlanterns.contains(t)){
						litlanterns.push(t);	
					}
				} else{
					litlanterns.remove(t);
				}
			}
			if(litlanterns.size() > 0){
				checkpoint = litlanterns.peek();
			}

			//CLEAR SHADOW CODE
			System.out.println("WINNER: "+(lanterns.size() == litlanterns.size()));
			clearShadows(lanterns.size() == litlanterns.size());
			moveShadows();


			closestTether = getClosestTetherTo(koi.getPosition());
			// INPUT CODE
			InputController input = InputController.getInstance();
			if (input.didTether() && !isWhirled() && !koi.bursting) {
				if((koi.isTethered() || koi.isAttemptingTether())){
					koi.setTethered(false);					
					koi.setAttemptingTether(false); 
					cameraController.resetCameraVelocity();
				}
				else {
					if(collisionController.inRange()){
						koi.setAttemptingTether(true); 
						cameraController.resetCameraVelocity();
					}
				}
			} else if(input.didKill()){
				koi.setDead(true);
				return;
			}
			/*
			//WHIRLPOOL CODE
			if (wpools.isEmpty()){
				closestWhirlpool = null;
			}
			else{
				closestWhirlpool = getClosestWhirl();
			}
			// CHECK IF KOI WILL BE SUCKED INTO WHIRLPOOL //
			Vector2 close;
			Vector2 init;
			if (closestWhirlpool != null) {
				close = closestWhirlpool.getPosition();
				init = koi.getInitialTangentPoint(close);
				if (close.dst(koi.getPosition()) < WhirlpoolModel.WHIRL_DEFAULT_RANGE) {
					koi.setWhirled(true);
				}
				if (koi.getPosition().sub(init).len2() < .01) {
					koi.setWhirled(true);
					koi.refreshWhirlForce(close, closestWhirlpool.getOrbitRadius());
				} else {
					koi.applyWhirlForce(close, closestWhirlpool.getOrbitRadius());
				}
			}
			 */

			// ENEMY PATROL CODE
			for (EnemyModel enemy : enemies) {
				enemy.patrol();
				enemy.moveTowardsGoal();
				enemy.fleeFind();
				enemy.fleeFind(lanterns);
				if (enemy.dead){
					enemy.deactivatePhysics(world);
				}
			}

			// KOI VEOLOCITY CODE
			if (isTethered() && !isWhirled()) {
				koi.setLinearVelocity(koi.getLinearVelocity().setLength(PLAYER_LINEAR_VELOCITY*1.5f));
			} else{
				koi.setLinearVelocity(koi.getLinearVelocity().setLength(PLAYER_LINEAR_VELOCITY*2));
			}

			// LOTUS LIGHTING CODE
			closestTether.setTethered(isTethered() && closestTether.isLotus() && collisionController.inRangeOf(closestTether));
			//System.out.println(isTethered());
			//System.out.println(closestTether.isLotus());
			//System.out.println(collisionController.inRangeOf(closestTether));
			//System.out.println(closestTether.set);

			// TETHER FORCE CODE
			Vector2 close = getClosestTether().getPosition();
			Vector2 init = koi.getInitialTangentPoint(close);

			if (close.dst(koi.getPosition()) > TetherModel.TETHER_DEFAULT_RANGE*1.3){
				koi.setAttemptingTether(false);
				koi.setTethered(false);
			}
			// HIT TANGENT
			if (koi.isAttemptingTether() && (koi.getPosition().sub(init).len2() < .01) ) {
				System.out.println("tether");
				koi.setTethered(true);
				koi.setAttemptingTether(false);
				koi.refreshTetherForce(close, closestTether.getOrbitRadius());
			}
			// PAST TANGENT
			else if (koi.isAttemptingTether() && !koi.willIntersect(init) && koi.pastTangent(init)) {
				koi.passAdjust(close);
			}
			else {}
			koi.applyTetherForce(close, closestTether.getOrbitRadius());


			// RESOLVE FISH IMG
			koi.resolveDirection();




			// CAMERA ZOOM CODE
			if (isTethered()){  
				cameraController.moveCameraTowards(closestTether.getPosition().cpy().scl(scale));
				cameraController.zoomOut();
			}
			else{
				cameraController.moveCameraTowards(koi.getPosition().cpy().scl(scale));
				cameraController.zoomIn();
			}

			//burst code
			koi.updateRestore();
			if (input.fast) {
				koi.burst();
				cameraController.moveCameraTowards(koi.getPosition().cpy().scl(scale));
			}

			//ANIMATION CODE
			stateTime += Gdx.graphics.getDeltaTime();           // #15
			lilycurrentFrame = lilyAnimation.getKeyFrame(stateTime, true);
			closedFlowercurrentFrame = closedFlowerAnimation.getKeyFrame(stateTime, true);
			openFlowercurrentFrame = openFlowerAnimation.getKeyFrame(stateTime, true);
			koiScurrentFrame = koiSAnimation.getKeyFrame(stateTime, true);
			koiCcurrentFrame = koiCAnimation.getKeyFrame(stateTime, true);
			KoiCcurrentFrameFlipped = koiCAnimationFlipped.getKeyFrame(stateTime, true);

			//System.out.println(relativeTime);
			//koiCcurrentFrame.flip(koi.left(closestTether), false);
			if (koi.isTethered()){
				koi.setCurved(true);
				if (koi.left(closestTether)){
					koi.setTexture(koiCcurrentFrame);
				}
				else{
					koi.setTexture(KoiCcurrentFrameFlipped);
				}
			}
			else{
				koi.setCurved(false);
				koi.setTexture(koiScurrentFrame);
			}
			//koi.setTexture(koiCcurrentFrame);


			//FSM to handle Lotus
			for (int i = 0; i < tethers.size(); i++){
				if (collisionController.inRangeOf(tethers.get(i))){
					tethers.get(i).inrange = true;
				}
				else{
					tethers.get(i).inrange = false;
				}

				if (tethers.get(i).getTetherType() == TetherType.Lilypad){
					tethers.get(i).setTexture(lilycurrentFrame);
				}
				if (tethers.get(i).getTetherType() == TetherType.Lantern) {
					//System.out.println("here");
					if (tethers.get(i).getOpening() == 0){
						tethers.get(i).setTexture(closedFlowercurrentFrame);
						if (tethers.get(i).set){

							tethers.get(i).setOpening(1);
						}
					}
					if (tethers.get(i).getOpening() == 1){

						if (!openingFlowerAnimation.isAnimationFinished(relativeTime))
						{openingFlowercurrentFrame = openingFlowerAnimation.getKeyFrame(relativeTime, true);
						relativeTime += Gdx.graphics.getDeltaTime();  
						tethers.get(i).setTexture(openingFlowercurrentFrame);
						if(!tethers.get(i).set){
							//go to closing
							//relativeTime = 0;
							tethers.get(i).setOpening(3);
						}
						}
						if (openingFlowerAnimation.isAnimationFinished(relativeTime)){
							System.out.println("finished");
							tethers.get(i).setOpening(2);
							relativeTime = 0;
						}

					}
					if (tethers.get(i).getOpening() == 2){
						tethers.get(i).setTexture(openFlowercurrentFrame);
						/*if (tethers.get(i).set){
							tethers.get(i).setOpening(1);
						}*/
					}
					if (tethers.get(i).getOpening() == 3){ 
						if(!tethers.get(i).set){
							if(!closingFlowerAnimation.isAnimationFinished(relativeTime)){
								closingFlowercurrentFrame = closingFlowerAnimation.getKeyFrame(relativeTime, true);
								relativeTime += Gdx.graphics.getDeltaTime();  
								tethers.get(i).setTexture(closingFlowercurrentFrame);
							}
							if(closingFlowerAnimation.isAnimationFinished(relativeTime)){
								tethers.get(i).setOpening(0);
								relativeTime = 0;
							}
						}
						if(tethers.get(i).set){
							tethers.get(i).setOpening(1);
						}
					}
					if(tethers.get(i).lit){
						tethers.get(i).setTexture(openFlowercurrentFrame);
					}
				}
			}
		}
		SoundController.getInstance().update();
		HUD.updateHUD(litlanterns.size(), koi.getEnergy());
	}






private void clearShadows(boolean b) {
	for (ShadowModel s : shadows){
		s.clearShadow(b);;
	}	}

private void moveShadows() {
	for (ShadowModel s : shadows){
		s.moveTowardsGoal();
	}
}

private boolean isTethered() {
	return koi.isTethered();
}

private TetherModel getClosestTether() {
	if(collisionController.inRange()){
		return collisionController.getClosestTetherInRange();
	}
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

private TetherModel getClosestTetherTo(Vector2 v) {
	TetherModel closestTether = tethers.get(0);
	float closestDistance = tethers.get(0).getPosition().sub(v).len();
	for (TetherModel tether : tethers) {
		float newDistance = tether.getPosition().sub(v).len();
		if (newDistance < closestDistance) {
			closestDistance = newDistance;
			closestTether = tether;
		}
	}
	return closestTether;
}
private boolean isWhirled(){
	return koi.isWhirled();
}

private WhirlpoolModel getClosestWhirl() {
	if(collisionController.inRangePool()){
		return collisionController.getClosestWhirlpoolInRange();
	}
	WhirlpoolModel closestPool = wpools.get(0);
	float closestDistance = wpools.get(0).getPosition().sub(koi.getPosition()).len2();
	for (WhirlpoolModel wp : wpools) {
		float newDistance = wp.getPosition().sub(koi.getPosition()).len2();
		if (newDistance < closestDistance) {
			closestDistance = newDistance;
			closestWhirlpool = wp;
		}
	}
	return closestWhirlpool;
}

public void draw(float delta) {

	//		System.out.println("paused: " + paused);
	//		System.out.println("waspaused: " + wasPaused);

	if (paused){
		pauseMenu.draw();
	}
	else {
		super.draw(delta);
		canvas.beginHUD();
		HUD.draw(canvas);
		for (ArrayList<Float> wall : walls) canvas.drawPath(wall);
		canvas.end();
	}

}

/**
 * Called when the Screen should render itself.
 *
 * We defer to the other methods update() and draw().  However, it is VERY important
 * that we only quit AFTER a draw.
 *
 * @param delta Number of seconds since last animation frame
 */
public void render(float delta) {
	InputController input = InputController.getInstance();
	if(wasPaused){

		paused =true;

	}
	else{
		paused = input.didPause();
		wasPaused = paused;
		//			if (paused) cameraController.pauseCamera();
	}

	if (active) {
		if (preUpdate(delta) && !paused) {
			update(delta); // This is the one that must be defined.
			postUpdate(delta);
		}
		this.draw(delta);
		if (goOptions() && listener != null) {
			//listener.exitScreen(this, WorldController.EXIT_OPTIONS);
		}
		if (goBack() && listener != null) {
			listener.exitScreen(this, WorldController.EXIT_MAIN);
		}
		if (restartLevel() && listener != null) {
			listener.exitScreen(this, this.level);
		}
		if (resumePlay() && listener != null) {
			resumeState = 0;
			restartState = 0;
			optionsState = 0;
			backState = 0;
			wasPaused = false;
			paused = false;
			//				cameraController.unpauseCamera();
		}

	}
}

/**
 * Dispose of all (non-static) resources allocated to this mode.
 */
public void dispose() {
	for(Obstacle obj : objects) {
		obj.deactivatePhysics(world);
	}
	objects.clear();
	addQueue.clear();
	world.dispose();
	pauseMenu.dispose();
	objects = null;
	addQueue = null;
	bounds = null;
	scale  = null;
	world  = null;
	canvas = null;
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
	koi.setDead(collisionController.begin(contact));
}

/**
 * Callback method for the start of a collision
 *
 * This method is called when two objects cease to touch.  We do not use it.
 */ 
public void endContact(Contact contact) {
	collisionController.end(contact);
}

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
}

//PAUSE MENU METHODS


public boolean goBack() {
	return backState == 2;
}

public boolean resumePlay(){
	return resumeState == 2;
}

public boolean goOptions(){
	return optionsState == 2;
}

public boolean restartLevel(){
	return restartState == 2;
}

// PROCESSING PLAYER INPUT
/**
 * Called when the screen was touched or a mouse button was pressed.
 *
 * This method checks to see if the play button is available and if the
 * click is in the bounds of the play button. If so, it signals the that the
 * button has been pressed and is currently down. Any mouse button is
 * accepted.
 *
 * @param screenX
 *            the x-coordinate of the mouse on the screen
 * @param screenY
 *            the y-coordinate of the mouse on the screen
 * @param pointer
 *            the button or touch finger number
 * @return whether to hand the event to other listeners.
 */
public boolean touchDown(int screenX, int screenY, int pointer, int button) {
	if (paused) {

		// Flip to match graphics coordinates
		screenY = canvas.getHeight() - screenY;
		float dx = Math.abs(screenX - PauseMenuMode.backPos.x);
		float dy = Math.abs(screenY - PauseMenuMode.backPos.y);

		if (dx < pauseMenu.scale * pauseMenu.back.getWidth() / 2
				&& dy < pauseMenu.scale * pauseMenu.back.getHeight() / 2) {
			backState = 1;
		}

		dx = Math.abs(screenX - PauseMenuMode.resumePos.x);
		dy = Math.abs(screenY - PauseMenuMode.resumePos.y);

		if (dx < pauseMenu.scale * pauseMenu.resume.getWidth() / 2
				&& dy < pauseMenu.scale * pauseMenu.resume.getHeight() / 2) {
			resumeState = 1;
		}

		dx = Math.abs(screenX - PauseMenuMode.restartPos.x);
		dy = Math.abs(screenY - PauseMenuMode.restartPos.y);

		if (dx < pauseMenu.scale * pauseMenu.restart.getWidth() / 2
				&& dy < pauseMenu.scale * pauseMenu.restart.getHeight() / 2) {
			restartState = 1;
		}

		dx = Math.abs(screenX - PauseMenuMode.optionsPos.x);
		dy = Math.abs(screenY - PauseMenuMode.optionsPos.y);

		if (dx < pauseMenu.scale * pauseMenu.options.getWidth() / 2
				&& dy < pauseMenu.scale * pauseMenu.options.getHeight() / 2) {
			optionsState = 1;
		}
	}
	return false;
}

/**
 * Called when a finger was lifted or a mouse button was released.
 *
 * This method checks to see if the play button is currently pressed down.
 * If so, it signals the that the player is ready to go.
 *
 * @param screenX
 *            the x-coordinate of the mouse on the screen
 * @param screenY
 *            the y-coordinate of the mouse on the screen
 * @param pointer
 *            the button or touch finger number
 * @return whether to hand the event to other listeners.
 */
public boolean touchUp(int screenX, int screenY, int pointer, int button) {
	if (paused) {
		if (backState == 1) {
			backState = 2;
			return false;
		}
		if (resumeState == 1) {
			resumeState = 2;
			return false;
		}
		if (restartState == 1) {
			restartState = 2;
			return false;
		}
		if (optionsState == 1) {
			optionsState = 2;
			return false;
		}
	}
	return true;
}

@Override
public boolean keyDown(int keycode) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public boolean keyUp(int keycode) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public boolean keyTyped(char character) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public boolean touchDragged(int screenX, int screenY, int pointer) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public boolean mouseMoved(int screenX, int screenY) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public boolean scrolled(int amount) {
	// TODO Auto-generated method stub
	return false;
}


}