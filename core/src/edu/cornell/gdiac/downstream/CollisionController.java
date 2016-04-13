package edu.cornell.gdiac.downstream;

import java.util.ArrayList;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;

import edu.cornell.gdiac.downstream.models.*;
import edu.cornell.gdiac.downstream.obstacle.Obstacle;

public class CollisionController {

	PlayerModel koi;
	public ArrayList<TetherModel> tethers = new ArrayList<TetherModel>();
	
	
	
	boolean death;
	
	public CollisionController(PlayerModel koi){
		this.koi = koi;
	}
	
	//Called when a collision is made
	public boolean begin(Contact contact){
		Body body1 = contact.getFixtureA().getBody();
		Body body2 = contact.getFixtureB().getBody();
		String s1 = ((Obstacle)body1.getUserData()).getName();
		String s2 = ((Obstacle)body2.getUserData()).getName();

		if(s1.startsWith("koi")){
			if(s2.startsWith("lily") || s2.startsWith("lotus") || s2.startsWith("fade")){
				if(!tethers.contains((TetherModel) body2.getUserData())){
					tethers.add((TetherModel) body2.getUserData());
				}
			}
			else if(s2.startsWith("enemy")){
				return true;
			}
			else if(s2.startsWith("land") || s2.startsWith("whirlpool")){}			
			else{
				System.out.println("COLLISION ERROR: "+s2);
			}
		}
		
		if(s2.startsWith("koi")){
			if(s1.startsWith("lily") || s1.startsWith("lotus") || s1.startsWith("fade")){
				if(!tethers.contains((TetherModel) body1.getUserData())){
					tethers.add((TetherModel) body1.getUserData());
				}			}
			else if(s1.startsWith("enemy")){
				return true;
			}
			else if(s2.startsWith("land") || s2.startsWith("whirlpool")){}			
			else{
				System.out.println("COLLISION ERROR: "+s2);
			}
		}
		return false;
	}
	
	public void end(Contact contact) {
		Body body1 = contact.getFixtureA().getBody();
		Body body2 = contact.getFixtureB().getBody();
		String s1 = ((Obstacle)body1.getUserData()).getName();
		String s2 = ((Obstacle)body2.getUserData()).getName();
		
		
		if(body1.getUserData() == koi){
			if(s2.startsWith("lily") || s2.startsWith("lotus") || s2.startsWith("fade")){
				tethers.remove((TetherModel) body2.getUserData());
			}
			else if(s2.startsWith("enemy")){}
			else if(s2.startsWith("land") || s2.startsWith("whirlpool")){}			
			else{
				System.out.println("COLLISION ERROR: "+s2);
			}
		}
		
		if(body2.getUserData() == koi){
			if(s1.startsWith("lily") || s1.startsWith("lotus") || s1.startsWith("fade")){
				tethers.remove((TetherModel) body1.getUserData());
			}
			else if(s1.startsWith("enemy")){}
			else if(s2.startsWith("land") || s2.startsWith("whirlpool")){}			
			else{
				System.out.println("COLLISION ERROR: "+s2);
			}
		}
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

	public boolean inRange() {
		return !tethers.isEmpty();
	}	
	public boolean inRangeOf(TetherModel t) {
		return tethers.contains(t);
	}
	
	
	public void initStart(TetherModel t){
		tethers.add(t);
	}
	
	
	
}