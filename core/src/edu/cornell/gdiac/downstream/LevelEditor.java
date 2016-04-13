package edu.cornell.gdiac.downstream;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
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
import edu.cornell.gdiac.downstream.models.TetherModel;
import edu.cornell.gdiac.downstream.obstacle.PolygonObstacle;
import edu.cornell.gdiac.util.SoundController;

public class LevelEditor extends WorldController {

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
	private TextureRegion earthTile;

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

		// Ship textures
		manager.load(KOI_TEXTURE, Texture.class);
		assets.add(KOI_TEXTURE);

		manager.load(LILY_TEXTURE, Texture.class);
		assets.add(LILY_TEXTURE);

		manager.load(LANTERN_TEXTURE, Texture.class);
		assets.add(LANTERN_TEXTURE);

		manager.load(LIGHTING_TEXTURE, Texture.class);
		assets.add(LIGHTING_TEXTURE);

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

		manager.load(EARTH_FILE,Texture.class);
		assets.add(EARTH_FILE);

		//sounds
		//manager.load(MAIN_FIRE_SOUND, Sound.class);
		//assets.add(MAIN_FIRE_SOUND);


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
		land4Texture = createTexture(manager,LAND_4SIDE_TEXTURE,false);
		leftLandTexture = createTexture(manager,LEFT_LAND_TEXTURE,false);
		rightLandTexture = createTexture(manager,RIGHT_LAND_TEXTURE,false);
		topLandTexture = createTexture(manager,TOP_LAND_TEXTURE,false);
		bottomLandTexture = createTexture(manager,BOTTOM_LAND_TEXTURE,false);
		earthTile = createTexture(manager,EARTH_FILE,true);

		SoundController sounds = SoundController.getInstance();
		//sounds.allocate(manager,MAIN_FIRE_SOUND);


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
	
	private ArrayList<Vector2> lilypads;
	private ArrayList<Vector2> lanterns;
	private HashMap<Vector2,ArrayList<Vector2>> enemies;
	private Vector2 player;
	private ArrayList<ArrayList<Vector2>> walls;
	private Vector2 goal;
	
	private PlayerModel koi;
	
	private boolean settingEnemyPath = false;
	private boolean settingWallPath = false;
	private boolean didEnter = false;
	private Vector2 currentEnemy;
	private ArrayList<Vector2> enemyPath;
	private ArrayList<Vector2> wallPath;
	
	private CameraController cameraController;
	
	public LevelEditor() {
		setDebug(false);
		setComplete(false);
		setFailure(false);
	}
	
	private void populateLevel() {
		cameraController = new CameraController(canvas.getCamera());
		lilypads = new ArrayList<Vector2>();
		lanterns = new ArrayList<Vector2>();
		enemies = new HashMap<Vector2,ArrayList<Vector2>>();
		enemyPath = new ArrayList<Vector2>();
		walls = new ArrayList<ArrayList<Vector2>>();
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
		
		if (input.didEnter() && settingEnemyPath) addEnemy(true);
		if (input.didEnter() && settingWallPath) addWall(true);
		
		if (input.isZoomIn()) 		cameraController.zoomInBoundless();
		else if (input.isZoomOut()) cameraController.zoomOutBoundless();
		
		
		handleClick: if (input.getClick() != null) {
			if (input.getSelection() == null) break handleClick;
			switch (input.getSelection()) {
				case Lilypad: 
					addLilypad();
					break;
				case Lantern:
					addLantern();
					break;
				case Enemy:
					addEnemy(didEnter);
					break;
				case Player:
					addPlayer();
					break;
				case Wall:
					addWall(didEnter);
				case Goal:
					addGoal();
					
					break;
			}
		}
		cameraController.handleArrowKeys(input.getUp(), input.getDown(), input.getLeft(), input.getRight());
	}
	
	private void addLilypad() {
		Vector3 click3 = canvas.getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		Vector2 click = new Vector2(click3.x/scale.x, click3.y/scale.y);
		lilypads.add(click);
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
	
	private void addLantern() {
		Vector3 click3 = canvas.getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		Vector2 click = new Vector2(click3.x/scale.x, click3.y/scale.y);
		lanterns.add(click);
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
	
	private void addEnemy(boolean enter) {
		if (enter) {
			settingEnemyPath = false;
			didEnter = false;
			enemies.put(currentEnemy, enemyPath);
			return;
		}
		Vector3 click3 = canvas.getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		Vector2 click = new Vector2(click3.x/scale.x, click3.y/scale.y);
		if (settingEnemyPath) {
			enemyPath.add(click.scl(scale));
			return;
		}
		settingEnemyPath = true;
		currentEnemy = click;
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
	
	private void addPlayer() {
		Vector3 click3 = canvas.getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		Vector2 click = new Vector2(click3.x/scale.x, click3.y/scale.y);
		player = click;
		if (koi != null) removeObject(koi);
		float dwidth  = koiTexture.getRegionWidth()/scale.x;
		float dheight = koiTexture.getRegionHeight()/scale.y;
		koi = new PlayerModel(click.x, click.y, dwidth, dheight);
		koi.setDrawScale(scale);
		koi.setName("koi");
		koi.setTexture(koiTexture);
		koi.setTethered(false);
		addObject(koi);
	}
	
	private void addWall(boolean enter) {
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
			System.out.println(Arrays.toString(wallFloat));
			obj = new PolygonObstacle(wallFloat, 0, 0);
			obj.setBodyType(BodyDef.BodyType.StaticBody);
			obj.setDensity(BASIC_DENSITY);
			obj.setFriction(BASIC_FRICTION);
			obj.setRestitution(BASIC_RESTITUTION);
			obj.setDrawScale(scale);
			obj.setTexture(earthTile);
			obj.setName("wall1");
			addObject(obj);
			return;
		}
		Vector3 click3 = canvas.getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		Vector2 click = new Vector2(click3.x/scale.x, click3.y/scale.y);
		if (settingWallPath) {
			wallPath.add(click.scl(scale));
			return;
		}
		settingWallPath = true;
		wallPath = new ArrayList<Vector2>();
		wallPath.add(click.scl(scale));
	}

	private void addGoal() {
//		System.out.println("IN GOAL");
		goal = new Vector2(0,0);
		saveToJson();
//		loadFromJson();
	}
	
	private void drawPaths() {
		if (settingEnemyPath) drawPath(enemyPath);
		if (settingWallPath) drawPath(wallPath);
		for (ArrayList<Vector2> path : enemies.values()) {
			drawPath(path);
		}
		for (ArrayList<Vector2> path : walls) {
			drawPath(path);
		}
	}
	
	private void drawPath(ArrayList<Vector2> path) {
		for (int i = 0; i < path.size() - 1; i++) {
			canvas.drawLeadingLine(path.get(i), path.get(i+1));
		}
	}
	
	private void saveToJson() {
		String filename = "level1.json";
		int n = 1;
		Vector2 p = player;
		Vector2 g = goal;
		HashMap<Vector2,ArrayList<Vector2>> e = enemies;
		ArrayList<Vector2> li = lilypads;
		ArrayList<Vector2> lo = lanterns;
		ArrayList<ArrayList<Vector2>> w = walls;
		Level level = new Level(n,p,g,e,li,lo,w);
		
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
		Gson gson = new Gson();
		try {
			JsonReader reader = new JsonReader(new FileReader("level1.json"));
			Level level = gson.fromJson(reader, Level.class);
			System.out.println(level);
			return level;
		} catch (Exception e){
			System.out.println(e);
			//pls no
			return null;
		}
	}

	public void draw(float delta) {
		super.draw(delta);
		drawPaths();
	}
	
	public class Level {
		int number;
		Vector2 player;
		Vector2 goal;
		HashMap<String,ArrayList<Vector2>> enemies;
		ArrayList<Vector2> lilypads;
		ArrayList<Vector2> lotuses;
		ArrayList<ArrayList<Float>> walls;
		
		private Level(int n, Vector2 p, Vector2 g, 
					  HashMap<Vector2,ArrayList<Vector2>> e,
					  ArrayList<Vector2> li,
					  ArrayList<Vector2> lo,
					  ArrayList<ArrayList<Vector2>> w) {
			number = n;
			player = p;
			goal = g;
			enemies = new HashMap<String,ArrayList<Vector2>>();
			for (Vector2 enemy : e.keySet()) {
				for (Vector2 v : e.get(enemy)) v.scl(1/scale.x,1/scale.y);
				enemies.put(enemy.toString(), e.get(enemy));
				
			}
			lilypads = li;
			lotuses = lo;
			walls = new ArrayList<ArrayList<Float>>();
			for (ArrayList<Vector2> vectorList : w) {
				ArrayList<Float> floatList = new ArrayList<Float>();
				for (Vector2 vector : vectorList) {
					floatList.add(vector.x/scale.x);
					floatList.add(vector.y/scale.y);
				}
				walls.add(floatList);
			}
		}
		
//		public String toString() {
//			return "Number: " + number + "\n" +
//				   "Player: " + player + "\n" +
//				   "Goal: " + goal + "\n" +
//				   "Enemies: " + enemies + "\n" +
//				   "Lilypads: " + lilypads + "\n" +
//				   "Lotuses: " + lotuses;
//		}
	}
	
	
	
}
