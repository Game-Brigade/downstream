package edu.cornell.gdiac.downstream.models;

import com.badlogic.gdx.graphics.Color;

import edu.cornell.gdiac.downstream.GameCanvas;
import edu.cornell.gdiac.downstream.obstacle.BoxObstacle;

public class TutorialItems extends BoxObstacle{

	public TutorialItems(float x, float y, float width, float height) {
		super(x, y, width, height);
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * Draws the physics object.
	 *
	 * @param canvas Drawing context
	 */
	public void draw(GameCanvas canvas) {
		if (texture != null) {
			canvas.draw(texture,Color.BLACK,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.x,getAngle(),1,1);
		}
	}

}
