package edu.cornell.gdiac.downstream;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class CameraController {

	private OrthographicCamera camera;
	
	public CameraController(OrthographicCamera cam) {
		camera = cam;
	}
	
	public void zoomOut() {
		camera.zoom = Math.min(1.5f, camera.zoom + 0.01f);
	}
	
	public void zoomIn() {
		camera.zoom = Math.max(1, camera.zoom - 0.01f);
	}
	
	public void moveCameraTowards(Vector2 newPosition, float velocity) {
		Vector2 difference = newPosition.cpy().sub(new Vector2(camera.position.x, camera.position.y));
		if (difference.len() < velocity) {
			camera.position.set(newPosition,0);
		} else {
			camera.translate(difference.setLength(velocity));
		}
		camera.update();
	}
	
	public void setCameraPosition(Vector2 newPosition) {
		camera.position.set(newPosition, 0);
	}
	
}
