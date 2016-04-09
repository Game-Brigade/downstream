package edu.cornell.gdiac.downstream.obstacle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import edu.cornell.gdiac.downstream.GameCanvas;

public class TerrainModel extends BoxObstacle {
	
	private float scaleX;
	
	private float scaleY;
	
	public TerrainModel(float x, float y, float width, float height, float sx, float sy) {
		super(x,y,width*sx,height*sy);
		scaleX = sx;
		scaleY = sy;
	}
	
	
	public void draw(GameCanvas canvas){
		canvas.draw(texture, Color.WHITE, origin.x, 
				origin.y, this.getX()*drawScale.x, this.getY()*drawScale.x, this.getAngle(),this.scaleX, this.scaleY);
	}

}
