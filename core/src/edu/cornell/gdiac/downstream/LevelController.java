package edu.cornell.gdiac.downstream;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import edu.cornell.gdiac.downstream.WorldController.AssetState;

public class LevelController {
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
	/** Reference to the repeating land texture */
	private static final String EARTH_FILE = "terrain/repeat tile.png";
	/** Reference to the whirlpool texture */
	private static final String WHIRLPOOL_TEXTURE = "terrain/whirlpool.png";
	/** Reference to the flipped whirlpool texture */
	private static final String WHIRLPOOL_FLIP_TEXTURE = "terrain/whirlpool_flip.png";

	/** References to sounds */
	private static final String CLICK_SOUND = "SOUNDS/menu_click.wav";
	private static final String LIGHTING_SOUND = "SOUNDS/lighting_1.mp3";
	private static final String DEATH_SOUND = "SOUNDS/fish_death.wav";
	private static final String BACKGROUND_SOUND = "SOUNDS/background_sound.mp3";
	private static final String ENERGYBAR_TEXTURE = "MENUS/energyBar.png";

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
	/** Texture assets for walls and platforms */
	private TextureRegion earthTile;
	/** Texture assets for whirlpools */
	private TextureRegion whirlpoolTexture;
	private TextureRegion whirlpoolFlipTexture;
	
	private TextureRegion energyBarTexture;

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

	private float PLAYER_LINEAR_VELOCITY = 6f;

	private boolean enableSlow = false;
	private boolean enableLeadingLine = false;
	private boolean enableTetherRadius = true;
	
	private Music deathSound;
	
	//animations//
	
	float stateTime;  
	float relativeTime = 0;
	    
	Animation lilyAnimation; // #3
	Texture lilySheet; // #4
	TextureRegion[] lilyFrames; // #5
	SpriteBatch lilyspriteBatch; // #6
	TextureRegion lilycurrentFrame; // #7

	Animation closedFlowerAnimation; // #3
	Texture closedFlowerSheet; // #4
	TextureRegion[] closedFlowerFrames; // #5
	SpriteBatch closedFlowerspriteBatch; // #6
	TextureRegion closedFlowercurrentFrame; // #7

	Animation openFlowerAnimation; // #3
	Texture openFlowerSheet; // #4
	TextureRegion[] openFlowerFrames; // #5
	SpriteBatch openFlowerspriteBatch; // #6
	TextureRegion openFlowercurrentFrame; // #7

	Animation openingFlowerAnimation; // #3
	Texture openingFlowerSheet; // #4
	TextureRegion[] openingFlowerFrames; // #5
	SpriteBatch openingFlowerspriteBatch; // #6
	TextureRegion openingFlowercurrentFrame; // #7

	Animation closingFlowerAnimation; // #3
	Texture closingFlowerSheet; // #4
	TextureRegion[] closingFlowerFrames; // #5
	SpriteBatch closingFlowerspriteBatch; // #6
	TextureRegion closingFlowercurrentFrame; // #7

	Animation koiSAnimation; // #3
	Texture koiSSheet; // #4
	TextureRegion[] koiSFrames; // #5
	SpriteBatch koiSspriteBatch; // #6
	TextureRegion koiScurrentFrame; // #7

	Animation koiCAnimation; // #3
	Texture koiCSheet; // #4
	TextureRegion[] koiCFrames; // #5
	SpriteBatch koiCspriteBatch; // #6
	TextureRegion koiCcurrentFrame; // #7
	    
	public HUDitems HUD;

	public LevelController() {
		
	}

}
