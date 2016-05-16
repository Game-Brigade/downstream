package edu.cornell.gdiac.downstream;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;

import edu.cornell.gdiac.downstream.obstacle.Obstacle;
import edu.cornell.gdiac.downstream.obstacle.SimpleObstacle;

public class HUDitems extends Obstacle {

	public int TotalLotus;
	public int LotusLit;
	public float Energy;

	private TextureRegion EnergyTexture;
	private TextureRegion Lilypad;
	private BitmapFont font = new BitmapFont();
	
	private boolean tutorial = false;
	private TextureRegion tutorialTexture;
	private boolean help = false;
	private TextureRegion helpTexture;

	public HUDitems(){
		TotalLotus = 0;
		LotusLit = 0;
		Energy = 0;
		font = new BitmapFont();
	}

	/***
	 * sets to true if you are on a tutorial level. Should only be true if it is level 1, 2 or 3
	 * @param b tutorial status
	 */
	public void setTutorialStatus(boolean b){
		tutorial = b;
	}
	
	/***
	 * sets the image that will be displayed before the level as the tutorial
	 * @param tr image of the tutorial
	 */
	public void setTutorialTexture(TextureRegion tr){
		tutorialTexture = tr;
	}

	public HUDitems(int lotuses, TextureRegion lily, TextureRegion energy, BitmapFont displayFont){
		TotalLotus = lotuses;
		LotusLit = 0;
		Energy = 2f;
		//font = displayFont;
		//font = new BitmapFont();
		//font.setColor(Color.WHITE);
		Lilypad = lily;
		EnergyTexture = energy;
	}

	@Override
	public boolean activatePhysics(World world) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void deactivatePhysics(World world) {
		// TODO Auto-generated method stub

	}

	private float energyLen(){
		return Energy * 170f;
	}

	private Color getBurstColor(){
		Color c = Color.CORAL.cpy();
		if (Energy >= 1.9){
			c = Color.GOLD.cpy();
		}
		c.a = .6f;
		return new Color(0, 0, 0, .2f);
	}
	
	public void setHelp(boolean b){
		help = b;
	}
	public void setHelpTexture(TextureRegion tr){
		helpTexture = tr;
	}


	@Override
	public void draw(GameCanvas canvas) {
		// TODO Auto-generated method stub
		if (tutorial && tutorialTexture != null){
			canvas.draw(tutorialTexture, canvas.getWidth()/2 - tutorialTexture.getRegionWidth()/2, canvas.getHeight()/2 - tutorialTexture.getRegionHeight()/2);
			
		}
		else if (EnergyTexture != null && Lilypad != null) {
			canvas.draw(EnergyTexture, new Color(0, 0, 0, .5f), canvas.getWidth()/1.1f - 450f + 120, canvas.getHeight()/1.1f - 20, energyLen(), 50);
			canvas.draw(EnergyTexture, new Color(0, 0, 0, .2f), canvas.getWidth()/1.1f - 450f + 110 , canvas.getHeight()/1.1f - 30, 360, 70);
			canvas.drawHUDText(TotalLotus - LotusLit  + "", font, -9, Lilypad);
		}
		if (helpTexture != null){
			canvas.draw(helpTexture, 40, 40);
		}
	}

	@Override
	public void drawDebug(GameCanvas canvas) {
		// TODO Auto-generated method stub

	}

	public void updateHUD(int lanternsLit, float energy){
		Energy = energy;
		LotusLit = lanternsLit;
	}



}
