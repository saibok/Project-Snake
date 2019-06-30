package backend;

import backend.Snake;
import backend.Point;
import java.util.Random;

public class Grid {
	private int height;
	private int width;
	private int pixelsPerSquare;
	Snake snake;
	Food food;

	public Grid(int width, int height, int pixelsPerSquare, Snake snake) {
		this.width = width;
		this.height = height;
		this.pixelsPerSquare = pixelsPerSquare;
		this.snake = snake;
		food = new Food(width / 2, height / 2);
	}

	public void reset() {
		food = new Food(width / 2, height / 2);
	}
	
	//Ueberpueft Kollision Schlangenkopf mit Essen
	public boolean foundFood(Snake snake) {
		boolean isIntersected = false;

		if (snake.getHeadLocation().equals(food.getLocation())) {
			isIntersected = true;
		}

		return isIntersected;
	}
	
	//Plaziert Essen & ueberprueft Kollision mit Schlange
	public void addFood() {
		Random rand = new Random();

		while (true) {
			int y = rand.nextInt(height);
			int x = rand.nextInt(width);

			x = Math.round(x / pixelsPerSquare) * pixelsPerSquare;
			y = Math.round(x / pixelsPerSquare) * pixelsPerSquare;

			Point p = new Point(x, y);

			if (snake.checkCollision(p)) {
				continue;
			}

			food = new Food(x, y);

			return;
		}
	}

	public Food getFood() {
		return food;
	}

}
