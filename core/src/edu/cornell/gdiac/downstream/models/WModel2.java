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
	private float angle = 90;
	/** circle for checking if its within whirlpool range**/
	private Circle checkingCircle = new Circle(getX(),getY(), 5);
	/** t is used in the bezier curve**/
	private float t = 0;
	/** rotation for the angle of the whirlpool spinning*/
	private float rot = 0;
	private float radius = 4;
	public float degreesAim;
	public Vector2 aim;
	private static final int WHIRLPOOL_RANGE = 4;
	/** The direction the pool spins; 1 is ccw, -1 is cw */
	private float direction = 1;

	private Vector2 startK;
	private Vector2 farOffk;

	private PlayerModel koi;
	private TextureRegion ArrowTexture;
	private Vector2 bez3;
	private Vector2 bez4;
	
	Color c = new Color(255, 255, 255, .5f);
	private boolean debug;
	private Vector2 pull;
	
	/***
	 * constructor method
	 * @param x position of x in game coordinate
	 * @param y position of y in game coordinates
	 * @param radius this is not really relevant. It will show up in debug mode
	 */
	public WModel2(float x, float y, float radius, float degrees) {
		super(x, y, WHIRLPOOL_RANGE);
		checkingCircle.radius = WHIRLPOOL_RANGE;
		degreesAim = degrees;
		aim = new Vector2((float)Math.cos(degrees),(float)Math.sin(degrees));

	}
	
	public WModel2(float x, float y, float r, Vector2 aim){
		super(x, y, WHIRLPOOL_RANGE);
		checkingCircle.radius = WHIRLPOOL_RANGE;
		this.aim = aim;
		degreesAim = (float) (Math.atan2(aim.y, aim.x)%(Math.PI*2));
		degreesAim += (Math.PI*2);
		degreesAim %= (Math.PI*2);
	}
	
	public WModel2(float x, float y, Vector2 aim){
		super(x, y, WHIRLPOOL_RANGE);
		checkingCircle.radius = WHIRLPOOL_RANGE;
		this.aim = aim;
		degreesAim = (float) (Math.atan2(aim.y, aim.x));
		degreesAim += (Math.PI*2);
		degreesAim %= (Math.PI*2);
	}

	/***
	 * Called to draw the whirlpool. Use it in the update loop when you draw
	 * 
	 */
	public void draw(GameCanvas canvas){
//		for (float i = 0; i < 1; i = i + .001f){
//			float x = (float) (i*2*Math.PI);
//			Vector2 r = new Vector2();
//			r.set((float)Math.cos(x),(float)Math.sin(x));
//			Vector2 p = getPosition().cpy().sub(r.scl(3));
//			canvas.draw(this.texture, Color.WHITE, 
//					texture.getRegionWidth()/2, texture.getRegionHeight()/2, 
//					p.x * drawScale.x, p.y * drawScale.y, 
//					0, .4f, .4f);
//		}
		canvas.draw(texture, c, 
				texture.getRegionWidth()/2, texture.getRegionHeight()/2, 
				this.getX()*drawScale.x, this.getY()*drawScale.y, 
				(float)(Math.PI*rot*direction),0.4f, 0.4f);
		rot-=0.02;
		canvas.draw(ArrowTexture, c , 
				ArrowTexture.getRegionWidth()/2 *.6f , ArrowTexture.getRegionHeight()/2 *.6f,
				(this.getX()+aim.cpy().nor().scl(3).x)*drawScale.x, (this.getY()+aim.cpy().nor().scl(3).y)*drawScale.y, 
				degreesAim, .6f, .6f);

		

		//code for debugging
		/*if (koi != null && false){
			if(startK != null){

				canvas.draw(this.texture, Color.RED, 0, 0, startK.x * drawScale.x, startK.y * drawScale.x, 0, .1f, .1f);
				canvas.draw(this.texture, Color.GREEN, 0, 0, farOffk.x * drawScale.x, farOffk.y * drawScale.x, 0, .1f, .1f);
				canvas.draw(this.texture, Color.BLACK, 0, 0, bez3.x * drawScale.x, bez3.y * drawScale.x, 0, .1f, .1f);
				canvas.draw(this.texture, Color.PURPLE, 0, 0, bez4.x * drawScale.x, bez4.y * drawScale.x, 0, .1f, .1f);
				for (float i = 0; i < 1; i = i + .001f){
					Vector2 bez = CalculateBezierPoint(i, startK, farOffk, bez3, bez4);
					canvas.draw(this.texture, new Color (255, 255, 255, .01f), 0, 0, bez.x * drawScale.x, bez.y * drawScale.y, 0, .4f, .4f);
				}
			}
		}*/
	}

	/***
	 * Makes the player circulate a whirlpool
	 * @param k the player
	 */
	public void circulate(PlayerModel k){
		koi = k;
		setK(k);
		k.setLinearVelocity(Vector2.Zero);
		angle += 360;
		angle %= 360;
		double radians = Math.toRadians(angle);
		radians += (Math.PI*2);
		radians %= (Math.PI*2);

		double lo = ((Math.toDegrees(degreesAim- Math.PI*1.5f)-4)+360)%360;
		double hi = ((Math.toDegrees(degreesAim- Math.PI*1.5f)+4)+360)%360;
		
		//Moving on bezier
		if (t <= 1){
			t = t + .05f;
			moveBezier(k);
			k.wped = true;
			
			pull = k.getPosition().cpy().sub(getPosition());
			radius = pull.len();
		}
		//circling whirlpool
		else if (radius > .5){

			pull = k.getPosition().cpy().sub(getPosition());
			angle = pull.angle();
			incrementAngle();

			radians = Math.toRadians(angle);

			k.setPosition(getPosition().cpy().add((float)Math.cos(radians)*radius,(float)Math.sin(radians)*radius));

			k.setAngle((float) (radians + Math.PI*1.5f));
			decreaseRadius();
		}
		else if(radius <= .5 &&
					((lo < hi && (angle < lo || angle > hi )) ||
					(lo > hi && (angle > lo || angle < hi )))

					
			){
				k.setAngle( (float) (radians + Math.PI*1.5f));
				angle -= 4;
		}
		else{

			k.setLinearVelocity(aim.cpy().scl(2));
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
		radius -= .1f;
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
	 * @param k fish dammit
	 */
	public void moveBezier(PlayerModel k){
		Vector2 bez = CalculateBezierPoint(t, startK, farOffk, bez3, bez4);
		Vector2 bezNext = CalculateBezierPoint(t+.01f, startK, farOffk, bez3, bez4);
		
		//System.out.println(bez.angle(bezNext));
		k.setAngle(findA(bez, bezNext));

		k.setPosition(bez);
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

		bezierPoint.x = uuu * p0.x
				+ (3 * uu * t1 * p1.x)
				+ (3 * u * tt * p2.x)
				+ (ttt * p3.x);

		bezierPoint.y = uuu * p0.y
				+ (3 * uu * t1 * p1.y)
				+ (3 * u * tt * p2.y)
				+ (ttt * p3.y);

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
			pull = getPosition().cpy().sub(startK.cpy());
			//farOffk = startK.cpy().add(k.getLinearVelocity().cpy().nor().scl(4f));
			farOffk = startK.cpy().add(pull.cpy().rotate90(1).nor().scl(WHIRLPOOL_RANGE*.75f));;
			bez4 = getPosition().cpy().add(pull.nor().scl(WHIRLPOOL_RANGE*.75f));
			bez3 = bez4.cpy().add(pull.cpy().rotate90(1).nor().scl(WHIRLPOOL_RANGE*.75f));			
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
			bez3 = null;
			bez4 = null;
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
		float ang = (float) Math.toDegrees(Math.atan2(target.y - t2.y, target.x - t2.x));
		ang = ang - 180;
		return (float) Math.toRadians(ang);
	}

	public void setArrowTexture(TextureRegion whirlArrow) {
		ArrowTexture = whirlArrow;
		
	}

}
