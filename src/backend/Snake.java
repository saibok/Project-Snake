package backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Snake {

	private Direction direction;
	private Point headLocation = new Point(0, 0);
	private List<Point> tail = new ArrayList<Point>();
	private int height;
	private int width;
	private int blockSize;
	private boolean isCollidedWithWall = false;

	public Snake(int width, int height, int blockSize) {
		this.width = width;
		this.height = height;
		this.blockSize = blockSize;
		this.direction = Direction.RIGHT;
	}
	
	//Schlange Bewegung
	public void snakeUpdate() {

		if (tail.size() > 0) {
			tail.remove(tail.size() - 1);
			tail.add(0, new Point(headLocation.getX(), headLocation.getY()));
		}

		switch (direction) {
		case UP:
			headLocation.setY(headLocation.getY() - blockSize);
			if (headLocation.getY() < 0) {
				isCollidedWithWall = true;
				headLocation.setY(0);
			}
			break;

		case DOWN:
			headLocation.setY(headLocation.getY() + blockSize);
			if (headLocation.getY() >= height) {
				isCollidedWithWall = true;
				headLocation.setY(height - blockSize);
			}
			break;

		case LEFT:
			headLocation.setX(headLocation.getX() - blockSize);
			if (headLocation.getX() < 0) {
				isCollidedWithWall = true;
				headLocation.setX(0);
			}
			break;

		case RIGHT:
			headLocation.setX(headLocation.getX() + blockSize);
			if (headLocation.getX() >= width) {
				isCollidedWithWall = true;
				headLocation.setX(width - blockSize);
			}
			break;

		default:
			break;
		}
	}
	
	//Kollision Wand
	public boolean collidedWithWall() {
		return isCollidedWithWall;
	}
	
	//Kollision Schwanz
	public boolean collidedWithTail() {
		boolean isCollision = false;

		for (Point tailSegment : tail) {
			if (headLocation.equals(tailSegment)) {
				isCollision = true;
				break;
			}
		}

		return isCollision;
	}
	
	//Kollision Schlange
	public boolean checkCollision(Point x) {
		for (Point tailSegment : tail) {
			if (x.equals(tailSegment)) {
				return true;
			}
		}
		return x.equals(headLocation);
	}

	//Neues Schlangensegment
	public void addTailSegment() {
		tail.add(0, new Point(headLocation.getX(), headLocation.getY()));
		System.out.println("Add tail segment");
	}

	public void setDirection(Direction myDirection) {
		Map<Direction, Direction> inverses = new HashMap<Direction, Direction>();
		inverses.put(Direction.UP, Direction.DOWN);
		inverses.put(Direction.RIGHT, Direction.LEFT);
		inverses.put(Direction.DOWN, Direction.UP);
		inverses.put(Direction.LEFT, Direction.RIGHT);
		if (myDirection != inverses.get(this.direction)) {
			this.direction = myDirection;
		}
	}

	public int getBlockSize() {
		return blockSize;
	}

	//Koordinaten Schlangenkopf setzen
	public void setHeadLocation(int x, int y) {
		headLocation.setX(x);
		headLocation.setY(y);
	}

	//Koordinaten Schlangenkopf abrufen
	public Point getHeadLocation() {
		return headLocation;
	}
	
	//Koordinaten Schlangenschwanz abrufen
	public List<Point> getTail() {
		return tail;
	}

}
