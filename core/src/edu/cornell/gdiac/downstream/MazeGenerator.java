package edu.cornell.gdiac.downstream;

import java.util.Random;
import com.badlogic.gdx.math.Vector2;


public class MazeGenerator {
	
	int width;
	int height;
	MazeTile[][] maze;
	Vector2 start;
	Vector2 end;
	Random rng;
	
	public MazeGenerator(int width, int height) {
		this.width = width;
		this.height = height;
		maze = new MazeTile[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				maze[i][j] = MazeTile.UNSEEN;
			}
		}
		rng = new Random();
		start = getRandomEdge();
		end = getRandomEdge();
		while (manhattanDistance(start, end) < 5) end = getRandomEdge();
		
		
	}

	private Vector2 getRandomEdge() {
		int wall = rng.nextInt(4);
		Vector2 edge = new Vector2();
		switch (wall){
		//north wall 
		case 0:
			edge = new Vector2(height-1,rng.nextInt(width));
			break;
		//east wall
		case 1:
			edge = new Vector2(rng.nextInt(height),width-1);
			break;
		//south wall
		case 2:
			edge = new Vector2(0,rng.nextInt(width));
			break;
		//west wall
		case 3:
			edge = new Vector2(rng.nextInt(height),0);
			break;
		}
		return edge;
	}
	
	private static int manhattanDistance(Vector2 start, Vector2 end) {
		return (int) (Math.abs(start.x - end.x) + Math.abs(start.y - end.y));
	}

	private enum MazeTile {UNSEEN, WALL, PATH};
	
	public static void main(String[] args) {
		
		for (int i = 0; i < 10; i++) {
			MazeGenerator maze = new MazeGenerator(9,6);
			System.out.println(maze.start + " " + maze.end);
		}
		
	}
	
}
