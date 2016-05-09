package edu.cornell.gdiac.downstream.models;

import com.badlogic.gdx.math.*;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.downstream.GameCanvas;
import edu.cornell.gdiac.downstream.obstacle.*;

public class ShadowModel extends BoxObstacle {

	Vector2 shadowDest;
	Vector2 init;
	Vector2 dir;
	boolean cleared;
	
	public ShadowModel(float x, float y, float w, float h, Vector2 g) {
		super(x,y,w,h);
		setBodyType(BodyDef.BodyType.StaticBody);
		shadowDest = g;
		init = getPosition();
		dir = Vector2.Zero;
	}
	
	public void clearShadow(boolean b){
		dir = b ? shadowDest.cpy().sub(getPosition()) : init.cpy().sub(getPosition());
		dir.scl(-1);
		cleared = b;
	}	

	public void moveTowardsGoal(){
		if(!shadowDest.epsilonEquals(getPosition(), .2f)){
			setY(getY() - dir.y *.005f);
			setX(getX() - dir.x *.005f);
		}
	}
	
	public void draw(GameCanvas canvas){
		canvas.draw(texture, Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y, getAngle(), 2f, 2f);
	}
	
}
