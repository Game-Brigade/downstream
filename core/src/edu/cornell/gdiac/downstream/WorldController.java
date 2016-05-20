/*
 * WorldController.java
 *
 * This is the most important new class in this lab.  This class serves as a combination 
 * of the CollisionController and GameplayController from the previous lab.  There is not 
 * much to do for collisions; Box2d takes care of all of that for us.  This controller 
 * invokes Box2d and then performs any after the fact modifications to the data 
 * (e.g. gameplay).
 *
 * If you study this class, and the contents of the edu.cornell.cs3152.physics.obstacles
 * package, you should be able to understand how the Physics engine works.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.downstream;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.downstream.models.TetherModel;
import edu.cornell.gdiac.downstream.obstacle.*;

/**
 * Base class for a world-specific controller.
 *
 *
 * A world has its own objects, assets, and input controller.  Thus this is 
 * really a mini-GameEngine in its own right.  The only thing that it does
 * not do is create a GameCanvas; that is shared with the main application.
 *
 * You will notice that asset loading is not done with static methods this time.  
 * Instance asset loading makes it easier to process our game modes in a loop, which 
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public abstract class WorldController implements Screen {

	/** 
	 * Tracks the asset state.  Otherwise subclasses will try to load assets 
	 */
	protected enum AssetState {
		/** No assets loaded */
		EMPTY,
		/** Still loading assets */
		LOADING,
		/** Assets are complete */
		COMPLETE
	}

	/** Track asset loading from all instances and subclasses */
	protected AssetState worldAssetState = AssetState.EMPTY;
	/** Track all loaded assets (for unloading purposes) */
	protected Array<String> assets;	

	//References to shared shared textures//
	/** The background image for the level */
	protected static final String BACKGROUND_FILE_N = "terrain/Water_Night.jpg";
	protected static final String BACKGROUND_FILE_D = "terrain/Water_Day.jpg";
	protected static final String BACKGROUND_FILE_S = "terrain/Water_Sunset.jpg";
	protected static final String OVERLAY_FILE = "terrain/texture.jpg";
	/** Reference to the fish texture */
	protected static final String KOI_TEXTURE = "koi/koi.png";
	/** Reference to the enemy texture */
	protected static final String ENEMY_TEXTURE = "enemy/enemy.png";
	/** Reference to the lantern texture */
	protected static final String LANTERN_TEXTURE = "tethers/notlit.png";
	/** Reference to the lighting texture */
	protected static final String LIGHTING_TEXTURE = "tethers/aura.png";
	/** References to shadow and goal textures */
	protected static final String SHADOW_TEXTURE = "terrain/shadow.png";
	protected static final String GOAL_TEXTURE = "koi/Goal State small.png";
	/** References to the repeating land textures */
	protected static final String EARTH_FILE = "terrain/repeat tile.png";
	protected static final String EARTH_FILE_N = "terrain/Grass_Night.jpg";
	protected static final String EARTH_FILE_D = "terrain/Grass_Day.jpg";
	protected static final String EARTH_FILE_S = "terrain/Grass_Sunset.jpg";
	protected static final String SHORE_FILE = "terrain/shore.png";
	/** References to the rock textures */
	protected static final String ROCK_FILE_N = "terrain/Rock_Night.png";
	protected static final String ROCK_FILE_D = "terrain/Rock_Day.png";
	protected static final String ROCK_FILE_S = "terrain/Rock_Sunset.png";
	/** References to the lilypad textures */
	protected static final String LILY_TEXTURE = "tethers/Lily_Day_2.png";
	protected static final String LILY_TEXTURE_S = "tethers/Lily_Sunset.png";
	protected static final String LILY_TEXTURE_N = "tethers/Lily_Night.png";
	protected static final String LILY_TEXTURE_D = "tethers/Lily_Day.png";
	/** References to the whirlpool textures */
	protected static final String WHIRLPOOL_TEXTURE = "Final_Assets/Beta_Art_Assets/Objects(PNGs)/whirlpool.png";
	protected static final String WHIRLPOOL_TEXTURE_N = "Final_Assets/Beta Art Assets/Objects(PNGs)/whirlpool_night.png";
	protected static final String WHIRLPOOL_TEXTURE_D = "Final_Assets/Beta Art Assets/Objects(PNGs)/whirlpool_day.png";
	protected static final String WHIRLPOOL_TEXTURE_S = "Final_Assets/Beta Art Assets/Objects(PNGs)/whirlpool_sunset.png";
	
	/** HUD textures */
	protected static final String ENERGYBAR_TEXTURE = "MENUS/UI_bar.png";
	protected static final String UI_FLOWER = "MENUS/UI_lotus.png";
	protected static final String OVERLAY = "terrain/texture.jpg";
	protected static final String KOI_ARROW = "koi/koi_arrow.png";
	protected static final String WHIRL_ARROW = "terrain/whirl_arrow.png";
	protected static final String TUTORIAL_TEXTURE1 = "MENUS/tip_1.png";
	protected static final String TUTORIAL_TEXTURE2 = "MENUS/tip_2.png";
	protected static final String TUTORIAL_TEXTURE3 = "MENUS/tip_3.png";
	protected static final String TUTORIAL_TEXTURE4 = "MENUS/tip_4.png";
	protected static final String TUTORIAL_TEXTURE5 = "MENUS/tip_5.png";
	protected static final String TUTORIAL_TEXTURE6 = "MENUS/tip_6.png";
	protected static final String HELP_TEXTURE = "MENUS/help_button.png";
	protected static final String ENEMY_TEXTUREA = "enemy/enemy_fish.png";

	// TextureRegions//
	/** Texture assets for the koi */
	protected TextureRegion koiTexture;
	/** Texture assets for lilypads */
	protected TextureRegion lilyTexture;
	protected TextureRegion lilyTextureDay;
	protected TextureRegion lilyTextureNight;
	protected TextureRegion lilyTextureSunset;
	/** Texture assets for enemy fish */
	protected TextureRegion enemyTexture;
	/** Texture assets for lanterns */
	protected TextureRegion lanternTexture;
	/** Texture assets for light */
	protected TextureRegion lightingTexture;
	protected TextureRegion shadowTexture;
	protected TextureRegion goalTexture;
	/** Texture assets for walls and rocks*/
	protected TextureRegion earthTile;
	protected TextureRegion earthTileDay;
	protected TextureRegion earthTileNight;
	protected TextureRegion earthTileSunset;
	protected TextureRegion shoreTile;
	protected TextureRegion rockDay;
	protected TextureRegion rockNight;
	protected TextureRegion rockSunset;
	/** Texture assets for whirlpools */
	protected TextureRegion whirlpoolTexture;
	/** Texture assets for HUD */
	protected TextureRegion energyBarTexture;
	protected TextureRegion UILotusTexture;
	protected TextureRegion koiArrow;
	protected TextureRegion whirlArrow;
	protected TextureRegion tutorial1;
	protected TextureRegion tutorial2;
	protected TextureRegion tutorial3;
	protected TextureRegion tutorial4;
	protected TextureRegion tutorial5;
	protected TextureRegion tutorial6;
	protected TextureRegion helpTexture;
	//Sounds//
	protected static final String LIGHTING_SOUND = "Final_Assets/Sounds/lotus_light.mp3";
	protected static final String MENU_CLICK_SOUND = "Final_Assets/Sounds/menu_click.mp3";
	protected static final String FAIL_SOUND = "Final_Assets/Sounds/fail_level.mp3";
	
	protected Music lightingSound;
	protected Music clickSound;
	protected Music failSound;

	private static int dayTime = 0;

	/** Retro font for displaying messages */
	private static String FONT_FILE = "loading/marathon.ttf";
	private static int FONT_SIZE = 64;
	/** Retro font for displaying messages */
	private static String FONT_FILE2 = "loading/MarkerFelt.ttf";
	private static int FONT_SIZE2 = 57;
	/** The font for giving messages to the player */
	protected BitmapFont displayFont;
	protected BitmapFont secondFont;
	/** The background image for the battle */
	private static Texture backgroundN; 
	private static Texture backgroundD;
	private static Texture backgroundS;
	private static Texture overlay;
	private Color referenceC = Color.WHITE.cpy();
	
	protected ArrayList<ArrayList<Float>> walls = new ArrayList<ArrayList<Float>>();

	//Animations//
	protected Animation lilyAnimation; // This is the only one
	
	protected TextureRegion[] lilyFrames;
	protected TextureRegion[] lilyFramesDay;
	protected TextureRegion[] lilyFramesNight;
	protected TextureRegion[] lilyFramesSunset;
	protected TextureRegion lilycurrentFrame; // also the only one! 

	protected Animation closedFlowerAnimation; // #3
	protected TextureRegion[] closedFlowerFramesDay; 
	protected TextureRegion[] closedFlowerFramesNight;
	protected TextureRegion[] closedFlowerFramesSunset;
	protected TextureRegion closedFlowercurrentFrame; // #7

	protected Animation openFlowerAnimation; // #3
	protected TextureRegion[] openFlowerFramesDay;
	protected TextureRegion[] openFlowerFramesNight;
	protected TextureRegion[] openFlowerFramesSunset;
	protected TextureRegion openFlowercurrentFrame; // #7

	protected Animation openingFlowerAnimation; // #3
	protected TextureRegion[] openingFlowerFramesDay; // #5
	protected TextureRegion[] openingFlowerFramesNight;
	protected TextureRegion[] openingFlowerFramesSunset;
	protected TextureRegion openingFlowercurrentFrame; // #7

	protected Animation closingFlowerAnimation; // #3
	protected TextureRegion[] closingFlowerFramesDay; // #5
	protected TextureRegion[] closingFlowerFramesNight; // #5
	protected TextureRegion[] closingFlowerFramesSunset; // #5
	protected TextureRegion closingFlowercurrentFrame; // #7
	
	protected Animation closingFlowerAnimationT; // #3
	protected Animation openingFlowerAnimationT; // #3
	protected Animation openFlowerAnimationT; // #3
	protected Animation closedFlowerAnimationT; // #3
	protected TextureRegion[] openingFlowerFramesT; // #5
	protected TextureRegion[] closingFlowerFramesT; // #5
	protected TextureRegion[] openFlowerFramesT; // #5
	protected TextureRegion[] closedFlowerFramesT; // #5
	protected TextureRegion openingFlowercurrentFrameT; // #7
	protected TextureRegion closingFlowercurrentFrameT; // #7
	protected TextureRegion openFlowercurrentFrameT; // #7
	protected TextureRegion closedFlowercurrentFrameT; // #7

	protected Animation koiSAnimation; // #3
	protected Texture koiSSheet; // #4
	protected TextureRegion[] koiSFrames; // #5
	protected SpriteBatch koiSspriteBatch; // #6
	protected TextureRegion koiScurrentFrame; // #7

	protected Animation koiCAnimation; // #3
	protected Texture koiCSheet; // #4
	protected TextureRegion[] koiCFrames; // #5
	protected SpriteBatch koiCspriteBatch; // #6
	protected TextureRegion koiCcurrentFrame; // #7

	protected Animation koiCAnimationFlipped;
	protected TextureRegion[]	koiCFramesFlipped;
	protected TextureRegion KoiCcurrentFrameFlipped;
	
	protected Animation enemyAnimation;
	protected TextureRegion[] enemyFrames;
	protected TextureRegion enemyCurrentFrame;

	protected Animation goalAnimation;
	protected TextureRegion goalCurrentFrame;
	protected TextureRegion[] goalFrames;
	protected Color levelAlpha = new Color(255,255,255,.5f);

	
	

	protected Animation lilyAnimation2;
	protected Animation openingFlowerAnimation2;
	protected Animation closingFlowerAnimation2;
	protected Animation closedFlowerAnimation2;
	protected Animation openFlowerAnimation2;

	protected TextureRegion lilycurrentFrame2;
	protected TextureRegion closedFlowercurrentFrame2;
	protected TextureRegion openFlowercurrentFrame2;
	protected TextureRegion openingFlowercurrentFrame2;
	protected TextureRegion closingFlowerCurrentFrame2;

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
		if (worldAssetState != AssetState.EMPTY) {
			return;
		}

		worldAssetState = AssetState.LOADING;

		manager.load(KOI_TEXTURE, Texture.class);
		assets.add(KOI_TEXTURE);

		manager.load(ENEMY_TEXTURE, Texture.class);
		assets.add(ENEMY_TEXTURE);

		manager.load(LILY_TEXTURE, Texture.class);
		assets.add(LILY_TEXTURE);
		
		manager.load(LILY_TEXTURE_N, Texture.class);
		assets.add(LILY_TEXTURE_N);
		
		manager.load(LILY_TEXTURE_S, Texture.class);
		assets.add(LILY_TEXTURE_S);

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
		manager.load(SHORE_FILE,Texture.class);
		assets.add(SHORE_FILE);

		manager.load(ROCK_FILE_D,Texture.class);
		assets.add(ROCK_FILE_D);
		manager.load(ROCK_FILE_N,Texture.class);
		assets.add(ROCK_FILE_N);
		manager.load(ROCK_FILE_S,Texture.class);
		assets.add(ROCK_FILE_S);

		manager.load(WHIRLPOOL_TEXTURE, Texture.class);
		assets.add(WHIRLPOOL_TEXTURE);
		
		manager.load(ENERGYBAR_TEXTURE, Texture.class);
		assets.add(ENERGYBAR_TEXTURE);

		manager.load(UI_FLOWER, Texture.class);
		assets.add(UI_FLOWER);

		manager.load(OVERLAY, Texture.class);
		assets.add(OVERLAY);

		manager.load(BACKGROUND_FILE_N, Texture.class);
		assets.add(BACKGROUND_FILE_N);

		manager.load(BACKGROUND_FILE_D, Texture.class);
		assets.add(BACKGROUND_FILE_D);

		manager.load(BACKGROUND_FILE_S, Texture.class);
		assets.add(BACKGROUND_FILE_S);

		manager.load(OVERLAY_FILE, Texture.class);
		assets.add(OVERLAY_FILE);

		manager.load(KOI_ARROW, Texture.class);
		assets.add(KOI_ARROW);
		manager.load(WHIRL_ARROW, Texture.class);
		assets.add(WHIRL_ARROW);
		
		manager.load(TUTORIAL_TEXTURE1, Texture.class);
		assets.add(TUTORIAL_TEXTURE1);
		manager.load(TUTORIAL_TEXTURE2, Texture.class);
		assets.add(TUTORIAL_TEXTURE2);
		manager.load(TUTORIAL_TEXTURE3, Texture.class);
		assets.add(TUTORIAL_TEXTURE3);
		manager.load(TUTORIAL_TEXTURE4, Texture.class);
		assets.add(TUTORIAL_TEXTURE4);
		manager.load(TUTORIAL_TEXTURE5, Texture.class);
		assets.add(TUTORIAL_TEXTURE5);
		manager.load(TUTORIAL_TEXTURE6, Texture.class);
		assets.add(TUTORIAL_TEXTURE6);
		
		manager.load(HELP_TEXTURE, Texture.class);
		assets.add(HELP_TEXTURE);
		

		referenceC.a = 0f;

		// Load the font
		FreetypeFontLoader.FreeTypeFontLoaderParameter size2Params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
		size2Params.fontFileName = FONT_FILE;
		size2Params.fontParameters.size = FONT_SIZE;
		manager.load(FONT_FILE, BitmapFont.class, size2Params);
		assets.add(FONT_FILE);

		FreetypeFontLoader.FreeTypeFontLoaderParameter size3Params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
		size3Params.fontFileName = FONT_FILE2;
		size3Params.fontParameters.size = FONT_SIZE2;
		manager.load(FONT_FILE2, BitmapFont.class, size3Params);
		assets.add(FONT_FILE2);
	}


	protected TextureRegion[] splice(int cols, int rows, String filePath){

		Texture sheet = new Texture(Gdx.files.internal(filePath));

		TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth()/cols, sheet.getHeight()/rows);
		TextureRegion[] Frames = new TextureRegion[cols * rows];
		int index = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				Frames[index++] = tmp[i][j];
			}
		}
		return Frames;
	}	
	
	protected TextureRegion[] splice(int cols, int rows, int images, String filePath){

		Texture sheet = new Texture(Gdx.files.internal(filePath));

		TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth()/cols, sheet.getHeight()/rows);
		TextureRegion[] Frames = new TextureRegion[images];
		int index = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (index < images){
					Frames[index++] = tmp[i][j];
				}
			}
		}
		return Frames;
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
		if (worldAssetState != AssetState.LOADING) {
			return;
		}
		
		

		int cols = 11;
		int rows = 1;

		//animationDef
		//load the animation content here

		lilyFrames = splice(4, 12, 47, LILY_TEXTURE);
		//lilyFramesDay = splice(47, "tethers/Lily_Day.png");
		lilyFramesNight = splice(47, 1, LILY_TEXTURE_N);
//		lilyFramesSunset = splice(47, LILY_TEXTURE_S);
		goalFrames = splice(4,20, GOAL_TEXTURE);
		goalAnimation = new Animation(.01f, goalFrames);
		//lilyAnimation = new Animation(.2f, lilyFrames);
		
		enemyFrames = splice(4, 10, 39, ENEMY_TEXTUREA);
		enemyAnimation = new Animation(.02f, enemyFrames);


		cols = 11;
		rows = 1;


		closedFlowerFramesDay = splice(26, 1, "tethers/Floating_Closed_Day_small.png");
		closedFlowerFramesNight = splice(26, 1, "tethers/Floating_Closed_Night_small.png");
		closedFlowerFramesSunset = splice(26, 1, "tethers/Floating_Closed_Sunset_small.png");
		closedFlowerFramesT = splice(23, 1, "tethers/Floating_Closed_Sunset2.png");


		int index = 0;

		openFlowerFramesDay = splice(26, 1, "tethers/Floating_Open_Day_small.png");
		openFlowerFramesNight = splice(26, 1, "tethers/Floating_Open_Night_small.png");
		openFlowerFramesSunset = splice(26, 1, "tethers/Floating_Open_Sunset_small.png");
		openFlowerFramesT = splice(23, 1, "tethers/Floating_Open_Sunset2.png");


		cols = 8; 

		openingFlowerFramesDay = splice(26, 1, "tethers/Opening_Flower_Day_small.png" );
		openingFlowerFramesNight = splice(26, 1, "tethers/Opening_Flower_Night_small.png");
		openingFlowerFramesSunset = splice(26, 1, "tethers/Opening_Flower_Sunset_small.png");
		openingFlowerFramesT = splice(26, 1, "tethers/Opening_Flower_Sunset2.png");

		closingFlowerFramesDay = splice(26, 1, "tethers/Closing_Flower_Day_small.png");
		closingFlowerFramesNight = splice(26, 1, "tethers/Closing_Flower_Night_small.png");
		closingFlowerFramesSunset = splice(26, 1, "tethers/Closing_Flower_Sunset_small.png");
		closingFlowerFramesT = splice(26, 1, "tethers/Opening_Flower_Sunset2.png");

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
		koiCAnimation = new Animation(.02f, koiCFrames); 
		koiCAnimationFlipped = new Animation(.02f, koiCFramesFlipped);
		koiCspriteBatch = new SpriteBatch(); 



		energyBarTexture = createTexture(manager, ENERGYBAR_TEXTURE, false);
		//enemyTexture = createTexture(manager,ENEMY_TEXTURE,false);
		enemyTexture = enemyFrames[0];
		koiTexture = koiSFrames[0];
		//koiTexture = createTexture(manager, KOI_TEXTURE, false);

		//lilyTexture = lilyFramesNight[0];
		lilyTexture = lilyFrames[0];
		lanternTexture = closedFlowerFramesDay[0];
		lightingTexture = createTexture(manager, LIGHTING_TEXTURE, false);
		shadowTexture = createTexture(manager, SHADOW_TEXTURE, false);
		//goalTexture = createTexture(manager, GOAL_TEXTURE, false);
		goalTexture = goalFrames[0];

		UILotusTexture = createTexture(manager, UI_FLOWER, false);	

		earthTile = createTexture(manager,EARTH_FILE_N,true);
		earthTileDay = createTexture(manager,EARTH_FILE_D, true);
		earthTileNight = createTexture(manager,EARTH_FILE_N, true);
		earthTileSunset = createTexture(manager,EARTH_FILE_S, true);
		shoreTile = createTexture(manager,SHORE_FILE,true);

		rockDay = createTexture(manager,ROCK_FILE_D, true);
		rockNight = createTexture(manager,ROCK_FILE_N, true);
		rockSunset = createTexture(manager,ROCK_FILE_S, true);

		
		
		whirlpoolTexture = createTexture(manager, WHIRLPOOL_TEXTURE, true);

		koiArrow = createTexture(manager, KOI_ARROW, false);
		whirlArrow = createTexture(manager, WHIRL_ARROW, false);
		
		tutorial1 = createTexture(manager, TUTORIAL_TEXTURE1, false);
		tutorial2 = createTexture(manager, TUTORIAL_TEXTURE2, false);
		tutorial3 = createTexture(manager, TUTORIAL_TEXTURE3, false);
		tutorial4 = createTexture(manager, TUTORIAL_TEXTURE4, false);
		tutorial5 = createTexture(manager, TUTORIAL_TEXTURE5, false);
		tutorial6 = createTexture(manager, TUTORIAL_TEXTURE6, false);
		
		helpTexture = createTexture(manager, HELP_TEXTURE, false);

		// Allocate the tiles

		
		//setBackground(manager.get(BACKGROUND_FILE_N, Texture.class), 2);
		setBackground(createTexture(manager, BACKGROUND_FILE_N, false).getTexture(), 2);
		getBackground(2).setWrap(TextureWrap.Repeat, TextureWrap.Repeat);

		setBackground(createTexture(manager, BACKGROUND_FILE_D, false).getTexture(), 0);
		getBackground(0).setWrap(TextureWrap.Repeat, TextureWrap.Repeat);

		setBackground(createTexture(manager, BACKGROUND_FILE_N, false).getTexture(), 1);
		getBackground(1).setWrap(TextureWrap.Repeat, TextureWrap.Repeat);

		overlay = createTexture(manager, OVERLAY_FILE, true).getTexture();
		overlay.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);

		// Allocate the font
		if (manager.isLoaded(FONT_FILE)) {
			displayFont = manager.get(FONT_FILE,BitmapFont.class);
		} else {
			displayFont = null;
		}

		if (manager.isLoaded(FONT_FILE2)) {
			secondFont = manager.get(FONT_FILE2,BitmapFont.class);
		} else {
			secondFont = null;
		}
		
		lightingSound = Gdx.audio.newMusic(Gdx.files.internal(LIGHTING_SOUND));
		lightingSound.setLooping(false);
		
		clickSound = Gdx.audio.newMusic(Gdx.files.internal(MENU_CLICK_SOUND));
		clickSound.setLooping(false);
		
		failSound = Gdx.audio.newMusic(Gdx.files.internal(FAIL_SOUND));
		failSound.setLooping(false);
		
		worldAssetState = AssetState.COMPLETE;
	}

	/*private void backgroundCycle(AssetManager manager){
		if (dayTime == 1){
			setBackground(manager.get(BACKGROUND_FILE_N, Texture.class));
			getBackground().setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		}
		if (dayTime == 0){
			setBackground(manager.get(BACKGROUND_FILE_D, Texture.class));
			getBackground().setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		}
		if(dayTime == 2){
			setBackground(manager.get(BACKGROUND_FILE_S, Texture.class));
			getBackground().setWrap(TextureWrap.Repeat, TextureWrap.Repeat);

		}
	}*/

	/**
	 * Returns a newly loaded texture region for the given file.
	 *
	 * This helper methods is used to set texture settings (such as scaling, and
	 * whether or not the texture should repeat) after loading.
	 *
	 * @param manager 	Reference to global asset manager.
	 * @param file		The texture (region) file
	 * @param repeat	Whether the texture should be repeated
	 *
	 * @return a newly loaded texture region for the given file.
	 */
	protected TextureRegion createTexture(AssetManager manager, String file, boolean repeat) {
		if (manager.isLoaded(file)) {
			TextureRegion region = new TextureRegion(manager.get(file, Texture.class));
			region.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
			if (repeat) {
				region.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
			}
			return region;
		}
		return null;
	}	
	
	
	public void setDayTime(int i){
		dayTime = i;
		this.fadeOut = fadeOut;
		this.fadeIn = fadeIn;
	}
	
	public void setLevelAlpha(Color color){
		levelAlpha = color;
	}
	
	/**
	 * Returns a newly loaded filmstrip for the given file.
	 *
	 * This helper methods is used to set texture settings (such as scaling, and
	 * the number of animation frames) after loading.
	 *
	 * @param manager 	Reference to global asset manager.
	 * @param file		The texture (region) file
	 * @param rows 		The number of rows in the filmstrip
	 * @param cols 		The number of columns in the filmstrip
	 * @param size 		The number of frames in the filmstrip
	 *
	 * @return a newly loaded texture region for the given file.
	 */
	protected FilmStrip createFilmStrip(AssetManager manager, String file, int rows, int cols, int size) {
		if (manager.isLoaded(file)) {
			FilmStrip strip = new FilmStrip(manager.get(file, Texture.class),rows,cols,size);
			strip.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
			return strip;
		}
		return null;
	}

	/** 
	 * Unloads the assets for this game.
	 * 
	 * This method erases the static variables.  It also deletes the associated textures 
	 * from the asset manager. If no assets are loaded, this method does nothing.
	 * 
	 * @param manager Reference to global asset manager.
	 */
	public void unloadContent(AssetManager manager) {
		for(String s : assets) {
			if (manager.isLoaded(s)) {
				manager.unload(s);
			}
		}
	}

	/** Exit code for quitting the game */
	public static final int EXIT_QUIT = 0;
	public static final int EXIT_MAIN = 17;
	public static final int EXIT_SELECT = 18;
	public static final int EXIT_PAUSE = 19;
	public static final int EXIT_EDIT = 20;
	public static final int EXIT_OPTIONS = 21;
	public static final int EXIT_NEXT = 22;
	public static final int EXIT_PREV = 23;
	public static final int EXIT_WIN = 24;
	public static final int EXIT_WIN_DONE = 25;

	/** How many frames after winning/losing do we continue? */
	public static final int EXIT_COUNT = 120;

	/** The amount of time for a physics engine step. */
	public static final float WORLD_STEP = 1/60.0f;
	/** Number of velocity iterations for the constrain solvers */
	public static final int WORLD_VELOC = 6;
	/** Number of position iterations for the constrain solvers */
	public static final int WORLD_POSIT = 2;

	/** Width of the game world in Box2d units */
	protected static final float DEFAULT_WIDTH  = 32.0f;
	/** Height of the game world in Box2d units */
	protected static final float DEFAULT_HEIGHT = 18.0f;
	/** The default value of gravity (going down) */
	protected static final float DEFAULT_GRAVITY = -4.9f;

	/** Reference to the game canvas */
	protected GameCanvas canvas;
	/** All the objects in the world. */
	protected PooledList<Obstacle> objects  = new PooledList<Obstacle>();
	/** Queue for adding objects */
	protected PooledList<Obstacle> addQueue = new PooledList<Obstacle>();
	/** Listener that will update the player mode when we are done */
	protected ScreenListener listener;

	protected HUDitems HUD;

	/** The Box2D world */
	protected World world;
	/** The boundary of the world */
	protected Rectangle bounds;
	/** The world scale */
	protected Vector2 scale;

	/** Whether or not this is an active controller */
	public boolean active;
	/** Whether we have completed this level */
	private boolean complete;
	/** Whether we have failed at this world (and need a reset) */
	private boolean failed;
	/** Whether or not debug mode is active */
	protected boolean debug;
	/** Countdown active for winning or losing */
	private int countdown;

	private int cw;
	private int ch;
	protected int fadeIn;
	protected int fadeOut;
	

	/**
	 * Returns true if debug mode is active.
	 *
	 * If true, all objects will display their physics bodies.
	 *
	 * @return true if debug mode is active.
	 */
	public boolean isDebug( ) {
		return debug;
	}

	/**
	 * Sets whether debug mode is active.
	 *
	 * If true, all objects will display their physics bodies.
	 *
	 * @param value whether debug mode is active.
	 */
	public void setDebug(boolean value) {
		debug = value;
	}

	/**
	 * Returns true if the level is completed.
	 *
	 * If true, the level will advance after a countdown
	 *
	 * @return true if the level is completed.
	 */
	public boolean isComplete( ) {
		return complete;
	}

	/**
	 * Sets whether the level is completed.
	 *
	 * If true, the level will advance after a countdown
	 *
	 * @param value whether the level is completed.
	 */
	public void setComplete(boolean value) {
		if (value) {
			countdown = EXIT_COUNT;
		}
		complete = value;
		//		if (value) waitSeconds(3);
	}

	/**
	 * Returns true if the level is failed.
	 *
	 * If true, the level will reset after a countdown
	 *
	 * @return true if the level is failed.
	 */
	public boolean isFailure( ) {
		return failed;
	}

	/**
	 * Sets whether the level is failed.
	 *
	 * If true, the level will reset after a countdown
	 *
	 * @param value whether the level is failed.
	 */
	public void setFailure(boolean value) {
		if (value) {
			countdown = EXIT_COUNT;
		}
		failed = value;
	}

	/**
	 * Returns true if this is the active screen
	 *
	 * @return true if this is the active screen
	 */
	public boolean isActive( ) {
		return active;
	}

	/**
	 * Returns the canvas associated with this controller
	 *
	 * The canvas is shared across all controllers
	 *
	 * @param the canvas associated with this controller
	 */
	public GameCanvas getCanvas() {
		return canvas;
	}

	/**
	 * Sets the canvas associated with this controller
	 *
	 * The canvas is shared across all controllers.  Setting this value will compute
	 * the drawing scale from the canvas size.
	 *
	 * @param value the canvas associated with this controller
	 */
	public void setCanvas(GameCanvas canvas) {
		this.canvas = canvas;
		this.scale.x = canvas.getWidth()/bounds.getWidth();
		this.scale.y = canvas.getHeight()/bounds.getHeight();
	}

	/**
	 * Creates a new game world with the default values.
	 *
	 * The game world is scaled so that the screen coordinates do not agree
	 * with the Box2d coordinates.  The bounds are in terms of the Box2d
	 * world, not the screen.
	 */
	protected WorldController() {
		this(new Rectangle(0,0,DEFAULT_WIDTH,DEFAULT_HEIGHT), 
				new Vector2(0,DEFAULT_GRAVITY));
	}

	/**
	 * Creates a new game world
	 *
	 * The game world is scaled so that the screen coordinates do not agree
	 * with the Box2d coordinates.  The bounds are in terms of the Box2d
	 * world, not the screen.
	 *
	 * @param width  	The width in Box2d coordinates
	 * @param height	The height in Box2d coordinates
	 * @param gravity	The downward gravity
	 */
	protected WorldController(float width, float height, float gravity) {
		this(new Rectangle(0,0,width,height), new Vector2(0,gravity));
	}

	/**
	 * Creates a new game world
	 *
	 * The game world is scaled so that the screen coordinates do not agree
	 * with the Box2d coordinates.  The bounds are in terms of the Box2d
	 * world, not the screen.
	 *
	 * @param bounds	The game bounds in Box2d coordinates
	 * @param gravity	The gravitational force on this Box2d world
	 */
	protected WorldController(Rectangle bounds, Vector2 gravity) {
		assets = new Array<String>();
		world = new World(gravity,false);
		this.bounds = new Rectangle(bounds);
		this.scale = new Vector2(1,1);
		complete = false;
		failed = false;
		debug  = false;
		active = false;
		countdown = -1;
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
		objects = null;
		addQueue = null;
		bounds = null;
		scale  = null;
		world  = null;
		canvas = null;
		
		clickSound.dispose();
		
		lightingSound.dispose();
		
	}

	/**
	 *
	 * Adds a physics object in to the insertion queue.
	 *
	 * Objects on the queue are added just before collision processing.  We do this to 
	 * control object creation.
	 *
	 * param obj The object to add
	 */
	public void addQueuedObject(Obstacle obj) {
		assert inBounds(obj) : "Object is not in bounds";
		addQueue.add(obj);
	}

	/**
	 * Immediately adds the object to the physics world
	 *
	 * param obj The object to add
	 */
	protected void addObject(Obstacle obj) {
		assert inBounds(obj) : "Object is not in bounds";
		objects.add(obj);
		obj.activatePhysics(world);
	}

	protected void removeObject(Obstacle obj) {
		assert inBounds(obj) : "Object is not in bounds";
		objects.remove(obj);
		obj.deactivatePhysics(world);
	}

	protected void addHUD(HUDitems h){
		HUD = h;
	}

	/**
	 * Returns true if the object is in bounds.
	 *
	 * This assertion is useful for debugging the physics.
	 *
	 * @param obj The object to check.
	 *
	 * @return true if the object is in bounds.
	 */
	public boolean inBounds(Obstacle obj) {
		boolean horiz = (bounds.x <= obj.getX() && obj.getX() <= bounds.x+bounds.width);
		boolean vert  = (bounds.y <= obj.getY() && obj.getY() <= bounds.y+bounds.height);
		return horiz && vert;
	}

	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public abstract void reset();

	/**
	 * Returns whether to process the update loop
	 *
	 * At the start of the update loop, we check if it is time
	 * to switch to a new game mode.  If not, the update proceeds
	 * normally.
	 *
	 * @param delta Number of seconds since last animation frame
	 * 
	 * @return whether to process the update loop
	 */
	public boolean preUpdate(float dt) {
		InputController input = InputController.getInstance();
		input.readInput(bounds, scale);
		if (listener == null) {
			return true;
		}

		// Toggle debug
		if (input.didDebug()) {
			debug = !debug;
		}

		// Handle resets
		if (input.didReset()) {
			reset();
		}

		// Now it is time to maybe switch screens.
		if (input.didExit()) {
			listener.exitScreen(this, EXIT_QUIT);
			return false;
		} else if (countdown > 0) {
			countdown--;
		} else if (countdown == 0) {
			if (failed) {
				reset();
			} 
		} 
		return true;
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
	public abstract void update(float dt);

	/**
	 * Processes physics
	 *
	 * Once the update phase is over, but before we draw, we are ready to handle
	 * physics.  The primary method is the step() method in world.  This implementation
	 * works for all applications and should not need to be overwritten.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	public void postUpdate(float dt) {
		// Add any objects created by actions
		while (!addQueue.isEmpty()) {
			addObject(addQueue.poll());
		}

		// Turn the physics engine crank.
		world.step(WORLD_STEP,WORLD_VELOC,WORLD_POSIT);

		// Garbage collect the deleted objects.
		// Note how we use the linked list nodes to delete O(1) in place.
		// This is O(n) without copying.
		Iterator<PooledList<Obstacle>.Entry> iterator = objects.entryIterator();
		while (iterator.hasNext()) {
			PooledList<Obstacle>.Entry entry = iterator.next();
			Obstacle obj = entry.getValue();
			if (obj.isRemoved()) {
				obj.deactivatePhysics(world);
				entry.remove();
			} else {
				// Note that update is called last!
				obj.update(dt);
			}
		}
	}

	/**
	 * Draw the physics objects to the canvas
	 *
	 * For simple worlds, this method is enough by itself.  It will need
	 * to be overriden if the world needs fancy backgrounds or the like.
	 *
	 * The method draws all objects in the order that they were added.
	 *
	 * @param canvas The drawing context
	 */
	public void draw(float delta) {
		canvas.clear();
		canvas.begin();
		cw = canvas.getWidth();
		ch = canvas.getHeight();
		for (int i = -5; i < 5; i++) {
			for (int j = -5; j < 5; j++) {
				canvas.draw(getBackground(dayTime), Color.WHITE, cw*i * 2, ch*j * 2, cw * 2,   ch * 2);
				if(dayTime == 0 || dayTime == 1){
					canvas.draw(getBackground(dayTime + 1), levelAlpha, cw*i * 2, ch*j * 2, cw * 2,   ch * 2);
				}
			}
		}
		canvas.end();
		if (walls.size() > 0){ 
			for (ArrayList<Float> wall : walls) canvas.drawPath(wall);
		}
		canvas.begin();
		//		canvas.draw(background, Color.WHITE, 0, 0, canvas.getWidth(), canvas.getHeight());
		//canvas.draw(rocks, Color.WHITE, 0, 0, canvas.getWidth(), canvas.getHeight());
		
		
//		for (ArrayList<Float> wall : walls) canvas.drawPath(wall);
		
		for(Obstacle obj : objects) {
			obj.draw(canvas);
		}
		

		for (int i = -5; i < 5; i++) {
			for (int j = -5; j < 5; j++) {
				canvas.draw(overlay, referenceC, cw*i, ch*j, cw,   ch);
			}
		}

		canvas.end();

		if (debug) {
			canvas.beginDebug();
			for(Obstacle obj : objects) {
				obj.drawDebug(canvas);
			}
			canvas.endDebug();
		}



//		// Final message
//		if (complete && !failed) {
//			displayFont.setColor(Color.BLACK);
//			canvas.begin(); // DO NOT SCALE
//			canvas.drawTextCentered("VICTORY!", displayFont, 0.0f);
//			canvas.end();
//			displayFont.setColor(Color.BLACK);
//		} else if (failed) {
//			displayFont.setColor(Color.RED);
//			canvas.begin(); // DO NOT SCALE
//			canvas.drawTextCentered("FAILURE!", displayFont, 0.0f);
//			canvas.end();
//		}
	}

	/**
	 * Called when the Screen is resized. 
	 *
	 * This can happen at any point during a non-paused state but will never happen 
	 * before a call to show().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		// IGNORE FOR NOW
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
		if (active) {
			if (preUpdate(delta)) {
				update(delta); // This is the one that must be defined.
				postUpdate(delta);

			}
			draw(delta);
		}
	}

	/**
	 * Called when the Screen is paused.
	 * 
	 * This is usually when it's not active or visible on screen. An Application is 
	 * also paused before it is destroyed.
	 */
	public void pause() {
		// TODO Auto-generated method stub
	}

	/**
	 * Called when the Screen is resumed from a paused state.
	 *
	 * This is usually when it regains focus.
	 */
	public void resume() {
		// TODO Auto-generated method stub
	}

	/**
	 * Called when this screen becomes the current screen for a Game.
	 */
	public void show() {
		// Useless if called in outside animation loop
		active = true;
	}

	/**
	 * Called when this screen is no longer the current screen for a Game.
	 */
	public void hide() {
		// Useless if called in outside animation loop
		active = false;
	}

	/**
	 * Sets the ScreenListener for this mode
	 *
	 * The ScreenListener will respond to requests to quit.
	 */
	public void setScreenListener(ScreenListener listener) {
		this.listener = listener;
	}

	public static Texture getBackground(int n) {
		if (n == 0){
			return backgroundD;
		}
		else if (n == 1){
			return backgroundS;
		}
		else{
			return backgroundN;
		}
	}

	public static void setBackground(Texture background, int n) {
		if (n == 0){
			WorldController.backgroundD = background;
		}
		else if (n == 1){
			WorldController.backgroundS = background;
		}
		else {
			WorldController.backgroundN = background;
		}
	}

	public static void waitSeconds(int seconds) {
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() < start + seconds * 1000) {
			continue;
		}
	}

	protected static Vector2 vectorOfString(String s) {
		int comma = s.indexOf(",");
		int openParens = s.indexOf("(");
		int closeParens = s.indexOf(")");
		String xstr = s.substring(openParens+1,comma);
		String ystr = s.substring(comma+1,closeParens);
		float x = Float.parseFloat(xstr);
		float y = Float.parseFloat(ystr);
		return new Vector2(x,y);
	}

}