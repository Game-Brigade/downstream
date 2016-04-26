package edu.cornell.gdiac.downstream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class CameraController {

	public OrthographicCamera camera;
	
	private static final float MAX_VELOCITY = 9.0f;
	private static final float MIN_VELOCITY = 5.0f;
	private static final float ACCELERATION = 0.075f;
	private static float currentVelocity = MIN_VELOCITY;
	private static final float MAX_ZOOM_OUT = 2.0f;
	private static final float MIN_ZOOM_IN = 1.25f;
	private static final int numSteps = 200;
	private Vector2 playerPosition;
	private Vector2 stepMove;
	private float stepZoom;
	private boolean isZooming;
	
	public CameraController(OrthographicCamera cam) {
		camera = cam;
		playerPosition = new Vector2();
		stepMove = new Vector2();
		stepZoom = 0;
		isZooming = true;
	}
	
	public void zoomOut() {
		camera.zoom = Math.min(MAX_ZOOM_OUT, camera.zoom + 0.01f);
		camera.update();
	}
	
	
	
	public void zoomOutBoundless() {
		camera.zoom += .01;
		camera.update();
	}
	
	public void zoomIn() {
		camera.zoom = Math.max(MIN_ZOOM_IN, camera.zoom - 0.01f);
		camera.update();
	}

	public void zoomInBoundless() {
		camera.zoom -= 0.01;
		camera.update();
	}
	
	public void zoomStart(float width, Vector2 center, Vector2 player) {
		playerPosition.x = player.x; playerPosition.y = player.y;
		stepMove.x = (player.x - center.x) / numSteps;
		stepMove.y = (player.y - center.y) / numSteps;
		stepZoom = ((width / Gdx.graphics.getWidth()) - MIN_ZOOM_IN) / numSteps;
//		System.out.println(stepZoom);
		
		camera.position.x = center.x; camera.position.y = center.y;
		camera.zoom = width / Gdx.graphics.getWidth();
		camera.update();
//		System.out.println("Center: " + center);
//		System.out.println("Player: " + player);
//		System.out.println("Camera: " + camera.position);
		
	}
	
	public void zoomToPlayer() {
//		System.out.println("Camera: " + camera.position);
		camera.zoom -= stepZoom;
		camera.translate(stepMove);
		camera.update();
	}
	
	public boolean isZoomedToPlayer() {
		boolean zoomed = playerPosition.epsilonEquals(camera.position.x, camera.position.y, 1f);
		if (zoomed) isZooming = false;
		return !isZooming || zoomed;
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
	
	public Vector2 getCameraPosition() {
		return new Vector2(camera.position.x, camera.position.y);
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
