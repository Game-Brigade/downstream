package edu.cornell.gdiac.downstream;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class CameraController {

	private OrthographicCamera camera;
	
	private static final float MAX_VELOCITY = 7.0f;
	private static final float MIN_VELOCITY = 3.0f;
	private static final float ACCELERATION = 0.05f;
	private static float currentVelocity = MIN_VELOCITY;
	
	public CameraController(OrthographicCamera cam) {
		camera = cam;
	}
	
	public void zoomOut() {
		camera.zoom = Math.min(2.0f, camera.zoom + 0.01f);
		camera.update();
	}
	
	public void zoomOutBoundless() {
		camera.zoom += .01;
		camera.update();
	}
	
	public void zoomIn() {
		camera.zoom = Math.max(1.25f, camera.zoom - 0.01f);
		camera.update();
	}

	public void zoomInBoundless() {
		camera.zoom -= 0.01;
		camera.update();
	}
	
	private void moveCameraTowards(Vector2 newPosition, float velocity) {
		Vector2 difference = newPosition.cpy().sub(new Vector2(camera.position.x, camera.position.y));
		if (difference.len() < velocity) {
			camera.position.set(newPosition,0);
		} else {
			camera.translate(difference.setLength(velocity));
		}
		camera.update();
	}
	
	public void moveCameraTowards(Vector2 newPosition) {
//		System.out.println(currentVelocity);
		currentVelocity = Math.min(currentVelocity+ACCELERATION,MAX_VELOCITY);
		moveCameraTowards(newPosition, currentVelocity);
	}
	
	public void resetCameraVelocity() {
		currentVelocity = MIN_VELOCITY;
	}
	
	public void setCameraPosition(Vector2 newPosition) {
		camera.position.set(newPosition, 0);
	}
	
	public void handleArrowKeys(boolean up, boolean down, boolean left, boolean right) {
		if (up)    camera.translate(new Vector2(0,MAX_VELOCITY));
		else if (down)  camera.translate(new Vector2(0,-MAX_VELOCITY));
		if (left)  camera.translate(new Vector2(-MAX_VELOCITY,0));
		else if (right) camera.translate(new Vector2(MAX_VELOCITY,0));
		camera.update();
	}
	
}
