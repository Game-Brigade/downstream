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
	private BitmapFont font;
	
	public HUDitems(){
		TotalLotus = 0;
		LotusLit = 0;
		Energy = 0;
		font = new BitmapFont();
	}
	
	public HUDitems(int lotuses, TextureRegion lily, TextureRegion energy, BitmapFont displayFont){
		TotalLotus = lotuses;
		LotusLit = 0;
		Energy = 2f;
		font = displayFont;
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
		return c;
		
	}
	

	@Override
	public void draw(GameCanvas canvas) {
		// TODO Auto-generated method stub
		if (TotalLotus != 0 && EnergyTexture != null && Lilypad != null) {
			canvas.draw(EnergyTexture, getBurstColor(), canvas.getWidth()/1.1f - 450f , canvas.getHeight()/1.1f - 5, energyLen(), 30);
			canvas.draw(EnergyTexture, Color.WHITE, 0, 0, 0, 0);
			canvas.drawHUDText(TotalLotus - LotusLit  + "", font, -10, Lilypad);
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
