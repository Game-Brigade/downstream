package edu.cornell.gdiac.downstream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import edu.cornell.gdiac.util.ScreenListener;

public class LevelSelectMode implements Screen, InputProcessor, ControllerListener {
	
	
	/** Textures necessary to support the level select screen */
	private static final String BACKGROUND = "MENUS/general_background_new.png";
	private static final String BACK_BUTTON = "MENUS/back to main-b.png";
	private static final String LEVEL1_BUTTON = "MENUS/Level Select/lvl 1.png";
	private static final String LEVEL2_BUTTON = "MENUS/Level Select/lvl 2.png";
	private static final String LEVEL3_BUTTON = "MENUS/Level Select/lvl 3.png";
	private static final String LEVEL4_BUTTON = "MENUS/Level Select/lvl 4.png";
	private static final String LEVEL5_BUTTON = "MENUS/Level Select/lvl 5.png";
	private static final String LEVEL6_BUTTON = "MENUS/Level Select/lvl 6.png";
	private static final String LEVEL7_BUTTON = "MENUS/Level Select/lvl 7.png";
	private static final String LEVEL8_BUTTON = "MENUS/Level Select/lvl 8.png";
	
	
	/** Retro font for displaying messages */
	private static String FONT_FILE = "loading/marathon.ttf";
	private static int FONT_SIZE = 150;
	/** The font for giving messages to the player */
	protected BitmapFont displayFont;
	
	private Texture back;
	private Texture level1button;
	private Texture level2button;
	private Texture level3button;
	private Texture level4button;
	private Texture level5button;
	private Texture level6button;
	private Texture level7button;
	private Texture level8button;
	private Texture background;

	/** AssetManager to be loading in the background */
	private AssetManager manager;
	/** Reference to GameCanvas created by the root */
	private GameCanvas canvas;
	/** Listener that will update the player mode when we are done */
	private ScreenListener listener;
	
	/** Whether or not this player mode is still active */
	private boolean active;
	/** The current states of the buttons */
	private int backState;
	private int button1State;
	private int button2State;
	private int button3State;
	private int button4State;
	private int button5State;
	private int button6State;
	private int button7State;
	private int button8State;
	
	
	/** Scaling factor for when the student changes the resolution. */
	private float scale;
	
	/** Standard window size (for scaling) */
	private static int STANDARD_WIDTH  = 800;
	/** Standard window height (for scaling) */
	private static int STANDARD_HEIGHT = 700;
	
	/** Positions of buttons */
	private static Vector2 backPos = new Vector2();
	private static Vector2 button1 = new Vector2();
	private static Vector2 button2 = new Vector2();
	private static Vector2 button3 = new Vector2();
	private static Vector2 button4 = new Vector2();
	private static Vector2 button5 = new Vector2();
	private static Vector2 button6 = new Vector2();
	private static Vector2 button7 = new Vector2();
	private static Vector2 button8 = new Vector2();
	
	

	/**
	 * Creates a LevelSelectMode with the default budget, size and position.
	 *
	 * @param manager
	 *            The AssetManager to load in the background
	 */
	public LevelSelectMode(GameCanvas canvas, AssetManager manager) {
		this.manager = manager;
		this.canvas = canvas;
		
		// Compute the dimensions from the canvas
		resize(canvas.getWidth(),canvas.getHeight());
		
		backPos.set(new Vector2((float)canvas.getWidth()/2,(float)canvas.getHeight()/18*2));
		button1.set(new Vector2((float)canvas.getWidth()/12*3,(float)canvas.getHeight()/12*8));
		button2.set(new Vector2((float)canvas.getWidth()/12*5,(float)canvas.getHeight()/12*8));
		button3.set(new Vector2((float)canvas.getWidth()/12*7,(float)canvas.getHeight()/12*8));
		button4.set(new Vector2((float)canvas.getWidth()/12*9,(float)canvas.getHeight()/12*8));
		button5.set(new Vector2((float)canvas.getWidth()/12*3,(float)canvas.getHeight()/12*5));
		button6.set(new Vector2((float)canvas.getWidth()/12*5,(float)canvas.getHeight()/12*5));
		button7.set(new Vector2((float)canvas.getWidth()/12*7,(float)canvas.getHeight()/12*5));
		button8.set(new Vector2((float)canvas.getWidth()/12*9,(float)canvas.getHeight()/12*5));
		
		// Load the font
		FreetypeFontLoader.FreeTypeFontLoaderParameter size2Params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
		size2Params.fontFileName = FONT_FILE;
		size2Params.fontParameters.size = FONT_SIZE;
		manager.load(FONT_FILE, BitmapFont.class, size2Params);
		if (manager.isLoaded(FONT_FILE)) {
			displayFont = manager.get(FONT_FILE,BitmapFont.class);
			displayFont.setColor(new Color(0,0,0,1));
		} else {
			displayFont = null;
		}
		

		// Load images immediately.
		back = new Texture(BACK_BUTTON);
		level1button = new Texture(LEVEL1_BUTTON);
		level2button = new Texture(LEVEL2_BUTTON);
		level3button = new Texture(LEVEL3_BUTTON);
		level4button = new Texture(LEVEL4_BUTTON);
		level5button = new Texture(LEVEL5_BUTTON);
		level6button = new Texture(LEVEL6_BUTTON);
		level7button = new Texture(LEVEL7_BUTTON);
		level8button = new Texture(LEVEL8_BUTTON);
		background = new Texture(BACKGROUND);
			
		// No progress so far.
		backState = 0;
		button1State = 0;
		button2State = 0;
		button3State = 0;
		button4State = 0;
		button5State = 0;
		button6State = 0;
		button7State = 0;
		button8State = 0;
		
		active = true;
	}
	
	
	/**
	 * Called when this screen should release all resources.
	 */
	public void dispose() {
		level1button.dispose();
		level2button.dispose();
		level3button.dispose();
		level4button.dispose();
		level5button.dispose();
		level6button.dispose();
		level7button.dispose();
		level8button.dispose();
		back.dispose();
		background.dispose();
	}


	/**
	 * Draw the status of this player mode.
	 *
	 * We prefer to separate update and draw from one another as separate
	 * methods, instead of using the single render() method that LibGDX does. We
	 * will talk about why we prefer this in lecture.
	 */
	private void draw() {
		canvas.beginMENU();
		canvas.clear();
		
		canvas.draw(background, Color.WHITE, background.getWidth()/2, background.getHeight()/2, 
				canvas.getWidth()/2, canvas.getHeight()/2, 0, scale, scale);
		canvas.drawText("Level Select", displayFont, canvas.getWidth()/3-15, canvas.getHeight()/8*7);
		canvas.draw(level1button, Color.WHITE, level1button.getWidth()/2, level1button.getHeight()/2, 
				button1.x, button1.y, 0, scale, scale);
		canvas.draw(level2button, Color.WHITE, level2button.getWidth()/2, level2button.getHeight()/2, 
				button2.x, button2.y, 0, scale, scale);
		canvas.draw(level3button, Color.WHITE, level3button.getWidth()/2, level3button.getHeight()/2, 
				button3.x, button3.y, 0, scale, scale);
		canvas.draw(level4button, Color.WHITE, level4button.getWidth()/2, level4button.getHeight()/2, 
				button4.x, button4.y, 0, scale, scale);
		canvas.draw(level5button, Color.WHITE, level5button.getWidth()/2, level5button.getHeight()/2, 
				button5.x, button5.y, 0, scale, scale);
		canvas.draw(level6button, Color.WHITE, level6button.getWidth()/2, level6button.getHeight()/2, 
				button6.x, button6.y, 0, scale, scale);
		canvas.draw(level7button, Color.WHITE, level7button.getWidth()/2, level7button.getHeight()/2, 
				button7.x, button7.y, 0, scale, scale);
		canvas.draw(level8button, Color.WHITE, level8button.getWidth()/2, level8button.getHeight()/2, 
				button8.x, button8.y, 0, scale, scale);
		
		canvas.draw(back, Color.WHITE, back.getWidth()/2, back.getHeight()/2, backPos.x, backPos.y, 0, scale, scale);
		canvas.end();
	}

	// ADDITIONAL SCREEN METHODS
	/**
	 * Called when the Screen should render itself.
	 *
	 * We defer to the other methods update() and draw(). However, it is VERY
	 * important that we only quit AFTER a draw.
	 *
	 * @param delta
	 *            Number of seconds since last animation frame
	 */
	public void render(float delta) {
		if (active) {
			draw();
			
			// We are are ready, notify our listener
			if (goBack() && listener != null) {
				listener.exitScreen(this, WorldController.EXIT_MAIN);
			}
			if (Level1() && listener != null) {
				listener.exitScreen(this, 1);
			}
			if (Level2() && listener != null) {
				listener.exitScreen(this, 2);
			}
			if (Level3() && listener != null) {
				listener.exitScreen(this, 3);
			}
			if (Level4() && listener != null) {
				listener.exitScreen(this, 4);
			}
			/*
			if (Level5() && listener != null) {
				listener.exitScreen(this, 5);
			}
			if (Level6() && listener != null) {
				listener.exitScreen(this, 6);
			}
			if (Level7() && listener != null) {
				listener.exitScreen(this, 7);
			}
			if (Level8() && listener != null) {
				listener.exitScreen(this, 8);
			}
			*/
			
			
		}
	}
	
	/**
	 * Returns true if all assets are loaded and the player is ready to go.
	 *
	 * @return true if the player is ready to go
	 */
	public boolean goBack() {
		return backState == 2;
	}
	
	public boolean Level1() {
		return button1State == 2;
	}
	
	public boolean Level2() {
		return button2State == 2;
	}
	
	public boolean Level3() {
		return button3State == 2;
	}
	
	public boolean Level4() {
		return button4State == 2;
	}
	
	public boolean Level5() {
		return button5State == 2;
	}
	
	public boolean Level6() {
		return button6State == 2;
	}
	
	public boolean Level7() {
		return button7State == 2;
	}
	
	public boolean Level8() {
		return button8State == 2;
	}

	/**
	 * Called when the Screen is resized.
	 *
	 * This can happen at any point during a non-paused state but will never
	 * happen before a call to show().
	 *
	 * @param width
	 *            The new width in pixels
	 * @param height
	 *            The new height in pixels
	 */
	public void resize(int width, int height) {
		// Compute the drawing scale
		float sx = ((float) width) / STANDARD_WIDTH;
		float sy = ((float) height) / STANDARD_HEIGHT;
		scale = (sx < sy ? sx : sy);
	}

	/**
	 * Called when the Screen is paused.
	 * 
	 * This is usually when it's not active or visible on screen. An Application
	 * is also paused before it is destroyed.
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
		
		// Flip to match graphics coordinates
		screenY = canvas.getHeight()-screenY;
		
		float dx = Math.abs(screenX - backPos.x);
		float dy = Math.abs(screenY - backPos.y);
		if (dx < scale*back.getWidth()/2 && dy < scale*back.getHeight()/2) {
			backState = 1;
		}
		
		dx = Math.abs(screenX - button1.x);
		dy = Math.abs(screenY - button1.y);
		if (dx < scale*level1button.getWidth()/2 && dy < scale*level1button.getHeight()/2) {
			button1State = 1;
		}
		
		dx = Math.abs(screenX - button2.x);
		dy = Math.abs(screenY - button2.y);
		if (dx < scale*level2button.getWidth()/2 && dy < scale*level2button.getHeight()/2) {
			button2State = 1;
		}
		
		dx = Math.abs(screenX - button3.x);
		dy = Math.abs(screenY - button3.y);
		if (dx < scale*level3button.getWidth()/2 && dy < scale*level3button.getHeight()/2) {
			button3State = 1;
		}
		
		dx = Math.abs(screenX - button4.x);
		dy = Math.abs(screenY - button4.y);
		if (dx < scale*level4button.getWidth()/2 && dy < scale*level4button.getHeight()/2) {
			button4State = 1;
		}
		
		dx = Math.abs(screenX - button5.x);
		dy = Math.abs(screenY - button5.y);
		if (dx < scale*level5button.getWidth()/2 && dy < scale*level5button.getHeight()/2) {
			button5State = 1;
		}
		
		dx = Math.abs(screenX - button6.x);
		dy = Math.abs(screenY - button6.y);
		if (dx < scale*level6button.getWidth()/2 && dy < scale*level6button.getHeight()/2) {
			button6State = 1;
		}
		
		dx = Math.abs(screenX - button7.x);
		dy = Math.abs(screenY - button7.y);
		if (dx < scale*level7button.getWidth()/2 && dy < scale*level7button.getHeight()/2) {
			button7State = 1;
		}
		
		dx = Math.abs(screenX - button8.x);
		dy = Math.abs(screenY - button8.y);
		if (dx < scale*level8button.getWidth()/2 && dy < scale*level8button.getHeight()/2) {
			button8State = 1;
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
		if (backState == 1 || button1State == 1 || button2State == 1 || button3State == 1 || button4State == 1
				|| button5State == 1 || button6State == 1 || button7State == 1 || button8State == 1) {
			if(backState == 1){
				backState = 2;
				return false;
			}
			if(button1State == 1){
				button1State = 2;
				return false;
			}
			if(button2State == 1){
				button2State = 2;
				return false;
			}
			if(button3State == 1){
				button3State = 2;
				return false;
			}
			if(button4State == 1){
				button4State = 2;
				return false;
			}
			if(button5State == 1){
				button5State = 2;
				return false;
			}
			if(button6State == 1){
				button6State = 2;
				return false;
			}
			if(button7State == 1){
				button7State = 2;
				return false;
			}
			if(button8State == 1){
				button8State = 2;
				return false;
			}
			
		}
		return true;
	}
		
	/**
	 * Called when a button on the Controller was pressed.
	 *
	 * The buttonCode is controller specific. This listener only supports the
	 * start button on an X-Box controller. This outcome of this method is
	 * identical to pressing (but not releasing) the play button.
	 *
	 * @param controller
	 *            The game controller
	 * @param buttonCode
	 *            The button pressed
	 * @return whether to hand the event to other listeners.
	 */
	public boolean buttonDown(Controller controller, int buttonCode) {
		/*
		if (buttonCode == startButton && pressState == 0) {
			pressState = 1;
			return false;
		}
		*/
		return true;
	}
		
	/**
	 * Called when a button on the Controller was released.
	 *
	 * The buttonCode is controller specific. This listener only supports the
	 * start button on an X-Box controller. This outcome of this method is
	 * identical to releasing the the play button after pressing it.
	 *
	 * @param controller
	 *            The game controller
	 * @param buttonCode
	 *            The button pressed
	 * @return whether to hand the event to other listeners.
	 */
	public boolean buttonUp(Controller controller, int buttonCode) {
		/*
		if (pressState == 1 && buttonCode == startButton) {
			pressState = 2;
			return false;
		}
		*/
		return true;
	}
	
	// UNSUPPORTED METHODS FROM InputProcessor

	/**
	 * Called when a key is pressed (UNSUPPORTED)
	 *
	 * @param keycode
	 *            the key pressed
	 * @return whether to hand the event to other listeners.
	 */
	public boolean keyDown(int keycode) {
		return true;
	}

	/**
	 * Called when a key is typed (UNSUPPORTED)
	 *
	 * @param keycode
	 *            the key typed
	 * @return whether to hand the event to other listeners.
	 */
	public boolean keyTyped(char character) {
		return true;
	}

	/**
	 * Called when a key is released.
	 * 
	 * We allow key commands to start the game this time.
	 *
	 * @param keycode
	 *            the key released
	 * @return whether to hand the event to other listeners.
	 */
	public boolean keyUp(int keycode) {
		if (keycode == Input.Keys.N || keycode == Input.Keys.P) {
			backState = 2;
			return false;
		}
		return true;
	}

	/**
	 * Called when the mouse was moved without any buttons being pressed.
	 * (UNSUPPORTED)
	 *
	 * @param screenX
	 *            the x-coordinate of the mouse on the screen
	 * @param screenY
	 *            the y-coordinate of the mouse on the screen
	 * @return whether to hand the event to other listeners.
	 */
	public boolean mouseMoved(int screenX, int screenY) {
		return true;
	}

	/**
	 * Called when the mouse wheel was scrolled. (UNSUPPORTED)
	 *
	 * @param amount
	 *            the amount of scroll from the wheel
	 * @return whether to hand the event to other listeners.
	 */
	public boolean scrolled(int amount) {
		return true;
	}
	
	/** 
	 * Called when the mouse or finger was dragged. (UNSUPPORTED)
	 *
	 * @param screenX the x-coordinate of the mouse on the screen
	 * @param screenY the y-coordinate of the mouse on the screen
	 * @param pointer the button or touch finger number
	 * @return whether to hand the event to other listeners. 
	 */		
	public boolean touchDragged(int screenX, int screenY, int pointer) { 
		return true; 
	}

	@Override
	public void connected(Controller controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnected(Controller controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		// TODO Auto-generated method stub
		return false;
	}


}
