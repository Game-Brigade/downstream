package edu.cornell.gdiac.downstream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import edu.cornell.gdiac.downstream.InputController.SelectionType;
import edu.cornell.gdiac.downstream.WorldController.AssetState;
import edu.cornell.gdiac.downstream.models.EnemyModel;
import edu.cornell.gdiac.downstream.models.PlayerModel;
import edu.cornell.gdiac.downstream.models.RockModel;
import edu.cornell.gdiac.downstream.models.TetherModel;
import edu.cornell.gdiac.downstream.models.WhirlpoolModel;
import edu.cornell.gdiac.downstream.obstacle.PolygonObstacle;
import edu.cornell.gdiac.util.SoundController;

public class LevelEditor extends WorldController {

	//	/** Reference to the fish texture */
	//	private static final String KOI_TEXTURE = "koi/koi.png";
	//	/** The reference for the tether textures  */
	//	private static final String LILY_TEXTURE = "terrain/lilypad1_scaled.png";
	//	/** Reference to the enemy image assets */
	//	private static final String ENEMY_TEXTURE = "enemy/enemy.png";
	//	/** Reference to the Lantern asset image*/
	//	private static final String LANTERN_TEXTURE = "tethers/notlit.png";
	//	/** Reference to the Lighting Texture image */
	//	private static final String LIGHTING_TEXTURE = "tethers/aura.png";
	//	/** Reference to the land texture */
	//	private static final String EARTH_FILE = "terrain/swirl_grass.png";
	//	/** Reference to the whirlpool texture */
	//	private static final String WHIRLPOOL_TEXTURE = "terrain/whirlpool.png";
	//	/** Reference to the flipped whirlpool texture */
	//	private static final String WHIRLPOOL_FLIP_TEXTURE = "terrain/whirlpool_flip.png";
	//
	//	/** Texture assets for the koi */
	//	private TextureRegion koiTexture;
	//	/** Texture assets for the lilypads */
	//	private TextureRegion lilyTexture;
	//	/** Texture assets for the enemy fish */
	//	private TextureRegion enemyTexture;
	//	/** Texture assets for lantern */
	//	private TextureRegion lanternTexture;
	//	/** Texture assets for light */
	//	private TextureRegion lightingTexture;
	//	/** Texture assets for the land */
	//	private TextureRegion earthTile;
	//	/** Texture assets for the whirlpools */
	//	private TextureRegion whirlpoolTexture;
	//	private TextureRegion whirlpoolFlipTexture;



	/** Track asset loading from all instances and subclasses */
	private AssetState fishAssetState = AssetState.EMPTY;

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

		manager.load(KOI_TEXTURE, Texture.class);
		assets.add(KOI_TEXTURE);

		manager.load(LILY_TEXTURE, Texture.class);
		assets.add(LILY_TEXTURE);

		manager.load(LANTERN_TEXTURE, Texture.class);
		assets.add(LANTERN_TEXTURE);

		manager.load(LIGHTING_TEXTURE, Texture.class);
		assets.add(LIGHTING_TEXTURE);

		manager.load(EARTH_FILE,Texture.class);
		assets.add(EARTH_FILE);

		manager.load(WHIRLPOOL_TEXTURE, Texture.class);
		assets.add(WHIRLPOOL_TEXTURE);

		manager.load(WHIRLPOOL_FLIP_TEXTURE, Texture.class);
		assets.add(WHIRLPOOL_FLIP_TEXTURE);

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
		lilyTexture = createTexture(manager,LILY_TEXTURE,false);
		lanternTexture = createTexture(manager, LANTERN_TEXTURE, false);
		lightingTexture = createTexture(manager, LIGHTING_TEXTURE, false);
		earthTile = createTexture(manager,EARTH_FILE,true);
		whirlpoolTexture = createTexture(manager, WHIRLPOOL_TEXTURE, false);
		whirlpoolFlipTexture = createTexture(manager, WHIRLPOOL_FLIP_TEXTURE, false);

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

	private static final float TETHER_DENSITY = ENEMY_DENSITY;
	private static final float TETHER_FRICTION = ENEMY_FRICTION;
	private static final float TETHER_RESTITUTION = BASIC_RESTITUTION;

	private ArrayList<Vector2> lilypads;
	private ArrayList<Vector2> lanterns;
	private ArrayList<Vector3> whirlpools;
	private ArrayList<Vector2> rocks;
	private HashMap<String,ArrayList<Vector2>> enemies;
	private Vector2 player;
	private ArrayList<ArrayList<Vector2>> walls;
	private ArrayList<ArrayList<Vector2>> shores;
	private ArrayList<Vector2> mapArea;
	private ArrayList<Vector2> goal;
	private PlayerModel koi;

	private boolean settingEnemyPath = false;
	private boolean settingWallPath = false;
	private boolean settingShorePath = false;
	private boolean settingWhirlDir = false;
	private boolean didEnter = false;
	private Vector2 currentEnemy;
	private ArrayList<Vector2> enemyPath;
	private ArrayList<Vector2> wallPath;
	private ArrayList<Vector2> shorePath;
	private boolean newClick;
	private Vector2 currentClick;

	private static String filename = null;
	private boolean buildingLevel = false;

	private CameraController cameraController;

	public LevelEditor() {
		setDebug(false);
		setComplete(false);
		setFailure(false);
	}

	private void populateLevel() {
		cameraController = new CameraController(canvas.getCamera());
		goal = new ArrayList<Vector2>();
		lilypads = new ArrayList<Vector2>();
		lanterns = new ArrayList<Vector2>();
		enemies = new HashMap<String,ArrayList<Vector2>>();
		enemyPath = new ArrayList<Vector2>();
		walls = new ArrayList<ArrayList<Vector2>>();
		shores = new ArrayList<ArrayList<Vector2>>();
		whirlpools = new ArrayList<Vector3>();
		newClick = false;
		currentClick = new Vector2(0,0);
		mapArea = new ArrayList<Vector2>();
		rocks = new ArrayList<Vector2>();

		boolean loadFile = true;
		buildingLevel = loadFile;
		if (loadFile) {
			loadPartialLevel();
		}
		buildingLevel = false;

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		//		saveToJson();

		populateLevel();
	}

	@Override
	public void update(float dt) {

		InputController input = InputController.getInstance();

		didEnter = input.didEnter() || didEnter;

		if (input.didEnter() && settingEnemyPath) addEnemy(null,true);
		if (input.didEnter() && settingWallPath) addWall(null,true);
		if (input.didEnter() && settingShorePath) addShore(null,true);

		if (input.isZoomIn()) 		cameraController.zoomInBoundless();
		else if (input.isZoomOut()) cameraController.zoomOutBoundless();
		cameraController.handleArrowKeys(input.getUp(), input.getDown(), input.getLeft(), input.getRight());

		handleClick: if (input.getClick() != null) {
			if (input.getSelection() == null) break handleClick;	
			updateClicks();
			switch (input.getSelection()) {
			case Lilypad: 
				addLilypad(currentClick);
				return;
			case Lantern:
				addLantern(currentClick);
				return;
			case Enemy:
				addEnemy(currentClick,didEnter);
				return;
			case Player:
				addPlayer(currentClick);
				return;
			case Wall:
				addWall(currentClick,didEnter);
				return;
			case Shore:
//				System.out.println(didEnter);
				addShore(currentClick,didEnter);
				return;
			case MapArea:
				addMapArea(currentClick);
				return;
			case Goal:
				addGoal(currentClick);
				return;
			case Rock:
				addRock(currentClick);
				return;
			case Whirlpool:
				addWhirlpool(currentClick, didEnter);
				return;
			case Save:
				this.saveToJson();
				return;
			}
		}
		//		System.out.println("please");
		// we can create walls by holding and dragging
		if (input.leftClickHeldDown() && 
				input.getSelection() != null && 
				input.getSelection() == SelectionType.Wall) {
			updateClicks();
			addWall(currentClick,didEnter);
		} else if (input.leftClickHeldDown() &&
				input.getSelection() != null &&
				input.getSelection() == SelectionType.Shore) {
			updateClicks();
			addShore(currentClick,didEnter);
		}
	}

	private void addLilypad(Vector2 click) {
		lilypads.add(click.cpy());
		float rad = lilyTexture.getRegionWidth()/scale.x/2;
		TetherModel lily = new TetherModel(click.x, click.y, rad);
		lily.setBodyType(BodyDef.BodyType.StaticBody);
		lily.setName("lily"+ 1);
		lily.setDensity(TETHER_DENSITY);
		lily.setFriction(TETHER_FRICTION);
		lily.setRestitution(TETHER_RESTITUTION);
		lily.setlightingTexture(lightingTexture);
		lily.setSensor(false);
		lily.setDrawScale(scale);
		lily.setTexture(lilyTexture);
		addObject(lily);
	}

	private void addRock(Vector2 click) {
		rocks.add(click.cpy());
		float rad = lilyTexture.getRegionWidth()/scale.x/2;
		RockModel rock = new RockModel(click.x, click.y, rad);
		rock.setBodyType(BodyDef.BodyType.StaticBody);
		rock.setName("lily"+ 1);
		rock.setDensity(TETHER_DENSITY);
		rock.setFriction(TETHER_FRICTION);
		rock.setRestitution(TETHER_RESTITUTION);
		rock.setSensor(false);
		rock.setDrawScale(scale);
		rock.setTexture(lilyTexture);
		addObject(rock);
	}


	private void addWhirlpool(Vector2 click, boolean enter){
		
		if(enter){
			if(currentWhirlpool.size() != 2){
				didEnter = false;
			}
			else{
				placingWhirlpool = false;
				didEnter = false;
				Vector2 v = currentWhirlpool.get(0);
				Vector2 v2 = currentWhirlpool.get(1);
				whirlpools.add(new Vector4(v.x, v.y, v2.x, v2.y));
				currentWhirlpool.clear();
			}
		}
		else if(currentWhirlpool.size() == 2){
			currentWhirlpool.clear();
			currentWhirlpool.add(click.cpy());
			placingWhirlpool = false;
		}
		else{
			currentWhirlpool.add(click.cpy());
			placingWhirlpool = true;
		}
		
	}


	private void addLantern(Vector2 click) {
		lanterns.add(click.cpy());
		float rad = lilyTexture.getRegionWidth()/scale.x/2;
		TetherModel lantern = new TetherModel(click.x, click.y, rad, true);
		lantern.setBodyType(BodyDef.BodyType.StaticBody);
		lantern.setName("lantern"+ 1);
		lantern.setDensity(TETHER_DENSITY);
		lantern.setFriction(TETHER_FRICTION);
		lantern.setRestitution(TETHER_RESTITUTION);
		lantern.setSensor(false);
		lantern.setDrawScale(scale);
		lantern.setTexture(lanternTexture);
		lantern.setlightingTexture(lightingTexture);
		lantern.setRotation(0);
		addObject(lantern);
	}

	private void addEnemy(Vector2 click, boolean enter) {
		if (enter) {
			settingEnemyPath = false;
			didEnter = false;
			enemies.put(currentEnemy.toString(), enemyPath);
			return;
		}
		if (settingEnemyPath) {
			enemyPath.add(click.cpy().scl(scale));
			return;
		}
		settingEnemyPath = true;
		currentEnemy = click.cpy();
		enemyPath = new ArrayList<Vector2>();
		enemyPath.add(click.cpy().scl(scale));
		TextureRegion etexture = enemyTexture;
		float dwidth  = etexture.getRegionWidth()/scale.x;
		float dheight = etexture.getRegionHeight()/scale.y;
		EnemyModel eFish = new EnemyModel(click.x, click.y, dwidth, dheight);
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
	}

	private void addPlayer(Vector2 click) {
		player = click.cpy();
		if (koi != null) removeObject(koi);
		float dwidth  = koiTexture.getRegionWidth()/scale.x;
		float dheight = koiTexture.getRegionHeight()/scale.y;
		koi = new PlayerModel(click.x, click.y, dwidth, dheight);
		koi.setDrawScale(scale);
		koi.setName("koi");
		koi.setTexture(enemyTexture);
		koi.setTethered(false);
		addObject(koi);
	}

	private void addWall(Vector2 click, boolean enter) {
		if (enter) {
			settingWallPath = false;
			didEnter = false;
			walls.add(wallPath);
			PolygonObstacle obj;
			ArrayList<Float> wall = new ArrayList<Float>();
			for (Vector2 v : wallPath) {
				wall.add(v.x/scale.x);
				wall.add(v.y/scale.y);
			}
			float[] wallFloat = new float[wall.size()];
			for (int i = 0; i < wall.size(); i++) wallFloat[i] = wall.get(i);
			if (wallFloat.length == 0) return;
			obj = new PolygonObstacle(wallFloat, 0, 0);
			obj.setBodyType(BodyDef.BodyType.StaticBody);
			obj.setDensity(BASIC_DENSITY);
			obj.setFriction(BASIC_FRICTION);
			obj.setRestitution(BASIC_RESTITUTION);
			obj.setDrawScale(scale);
			obj.setTexture(earthTileDay);
			obj.setName("wall1");
			addObject(obj);
			return;
		}
		//		System.out.println("Not building leveL: " + !buildingLevel);
		//		System.out.println("Not new click: " + !newClick);
		if (!newClick && !buildingLevel) return;
		if (settingWallPath) {
			if (wallPath.get(wallPath.size()-1) != click) wallPath.add(click.cpy().scl(scale));
			return;
		}
		settingWallPath = true;
		wallPath = new ArrayList<Vector2>();
		wallPath.add(click.cpy().scl(scale));
	}

	private void addMapArea(Vector2 click) {
		switch (mapArea.size()) {
		case 0:
		case 1:
			mapArea.add(click.cpy().scl(scale));
			return;
		case 2:
		default:
			mapArea.clear();
			mapArea.add(click.cpy().scl(scale));
			return;
		}
	}

	private void addGoal(Vector2 click) {
		switch (goal.size()) {
		case 0:
		case 1:
			goal.add(click.cpy());
			return;
		case 2:
		default:
			goal.clear();
			goal.add(click.cpy());
			return;
		}
	}

	private void addShore(Vector2 click, boolean enter) {
		if (enter) {
			settingShorePath = false;
			didEnter = false;
			shores.add(shorePath);
			PolygonObstacle obj;
			ArrayList<Float> shore = new ArrayList<Float>();
			for (Vector2 v : shorePath) {
				shore.add(v.x/scale.x);
				shore.add(v.y/scale.y);
			}
			float[] shoreFloat = new float[shore.size()];
			for (int i = 0; i < shore.size(); i++) shoreFloat[i] = shore.get(i);
			if (shoreFloat.length == 0) return;
			System.out.println(Arrays.toString(shoreFloat));
			obj = new PolygonObstacle(shoreFloat, 0, 0);
			obj.setBodyType(BodyDef.BodyType.StaticBody);
			obj.setDensity(BASIC_DENSITY);
			obj.setFriction(BASIC_FRICTION);
			obj.setRestitution(BASIC_RESTITUTION);
			obj.setDrawScale(scale);
			obj.setTexture(earthTile);
			obj.setName("shore");
			addObject(obj);
			return;
		}
		//		System.out.println("Not building leveL: " + !buildingLevel);
		//		System.out.println("Not new click: " + !newClick);
		if (!newClick && !buildingLevel) return;
		if (settingShorePath) {
			if (shorePath.get(shorePath.size()-1) != click) shorePath.add(click.cpy().scl(scale));
			return;
		}
		settingShorePath = true;
		shorePath = new ArrayList<Vector2>();
		shorePath.add(click.cpy().scl(scale));
	}

	private void drawPaths() {
		if (settingEnemyPath) drawPath(enemyPath);
		if (settingWallPath) drawPath(wallPath);
		if (settingShorePath) drawPath(shorePath);
		for (ArrayList<Vector2> path : enemies.values()) {
			drawPath(path);
		}
		for (ArrayList<Vector2> path : walls) {
			drawPath(path);
		}
		for (ArrayList<Vector2> path : shores) {
			drawPath(path);
		}
		if (mapArea.size() == 2) canvas.drawRectangle(mapArea.get(0), mapArea.get(1));
		if (goal.size() == 2) canvas.drawLeadingLine(goal.get(0).cpy().scl(scale), goal.get(1).cpy().scl(scale));
	}

	private void drawPath(ArrayList<Vector2> path) {
		for (int i = 0; i < path.size() - 1; i++) {
			canvas.drawLeadingLine(path.get(i), path.get(i+1));
		}
		canvas.drawLeadingLine(path.get(0), path.get(path.size()-1));
	}

	private boolean updateClicks() {
		Vector3 click3 = canvas.getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		Vector2 temp = new Vector2(click3.x/scale.x, click3.y/scale.y);
		if (temp.dst2(currentClick) > 0.5) {
			newClick = true;
			currentClick.x = temp.x; currentClick.y = temp.y;
			return true;
		}
		newClick = false;
		return false;
	}

	private void saveToJson() {
		int n = 1;
		Vector2 p = player;
		ArrayList<Vector2> g = goal;
		HashMap<String,ArrayList<Vector2>> e = enemies;
		ArrayList<Vector2> li = lilypads;
		ArrayList<Vector2> lo = lanterns;
		ArrayList<ArrayList<Vector2>> w = walls;
		ArrayList<ArrayList<Vector2>> s = shores;
		ArrayList<Vector3> wp = whirlpools;
		ArrayList<Vector2> m = mapArea;
		ArrayList<Vector2> r = rocks;
		Level level = new Level(n,p,g,e,li,lo,w, s,wp, r ,m);
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			System.setOut(new PrintStream(new FileOutputStream(filename)));
			System.out.println(gson.toJson(level));
		} catch (Exception e1) {
			// haha please no
			e1.printStackTrace();
		}
	}

	protected static Level loadFromJson() {
		//	    return defaultLevel();
		Gson gson = new Gson();
		try {
			if (filename == null) filename = getFileName();
			JsonReader reader = new JsonReader(new FileReader(filename));
			//	      JsonReader reader = new JsonReader(new InputStreamReader(LevelEditor.class.getResourceAsStream("/json/"+filename)));
			Level level = gson.fromJson(reader, Level.class);
			//	      System.out.println(level);
			return level;
		} catch (Exception e){
			System.out.println(e);
			//pls no
			return null;
		}
	}

	public static Level loadFromJson(int lvl) {
		//	    return defaultLevel();
		Gson gson = new Gson();
		try {
			filename = lvl + ".json";
			JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(filename)));
			Level level = gson.fromJson(reader, Level.class);
//			System.out.println(level.rocks);
			return level;
		} catch (Exception e) {
			System.out.println(e);
			return defaultLevel();
		}
	}

	private static Level defaultLevel() {
		int number = 1;
		Vector2 player = new Vector2(-4.537486f,16.284998f);
		ArrayList<Vector2> goal = new ArrayList<Vector2>();
		goal.add(new Vector2(47.862225f,-7.3327384f));goal.add(new Vector2(59.01847f,-21.969727f));
		HashMap<String,ArrayList<Vector2>> enemiesLevel = new HashMap<String,ArrayList<Vector2>>();
		ArrayList<Vector2> lilypads = new ArrayList<Vector2>();
		lilypads.add(new Vector2(-1.4039901f,19.247744f));lilypads.add(new Vector2(29.854492f,-1.2477448f));
		ArrayList<Vector2> lotuses = new ArrayList<Vector2>();
		lotuses.add(new Vector2(14.511503f,9.057251f));
		ArrayList<Vector3> wpools = new ArrayList<Vector3>();
		ArrayList<Vector2> map = new ArrayList<Vector2>();
		map.add(new Vector2(-719.5991f,1317.8994f));map.add(new Vector2(2249.889f,-604.0794f));
		ArrayList<ArrayList<Float>> walls = new ArrayList<ArrayList<Float>>();
		walls.add(new ArrayList<Float>(Arrays.asList(new Float[] {
				-18.874977f,
				33.954987f,
				58.15998f,
				33.489986f,
				57.617474f,
				-16.652487f,
				-19.882475f,
				-14.792486f,
				-19.107475f,
				28.14249f,
				-13.44998f,
				28.14249f,
				-12.597481f,
				-8.514992f,
				50.64248f,
				-9.90999f,
				51.804974f,
				28.064991f,
				-19.02998f,
				27.754993f})));
		LevelEditor le = new LevelEditor();
		ArrayList<ArrayList<Vector2>> w = new ArrayList<ArrayList<Vector2>>();
		ArrayList<Vector2> r = new ArrayList<Vector2>();
		ArrayList<ArrayList<Vector2>> s = new ArrayList<ArrayList<Vector2>>();
		Level defaultLevel =  le.new Level(number, player, goal, enemiesLevel, lilypads, lotuses, w, s, wpools, r, map);
		defaultLevel.walls = walls;
		return defaultLevel;
	}

	public void draw(float delta) {
		super.draw(delta);
		drawPaths();
	}

	private void loadPartialLevel() {
		buildingLevel = true;
		Level level = loadFromJson();
//		System.out.println(level);
		if (level == null) return;
		ArrayList<ArrayList<Vector2>> tempWalls = new ArrayList<ArrayList<Vector2>>();
		for (int i = 0; i < level.walls.size(); i++) {
			ArrayList<Vector2> wall = new ArrayList<Vector2>();
			for (int j = 0; j < level.walls.get(i).size(); j+=2) {
				wall.add(new Vector2(level.walls.get(i).get(j),
						level.walls.get(i).get(j+1)));
			}
			tempWalls.add(wall);
		}
		if (level.player != null) {
			addPlayer(level.player);
		}
		if (level.map != null) {
			mapArea = level.map;
		}
		if (level.goal != null) {
			goal = level.goal;
		}
		for (Vector2 lilypad : level.lilypads) {
			addLilypad(lilypad);
		}
		for (Vector2 lotus : level.lotuses) {
			addLantern(lotus);
		}
		if (level.whirlpools != null){
			for(Vector3 pool: level.whirlpools){
				addWhirlpool(new Vector2(pool.x, pool.y), false);
			}
		}
		if (level.goal != null && level.goal.size() != 1) {
			for(Vector2 goal: level.goal){
				addGoal(goal);
			}
		}
		for (ArrayList<Vector2> wall : tempWalls) {
			settingWallPath = false;
			for (Vector2 vertex : wall) {
				addWall(vertex, false);
			}
			addWall(null,true);
		}
		for (String enemy : level.enemiesLevel.keySet()) {
			addEnemy(vectorOfString(enemy), false);
			for (Vector2 waypoint : level.enemiesLevel.get(enemy)) {
				addEnemy(waypoint,false);
			}
			addEnemy(null,true);
		}
		if (level.rocks != null) {
			for (Vector2 rock : level.rocks) {
				addRock(rock);
			}
		}
		buildingLevel = false;
	}

	private static String getFileName() throws IOException {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		int result = fileChooser.showOpenDialog(new JFrame());
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();

			String abs = selectedFile.getAbsolutePath();
			int lfs = abs.lastIndexOf("/") + 1;
			return abs.substring(lfs);
		}
		return null;
	}



	class Level {
		int number;
		Vector2 player;
		ArrayList<Vector2> goal;
		HashMap<String,ArrayList<Vector2>> enemiesLevel;
		ArrayList<Vector2> lilypads;
		ArrayList<Vector2> lotuses;
		ArrayList<ArrayList<Float>> walls;
		ArrayList<ArrayList<Float>> shores;
		ArrayList<Vector3> whirlpools;
		ArrayList<Vector2> map;
		ArrayList<Vector2> rocks;

		private Level(int n, Vector2 p, 
				ArrayList<Vector2> g, 
				HashMap<String,ArrayList<Vector2>> e,
				ArrayList<Vector2> li,
				ArrayList<Vector2> lo,
				ArrayList<ArrayList<Vector2>> w, 
				ArrayList<ArrayList<Vector2>> s, 
				ArrayList<Vector3> wp,
				ArrayList<Vector2> r,
				ArrayList<Vector2> m) {
			//			System.out.println(e.values());


			number = n;
			player = p;
			goal = g;
			enemiesLevel = new HashMap<String,ArrayList<Vector2>>();
			for (String enemy : e.keySet()) {
				ArrayList<Vector2> enemyPath = new ArrayList<Vector2>();
				for (Vector2 v : e.get(enemy)) enemyPath.add(v.cpy().scl(1/scale.x,1/scale.y));
				enemiesLevel.put(enemy, enemyPath);

			}
			lilypads = li;
			lotuses = lo;
			whirlpools = wp;
			walls = new ArrayList<ArrayList<Float>>();
			for (ArrayList<Vector2> vectorList : w) {
				ArrayList<Float> floatList = new ArrayList<Float>();
				for (Vector2 vector : vectorList) {
					floatList.add(vector.x/scale.x);
					floatList.add(vector.y/scale.y);
				}
				walls.add(floatList);
			}
			shores = new ArrayList<ArrayList<Float>>();
			for (ArrayList<Vector2> vectorList : s) {
				ArrayList<Float> floatList = new ArrayList<Float>();
				for (Vector2 vector : vectorList) {
					floatList.add(vector.x/scale.x);
					floatList.add(vector.y/scale.y);
				}
				shores.add(floatList);
			}
			rocks = r;
			map = m;
		}


	}


}


