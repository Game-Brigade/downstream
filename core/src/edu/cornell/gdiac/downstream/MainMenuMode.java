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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import edu.cornell.gdiac.util.ScreenListener;

public class MainMenuMode implements Screen, InputProcessor, ControllerListener {
	
	
	/** Textures necessary to support the main menu screen */
	private static final String LOGO_FILE = "Final_Assets/downstream_logo.png";
	private static final String PLAY_FILE = "Final_Assets/Menus/play.png";
	private static final String SELECT_FILE = "Final_Assets/Menus/levelselect.png";
	private static final String EDIT_FILE = "Final_Assets/Menus/levelbuild.png";
	private static final String BACKGROUND_FILE = "Final_Assets/Beta Art Assets/Textures(JPGs)/homescreen.png"; //britt will provide new bg
	
	/** Logo texture */
	private Texture logo;
	/** Play button */
	private Texture play;
	/** Select texture */
	private Texture select;
	/** Edit texture */
	private Texture edit;
	/** Background texture */
	private Texture background;
	
	
	/** AssetManager to be loading in the background */
	private AssetManager manager;
	/** Reference to GameCanvas created by the root */
	private GameCanvas canvas;
	/** Listener that will update the player mode when we are done */
	private ScreenListener listener;
	
	/** Whether or not this player mode is still active */
	private boolean active;
	/** The current state of the play button */
	private int playState;
	/** The current state of the select button */
	private int selectState;
	/** The current state of the edit button */
	private int editState;
	/** Scaling factor for when the student changes the resolution. */
	private float scale;
	
	/** Standard window size (for scaling) */
	private static int STANDARD_WIDTH  = 800;
	/** Standard window height (for scaling) */
	private static int STANDARD_HEIGHT = 700;
	
	/** Positions of buttons */
	private static Vector2 logoPos = new Vector2();
	private static Vector2 playPos = new Vector2();
	private static Vector2 editPos = new Vector2();
	private static Vector2 selectPos = new Vector2();
	private static Vector2 backPos = new Vector2();
	
	
	

	/**
	 * Creates a LoadingMode with the default budget, size and position.
	 *
	 * @param manager
	 *            The AssetManager to load in the background
	 */
	public MainMenuMode(GameCanvas canvas, AssetManager manager) {
		this.manager = manager;
		this.canvas = canvas;
		
		// Compute the dimensions from the canvas
		resize(canvas.getWidth(),canvas.getHeight());
		
		logoPos.set(new Vector2((float)canvas.getWidth()/2,(float)canvas.getHeight()/2+50));
		playPos.set(new Vector2((float)canvas.getWidth()/3-70,(float)canvas.getHeight()/5-20));
		selectPos.set(new Vector2((float)canvas.getWidth()/3*2+70,(float)canvas.getHeight()/5-20));
		editPos.set(new Vector2((float)canvas.getWidth()/8*7,(float)canvas.getHeight()/8*7));
		backPos.set(new Vector2((float)canvas.getWidth()/2,(float)canvas.getHeight()/2));

		// Load images immediately.
		logo = new Texture(LOGO_FILE);
		play = new Texture(PLAY_FILE);
		edit = new Texture(EDIT_FILE);
		select = new Texture(SELECT_FILE);
		background = new Texture(BACKGROUND_FILE);
			
		// No progress so far.
		playState = 0;
		selectState = 0;
		editState = 0;
		active = false;
		

		active = true;
	}
	
	
	/**
	 * Called when this screen should release all resources.
	 */
	public void dispose() {
		logo.dispose();
		edit.dispose();
		select.dispose();
		play.dispose();
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
		canvas.draw(background, Color.WHITE, background.getWidth()/2, background.getHeight()/2, backPos.x, backPos.y, 0, scale, scale);
		canvas.draw(logo, Color.WHITE, logo.getWidth()/2, logo.getHeight()/2, logoPos.x, logoPos.y, 0, scale*0.8f, scale*0.8f);
		canvas.draw(play, Color.WHITE, play.getWidth()/2, play.getHeight()/2, playPos.x, playPos.y, 0, scale, scale);
		canvas.draw(select, Color.WHITE, select.getWidth()/2, select.getHeight()/2, selectPos.x, selectPos.y, 0, scale, scale);
		canvas.draw(edit, Color.WHITE, edit.getWidth()/2, edit.getHeight()/2, editPos.x, editPos.y, 0, scale*0.9f, scale*0.9f);
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
			if (startPlay() && listener != null) {
				listener.exitScreen(this, 1);
			}
			if (startSelect() && listener != null){
				listener.exitScreen(this, WorldController.EXIT_SELECT);
			}
			if (startEdit() && listener != null){
				listener.exitScreen(this, WorldController.EXIT_EDIT);
			}
		}
	}
	
	/**
	 * Returns true if all assets are loaded and the player is ready to go.
	 *
	 * @return true if the player is ready to go
	 */
	public boolean startPlay() {
		return playState == 2;
	}
	
	public boolean startSelect(){
		return selectState == 2;
	}
	
	public boolean startEdit(){
		return editState == 2;
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
		float dx = Math.abs(screenX - playPos.x);
		float dy = Math.abs(screenY - playPos.y);
		
		if (dx < scale*play.getWidth()/2 && dy < scale*play.getHeight()/2) {
			playState = 1;
		}
		
		dx = Math.abs(screenX - selectPos.x);
		dy = Math.abs(screenY - selectPos.y);
		if (dx < scale*select.getWidth()/2 && dy < scale*select.getHeight()/2) {
			selectState = 1;
		}
		
		dx = Math.abs(screenX - editPos.x);
		dy = Math.abs(screenY - editPos.y);
		if (dx < scale*edit.getWidth()/2 && dy < scale*edit.getHeight()/2) {
			editState = 1;
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
		if (playState == 1 || editState == 1 || selectState == 1) {
			if(playState == 1){
				playState = 2;
				return false;
			}
			if(editState == 1){
				editState = 2;
				return false;
			}
			if(selectState == 1){
				selectState = 2;
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
			playState = 2;
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
