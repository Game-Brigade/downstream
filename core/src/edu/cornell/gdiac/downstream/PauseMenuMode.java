package edu.cornell.gdiac.downstream;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import edu.cornell.gdiac.util.ScreenListener;

public class PauseMenuMode{
	
	/** Textures for buttons */
	private static final String PAUSE_HEADER = "MENUS/Paused/paused-H.png";
	private static final String RESUME_BUTTON = "Final_Assets/Menus/resume.png";
	private static final String RESUME_HOVER_BUTTON = "Final_Assets/Menus/resume_h.png";
	private static final String RESTART_BUTTON = "Final_Assets/Menus/restart.png";
	private static final String RESTART_HOVER_BUTTON = "Final_Assets/Menus/restart_h.png";
	private static final String BACK_BUTTON = "Final_Assets/Menus/back.png";
	private static final String BACK_HOVER_BUTTON = "Final_Assets/Menus/back_h.png";
	private static final String BACKGROUND_FILE = "MENUS/general_background_new.png";
	
	public Texture pauseHeader;
	public Texture resume;
	public Texture resumeHoverButton;
	public Texture restart;
	public Texture restartHoverButton;
	public Texture back;
	public Texture backHoverButton;
	public Texture background;
	
	private Color alphaSet = new Color(255, 255, 255, .1f);

	private boolean resumeHover;
	private boolean restartHover;
	private boolean backHover;
	
	/** Reference to GameCanvas created by the root */
	public GameCanvas canvas;
	
	/** Scaling factor for when the student changes the resolution. */
	public float scale;
	
	/** Standard window size (for scaling) */
	private static int STANDARD_WIDTH  = 800;
	/** Standard window height (for scaling) */
	private static int STANDARD_HEIGHT = 700;
	
	/** Positions of buttons */
	public static Vector2 headerPos = new Vector2();
	public static Vector2 resumePos = new Vector2();
	public static Vector2 restartPos = new Vector2();
	public static Vector2 backPos = new Vector2();
	public static Vector2 backgroundPos = new Vector2();

	/**
	 * Creates a PauseMenuMode with the default budget, size and position.
	 *
	 * @param manager
	 *            The AssetManager to load in the background
	 */
	public PauseMenuMode(GameCanvas canvas) {
		//this.manager = manager;
		this.canvas = canvas;
		
		// Compute the dimensions from the canvas
		resize(canvas.getWidth(),canvas.getHeight());
		
		headerPos.set(new Vector2(canvas.getWidth()/2, canvas.getHeight()/18*15));
		resumePos.set(new Vector2(canvas.getWidth()/2, canvas.getHeight()/18*11));
		restartPos.set(new Vector2(canvas.getWidth()/2, canvas.getHeight()/18*8));
		backPos.set(new Vector2(canvas.getWidth()/2, canvas.getHeight()/18*5));
		backgroundPos.set(new Vector2(canvas.getWidth()/2, canvas.getHeight()/2));
		

		// Load images immediately.
		pauseHeader = new Texture(PAUSE_HEADER);
		resume = new Texture(RESUME_BUTTON);
		resumeHoverButton = new Texture(RESUME_HOVER_BUTTON);
		restart = new Texture(RESTART_BUTTON);
		restartHoverButton = new Texture(RESTART_HOVER_BUTTON);
		back = new Texture(BACK_BUTTON);
		backHoverButton = new Texture(BACK_HOVER_BUTTON);
		background = new Texture(BACKGROUND_FILE);
		
		resumeHover = false;
		restartHover = false;
		backHover = false;

	}
	
	
	/**
	 * Called when this screen should release all resources.
	 */
	public void dispose() {
		pauseHeader.dispose();
		resume.dispose();
		resumeHoverButton.dispose();
		restart.dispose();
		restartHoverButton.dispose();
		back.dispose();
		backHoverButton.dispose();
		background.dispose();
	}


	/**
	 * Draw the status of this player mode.
	 *
	 * We prefer to separate update and draw from one another as separate
	 * methods, instead of using the single render() method that LibGDX does. We
	 * will talk about why we prefer this in lecture.
	 */
	public void draw() {
		canvas.beginMENU();
		
		canvas.draw(background, alphaSet, background.getWidth()/2, background.getHeight()/2, 
				backgroundPos.x, backgroundPos.y, 0, scale, scale);
		canvas.draw(pauseHeader, Color.WHITE, pauseHeader.getWidth()/2, pauseHeader.getHeight()/2, 
				headerPos.x, headerPos.y, 0, scale, scale);
		
		if(resumeHover == true){
			canvas.draw(resumeHoverButton, Color.WHITE, resumeHoverButton.getWidth()/2, resumeHoverButton.getHeight()/2, 
					resumePos.x, resumePos.y, 0, scale, scale);
		}
		else{
			canvas.draw(resume, Color.WHITE, resume.getWidth()/2, resume.getHeight()/2, 
					resumePos.x, resumePos.y, 0, scale, scale);
		}
		
		if(restartHover == true){
			canvas.draw(restartHoverButton, Color.WHITE, restartHoverButton.getWidth()/2, restartHoverButton.getHeight()/2, 
					restartPos.x, restartPos.y, 0, scale, scale);
		}
		else{
			canvas.draw(restart, Color.WHITE, restart.getWidth()/2, restart.getHeight()/2, 
					restartPos.x, restartPos.y, 0, scale, scale);
		}
		
		if(backHover == true){
			canvas.draw(backHoverButton, Color.WHITE, backHoverButton.getWidth()/2, backHoverButton.getHeight()/2, 
					backPos.x, backPos.y, 0, scale, scale);
		}
		else{
			canvas.draw(back, Color.WHITE, back.getWidth()/2, back.getHeight()/2, backPos.x, backPos.y, 0, scale, scale);
		}
			
		canvas.end();
	}
	
	// ADDITIONAL SCREEN METHODS
	
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
	
	public void setResumeHover(boolean val){
		this.resumeHover = val;
	}
	
	public void setBackHover(boolean val){
		this.backHover = val;
	}
	
	public void setRestartHover(boolean val){
		this.restartHover = val;
	}
	
	
		
}