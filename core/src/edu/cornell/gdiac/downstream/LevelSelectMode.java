package edu.cornell.gdiac.downstream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
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
	private static final String BACKGROUND = "Final_Assets/Menus/Level_Select/level select 2nd try.jpg";
	private static final String BACK_BUTTON = "Final_Assets/Menus/back_button.png";
	private static final String BACK_BUTTON_HOVER = "Final_Assets/Menus/back_button_gold.png";
	private static final String LEVEL1_BUTTON = "Final_Assets/Menus/level_select_buttons/1.png";
	private static final String LEVEL2_BUTTON = "Final_Assets/Menus/level_select_buttons/2.png";
	private static final String LEVEL3_BUTTON = "Final_Assets/Menus/level_select_buttons/3.png";
	private static final String LEVEL4_BUTTON = "Final_Assets/Menus/level_select_buttons/4.png";
	private static final String LEVEL5_BUTTON = "Final_Assets/Menus/level_select_buttons/5.png";
	private static final String LEVEL6_BUTTON = "Final_Assets/Menus/level_select_buttons/6.png";
	private static final String LEVEL7_BUTTON = "Final_Assets/Menus/level_select_buttons/7.png";
	private static final String LEVEL8_BUTTON = "Final_Assets/Menus/level_select_buttons/8.png";
	private static final String LEVEL9_BUTTON = "Final_Assets/Menus/level_select_buttons/9.png";
	private static final String LEVEL10_BUTTON = "Final_Assets/Menus/level_select_buttons/10.png";
	private static final String LEVEL11_BUTTON = "Final_Assets/Menus/level_select_buttons/11.png";
	private static final String LEVEL12_BUTTON = "Final_Assets/Menus/level_select_buttons/12.png";
	private static final String LEVEL13_BUTTON = "Final_Assets/Menus/level_select_buttons/13.png";
	private static final String LEVEL14_BUTTON = "Final_Assets/Menus/level_select_buttons/14.png";
	private static final String LEVEL15_BUTTON = "Final_Assets/Menus/level_select_buttons/15.png";
	//private static final String LEVEL16_BUTTON = "Final_Assets/Menus/level_select_buttons/16.png";
	private static final String LEVEL_BUTTON_HOVER = "Final_Assets/Menus/level_select_buttons/ring.png";;
	
	private static final String MENU_CLICK_SOUND = "Final_Assets/Sounds/menu_click.mp3";
	private Music clickSound;
	
	private Texture background;
	/** Back to main menu button */
	private Texture backButton;
	private Texture backButtonHover;
	/** Level buttons */
	private Texture level1button;
	private Texture level2button;
	private Texture level3button;
	private Texture level4button;
	private Texture level5button;
	private Texture level6button;
	private Texture level7button;
	private Texture level8button;
	private Texture level9button;
	private Texture level10button;
	private Texture level11button;
	private Texture level12button;
	private Texture level13button;
	private Texture level14button;
	private Texture level15button;
	//private Texture level16button;
	private Texture levelButtonHover;

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
	private int button9State;
	private int button10State;
	private int button11State;
	private int button12State;
	private int button13State;
	private int button14State;
	private int button15State;
	//private int button16State;
	
	private boolean backHover;
	private boolean button1Hover;
	private boolean button2Hover;
	private boolean button3Hover;
	private boolean button4Hover;
	private boolean button5Hover;
	private boolean button6Hover;
	private boolean button7Hover;
	private boolean button8Hover;
	private boolean button9Hover;
	private boolean button10Hover;
	private boolean button11Hover;
	private boolean button12Hover;
	private boolean button13Hover;
	private boolean button14Hover;
	private boolean button15Hover;
	//private boolean button16Hover;
	
	
	/** Scaling factor for when the student changes the resolution. */
	private float scale;
	private float buttonScale;
	private float backScale;
	
	/** Standard window size (for scaling) */
	private static int STANDARD_WIDTH  = 800;
	/** Standard window height (for scaling) */
	private static int STANDARD_HEIGHT = 700;
	
	/** Positions of buttons */
	private static Vector2 bgPos = new Vector2();
	private static Vector2 backPos = new Vector2();
	private static Vector2 button1 = new Vector2();
	private static Vector2 button2 = new Vector2();
	private static Vector2 button3 = new Vector2();
	private static Vector2 button4 = new Vector2();
	private static Vector2 button5 = new Vector2();
	private static Vector2 button6 = new Vector2();
	private static Vector2 button7 = new Vector2();
	private static Vector2 button8 = new Vector2();
	private static Vector2 button9 = new Vector2();
	private static Vector2 button10 = new Vector2();
	private static Vector2 button11 = new Vector2();
	private static Vector2 button12 = new Vector2();
	private static Vector2 button13 = new Vector2();
	private static Vector2 button14 = new Vector2();
	private static Vector2 button15 = new Vector2();
	//private static Vector2 button16 = new Vector2();
	
	

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
		buttonScale = scale*.2f;
		backScale = scale*.5f;
		
		//screen positions of the buttons
		bgPos.set(new Vector2((float)canvas.getWidth()/2,(float)canvas.getHeight()/2));
		backPos.set(new Vector2((float)canvas.getWidth()/8*7,(float)canvas.getHeight()/18*17));
		button1.set(new Vector2((float)canvas.getWidth()/16*2+10,(float)canvas.getHeight()/12*5+20));
		button2.set(new Vector2((float)canvas.getWidth()/16*4-30,(float)canvas.getHeight()/12*5+45));
		button3.set(new Vector2((float)canvas.getWidth()/16*5,(float)canvas.getHeight()/12*7+10));
		button4.set(new Vector2((float)canvas.getWidth()/16*7-60,(float)canvas.getHeight()/12*9));
		button5.set(new Vector2((float)canvas.getWidth()/16*8-10,(float)canvas.getHeight()/12*10+10));
		button6.set(new Vector2((float)canvas.getWidth()/16*9-30,(float)canvas.getHeight()/12*9-30));
		button7.set(new Vector2((float)canvas.getWidth()/16*8-5,(float)canvas.getHeight()/12*6+10));
		button8.set(new Vector2((float)canvas.getWidth()/16*7+20,(float)canvas.getHeight()/12*5-45));
		button9.set(new Vector2((float)canvas.getWidth()/16*7+40,(float)canvas.getHeight()/12*2));
		button10.set(new Vector2((float)canvas.getWidth()/16*9,(float)canvas.getHeight()/12+10));
		button11.set(new Vector2((float)canvas.getWidth()/16*10+10,(float)canvas.getHeight()/12*2+15));
		button12.set(new Vector2((float)canvas.getWidth()/16*11-10,(float)canvas.getHeight()/12*5-45));
		button13.set(new Vector2((float)canvas.getWidth()/16*12,(float)canvas.getHeight()/12*6+10));
		button14.set(new Vector2((float)canvas.getWidth()/16*14-20,(float)canvas.getHeight()/12*6+35));
		button15.set(new Vector2((float)canvas.getWidth()/16*15+20,(float)canvas.getHeight()/12*6+25));
		//button16.set(new Vector2((float)canvas.getWidth()/16,(float)canvas.getHeight()/12*5));
		

		// Load images immediately
		background = new Texture(BACKGROUND);
		backButton = new Texture(BACK_BUTTON);
		backButtonHover = new Texture(BACK_BUTTON_HOVER);
		level1button = new Texture(LEVEL1_BUTTON);
		level2button = new Texture(LEVEL2_BUTTON);
		level3button = new Texture(LEVEL3_BUTTON);
		level4button = new Texture(LEVEL4_BUTTON);
		level5button = new Texture(LEVEL5_BUTTON);
		level6button = new Texture(LEVEL6_BUTTON);
		level7button = new Texture(LEVEL7_BUTTON);
		level8button = new Texture(LEVEL8_BUTTON);
		level9button = new Texture(LEVEL9_BUTTON);
		level10button = new Texture(LEVEL10_BUTTON);
		level11button = new Texture(LEVEL11_BUTTON);
		level12button = new Texture(LEVEL12_BUTTON);
		level13button = new Texture(LEVEL13_BUTTON);
		level14button = new Texture(LEVEL14_BUTTON);
		level15button = new Texture(LEVEL15_BUTTON);
		//level16button = new Texture(LEVEL16_BUTTON);
		levelButtonHover = new Texture(LEVEL_BUTTON_HOVER);
		
		clickSound = Gdx.audio.newMusic(Gdx.files.internal(MENU_CLICK_SOUND));
		clickSound.setLooping(false);

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
		button9State = 0;
		button10State = 0;
		button11State = 0;
		button12State = 0;
		button13State = 0;
		button14State = 0;
		button15State = 0;
		//button16State = 0;
		
		button1Hover = false;
		button2Hover = false;
		button3Hover = false;
		button4Hover = false;
		button5Hover = false;
		button6Hover = false;
		button7Hover = false;
		button8Hover = false;
		button9Hover = false;
		button10Hover = false;
		button11Hover = false;
		button12Hover = false;
		button13Hover = false;
		button14Hover = false;
		button15Hover = false;
		//button16Hover = false;
		
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
		level9button.dispose();
		level10button.dispose();
		level11button.dispose();
		level12button.dispose();
		level13button.dispose();
		level14button.dispose();
		level15button.dispose();
		//level16button.dispose();
		backButton.dispose();
		backButtonHover.dispose();
		levelButtonHover.dispose();
		background.dispose();
		clickSound.dispose();
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
		
		
		//draw bg and back button
		canvas.draw(background, Color.WHITE, background.getWidth()/2, background.getHeight()/2, 
				canvas.getWidth()/2, canvas.getHeight()/2, 0, scale, scale);
		canvas.draw(backButton, Color.WHITE, backButton.getWidth()/2, backButton.getHeight()/2, backPos.x, backPos.y, 0, backScale, backScale);
		if(backHover == true){
			canvas.draw(backButtonHover, Color.WHITE, backButtonHover.getWidth()/2, backButtonHover.getHeight()/2, backPos.x, backPos.y, 0, backScale, backScale);
		}
		
		// draw the level numbers
		
		//button1
		canvas.draw(level1button, Color.WHITE, level1button.getWidth() / 2, level1button.getHeight() / 2, button1.x,
				button1.y, 0, buttonScale, buttonScale);
		if (button1Hover == true) {
			canvas.draw(levelButtonHover, Color.WHITE, levelButtonHover.getWidth() / 2, levelButtonHover.getHeight() / 2, button1.x,
					button1.y, 0, buttonScale, buttonScale);
		}
		//button2	
		canvas.draw(level2button, Color.WHITE, level2button.getWidth() / 2, level2button.getHeight() / 2, button2.x,
				button2.y, 0, buttonScale, buttonScale);
		if (button2Hover == true) {
			canvas.draw(levelButtonHover, Color.WHITE, levelButtonHover.getWidth() / 2, levelButtonHover.getHeight() / 2, button2.x,
					button2.y, 0, buttonScale, buttonScale);
		}
		//button3
		canvas.draw(level3button, Color.WHITE, level3button.getWidth() / 2, level3button.getHeight() / 2, button3.x,
				button3.y, 0, buttonScale, buttonScale);
		if (button3Hover == true) {
			canvas.draw(levelButtonHover, Color.WHITE, levelButtonHover.getWidth() / 2, levelButtonHover.getHeight() / 2, button3.x,
					button3.y, 0, buttonScale, buttonScale);
		}
		//button4
		canvas.draw(level4button, Color.WHITE, level4button.getWidth() / 2, level4button.getHeight() / 2, button4.x,
				button4.y, 0, buttonScale, buttonScale);
		if (button4Hover == true) {
			canvas.draw(levelButtonHover, Color.WHITE, levelButtonHover.getWidth() / 2, levelButtonHover.getHeight() / 2, button4.x,
					button4.y, 0, buttonScale, buttonScale);
		}
		//button5
		canvas.draw(level5button, Color.WHITE, level5button.getWidth() / 2, level5button.getHeight() / 2, button5.x,
				button5.y, 0, buttonScale, buttonScale);
		if (button5Hover == true) {
			canvas.draw(levelButtonHover, Color.WHITE, levelButtonHover.getWidth() / 2, levelButtonHover.getHeight() / 2, button5.x,
					button5.y, 0, buttonScale, buttonScale);
		}
		//button6
		canvas.draw(level6button, Color.WHITE, level6button.getWidth() / 2, level6button.getHeight() / 2, button6.x,
				button6.y, 0, buttonScale, buttonScale);
		if (button6Hover == true) {
			canvas.draw(levelButtonHover, Color.WHITE, levelButtonHover.getWidth() / 2, levelButtonHover.getHeight() / 2, button6.x,
					button6.y, 0, buttonScale, buttonScale);
		}
		//button7
		canvas.draw(level7button, Color.WHITE, level7button.getWidth() / 2, level7button.getHeight() / 2, button7.x,
				button7.y, 0, buttonScale, buttonScale);
		if (button7Hover == true) {
			canvas.draw(levelButtonHover, Color.WHITE, levelButtonHover.getWidth() / 2, levelButtonHover.getHeight() / 2, button7.x,
					button7.y, 0, buttonScale, buttonScale);
		}
		//button8
		canvas.draw(level8button, Color.WHITE, level8button.getWidth() / 2, level8button.getHeight() / 2, button8.x,
				button8.y, 0, buttonScale, buttonScale);
		if (button8Hover == true) {
			canvas.draw(levelButtonHover, Color.WHITE, levelButtonHover.getWidth() / 2, levelButtonHover.getHeight() / 2, button8.x,
					button8.y, 0, buttonScale, buttonScale);
		}
		//button9
		canvas.draw(level9button, Color.WHITE, level9button.getWidth() / 2, level9button.getHeight() / 2, button9.x,
				button9.y, 0, buttonScale, buttonScale);
		if (button9Hover == true) {
			canvas.draw(levelButtonHover, Color.WHITE, levelButtonHover.getWidth() / 2, levelButtonHover.getHeight() / 2, button9.x,
					button9.y, 0, buttonScale, buttonScale);
		}
		//button10
		canvas.draw(level10button, Color.WHITE, level10button.getWidth() / 2, level10button.getHeight() / 2, button10.x,
				button10.y, 0, buttonScale, buttonScale);
		if (button10Hover == true) {
			canvas.draw(levelButtonHover, Color.WHITE, levelButtonHover.getWidth() / 2, levelButtonHover.getHeight() / 2, button10.x,
					button10.y, 0, buttonScale, buttonScale);
		}
		//button11
		canvas.draw(level11button, Color.WHITE, level11button.getWidth() / 2, level11button.getHeight() / 2, button11.x,
				button11.y, 0, buttonScale, buttonScale);
		if (button11Hover == true) {
			canvas.draw(levelButtonHover, Color.WHITE, levelButtonHover.getWidth() / 2, levelButtonHover.getHeight() / 2, button11.x,
					button11.y, 0, buttonScale, buttonScale);
		}
		//button12
		canvas.draw(level12button, Color.WHITE, level12button.getWidth() / 2, level12button.getHeight() / 2, button12.x,
				button12.y, 0, buttonScale, buttonScale);
		if (button12Hover == true) {
			canvas.draw(levelButtonHover, Color.WHITE, levelButtonHover.getWidth() / 2, levelButtonHover.getHeight() / 2, button12.x,
					button12.y, 0, buttonScale, buttonScale);
		}
		//button13
		canvas.draw(level13button, Color.WHITE, level13button.getWidth() / 2, level13button.getHeight() / 2, button13.x,
				button13.y, 0, buttonScale, buttonScale);
		if (button13Hover == true) {
			canvas.draw(levelButtonHover, Color.WHITE, levelButtonHover.getWidth() / 2, levelButtonHover.getHeight() / 2, button13.x,
					button13.y, 0, buttonScale, buttonScale);
		}
		//button14
		canvas.draw(level14button, Color.WHITE, level14button.getWidth() / 2, level14button.getHeight() / 2, button14.x,
				button14.y, 0, buttonScale, buttonScale);
		if (button14Hover == true) {
			canvas.draw(levelButtonHover, Color.WHITE, levelButtonHover.getWidth() / 2, levelButtonHover.getHeight() / 2, button14.x,
					button14.y, 0, buttonScale, buttonScale);
		}
		//button15
		canvas.draw(level15button, Color.WHITE, level15button.getWidth() / 2, level15button.getHeight() / 2, button15.x,
				button15.y, 0, buttonScale, buttonScale);
		if (button15Hover == true) {
			canvas.draw(levelButtonHover, Color.WHITE, levelButtonHover.getWidth() / 2, levelButtonHover.getHeight() / 2, button15.x,
					button15.y, 0, buttonScale, buttonScale);
		}
		//button16
//		canvas.draw(level16button, Color.WHITE, level16button.getWidth() / 2, level16button.getHeight() / 2, button16.x,
//				button16.y, 0, scale, scale);
//		if (button16Hover == true) {
//			canvas.draw(levelButtonHover, Color.WHITE, levelButtonHover.getWidth() / 2, levelButtonHover.getHeight() / 2, button16.x,
//					button16.y, 0, scale, scale);
//		}

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
			
			//Change screens if buttons were pressed
			if (goBack() && listener != null) {
				clickSound.play();
				listener.exitScreen(this, WorldController.EXIT_MAIN);
			}
			
			if (b1() && listener != null) {
				clickSound.play();
				listener.exitScreen(this, 1);
			}
			if (b2() && listener != null) {
				clickSound.play();
				listener.exitScreen(this, 2);
			}
			if (b3() && listener != null) {
				clickSound.play();
				listener.exitScreen(this, 3);
			}
			if (b4() && listener != null) {
				clickSound.play();
				listener.exitScreen(this, 4);
			}
			if (b5() && listener != null) {
				clickSound.play();
				listener.exitScreen(this, 5);
			}
			if (b6() && listener != null) {
				clickSound.play();
				listener.exitScreen(this, 6);
			}
			if (b7() && listener != null) {
				clickSound.play();
				listener.exitScreen(this, 7);
			}
			if (b8() && listener != null) {
				clickSound.play();
				listener.exitScreen(this, 8);
			}
			if (b9() && listener != null) {
				clickSound.play();
				listener.exitScreen(this, 9);
			}
			if (b10() && listener != null) {
				clickSound.play();
				listener.exitScreen(this, 10);
			}
			if (b11() && listener != null) {
				clickSound.play();
				listener.exitScreen(this, 11);
			}
			if (b12() && listener != null) {
				clickSound.play();
				listener.exitScreen(this, 12);
			}
			if (b13() && listener != null) {
				clickSound.play();
				listener.exitScreen(this, 13);
			}
			if (b14() && listener != null) {
				clickSound.play();
				listener.exitScreen(this, 14);
			}
			if (b15() && listener != null) {
				clickSound.play();
				listener.exitScreen(this, 15);
			}
//			if (b16() && listener != null) {
//				listener.exitScreen(this, 16);
//			}
	
			
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
	
	public boolean b1() {
		return button1State == 2;
	}
	
	public boolean b2() {
		return button2State == 2;
	}
	
	public boolean b3() {
		return button3State == 2;
	}
	
	public boolean b4() {
		return button4State == 2;
	}
	
	public boolean b5() {
		return button5State == 2;
	}
	
	public boolean b6() {
		return button6State == 2;
	}
	
	public boolean b7() {
		return button7State == 2;
	}
	
	public boolean b8() {
		return button8State == 2;
	}
	
	public boolean b9() {
		return button9State == 2;
	}
	
	public boolean b10() {
		return button10State == 2;
	}
	
	public boolean b11() {
		return button11State == 2;
	}
	
	public boolean b12() {
		return button12State == 2;
	}
	
	public boolean b13() {
		return button13State == 2;
	}
	
	public boolean b14() {
		return button14State == 2;
	}
	
	public boolean b15() {
		return button15State == 2;
	}
	
//	public boolean b16() {
//		return button16State == 2;
//	}

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
		
		//back button
		float dx = Math.abs(screenX - backPos.x);
		float dy = Math.abs(screenY - backPos.y);
		if (dx < backScale*backButton.getWidth()/2 && dy < backScale*backButton.getHeight()/2) {
			backState = 1;
		}
		//button1
		dx = Math.abs(screenX - button1.x);
		dy = Math.abs(screenY - button1.y);
		if (dx < buttonScale*level1button.getWidth()/2 && dy < buttonScale*level1button.getHeight()/2) {
			button1State = 1;
		}
		//button2
		dx = Math.abs(screenX - button2.x);
		dy = Math.abs(screenY - button2.y);
		if (dx < buttonScale*level2button.getWidth()/2 && dy < buttonScale*level2button.getHeight()/2) {
			button2State = 1;
		}
		//button3
		dx = Math.abs(screenX - button3.x);
		dy = Math.abs(screenY - button3.y);
		if (dx < buttonScale*level3button.getWidth()/2 && dy < buttonScale*level3button.getHeight()/2) {
			button3State = 1;
		}
		//button4
		dx = Math.abs(screenX - button4.x);
		dy = Math.abs(screenY - button4.y);
		if (dx < buttonScale*level4button.getWidth()/2 && dy < buttonScale*level4button.getHeight()/2) {
			button4State = 1;
		}
		//button5
		dx = Math.abs(screenX - button5.x);
		dy = Math.abs(screenY - button5.y);
		if (dx < buttonScale*level5button.getWidth()/2 && dy < buttonScale*level5button.getHeight()/2) {
			button5State = 1;
		}
		//button6
		dx = Math.abs(screenX - button6.x);
		dy = Math.abs(screenY - button6.y);
		if (dx < buttonScale*level6button.getWidth()/2 && dy < buttonScale*level6button.getHeight()/2) {
			button6State = 1;
		}
		//button7
		dx = Math.abs(screenX - button7.x);
		dy = Math.abs(screenY - button7.y);
		if (dx < buttonScale*level7button.getWidth()/2 && dy < buttonScale*level7button.getHeight()/2) {
			button7State = 1;
		}
		//button8
		dx = Math.abs(screenX - button8.x);
		dy = Math.abs(screenY - button8.y);
		if (dx < buttonScale*level8button.getWidth()/2 && dy < buttonScale*level8button.getHeight()/2) {
			button8State = 1;
		}
		//button9
		dx = Math.abs(screenX - button9.x);
		dy = Math.abs(screenY - button9.y);
		if (dx < buttonScale*level9button.getWidth()/2 && dy < buttonScale*level9button.getHeight()/2) {
			button9State = 1;
		}
		//button10
		dx = Math.abs(screenX - button10.x);
		dy = Math.abs(screenY - button10.y);
		if (dx < buttonScale*level10button.getWidth()/2 && dy < buttonScale*level10button.getHeight()/2) {
			button10State = 1;
		}
		//button11
		dx = Math.abs(screenX - button11.x);
		dy = Math.abs(screenY - button11.y);
		if (dx < buttonScale*level11button.getWidth()/2 && dy < buttonScale*level11button.getHeight()/2) {
			button11State = 1;
		}
		//button12
		dx = Math.abs(screenX - button12.x);
		dy = Math.abs(screenY - button12.y);
		if (dx < buttonScale*level12button.getWidth()/2 && dy < buttonScale*level12button.getHeight()/2) {
			button12State = 1;
		}
		//button13
		dx = Math.abs(screenX - button13.x);
		dy = Math.abs(screenY - button13.y);
		if (dx < buttonScale*level13button.getWidth()/2 && dy < buttonScale*level13button.getHeight()/2) {
			button13State = 1;
		}
		//button14
		dx = Math.abs(screenX - button14.x);
		dy = Math.abs(screenY - button14.y);
		if (dx < buttonScale*level14button.getWidth()/2 && dy < buttonScale*level14button.getHeight()/2) {
			button14State = 1;
		}
		//button15
		dx = Math.abs(screenX - button15.x);
		dy = Math.abs(screenY - button15.y);
		if (dx < buttonScale*level15button.getWidth()/2 && dy < buttonScale*level15button.getHeight()/2) {
			button15State = 1;
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
				|| button5State == 1 || button6State == 1 || button7State == 1 || button8State == 1 || button9State == 1
				|| button10State == 1 || button11State == 1 || button12State == 1 || button13State == 1
				|| button14State == 1 || button15State == 1) {
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
			if(button9State == 1){
				button9State = 2;
				return false;
			}
			if(button10State == 1){
				button10State = 2;
				return false;
			}
			if(button11State == 1){
				button11State = 2;
				return false;
			}
			if(button12State == 1){
				button12State = 2;
				return false;
			}
			if(button13State == 1){
				button13State = 2;
				return false;
			}
			if(button14State == 1){
				button14State = 2;
				return false;
			}
			if(button15State == 1){
				button15State = 2;
				return false;
			}
//			if(button16State == 1){
//				button16State = 2;
//				return false;
//			}
			
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
		// Flip to match graphics coordinates
		screenY = canvas.getHeight() - screenY;
		
		//back button
		float dx = Math.abs(screenX - backPos.x);
		float dy = Math.abs(screenY - backPos.y);
		if (dx < backScale * backButton.getWidth() / 2 && dy < backScale * backButton.getHeight() / 2) {
			backHover = true;
		}
		else{
			backHover = false;
		}
		//button1
		dx = Math.abs(screenX - button1.x);
		dy = Math.abs(screenY - button1.y);
		if (dx < buttonScale * level1button.getWidth() / 2 && dy < buttonScale * level1button.getHeight() / 2) {
			button1Hover = true;
		}
		else{
			button1Hover = false;
		}
		// button2
		dx = Math.abs(screenX - button2.x);
		dy = Math.abs(screenY - button2.y);
		if (dx < buttonScale * level2button.getWidth() / 2 && dy < buttonScale * level2button.getHeight() / 2) {
			button2Hover = true;
		} 
		else {
			button2Hover = false;
		}
		//button3
		dx = Math.abs(screenX - button3.x);
		dy = Math.abs(screenY - button3.y);
		if (dx < buttonScale * level3button.getWidth() / 2 && dy < buttonScale * level3button.getHeight() / 2) {
			button3Hover = true;
		} 
		else {
			button3Hover = false;
		}
		//button4
		dx = Math.abs(screenX - button4.x);
		dy = Math.abs(screenY - button4.y);
		if (dx < buttonScale * level4button.getWidth() / 2 && dy < buttonScale * level4button.getHeight() / 2) {
			button4Hover = true;
		} 
		else {
			button4Hover = false;
		}
		//button5
		dx = Math.abs(screenX - button5.x);
		dy = Math.abs(screenY - button5.y);
		if (dx < buttonScale * level5button.getWidth() / 2 && dy < buttonScale * level5button.getHeight() / 2) {
			button5Hover = true;
		} 
		else {
			button5Hover = false;
		}
		// button6
		dx = Math.abs(screenX - button6.x);
		dy = Math.abs(screenY - button6.y);
		if (dx < buttonScale * level6button.getWidth() / 2 && dy < buttonScale * level6button.getHeight() / 2) {
			button6Hover = true;
		} 
		else {
			button6Hover = false;
		}
		// button7
		dx = Math.abs(screenX - button7.x);
		dy = Math.abs(screenY - button7.y);
		if (dx < buttonScale * level7button.getWidth() / 2 && dy < buttonScale * level7button.getHeight() / 2) {
			button7Hover = true;
		} 
		else {
			button7Hover = false;
		}
		// button8
		dx = Math.abs(screenX - button8.x);
		dy = Math.abs(screenY - button8.y);
		if (dx < buttonScale * level8button.getWidth() / 2 && dy < buttonScale * level8button.getHeight() / 2) {
			button8Hover = true;
		} 
		else {
			button8Hover = false;
		}
		// button9
		dx = Math.abs(screenX - button9.x);
		dy = Math.abs(screenY - button9.y);
		if (dx < buttonScale * level9button.getWidth() / 2 && dy < buttonScale * level9button.getHeight() / 2) {
			button9Hover = true;
		} 
		else {
			button9Hover = false;
		}
		// button10
		dx = Math.abs(screenX - button10.x);
		dy = Math.abs(screenY - button10.y);
		if (dx < buttonScale * level10button.getWidth() / 2 && dy < buttonScale * level10button.getHeight() / 2) {
			button10Hover = true;
		} 
		else {
			button10Hover = false;
		}
		// button11
		dx = Math.abs(screenX - button11.x);
		dy = Math.abs(screenY - button11.y);
		if (dx < buttonScale * level11button.getWidth() / 2 && dy < buttonScale * level11button.getHeight() / 2) {
			button11Hover = true;
		} 
		else {
			button11Hover = false;
		}
		// button12
		dx = Math.abs(screenX - button12.x);
		dy = Math.abs(screenY - button12.y);
		if (dx < buttonScale * level12button.getWidth() / 2 && dy < buttonScale * level12button.getHeight() / 2) {
			button12Hover = true;
		} else {
			button12Hover = false;
		}
		// button13
		dx = Math.abs(screenX - button13.x);
		dy = Math.abs(screenY - button13.y);
		if (dx < buttonScale * level13button.getWidth() / 2 && dy < buttonScale * level13button.getHeight() / 2) {
			button13Hover = true;
		} 
		else {
			button13Hover = false;
		}
		// button14
		dx = Math.abs(screenX - button14.x);
		dy = Math.abs(screenY - button14.y);
		if (dx < buttonScale * level14button.getWidth() / 2 && dy < buttonScale * level14button.getHeight() / 2) {
			button14Hover = true;
		} else {
			button14Hover = false;
		}
		// button15
		dx = Math.abs(screenX - button15.x);
		dy = Math.abs(screenY - button15.y);
		if (dx < buttonScale * level15button.getWidth() / 2 && dy < buttonScale * level15button.getHeight() / 2) {
			button15Hover = true;
		} 
		else {
			button15Hover = false;
		}
		
		
		
		
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
