/*
 *
 * A tether object. A player enters it's radius and begins orbitting
 *
 * Author: Dashiell Brown
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.downstream.models;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.downstream.GameCanvas;
import edu.cornell.gdiac.downstream.obstacle.*;

public class TetherModel extends WheelObstacle {

	/** Radius of this tether, used for collision detection */
	private static final int TETHER_DEFAULT_RADIUS = 1;

	/** The radius at which the player orbits a tether */
	public static final float TETHER_DEFAULT_ORBIT = 3f;  

	/** The range at which the player can enter orbit around this tether */
	public static final int TETHER_DEFAULT_RANGE = 8;
	
	private static final String LIGHTTEXTURE = "tethers/lotusLight.png";
	private TextureRegion lotusLightTexture = new TextureRegion(new Texture(Gdx.files.internal(LIGHTTEXTURE)));
	
	
	private static final BodyDef.BodyType TETHER_BODY_TYPE = BodyDef.BodyType.StaticBody;

	/** The type of this tether */
	private TetherType type;

	private float rotations;
	private float radius;

	private Vector2 entry = new Vector2();

	private float sparkSize = 0;

	public boolean set = false;

	private static TextureRegion lightingTexture;
	
	public boolean isTethered = false;
	
	public boolean lit = false;
	
	private int isOpening = 0;
	//0 means its closed
	//1 means its opening
	//2 means its opened
	
	private float alpha = 1f;
	
	private float lightingScale = .4f;
	private boolean lightingIncrease = false;
	public boolean inrange = false;
	public boolean inpath = false;
	public Color c2;
	
	public Circle lightCircle = new Circle(getX(),getY(), 0);
	
	private TextureRegion overlayTexture;

	/** Tethers can be lilipads, lanterns, or lotus flowers */
	public enum TetherType {
		Lilypad,
		Lantern,
		Lotus,
	};

	public TetherModel(float x, float y, TetherType type) {
		super(x,y,TETHER_DEFAULT_RANGE);
		setType(type);
		setBodyType(TETHER_BODY_TYPE);
	}

	public TetherModel(float x, float y, float r, boolean b){
		super(x,y, TETHER_DEFAULT_RANGE);
		setType(TetherType.Lantern);
		setBodyType(TETHER_BODY_TYPE);
	}

	public TetherModel(float x, float y, float r) {
		super(x,y,TETHER_DEFAULT_RANGE);
		setType(TetherType.Lilypad);
		setBodyType(TETHER_BODY_TYPE);
	}

	public void setType(TetherType newType) {
		type = newType;
	}

	public float getRadius() {
		return TETHER_DEFAULT_RADIUS;
	}

	public float getOrbitRadius() {
		return TETHER_DEFAULT_ORBIT;
	}

	public float getSensorRadius() {
		return TETHER_DEFAULT_RANGE;
	}

	public void setRotation(float i){
		rotations = i;
	}
	public void updateRotations(){
		rotations = rotations + .5f;
	}

	public float getRotations(){
		return rotations;
	} 

	public Vector2 getEntry(){
		return entry;
	}
	public void setEntry(Vector2 v){
		entry = v;
	}
	public boolean isLotus(){
		return type == TetherType.Lantern || type == TetherType.Lotus;
	}

	public void setlightingTexture(TextureRegion t){
		lightingTexture = t;
	}
	
	public TetherType getTetherType(){
		return type;
	}
	
	private void setLightingScale(){
		if (lightingIncrease){
			lightingScale = lightingScale + .005f;
		}
		else{
			lightingScale = lightingScale - .005f;
		}
		if (lightingScale < .4 || lightingScale > .6){
			lightingIncrease = !lightingIncrease;
		}
	}
	
	public void drawLight(GameCanvas canvas){
		setLightingScale();
		canvas.draw(lotusLightTexture,new Color(255, 255, 255, .5f),texture.getRegionHeight()/2,texture.getRegionWidth()/2,getX()*drawScale.x,getY()*drawScale.x,getAngle(),lightingScale,lightingScale);
	}

	public void setOverlay(TextureRegion value){
		overlayTexture = value;
	}
	
	public void setC2(Color colo){
		c2 = colo;
	}
	
	public void draw(GameCanvas canvas) {
		
		if (texture != null) {
			if (type == TetherType.Lilypad){
				canvas.draw(texture,Color.WHITE,texture.getRegionHeight()/2,texture.getRegionWidth()/2,getX()*drawScale.x,getY()*drawScale.x,getAngle(),.4f,.4f);
				
			}
			if (type == TetherType.Lantern || type == TetherType.Lotus){
				canvas.draw(texture,Color.WHITE,texture.getRegionHeight()/2,texture.getRegionWidth()/2,getX()*drawScale.x,getY()*drawScale.x,getAngle(),.35f,.35f);
				if (sparkSize < 2 && this.set){
					sparkSize += .01f;
				}
				if (sparkSize < 2 && !this.set && !(sparkSize <= 0)){
					sparkSize += -.01f;
				}

			}
			if (sparkSize >= 2){
				lit = true;
				sparkSize = 2f;
			}
			if (lit && type == TetherType.Lotus && alpha > 0 && !this.set){
				alpha = alpha - .005f;
			}
			if (alpha <= 0){
				lit = false;
				alpha = 1;
				sparkSize = 0;
			}
			findCircle();
			canvas.draw(lightingTexture,new Color(255, 255, 255, alpha), lightingTexture.getRegionWidth()/2, lightingTexture.getRegionHeight()/2,getX()*drawScale.x,getY()*drawScale.x,getAngle(),sparkSize,sparkSize);
			if (inrange || inpath){
				drawLight(canvas);
			}
		}
		if (overlayTexture != null){
			canvas.draw(overlayTexture,c2,overlayTexture.getRegionHeight()/2,overlayTexture.getRegionWidth()/2,getX()*drawScale.x,getY()*drawScale.x,getAngle(),.35f,.35f);
			if (type == TetherType.Lilypad){
				canvas.draw(texture,c2,texture.getRegionHeight()/2,texture.getRegionWidth()/2,getX()*drawScale.x,getY()*drawScale.x,getAngle(),.4f,.4f);
			}
			if (type == TetherType.Lantern || type == TetherType.Lotus){
				canvas.draw(texture,c2,texture.getRegionHeight()/2,texture.getRegionWidth()/2,getX()*drawScale.x,getY()*drawScale.x,getAngle(),.35f,.35f);
			}
		}
	}
	
	
	private void findCircle(){
		lightCircle.setRadius((lightingTexture.getRegionWidth()/2 * sparkSize)/drawScale.x);
	}
	
	public void setTethered(boolean b) {
		set = b;
	}
	
	public void setOpening(int i){
		isOpening = i;
	}
	public int getOpening(){
		return isOpening;
	}

}
