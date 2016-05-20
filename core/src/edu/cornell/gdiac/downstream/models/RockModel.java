/*
 * Tether.java
 *
 * A tether object. A player enters it's radius and begins orbitting
 *
 * Author: Dashiell Brown
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.downstream.models;

import com.badlogic.gdx.math.*;

import java.util.Random;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.downstream.GameCanvas;
import edu.cornell.gdiac.downstream.obstacle.*;

public class RockModel extends WheelObstacle {
	RockType t;
	
	/** Tethers can be lilipads, lanterns, or lotus flowers */
	public enum RockType {
		s,
		m,
		l,
	};

	public RockModel(float x, float y, float r) {
		super(x,y,r);
		setBodyType(BodyDef.BodyType.StaticBody);
		setSensor(true);
		setName("rock");
	}
	
	public int setType(){
		Random r = new Random();
		float t = r.nextFloat();
		if (t > .66){
			return 0;
		} 
		else if (t > .33){
			return 1;
		} else{
			return 2;
		}
		
	}

}
