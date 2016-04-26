package edu.cornell.gdiac.downstream.models;

import com.badlogic.gdx.math.*;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.downstream.GameCanvas;
import edu.cornell.gdiac.downstream.obstacle.*;

public class ShadowModel extends BoxObstacle {

	Vector2 goal;
	Vector2 init;
	Vector2 dir;
	boolean cleared;
	
	public ShadowModel(float x, float y, float w, float h, Vector2 g) {
		super(x,y);
		setBodyType(BodyDef.BodyType.StaticBody);
		setSensor(true);
		setName("shadow");
		goal = g;
		init = getPosition();
		dir = Vector2.Zero;
	}
	
	public void clearShadow(boolean b){
		dir = b ? goal.cpy().sub(getPosition()) : init.cpy().sub(getPosition());
		cleared = b;
	}	

	public void moveTowardsGoal(){
		if(!goal.epsilonEquals(getPosition(), .2f)){
			setY(getY() + dir.y *.2f);
			setX(getX() + dir.x *.2f);
		}
	}
	
	
}