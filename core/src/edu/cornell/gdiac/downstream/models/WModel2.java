package edu.cornell.gdiac.downstream.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;

import edu.cornell.gdiac.downstream.GameCanvas;
import edu.cornell.gdiac.downstream.obstacle.WheelObstacle;

public class WModel2 extends WheelObstacle {

	/**whirlpool texture**/
	private static TextureRegion whirpoolImage;
	/**do not change. this is the angle and will update automatically**/
	//TODO change to private
	public float angle = 90;
	/** circle for checking if its within whirlpool range**/
	private Circle checkingCircle = new Circle(getX(),getY(), 5);
	/** t is used in the bezier curve**/
	private float t = 0;
	/** rotation for the angle of the whirlpool spinning*/
	private float rot = 0;
	private float radius = 4;
	public float degreesAim;

	/** The direction the pool spins; 1 is ccw, -1 is cw */
	private float direction = 1;

	private Vector2 startK;
	private Vector2 farOffk;

	private PlayerModel koi;

	/***
	 * constructor method
	 * @param x position of x in game coordinate
	 * @param y position of y in game coordinates
	 * @param radius this is not really relevant. It will show up in debug mode
	 */
	public WModel2(float x, float y, float radius, float degrees) {
		super(x, y, radius);
		degreesAim = degrees;
		degreesAim = 200;

	}
	
	public WModel2(float x, float y, float r, Vector2 aim){
		super(x, y, r);
		degreesAim = aim.angle(getPosition());
		
	}

	/***
	 * Called to draw the whirlpool. Use it in the update loop when you draw
	 * 
	 */
	public void draw(GameCanvas canvas){
		//super.draw(canvas);
		canvas.draw(texture, Color.WHITE, texture.getRegionWidth()/2, 
				texture.getRegionHeight()/2, this.getX()*drawScale.x, this.getY()*drawScale.x, (float)(Math.PI*rot*direction),0.4f, 0.4f);
		rot-=0.02;

		/*
		if (koi != null){
			//code for debugging
			for (float i = 0; i < 1; i = i + .001f){

				float sX = startK.x;
				float sY = startK.y;
				Vector2 first = new Vector2(sX, sY);

				float eX = this.getX();
				float eY = this.getY() + 4;
				Vector2 second = new Vector2(eX, eY);

				float bezierXend = eX - 4;
				float bezierYend = eY;
				Vector2 last = new Vector2(bezierXend, bezierYend);


				canvas.draw(this.texture, Color.RED, 0, 0, first.x * drawScale.x, first.y * drawScale.x, 0, .1f, .1f);
				canvas.draw(this.texture, Color.WHITE, 0, 0, farOffk.x * drawScale.x, farOffk.y * drawScale.x, 0, .1f, .1f);
				canvas.draw(this.texture, Color.BLACK, 0, 0, last.x * drawScale.x, last.y * drawScale.x, 0, .1f, .1f);
				canvas.draw(this.texture, Color.PURPLE, 0, 0, second.x * drawScale.x, second.y * drawScale.x, 0, .1f, .1f);
				canvas.draw(this.texture, new Color (255, 255, 255, .01f), 0, 0, CalculateBezierPoint(i, first, farOffk, last, second).x * drawScale.x, CalculateBezierPoint(i, first, farOffk, last, second).y * drawScale.x, 0, .4f, .4f);
			}
			
		}*/
	}

	/***
	 * Makes the player circulate a whirlpool
	 * @param k the player
	 */
	public void circulate(PlayerModel k){
		koi = k;
		float radians = angle*MathUtils.degreesToRadians;
		if (t <= 1){
			setK(k);
			moveBezier(k);
			t = t + .05f;
			k.wped = true;
		}
		else if (radius > 2 || !(angle > degreesAim - 10 && angle < degreesAim + 10)){
			float ox = this.getX();
			float oy = this.getY();
			
			k.setX((float) (Math.cos(radians)*radius + ox));
			k.setY((float) (Math.sin(radians)*radius + oy));
			k.setAngle(radians + 1.578f + 3.1415f);
			incrementAngle();
			decreaseRadius();
		}
		else{
			k.setLinearVelocity(k.getLinearVelocity().setAngleRad(radians + 1.578f + 3.1415f));
			k.wped = false;
		}

	}

	/***
	 * gets the starting angle of the player, used so that we can 
	 * get the starting angle for whirlpool circulation. 
	 * In other words, get position to start at for whirlpools.
	 * @param k player model
	 */
	public void startingAngle(PlayerModel k){
		float deltaX = this.getX() - k.getX();
		float deltaY = this.getY() - k.getY();
		double angleR = Math.atan2(deltaY, deltaX) * MathUtils.radiansToDegrees + 90;
		angle = (float) angleR;
	}

	/***
	 * increments the angle. Used to make the koi continuously move on the whirlpool
	 */
	public void incrementAngle(){
		if (angle < 0) angle += 360;
		else if (angle > 360) angle -= 360;
		angle %= 360;
		angle -= 8f;
	}
	
	private void decreaseRadius(){
		radius -= .05f;
	}

	/**
	 * Checks if the player is within the range of the whirlpool to be sucked in
	 * @param k the player
	 * @return
	 */
	public boolean shouldTether(PlayerModel k){
		return checkingCircle.contains(k.getPosition());
	}

	/**
	 * pay no attention to variable names, they are not helpful
	 * @param k fish dammit
	 */
	public void moveBezier(PlayerModel k){

		float sX = startK.x;
		float sY = startK.y;
		Vector2 first = new Vector2(sX, sY);

		float eX = this.getX();
		float eY = this.getY() + 4;
		Vector2 second = new Vector2(eX, eY);

		float bezierXend = eX - 4;
		float bezierYend = eY;
		Vector2 last = new Vector2(bezierXend, bezierYend);

		//System.out.println(CalculateBezierPoint(t, first, farOffk, last, second).angle(CalculateBezierPoint(t+ .04f, first, farOffk, last, second)));
		k.setAngle(findA(CalculateBezierPoint(t, first, farOffk, last, second), CalculateBezierPoint(t+ .04f, first, farOffk, last, second)));

		k.setPosition(CalculateBezierPoint(t, first, farOffk, last, second));
	}

	/**
	 * make a smooth bezier curve that increments t times
	 * assert to make sure the bezier points are in between the start and the end
	 * @param t1 the time variable. Increment this
	 * @param p0 the starting point
	 * @param p1 the first bezier point
	 * @param p2 the second bezier point associated with the end
	 * @param p3 the ending location
	 * @return the expected bezier point
	 */
	public Vector2 CalculateBezierPoint(float t1, Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3){

		Vector2 bezierPoint = new Vector2(0, 0);
		float u = 1-t1;
		float tt = t1*t1;
		float uu = u*u;
		float uuu = uu*u;
		float ttt = tt*t1;

		bezierPoint.x = uuu * p0.x;
		bezierPoint.x = bezierPoint.x + (3 * uu * t1 * p1.x);
		bezierPoint.x = bezierPoint.x + (3 * u * tt * p2.x);
		bezierPoint.x = bezierPoint.x + (ttt * p3.x);

		bezierPoint.y = uuu * p0.y;
		bezierPoint.y = bezierPoint.y + (3 * uu * t1 * p1.y);
		bezierPoint.y = bezierPoint.y + (3 * u * tt * p2.y);
		bezierPoint.y = bezierPoint.y + (ttt * p3.y);

		return bezierPoint;
	}

	/***
	 * This method will set the player koi status only once.
	 * Intended to be used to set the values for the bezier curve. 
	 * @param k
	 */
	private void setK(PlayerModel k){
		if(startK == null){
			startK = k.getPosition().cpy();
			farOffk = startK.cpy();
			farOffk.add(k.getLinearVelocity().cpy().nor().scl(4f));
		}
	}
	
	/***
	 * call when you are done with the bezier curve function
	 * it resets the attachement vector so that we can reattach it again when
	 * the fish comes back another time that isnt the first
	 */
	public void nullK(){
		if (startK != null){
			startK = null;
			farOffk = null;
			radius = 4;
			t = 0;
			angle = 90;
			
		}
	}

	/***
	 * finds the appropriate angle
	 * modeled after the enemy fish code
	 * Given two vectors, give the angle between them. Primarily used for finding the angle
	 * during the bezier curve
	 * @param target
	 * @param t2
	 * @return
	 */
	public float findA(Vector2 target, Vector2 t2) {
		float angle = (float) Math.toDegrees(Math.atan2(target.y - t2.y, target.x - t2.x));
		angle = angle - 180;
		return (float) Math.toRadians(angle);
	}

}
