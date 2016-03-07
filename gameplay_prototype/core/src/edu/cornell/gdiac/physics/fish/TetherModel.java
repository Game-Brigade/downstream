/*
 * Tether.java
 *
 * A tether object. A player enters it's radius and begins orbitting
 *
 * Author: Dashiell Brown
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.physics.fish;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.*;
import edu.cornell.gdiac.physics.obstacle.*;

public class TetherModel extends BoxObstacle {

  /** Width of this tether, used for collision detection */
  private static final int TETHER_WIDTH = 40;
  
  /** Height of this tether, used for collision detection */
  private static final int TETHER_HEIGHT = 40;
  
  /** The range at which the player can enter orbit around this tether */
  private static final int TETHER_RANGE = 400;

  /** The radius at which the player orbits a tether */
  private static final int TETHER_RADIUS = 200;
  
  private static final BodyDef.BodyType TETHER_BODY_TYPE = BodyDef.BodyType.StaticBody;

  /** The type of this tether */
  private TetherType type;

  /** Tethers can be lilipads, lanterns, or lotus flowers */
  public enum TetherType {
    Lilipad,
    Lantern,
    Lotus
  };

  public TetherModel(float x, float y, TetherType type) {
    super(x, y, TETHER_WIDTH, TETHER_HEIGHT);
    setType(type);
    setBodyType(TETHER_BODY_TYPE);
  }

  public void setType(TetherType newType) {
    type = newType;
  }

  public Vector2 calculateAttractiveForce(PlayerFishModel player) {
    Vector2 direction = this.getPosition().sub(player.getPosition());
    float radius = direction.len();
    float forceMagnitude = (float) (player.getMass() * Math.pow(player.getLinearVelocity().len(),2) / radius);
    return direction.setLength(forceMagnitude);
  }

}