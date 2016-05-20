/*
 * FishController.java
 *
 * Author: Walker M. White && Dashiell Brown && Omar Abdelaziz
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.downstream;

import java.util.ArrayList;
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

	//Game States//
	/** Track asset loading from all instances and subclasses */
	private AssetState fishAssetState = AssetState.EMPTY;
	/** Pause menu and button states */
	public PauseMenuMode pauseMenu;
	private int backState;
	private int resumeState;
	private int restartState;
	private boolean paused;
	/** Player states */
	private boolean dead;
	private boolean whirled;
	private boolean whirlpoolsOn;
	float speed;
	private TetherModel checkpoint;
	private float PLAYER_LINEAR_VELOCITY = 6f;
	private boolean enableSlow = false;
	private boolean enableLeadingLine = true;
	private boolean enableTetherRadius = true;

	// Physics constants //
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
	private ArrayList<WheelObstacle> rocks = new ArrayList<WheelObstacle>();
	private ArrayList<EnemyModel> enemies = new ArrayList<EnemyModel>();
	private ArrayList<WhirlpoolModel> wpools = new ArrayList<WhirlpoolModel>();
	private ArrayList<WModel2> wps = new ArrayList<WModel2>();

	private PlayerModel koi;
	private BoxObstacle goalTile;
	private EnemyModel eFish;
	private CameraController cameraController;
	private CollisionController collisionController;
	private TetherModel closestTether;
	private WhirlpoolModel closestWhirlpool;
	private int litLotusCount;
	private int level = -1;
	private static final int RESPAWN_TIME = 150;
	private static final float MIN_SPEED = .5f;
	private static final float MAX_SPEED = 1.75f;
	private int respawnTimer = RESPAWN_TIME;
	private TetherModel checkpoint0;
	private int tillNextLevel = 0;
	private Vector2 enemyPos8;
	private ArrayList<Vector2> enemyPath8;

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

		super.loadContent(manager);
		fishAssetState = AssetState.COMPLETE;
	}

	private float stateTime;  
	private float relativeTime = 0;
	private float levelCamWidth;
	private float levelCamHeight;
	private Vector2 center;
	private boolean started = false;
	private Vector2 cacheVel;
	private TetherModel cacheAttempt;
	private boolean tetherFade;

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
		whirlpoolsOn = true;
		world.setGravity(Vector2.Zero);
		paused = false;

		speed = 1;
	}

	public DownstreamController(int level) {
		this();
		this.level = level;
	}
	/***
	 * use when clearing the level to populate a new level
	 */
	public void deleteAll(){
		Vector2 gravity = new Vector2(world.getGravity() );

		for(Obstacle obj : objects) {
			obj.deactivatePhysics(world);
		}
		enemies.clear();
		lanterns.clear();
		litlanterns.clear();
		tethers.clear();
		rocks.clear();
		shadows.clear();
		wpools.clear();
		objects.clear();
		addQueue.clear();
		world.dispose();
		walls.clear();
		wps.clear();

		dead = false;
		whirled = false;
		started = false;
		paused = false;

		pauseMenu = new PauseMenuMode(canvas);
		world = new World(gravity,false);
		world.setContactListener(this);
		setComplete(false);
		setFailure(false);

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
		walls.clear();
		wps.clear();

		dead = false;
		whirled = false;

		started = false;
		paused = false;

		pauseMenu = new PauseMenuMode(canvas);
		world = new World(gravity,false);
		world.setContactListener(this);
		setComplete(false);
		setFailure(false);
		populateLevel();
	}

	private Color levelAlpha(int level){
		if (level <= 8){
			return new Color(255, 255, 255, (float)((level-1) % 8)/(8f));
		}
		else if (level <= 13){
			return new Color(255, 255, 255, (float)((level-9) % 5)/(5f));
		}
		else{
			return new Color(255, 255, 255, 1);
		}
	}

	private int NightDayDeterminer(int level){
		if (level < 5){
			return 0;
		}
		else if (level < 10){
			return 1;
		}
		else{ 
			return 2;
		}
	}

	private int dayNightBinary(int level){
		if (level <= 8){
			return 0;
		}
		else if (level <= 16){
			return 1;
		}
		else{
			return 2;
		}
	}

	/**
	 * Lays out the game geography.
	 */
	private void populateLevel() {

		LevelEditor.Level level;
		if (this.level != -1) {
			level = LevelEditor.loadFromJson(this.level);
		} else {
			level = LevelEditor.loadFromJson();
		}


		// 0 is day 1 is sunset 2 is night
		int staticNDS = dayNightBinary(this.level);
		int animationNDS = NightDayDeterminer(this.level);

		//Set fade for water/background
		if(this.level <= 7){
			setDayTime(staticNDS);
		} else if(this.level <= 15){
			setDayTime(staticNDS);
		}

		setLevelAlpha(levelAlpha(this.level));
		tillNextLevel = 0;
		tetherFade = false;
		//animation is a bitch
		if (!tetherFade){
			if (animationNDS == 0){
				lilyAnimation = new Animation(.05f, lilyFramesDay);
				openingFlowerAnimation = new Animation(.2f, openingFlowerFramesDay);
				closingFlowerAnimation = new Animation(.2f, closingFlowerFramesDay);
				closedFlowerAnimation = new Animation(.2f, closedFlowerFramesDay);
				openFlowerAnimation = new Animation(.2f, openFlowerFramesDay);

				lilyAnimation2 = new Animation(.1f, lilyFramesDay);
				openingFlowerAnimation2 = new Animation(.2f, openingFlowerFramesDay);
				closingFlowerAnimation2 = new Animation(.2f, closingFlowerFramesDay);
				closedFlowerAnimation2 = new Animation(.2f, closedFlowerFramesDay);
				openFlowerAnimation2 = new Animation(.2f, openFlowerFramesDay);
			}
			else if (animationNDS == 1){
				lilyAnimation = new Animation(.05f, lilyFramesSunset);
				openingFlowerAnimation = new Animation(.2f, openingFlowerFramesSunset);
				closingFlowerAnimation = new Animation(.2f, closingFlowerFramesSunset);
				closedFlowerAnimation = new Animation(.2f, closedFlowerFramesSunset);
				openFlowerAnimation = new Animation(.2f, openFlowerFramesSunset);

				lilyAnimation2 = new Animation(.1f, lilyFramesSunset);
				openingFlowerAnimation2 = new Animation(.2f, openingFlowerFramesSunset);
				closingFlowerAnimation2 = new Animation(.2f, closingFlowerFramesSunset);
				closedFlowerAnimation2 = new Animation(.2f, closedFlowerFramesSunset);
				openFlowerAnimation2 = new Animation(.2f, openFlowerFramesSunset);
			}
			else{
				lilyAnimation = new Animation(.05f, lilyFramesNight);
				openingFlowerAnimation = new Animation(.2f, openingFlowerFramesNight);
				closingFlowerAnimation = new Animation(.2f, closingFlowerFramesNight);
				closedFlowerAnimation = new Animation(.2f, closedFlowerFramesNight);
				openFlowerAnimation = new Animation(.2f, openFlowerFramesNight);

				lilyAnimation2 = new Animation(.1f, lilyFramesNight);
				openingFlowerAnimation2 = new Animation(.2f, openingFlowerFramesNight);
				closingFlowerAnimation2 = new Animation(.2f, closingFlowerFramesNight);
				closedFlowerAnimation2 = new Animation(.2f, closedFlowerFramesNight);
				openFlowerAnimation2 = new Animation(.2f, openFlowerFramesNight);
			}
		}
		else{
			//if overlaying time change
			if (staticNDS == 0){
				//day
				lilyAnimation = new Animation(.1f, lilyFramesDay);
				openingFlowerAnimation = new Animation(.2f, openingFlowerFramesDay);
				closingFlowerAnimation = new Animation(.2f, closingFlowerFramesDay);
				closedFlowerAnimation = new Animation(.2f, closedFlowerFramesDay);
				openFlowerAnimation = new Animation(.2f, openFlowerFramesDay);

				lilyAnimation2 = new Animation(.1f, lilyFramesSunset);
				openingFlowerAnimation2 = new Animation(.2f, openingFlowerFramesSunset);
				closingFlowerAnimation2 = new Animation(.2f, closingFlowerFramesSunset);
				closedFlowerAnimation2 = new Animation(.2f, closedFlowerFramesSunset);
				openFlowerAnimation2 = new Animation(.2f, openFlowerFramesSunset);
			}
			else if(staticNDS == 1){
				//night
				lilyAnimation = new Animation(.1f, lilyFramesSunset);
				openingFlowerAnimation = new Animation(.2f, openingFlowerFramesSunset);
				closingFlowerAnimation = new Animation(.2f, closingFlowerFramesSunset);
				closedFlowerAnimation = new Animation(.2f, closedFlowerFramesSunset);
				openFlowerAnimation = new Animation(.2f, openFlowerFramesSunset);

				lilyAnimation2 = new Animation(.1f, lilyFramesNight);
				openingFlowerAnimation2 = new Animation(.2f, openingFlowerFramesNight);
				closingFlowerAnimation2 = new Animation(.2f, closingFlowerFramesNight);
				closedFlowerAnimation2 = new Animation(.2f, closedFlowerFramesNight);
				openFlowerAnimation2 = new Animation(.2f, openFlowerFramesNight);
			}
			else{
				//keep level night after 15
				lilyAnimation = new Animation(.1f, lilyFramesSunset);
				openingFlowerAnimation = new Animation(.2f, openingFlowerFramesSunset);
				closingFlowerAnimation = new Animation(.2f, closingFlowerFramesSunset);
				closedFlowerAnimation = new Animation(.2f, closedFlowerFramesSunset);
				openFlowerAnimation = new Animation(.2f, openFlowerFramesSunset);

				lilyAnimation2 = new Animation(.1f, lilyFramesNight);
				openingFlowerAnimation2 = new Animation(.2f, openingFlowerFramesNight);
				closingFlowerAnimation2 = new Animation(.2f, closingFlowerFramesNight);
				closedFlowerAnimation2 = new Animation(.2f, closedFlowerFramesNight);
				openFlowerAnimation2 = new Animation(.2f, openFlowerFramesNight);
			}
		}





		cameraController = new CameraController(canvas.getCamera());
		cameraController.setWon(false);
		
		float dwidth;
		float dheight;
		float rad = lilyTexture.getRegionWidth()/scale.x/2;
		lilyTexture = lilyAnimation.getKeyFrame(stateTime);

		boolean sensorTethers = true;
		boolean sensorPools = true;


		if (level.whirlpools != null) {
			for (LevelEditor.Vector4 whirlpool: level.whirlpools) {

				Vector2 ang = new Vector2(whirlpool.z-whirlpool.x, whirlpool.w-whirlpool.y);
				WModel2 wp = new WModel2(whirlpool.x, whirlpool.y, ang);
				wp.setBodyType(BodyDef.BodyType.StaticBody);
				wp.setName("whirlpool"+ 1);
				wp.setDensity(TETHER_DENSITY);
				wp.setFriction(TETHER_FRICTION);
				wp.setRestitution(TETHER_RESTITUTION);
				wp.setSensor(sensorTethers);
				wp.setDrawScale(scale);
				wp.setTexture(whirlpoolTexture);
				wp.setArrowTexture(whirlArrow);
				addObject(wp);
				wps.add(wp);
			}
		}



		for (Map.Entry<String,ArrayList<Vector2>> entry : level.enemiesLevel.entrySet()) {
			Vector2 enemyPos = vectorOfString(entry.getKey());
			ArrayList<Vector2> enemyPath = entry.getValue();
			//			for (Vector2 vector : enemyPath) {vector.x /= scale.x; vector.y /= scale.y;}
			//			System.out.println(enemyPath);
			TextureRegion etexture = enemyTexture;
			//dwidth  = etexture.getRegionWidth()/scale.x;
			dwidth = 2.85f;
			//dheight = etexture.getRegionHeight()/scale.y;
			dheight = 1.675f;
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
			
			if (this.level == 12){
				enemyPos8 = enemyPos;
				enemyPath8 =enemyPath;
			}
		}

		//Create goal tile
		Vector2 goalPos = level.goal.get(0);
		Vector2 shadowDest = level.goal.get(1);

		cache = shadowDest.cpy().sub(goalPos).nor().scl(2);
		dwidth  = goalTexture.getRegionWidth()/scale.x;
		dheight = goalTexture.getRegionHeight()/scale.y;
		goalTexture.setRegionHeight(goalTexture.getRegionHeight());
		goalTexture.setRegionWidth(goalTexture.getRegionWidth());
		goalTile = new BoxObstacle(goalPos.x, goalPos.y, dwidth/2, dheight/2);
		goalTile.setName("goal");
		goalTile.setDrawScale(scale);
		goalTile.setTexture(goalTexture);
		goalTile.setDensity(BASIC_DENSITY);
		goalTile.setFriction(BASIC_FRICTION);
		goalTile.setRestitution(BASIC_RESTITUTION);
		goalTile.setSensor(true);
		goalTile.setAngle((float) Math.atan2(shadowDest.y-goalPos.y,shadowDest.x-goalPos.x));
		addObject(goalTile);

		// Create the fish avatar
		dwidth  = koiTexture.getRegionWidth()/scale.x;
		dheight = koiTexture.getRegionHeight()/scale.y;
		//		System.out.println(dwidth + " " + dheight);
		koi = new PlayerModel(level.player.x, level.player.y, 2.5f, 0.925f);
		koi.setDrawScale(scale);
		koi.setName("koi");
		koi.setTexture(koiTexture);
		koi.setWhirled(false);
		koi.setArrowTexture(koiArrow);
		addObject(koi);

		//create shadow(s)
		if(level.lotuses.size() > 0 || level.lanterns != null && level.lanterns.size() > 0){
			dwidth = shadowTexture.getRegionWidth()/scale.x*1.3f;
			dheight = shadowTexture.getRegionHeight()/scale.y*1.3f;
			ShadowModel shadow = new ShadowModel(goalPos.x, goalPos.y, dwidth, dheight, shadowDest);
			shadow.setName("shadow");
			shadow.setDrawScale(scale.cpy());
			shadow.setTexture(shadowTexture);

			shadow.setDensity(BASIC_DENSITY);
			shadow.setFriction(BASIC_FRICTION);
			shadow.setRestitution(BASIC_RESTITUTION);

			shadow.setSensor(false);			
			shadow.setAngle((float) Math.atan2(shadowDest.y-goalPos.y,shadowDest.x-goalPos.x));
			shadows.add(shadow);
			addObject(shadow);
		}


		if (level.shores != null) {
			for (ArrayList<Float> shore : level.shores) {
				PolygonObstacle obj;
				float[] shoreFloat = new float[shore.size()];
				for (int i = 0; i < shore.size(); i++) shoreFloat[i] = shore.get(i);
				obj = new PolygonObstacle(shoreFloat, 0, 0);
				obj.setBodyType(BodyDef.BodyType.StaticBody);
				obj.setDensity(BASIC_DENSITY);
				obj.setFriction(BASIC_FRICTION);
				obj.setRestitution(BASIC_RESTITUTION);
				obj.setDrawScale(scale);
				obj.setTexture(shoreTile);
				//obj.setTexture(earthTile);
				obj.setName("shore");
				obj.setSensor(true);
				ArrayList<Float> scaledShore = new ArrayList<Float>();
				for (Float f : shore) scaledShore.add(f*scale.x);
//				walls.add(scaledShore);
				addObject(obj);
			}
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
//			obj.setTexture(shoreTile);
			if (staticNDS == 0){
				obj.setTexture(earthTileDay);
				obj.setOverlay(earthTileSunset,levelAlpha);

			}
			else if (staticNDS == 1){
				obj.setTexture(earthTileSunset);
				obj.setOverlay(earthTileNight,levelAlpha);
			}
			else{
				//AFTER LEVEL 15
				obj.setTexture(earthTileNight);
			}

			//obj.setTexture(earthTile);
			obj.setName("wall1");
			ArrayList<Float> scaledWall = new ArrayList<Float>();
			for (Float f : wall) scaledWall.add(f*scale.x);
			walls.add(scaledWall);
			//			obj.drawnWall = scaledWall;
			addObject(obj);
		}


		if (level.rocks != null) {
			for (Vector2 rock : level.rocks) {
				WheelObstacle obj;
				//				System.out.println(rockDay);
				//				System.out.println("ROCK" + rockDay.getRegionWidth()/2/scale.x);
				obj = new WheelObstacle(rock.x,rock.y,rockDay.getRegionWidth()/4/scale.x);
				obj.setBodyType(BodyDef.BodyType.StaticBody);
				obj.setSensor(true);
				obj.setDrawScale(scale);
				if(staticNDS == 0){
					obj.setTexture(rockDay);
					obj.setOverlay(rockSunset, levelAlpha);
				}
				else if(staticNDS == 1){
					obj.setTexture(rockSunset);
					obj.setOverlay(rockNight, levelAlpha);
				}
				else{
					//AFTER LEVEL 15
					obj.setTexture(rockNight);
				}

				obj.setName("rock");
				rocks.add(obj);
				addObject(obj);
			}
		}	

		if (level.lanterns != null) {
			for (Vector2 lotus : level.lanterns) {
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
				lantern.setC2(levelAlpha);
				addObject(lantern);
				tethers.add(lantern);
				lanterns.add(lantern);
			}
		}

		if (level.lotuses != null) {
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
				lantern.setC2(levelAlpha);
				addObject(lantern);
				tethers.add(lantern);
				lanterns.add(lantern);
			}
		}

		//		System.out.println(levelAlpha(this.level).a);
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
			lily.setC2(levelAlpha);
			addObject(lily);
			tethers.add(lily);
		}


		//tethers.add(wp);

		//Setup checkpoint and collision controller
		collisionController = new CollisionController(koi);
		checkpoint0 = getClosestTetherTo(koi.initPos);
		checkpoint = checkpoint0;

		//START KOI CODE
		//koi on tether
		boolean onTether = false;
		if(onTether){
			koi.initPos = checkpoint.getPosition().add(koi.NE.cpy().rotate90(1).nor().scl(TetherModel.TETHER_DEFAULT_ORBIT));
			koi.setPosition(koi.initPos);
			koi.setAttemptingTether(true);
			koi.setTethered(true);
			collisionController.initStart(checkpoint0);
			cacheVel = koi.NE;
			koi.setLinearVelocity(cacheVel);
			koi.resolveDirection();
			koi.setLinearVelocity(Vector2.Zero);
		}
		//koi approaching tether
		else{
			koi.setTethered(false);
			koi.setAttemptingTether(true);
			Vector2 initVel = checkpoint0.getPosition().cpy().sub(koi.initPos.cpy()).nor();
			Vector2 initTan = checkpoint0.getPosition().add(initVel.cpy().rotate90(1).nor().scl(TetherModel.TETHER_DEFAULT_ORBIT));
			cacheVel = initTan.cpy().sub(koi.initPos.cpy()).nor();
			koi.setLinearVelocity(cacheVel);
			koi.resolveDirection();
			koi.setLinearVelocity(Vector2.Zero);
		}

		levelCamWidth = Math.abs(level.map.get(0).x - level.map.get(1).x);
		levelCamHeight = Math.abs(level.map.get(0).y - level.map.get(1).y);
		center = new Vector2((level.map.get(0).x + level.map.get(1).x)/2,
				(level.map.get(0).y + level.map.get(1).y)/2);
		cameraController.zoomStart(levelCamWidth, levelCamHeight, center, checkpoint0.getPosition().cpy().scl(scale));

		//TUTORIAL CODE
		HUD = new HUDitems(lanterns.size(), UILotusTexture, energyBarTexture, secondFont);
		if (this.level == 1){
			/*HUD.setTutorialTexture(tutorial1);
			HUD.setHelpTexture(helpTexture);
			HUD.setTutorialStatus(true);*/
			TutorialItems t1 = new TutorialItems(20, 2, tutorial1.getRegionHeight(), tutorial1.getRegionWidth());
			t1.setBodyType(BodyDef.BodyType.StaticBody);
			t1.setName("tutorial"+ 1);
			t1.setDensity(TETHER_DENSITY);
			t1.setFriction(TETHER_FRICTION);
			t1.setRestitution(TETHER_RESTITUTION);
			t1.setSensor(sensorTethers);
			t1.setDrawScale(scale);
			t1.setTexture(tutorial1);
			objects.add(t1);
		}
		else if (this.level == 2){
			/*HUD.setTutorialTexture(tutorial2);
			HUD.setHelpTexture(helpTexture);
			HUD.setTutorialStatus(true);*/
			TutorialItems t2 = new TutorialItems(18, 15, tutorial2.getRegionHeight(), tutorial1.getRegionWidth());
			t2.setBodyType(BodyDef.BodyType.StaticBody);
			t2.setName("tutorial"+ 1);
			t2.setDensity(TETHER_DENSITY);
			t2.setFriction(TETHER_FRICTION);
			t2.setRestitution(TETHER_RESTITUTION);
			t2.setSensor(sensorTethers);
			t2.setDrawScale(scale);
			t2.setTexture(tutorial2);
			objects.add(t2);
		}
		else if (this.level == 6){
			/*HUD.setTutorialTexture(tutorial3);
			HUD.setHelpTexture(helpTexture);
			HUD.setTutorialStatus(true);*/
			TutorialItems t3 = new TutorialItems(-13, 18, tutorial3.getRegionHeight(), tutorial1.getRegionWidth());
			t3.setBodyType(BodyDef.BodyType.StaticBody);
			t3.setName("tutorial"+ 1);
			t3.setDensity(TETHER_DENSITY);
			t3.setFriction(TETHER_FRICTION);
			t3.setRestitution(TETHER_RESTITUTION);
			t3.setSensor(sensorTethers);
			t3.setDrawScale(scale);
			t3.setTexture(tutorial3);
			objects.add(t3);
		}
		else if (this.level == 4){
			/*HUD.setTutorialTexture(tutorial4);
			HUD.setHelpTexture(helpTexture);
			HUD.setTutorialStatus(true);*/
			TutorialItems t4 = new TutorialItems(10, 3, tutorial4.getRegionHeight(), tutorial1.getRegionWidth());
			t4.setBodyType(BodyDef.BodyType.StaticBody);
			t4.setName("tutorial"+ 1);
			t4.setDensity(TETHER_DENSITY);
			t4.setFriction(TETHER_FRICTION);
			t4.setRestitution(TETHER_RESTITUTION);
			t4.setSensor(sensorTethers);
			t4.setDrawScale(scale);
			t4.setTexture(tutorial4);
			objects.add(t4);
		}
		else if (this.level == 7){
			/*HUD.setTutorialTexture(tutorial4);
			HUD.setHelpTexture(helpTexture);
			HUD.setTutorialStatus(true);*/
			TutorialItems t6 = new TutorialItems(11, 3, tutorial4.getRegionHeight(), tutorial1.getRegionWidth());
			t6.setBodyType(BodyDef.BodyType.StaticBody);
			t6.setName("tutorial"+ 1);
			t6.setDensity(TETHER_DENSITY);
			t6.setFriction(TETHER_FRICTION);
			t6.setRestitution(TETHER_RESTITUTION);
			t6.setSensor(sensorTethers);
			t6.setDrawScale(scale);
			t6.setTexture(tutorial6);
			objects.add(t6);
		}
		else if(this.level == 14){
			/*HUD.setTutorialTexture(tutorial6);
			HUD.setHelpTexture(helpTexture);
			HUD.setTutorialStatus(true);*/
			TutorialItems t5 = new TutorialItems(-25, 0, tutorial5.getRegionHeight(), tutorial1.getRegionWidth());
			t5.setBodyType(BodyDef.BodyType.StaticBody);
			t5.setName("tutorial"+ 1);
			t5.setDensity(TETHER_DENSITY);
			t5.setFriction(TETHER_FRICTION);
			t5.setRestitution(TETHER_RESTITUTION);
			t5.setSensor(sensorTethers);
			t5.setDrawScale(scale);
			t5.setTexture(tutorial5);
			objects.add(t5);
		}
		/*else if(this.level == 7){
			HUD.setTutorialTexture(tutorial5);
			HUD.setHelpTexture(helpTexture);
			HUD.setTutorialStatus(true);
		}*/
		addHUD(HUD);



	}


	// Respawns fish once it collides with a lethal object. 
	// The player is transported to the last checkpoint or initial start state if no lotuses have been lit
	private void respawn(){
		if(respawnTimer <= 0){
			collisionController.clear();
			collisionController.initStart(checkpoint);
			koi.setPosition(checkpoint.getPosition().add(koi.NE.cpy().rotate90(1).nor().scl(TetherModel.TETHER_DEFAULT_ORBIT)));
			koi.setAttemptingTether(true);
			koi.setTethered(true);
			cacheAttempt = checkpoint;
			koi.setLinearVelocity(koi.NE);
			koi.setDead(false);
			koi.setWhirled(false);
			respawnTimer = RESPAWN_TIME;
			//cameraController.zoomStart(levelCamWidth, levelCamHeight, center, koi.getPosition().cpy().scl(scale));
			return;
		} 
		else if(respawnTimer <= RESPAWN_TIME/2 && !collisionController.didWin()){
			cameraController.moveCameraTowards(checkpoint.getPosition().scl(scale));
			cameraController.resetCameraVelocity(); 
		}
		else if(respawnTimer == RESPAWN_TIME){
			koi.setAttemptingTether(false);
			koi.setWhirled(false);
			koi.setTethered(false);
			koi.bursting = false;
			koi.setLinearVelocity(Vector2.Zero);
			collisionController.clear();
		}
		respawnTimer--;
	}

	private void level8Fish(EnemyModel e){
		e.setPosition(enemyPos8);
		e.patrol(enemyPath8);

	}

	public void debugPrint(){
		System.out.println();
		System.out.println("BEGIN DEBUG");
		System.out.println("POSITION: "+koi.getPosition().cpy());
		System.out.println("VELOCITY: "+koi.getLinearVelocity().cpy());
		System.out.println("FORCE: "+koi.getForce().cpy());
		System.out.println("Pull: "+koi.pull.cpy());
		System.out.println("Cent: "+koi.cent.cpy());
		System.out.println("Dest: "+koi.dest.cpy());
		System.out.println("IS TETHERED: "+koi.isTethered());
		System.out.println("IS ATTEMPTING TETHER: "+koi.isAttemptingTether());
		System.out.println("END DEBUG");
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
		koi.getLinearVelocity();
		if(debug){
			debugPrint();
		}
		InputController input = InputController.getInstance();
		if (input.tutorial){
			HUD.setTutorialStatus(false);
			input.setHelpPressed(false);
		}

		if (input.helpPressed()){
			HUD.setTutorialStatus(true);
		}
		if (collisionController.didWin()) {
			listener.exitScreen(this, WorldController.EXIT_WIN);
			cameraController.setWon(true);
			
			setComplete(true);
			tillNextLevel++;
			if (tillNextLevel > 210){
				deleteAll();
				this.level = this.level + 1;
				populateLevel();
				listener.exitScreen(this, WorldController.EXIT_WIN_DONE);
			}
		}
		if(input.didAdvance()){
			deleteAll();
			this.level = this.level + 1;
			populateLevel();
		}
		else if(input.didRetreat() && this.level > 1){
			deleteAll();
			this.level = this.level - 1;
			populateLevel();
		}

		if (koi.isDead()) {
			failSound.setVolume(.3f);
			failSound.play();
			koi.die();
			koi.setLinearVelocity(Vector2.Zero);
			koi.setTethered(false);
			for (TetherModel t : tethers) {
				t.setTethered(false);
			}
			if (this.level == 12){
					level8Fish(enemies.get(0));
			}
			respawn();
		} else {
			// ZOOM IN TO PLAYER AT START OF LEVEL
			if (!cameraController.isZoomedToPlayer()) {
				cameraController.zoomToPlayer();
				return;
			}
			if(!started){
				System.out.println("STARTED");
				if(collisionController.inRangeOf(checkpoint0)){
					started = true;
					koi.setAttemptingTether(true);
					koi.arrowOn = true;
				}
			}
			koi.setLinearVelocity(cacheVel);


			// CHECKPOINT CODE
			checkpoint = checkpoint0;
			for (TetherModel t : lanterns) {
				if (t.lit) {
					if (!litlanterns.contains(t)) {
						lightingSound.setVolume(.5f);
						lightingSound.play();
						litlanterns.push(t);
					}
				} else {
					litlanterns.remove(t);
				}
			}
			if (litlanterns.size() > 0) {
				checkpoint = litlanterns.peek();
			}

			// CLEAR SHADOW CODE
			clearShadows(lanterns.size() == litlanterns.size());
			moveShadows();

			// ENEMY PATROL CODE
			for (EnemyModel enemy : enemies) {
				enemy.patrol();
				enemy.moveTowardsGoal();
				enemy.fleeFind();
				enemy.fleeFind(lanterns);
				if (enemy.dead) {
					enemy.deactivatePhysics(world);
				}
			}

			closestTether = getClosestTetherTo(koi.getPosition());
			// INPUT CODE
			if (input.didTether() && !isWhirled() && !koi.bursting) {
				if ((koi.isTethered() || koi.isAttemptingTether())) {
					koi.setTethered(false);
					koi.setAttemptingTether(false);
					cameraController.resetCameraVelocity();
				} else {
					if (collisionController.inRange()) {
						koi.setAttemptingTether(true);
						cameraController.resetCameraVelocity();
					}
				}
			} else if (input.didKill()) {
				koi.setDead(true);
				return;
			} else if (input.didFaster()) {
				speed += .5f;
				speed = Math.min(speed, MAX_SPEED);
			} else if (input.didSlower()) {
				speed -= .5f;
				speed = Math.max(speed, MIN_SPEED);
			}

			cameraController.scaleSpeed(speed);
			koi.scaleSpeed(speed);

			// KOI SPEED CODE
			koi.setLinearVelocity(koi.getLinearVelocity().setLength(PLAYER_LINEAR_VELOCITY * (isTethered() ? 1.3f : 2.5f) * speed));

			// LOTUS LIGHTING CODE
			closestTether.setTethered(
					isTethered() && closestTether.isLotus() && collisionController.inRangeOf(closestTether));


			//TETHER IN PATH
			for (TetherModel tether : tethers) {
				tether.inpath = false;
				if (koi.willIntersectTether(tether.getPosition(), TetherModel.TETHER_DEFAULT_RANGE * 1.3f)) {
					tether.inpath = true;
				}
			}

			// TETHER FORCE CODE
			Vector2 closeTeth = getClosestTether().getPosition();
			Vector2 initTeth = koi.getInitialTangentPoint(closeTeth);

			if (closeTeth.dst(koi.getPosition()) > TetherModel.TETHER_DEFAULT_RANGE * 1.3) {
				koi.setAttemptingTether(false);
				koi.setTethered(false);
			}
			if(koi.isAttemptingTether()){

				// HIT TANGENT
				if (koi.getPosition().sub(initTeth).len2() < .05) {
					koi.setTethered(true);
					koi.setAttemptingTether(false);
					koi.refreshTetherForce(closeTeth, closestTether.getOrbitRadius());
				}
				// PAST TANGENT
				else if (!koi.willIntersect(initTeth) && koi.pastTangent(initTeth)){
						koi.passAdjust(closeTeth);
				} 
				else {

				}
			}
			koi.applyTetherForce(closeTeth, closestTether.getOrbitRadius());


			//WHIRLPOOL 2
			if (wps.size() >= 1){
				for (WModel2 w : wps){
					if (w.shouldTether(koi)){
						koi.free();
						koi.setWhirled(true);
						w.circulate(koi);
					}
					else{
						koi.setWhirled(false);
						w.nullK();
					}
				}
			}
			koi.resolveDirection();

			// CAMERA ZOOM CODE
			if (isTethered()) {
				cameraController.moveCameraTowards(closestTether.getPosition().cpy().scl(scale));
				cameraController.zoomOut();
			} 
			else {
				cameraController.moveCameraTowards(koi.getPosition().cpy().scl(scale));
				cameraController.zoomIn();
			}

			// burst code
			koi.updateRestore();
			if (!isWhirled() && input.fast) {
				koi.burst();
				cameraController.moveCameraTowards(koi.getPosition().cpy().scl(scale));
			}

			// ANIMATION CODE
			stateTime += Gdx.graphics.getDeltaTime(); // #15
			lilycurrentFrame = lilyAnimation.getKeyFrame(stateTime, true);
			closedFlowercurrentFrame = closedFlowerAnimation.getKeyFrame(stateTime, true);
			openFlowercurrentFrame = openFlowerAnimation.getKeyFrame(stateTime, true);
			koiScurrentFrame = koiSAnimation.getKeyFrame(stateTime, true);
			koiCcurrentFrame = koiCAnimation.getKeyFrame(stateTime, true);
			KoiCcurrentFrameFlipped = koiCAnimationFlipped.getKeyFrame(stateTime, true);
			goalCurrentFrame = goalAnimation.getKeyFrame(stateTime, true);
			enemyCurrentFrame = enemyAnimation.getKeyFrame(stateTime, true);
			openFlowercurrentFrame = openFlowerAnimation.getKeyFrame(stateTime, true);
			closedFlowercurrentFrame = closedFlowerAnimation.getKeyFrame(stateTime, true);


			for(int i = 0; i < enemies.size(); i++){
				enemies.get(i).setTexture(enemyCurrentFrame);
			}
			/*
			lilycurrentFrame2 = lilyAnimation2.getKeyFrame(stateTime, true);
			closedFlowercurrentFrame2 = closedFlowerAnimation2.getKeyFrame(stateTime, true);
			openFlowercurrentFrame2 = openFlowerAnimation2.getKeyFrame(stateTime, true);*/


			if (isWhirled() || isTethered()) {
				koi.setCurved(true);
				if(isWhirled()){
					if(koi.left(closestWhirlpool)){
						koi.setTexture(koiCcurrentFrame);
					}
					else{
						koi.setTexture(KoiCcurrentFrameFlipped);
					}
				}
				else if(isTethered()){
					if (koi.left(closestTether)) {
						koi.setTexture(koiCcurrentFrame);
					} else {
						koi.setTexture(KoiCcurrentFrameFlipped);
					}
				}

			} 

			else {
				koi.setCurved(false);
				koi.setTexture(koiScurrentFrame);
			}
			// koi.setTexture(koiCcurrentFrame);

			// FSM to handle Lotus
			goalTile.setTexture(goalCurrentFrame);

			for (int i = 0; i < tethers.size(); i++) {
				if (collisionController.inRangeOf(tethers.get(i)) && tethers.get(i) == closestTether) {
					tethers.get(i).inrange = true;
				} else {
					tethers.get(i).inrange = false;
				}

				if (tethers.get(i).getTetherType() == TetherType.Lilypad) {
					tethers.get(i).setTexture(lilycurrentFrame);
					tethers.get(i).setOverlay(lilycurrentFrame2);
				}
				if (tethers.get(i).getTetherType() == TetherType.Lantern) {
					// System.out.println("here");
					if (tethers.get(i).getOpening() == 0) {
						tethers.get(i).setTexture(closedFlowercurrentFrame);
						tethers.get(i).setOverlay(closedFlowercurrentFrame2);
						if (tethers.get(i).set) {
							tethers.get(i).setOpening(1);
						}
					}
					if (tethers.get(i).getOpening() == 1) {

						if (!openingFlowerAnimation.isAnimationFinished(relativeTime)) {
							openingFlowercurrentFrame = openingFlowerAnimation.getKeyFrame(relativeTime, true);
							openingFlowercurrentFrame2 = openingFlowerAnimation2.getKeyFrame(relativeTime, true);
							relativeTime += Gdx.graphics.getDeltaTime();
							tethers.get(i).setTexture(openingFlowercurrentFrame);
							tethers.get(i).setOverlay(openingFlowercurrentFrame2);
							if (!tethers.get(i).set) {
								// go to closing
								// relativeTime = 0;
								tethers.get(i).setOpening(3);
							}
						}
						if (openingFlowerAnimation.isAnimationFinished(relativeTime)) {
							// System.out.println("finished");
							tethers.get(i).setOpening(2);
							relativeTime = 0;
						}

					}

				}
				if (tethers.get(i).getOpening() == 2) {
					tethers.get(i).setTexture(openFlowercurrentFrame);
					tethers.get(i).setOverlay(openFlowercurrentFrame2);

				}
				if (tethers.get(i).getOpening() == 3) {
					if (!tethers.get(i).set) {
						if (!closingFlowerAnimation.isAnimationFinished(relativeTime)) {
							closingFlowercurrentFrame = closingFlowerAnimation.getKeyFrame(relativeTime, true);
							closingFlowerCurrentFrame2 = closingFlowerAnimation2.getKeyFrame(relativeTime, true);
							relativeTime += Gdx.graphics.getDeltaTime();
							tethers.get(i).setTexture(closingFlowercurrentFrame);
							tethers.get(i).setOverlay(closingFlowerCurrentFrame2);
						}
						if (closingFlowerAnimation.isAnimationFinished(relativeTime)) {
							tethers.get(i).setOpening(0);
							relativeTime = 0;
						}
					}
					if (tethers.get(i).set) {
						tethers.get(i).setOpening(1);
					}
				}
				if (tethers.get(i).lit) {
					tethers.get(i).setTexture(openFlowercurrentFrame);
					tethers.get(i).setOverlay(openFlowercurrentFrame2);
				}
			}
		}
		HUD.updateHUD(litlanterns.size(), koi.getEnergy());
		cacheVel = koi.getLinearVelocity();
	}


	private void clearShadows(boolean b) {
		for (ShadowModel s : shadows){
			s.clearShadow(b);;
		}	
	}

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
		return whirlpoolsOn && koi.isWhirled();
	}

	private boolean isExitingWhirlpool(){
		return whirlpoolsOn && koi.isExitingWhirlpool();
	}

	private WhirlpoolModel getClosestWhirlpool() {
		if(collisionController.inRangePool()){
			return collisionController.getClosestWhirlpoolInRange();
		}
		WhirlpoolModel closestPool = wpools.get(0);
		float closestDistance = wpools.get(0).getPosition().sub(koi.getPosition()).len2();
		for (WhirlpoolModel wp : wpools) {
			float newDistance = wp.getPosition().sub(koi.getPosition()).len2();
			if (newDistance < closestDistance) {
				closestDistance = newDistance;
				closestPool = wp;
			}
		}
		return closestPool;
	}

	private WhirlpoolModel getClosestWhirlpoolTo(Vector2 v){
		WhirlpoolModel closestWhirlpool = wpools.get(0);
		float closestDistance = wpools.get(0).getPosition().sub(v).len();
		for (WhirlpoolModel pool: wpools){
			float newDistance = pool.getPosition().sub(v).len();
			if (newDistance < closestDistance){
				closestDistance = newDistance;
				closestWhirlpool = pool;
			}
		}
		return closestWhirlpool;
	}

	public void draw(float delta) {

		if (paused){
			cameraController.zoomStart(levelCamWidth, levelCamHeight, center, koi.getPosition().cpy().scl(scale));
			super.draw(delta);
			pauseMenu.draw();
		}
		else {
			super.draw(delta);
			//for (ArrayList<Float> wall : walls) canvas.drawPath(wall);
			canvas.beginHUD();
			HUD.draw(canvas);
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
		if(input.didPause()){
			paused = !paused;
		}

		if (active) {
			if (preUpdate(delta) && !paused) {
				update(delta); // This is the one that must be defined.
				postUpdate(delta);
			}
			this.draw(delta);
			
			if (goBack() && listener != null) {
				clickSound.play();
				listener.exitScreen(this, WorldController.EXIT_MAIN);
			}
			if (restartLevel() && listener != null) {
				clickSound.play();
				listener.exitScreen(this, this.level);
			}
			if (resumePlay() && listener != null) {
				clickSound.play();
				resumeState = 0;
				restartState = 0;
				backState = 0;
				paused = false;
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
		clickSound.dispose();
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
		if (paused) {

			// Flip to match graphics coordinates
			screenY = canvas.getHeight() - screenY;
			float dx = Math.abs(screenX - PauseMenuMode.backPos.x);
			float dy = Math.abs(screenY - PauseMenuMode.backPos.y);

			if (dx < pauseMenu.scale * pauseMenu.back.getWidth() / 2
					&& dy < pauseMenu.scale * pauseMenu.back.getHeight() / 2) {
				pauseMenu.setBackHover(true);
			}
			else{
				pauseMenu.setBackHover(false);
			}
			
			dx = Math.abs(screenX - PauseMenuMode.resumePos.x);
			dy = Math.abs(screenY - PauseMenuMode.resumePos.y);

			if (dx < pauseMenu.scale * pauseMenu.resume.getWidth() / 2
					&& dy < pauseMenu.scale * pauseMenu.resume.getHeight() / 2) {
				pauseMenu.setResumeHover(true);
			}
			else{
				pauseMenu.setResumeHover(false);
			}

			dx = Math.abs(screenX - PauseMenuMode.restartPos.x);
			dy = Math.abs(screenY - PauseMenuMode.restartPos.y);

			if (dx < pauseMenu.scale * pauseMenu.restart.getWidth() / 2
					&& dy < pauseMenu.scale * pauseMenu.restart.getHeight() / 2) {
				pauseMenu.setRestartHover(true);
			}
			else{
				pauseMenu.setRestartHover(false);
			}

		}
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}


}