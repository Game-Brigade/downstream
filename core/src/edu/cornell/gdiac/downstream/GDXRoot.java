/*
 * GDXRoot.java
 *
 * This is the primary class file for running the game.  It is the "static main" of
 * LibGDX.  In the first lab, we extended ApplicationAdapter.  In previous lab
 * we extended Game.  This is because of a weird graphical artifact that we do not
 * understand.  Transparencies (in 3D only) is failing when we use ApplicationAdapter. 
 * There must be some undocumented OpenGL code in setScreen.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
 package edu.cornell.gdiac.downstream;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.*;
import com.badlogic.gdx.assets.loaders.*;
import com.badlogic.gdx.assets.loaders.resolvers.*;

import edu.cornell.gdiac.util.*;

/**
 * Root class for a LibGDX.  
 * 
 * This class is technically not the ROOT CLASS. Each platform has another class above
 * this (e.g. PC games use DesktopLauncher) which serves as the true root.  However, 
 * those classes are unique to each platform, while this class is the same across all 
 * platforms. In addition, this functions as the root class all intents and purposes, 
 * and you would draw it as a root class in an architecture specification.  
 */
public class GDXRoot extends Game implements ScreenListener {
	/** AssetManager to load game assets (textures, sounds, etc.) */
	private AssetManager manager;
	/** Drawing context to display graphics (VIEW CLASS) */
	private GameCanvas canvas; 
	/** Player mode for the asset loading screen (CONTROLLER CLASS) */
	private LoadingMode loading;
	/** Player mode for the main menu */
	private MainMenuMode mainMenu;
	/** Player mode for the level select menu */
	private LevelSelectMode levelSelect;
	/** Player controller for playing the game */
	private DownstreamController playGame;
	/** Player mode for level editing */
	private LevelEditor editor;
	/** Player mode for the the game proper (CONTROLLER CLASS) */
	private int current;
	
	
	/**
	 * Creates a new game from the configuration settings.
	 *
	 * This method configures the asset manager, but does not load any assets
	 * or assign any screen.
	 */
	public GDXRoot() {
		// Start loading with the asset manager
		manager = new AssetManager();
		
		// Add font support to the asset manager
		FileHandleResolver resolver = new InternalFileHandleResolver();
		manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
	}

	/** 
	 * Called when the Application is first created.
	 * 
	 * This is method immediately loads assets for the loading screen, and prepares
	 * the asynchronous loader for all other assets.
	 */
	public void create() {
		canvas  = new GameCanvas();
		loading = new LoadingMode(canvas,manager,1);
		mainMenu = new MainMenuMode(canvas,manager);
		levelSelect = new LevelSelectMode(canvas,manager);
		playGame = new DownstreamController();
		editor = new LevelEditor();
		
		playGame.preLoadContent(manager);
		editor.preLoadContent(manager);
		
		loading.setScreenListener(this);
		setScreen(loading);
	}

	/** 
	 * Called when the Application is destroyed. 
	 *
	 * This is preceded by a call to pause().
	 */
	public void dispose() {
		// Call dispose on our children
		setScreen(null);
		
		//System.out.println("Hello");
		editor.unloadContent(manager);
		editor.dispose();

		canvas.dispose();
		canvas = null;
	
		// Unload all of the resources
		manager.clear();
		manager.dispose();
		super.dispose();
	}
	
	/**
	 * Called when the Application is resized. 
	 *
	 * This can happen at any point during a non-paused state but will never happen 
	 * before a call to create().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		canvas.resize();
		super.resize(width,height);
	}
	
	/**
	 * The given screen has made a request to exit its player mode.
	 *
	 * The value exitCode can be used to implement menu options.
	 *
	 * @param screen   The screen requesting to exit
	 * @param exitCode The state of the screen upon exit
	 */
	public void exitScreen(Screen screen, int exitCode) {
		if (screen == loading) {
			loading.dispose();
			loading = null;
			mainMenu.setScreenListener(this);
			setScreen(mainMenu);
			Gdx.input.setInputProcessor(mainMenu);
		}
		
		else if (screen == mainMenu && exitCode == WorldController.EXIT_PLAY) {
			
			playGame = new DownstreamController();
			playGame.preLoadContent(manager);
			editor.preLoadContent(manager);
			
			playGame.loadContent(manager);
			playGame.setScreenListener(this);
			playGame.setCanvas(canvas);
			
			playGame.reset();
			
			setScreen(playGame);
			Gdx.input.setInputProcessor(playGame);
			mainMenu.dispose();
			mainMenu = null;
		
		} 
		
		else if (screen == mainMenu && exitCode == WorldController.EXIT_EDIT){
			
			editor.loadContent(manager);
			editor.setScreenListener(this);
			editor.setCanvas(canvas);
			editor.reset();
			setScreen(editor);
			
			mainMenu.dispose();
			mainMenu = null;
		}
		
		else if (screen == mainMenu && exitCode == WorldController.EXIT_SELECT){
			
			mainMenu.dispose();
			mainMenu = null;
			levelSelect = new LevelSelectMode(canvas,manager);
			levelSelect.setScreenListener(this);
			setScreen(levelSelect);
			Gdx.input.setInputProcessor(levelSelect);
		}
		
		else if (screen == levelSelect && exitCode == WorldController.EXIT_MAIN){
			levelSelect.dispose();
			levelSelect = null;
			mainMenu = new MainMenuMode(canvas,manager);
			
			mainMenu.setScreenListener(this);
			setScreen(mainMenu);
			Gdx.input.setInputProcessor(mainMenu);
		}
		
		
		
		else if (screen == playGame && exitCode == WorldController.EXIT_MAIN){
			System.out.println("Hello");
			playGame.unloadContent(manager);
			playGame.dispose();
			playGame = null;
			mainMenu = new MainMenuMode(canvas,manager);
			
			mainMenu.setScreenListener(this);
			setScreen(mainMenu);
			Gdx.input.setInputProcessor(mainMenu);
		}
		
		
		
		else if (exitCode == WorldController.EXIT_QUIT) {
			// We quit the main application
			Gdx.app.exit();
		}
	}

}
