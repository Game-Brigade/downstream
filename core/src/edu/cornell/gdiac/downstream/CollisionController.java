package edu.cornell.gdiac.downstream;

import java.util.ArrayList;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;

import edu.cornell.gdiac.downstream.models.*;
import edu.cornell.gdiac.downstream.obstacle.Obstacle;

public class CollisionController {

	private static final boolean LETHAL_WALLS  = true;
	private static final boolean LETHAL_ROCKS  = true;
	private static final boolean LETHAL_ENEMIES  = true;
	private static final boolean LETHAL_WHIRLPOOLS  = false;
	private static final boolean LETHAL_SHADOWS = true;

	PlayerModel koi;
	public ArrayList<TetherModel> tethers = new ArrayList<TetherModel>();
	public ArrayList<WhirlpoolModel> pools = new ArrayList<WhirlpoolModel>();


	private Body bCollide;
	private String sCollide;
	private boolean win = false;

	public CollisionController(PlayerModel koi){
		this.koi = koi;
		bCollide = koi.getBody();
		sCollide = "koi";
	}

	//Called when a collision is made
	public boolean begin(Contact contact){
		Body body1 = contact.getFixtureA().getBody();
		Body body2 = contact.getFixtureB().getBody();
		String s1 = ((Obstacle)body1.getUserData()).getName();
		String s2 = ((Obstacle)body2.getUserData()).getName();
		if(s1.startsWith("koi")){
			sCollide = s2;
			bCollide = body2;
		}
		else if(s2.startsWith("koi")){
			sCollide = s1;
			bCollide = body1;
		}
		else {
			return false;
		}
		if(sCollide.startsWith("lily") || 
				sCollide.startsWith("lotus") || 
				sCollide.startsWith("fade") || 
				sCollide.startsWith("lantern")){
			if(!tethers.contains((TetherModel) bCollide.getUserData())){
				tethers.add((TetherModel) bCollide.getUserData());
			}
		}
		else if(sCollide.startsWith("goal")){win = true; return false;}
		else if(sCollide.startsWith("whirl")){return LETHAL_WHIRLPOOLS;}
		else if(sCollide.startsWith("rock")){return LETHAL_ROCKS;}
		else if(sCollide.startsWith("shadow")){return LETHAL_SHADOWS;}
		else if(sCollide.startsWith("enemy")){return LETHAL_ENEMIES;}
		else if(sCollide.startsWith("shore")){return false;}
		else if(sCollide.startsWith("wall") && !didWin()){
			koi.setTethered(false);
			koi.setAttemptingTether(false);
			return LETHAL_WALLS;
		}
		else { System.out.println("COLLISION ERROR: "+sCollide);}
		return false;
	}

	public void end(Contact contact) {


		Body body1 = contact.getFixtureA().getBody();
		Body body2 = contact.getFixtureB().getBody();
		String s1 = ((Obstacle)body1.getUserData()).getName();
		String s2 = ((Obstacle)body2.getUserData()).getName();
		Body bCollide;
		String sCollide;
		if(s1.startsWith("koi")){
			sCollide = s2;
			bCollide = body2;
		}
		else if(s2.startsWith("koi")){
			sCollide = s1;
			bCollide = body1;
		}
		else {
			return;
		}
		if(sCollide.startsWith("lily") || 
				sCollide.startsWith("lotus") || 
				sCollide.startsWith("fade") || 
				sCollide.startsWith("lantern")){
			tethers.remove((TetherModel) bCollide.getUserData());
		}
		else if(sCollide.startsWith("whirl")){}
		else if(sCollide.startsWith("rock")){}
		else if(sCollide.startsWith("shadow")){}
		else if(sCollide.startsWith("enemy")){}
		else if(sCollide.startsWith("wall")){}				
	}


	public TetherModel getClosestTetherInRange() {
		if(!inRange()){
			return null;
		}
		TetherModel closestTether = tethers.get(0);
		float closestDistance = closestTether.getPosition().sub(koi.getPosition()).len2();
		for (TetherModel tether : tethers) {
			float newDistance = tether.getPosition().sub(koi.getPosition()).len2();
			if (newDistance < closestDistance) {
				closestDistance = newDistance;
				closestTether = tether;
			}
		}
		return closestTether;
	}

	public WhirlpoolModel getClosestWhirlpoolInRange() {
		if(!inRangePool()){
			return null;
		}
		WhirlpoolModel closestPool = pools.get(0);
		float closestDistance = closestPool.getPosition().sub(koi.getPosition()).len2();
		for (WhirlpoolModel wpool : pools) {
			float newDistance = wpool.getPosition().sub(koi.getPosition()).len2();
			if (newDistance < closestDistance) {
				closestDistance = newDistance;
				closestPool = wpool;
			}
		}
		return closestPool;
	}
	public boolean inRange() {
		return !tethers.isEmpty();
	}	
	public boolean inRangeOf(TetherModel t) {
		return tethers.contains(t);
	}


	public boolean inRangePool(){
		return !pools.isEmpty();
	}

	public boolean inRangeOfPool(WhirlpoolModel w){
		return pools.contains(w);
	}


	public void initStart(TetherModel t){
		clear();
		tethers.add(t);
	}

	public void clear(){
		tethers.clear();
	}

	public boolean didWin(){
		return win;
	}



}