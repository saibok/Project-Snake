package application;

import java.util.Timer;
import java.util.TimerTask;

import backend.Direction;
import backend.Grid;
import backend.Point;
import backend.Snake;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SnakeGame extends Application {

	public static void main(String[] args) {
		Application.launch(args);
	}
	
	//Deklaration & Initiierung
	
    //Spielgeschwindigkeit
	//Durchfuehrungsfrequenz der Tasks
	private static final long TASK_UPDATE_PERIOD_MS = 150;
	private static final long TASK_UPDATE_DELAY_MS = TASK_UPDATE_PERIOD_MS;
	
	//Fenstergroesse
	private static final int WINDOW_HEIGHT = 400;
	private static final int WINDOW_WIDTH = 400;
	private static final int GRID_BLOCK_SIZE = 10;

	private GraphicsContext graphicsContext;
	private Button startButton;
	private Snake snake;
	private Grid grid;
	private AnimationTimer animationTimer;
	private Timer timer;
	private TimerTask task;

	private boolean isGameInProgress = false;
	private boolean isGameOver = false;
	private boolean isPaused = false;

		
	@Override
	public void start(Stage primaryStage) throws Exception {

		primaryStage.setTitle("Project Snake");
		Group root = new Group();
		Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
		graphicsContext = canvas.getGraphicsContext2D();
		root.getChildren().add(canvas);
		Scene scene = new Scene(root);

		snake = new Snake(WINDOW_WIDTH, WINDOW_HEIGHT, GRID_BLOCK_SIZE);
		grid = new Grid(WINDOW_WIDTH, WINDOW_HEIGHT, GRID_BLOCK_SIZE, snake);
		snake.setHeadLocation(GRID_BLOCK_SIZE, GRID_BLOCK_SIZE);

		drawGrid();
		
		startButton = new Button("Start!");
		startButton.setMinWidth(100);
		startButton.setMinHeight(36);

		VBox vBox = new VBox();
		vBox.prefWidthProperty().bind(canvas.widthProperty());
		vBox.prefHeightProperty().bind(canvas.heightProperty());
		vBox.setAlignment(Pos.CENTER);
		vBox.getChildren().add(startButton);

		startButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				isGameInProgress = true;
				isGameOver = false;
				startButton.setVisible(false);

				if (timer == null) {
					task = createTimerTask();
					timer = new Timer();
					timer.scheduleAtFixedRate(task, TASK_UPDATE_DELAY_MS, TASK_UPDATE_PERIOD_MS);
					animationTimer.start();
				}
			}
		});

		root.getChildren().add(vBox);

		
		//Steuerung 
		
		scene.setOnKeyPressed((e) -> {
			if (e.getCode() == KeyCode.UP) {
				snake.setDirection(Direction.UP);
			} else if (e.getCode() == KeyCode.DOWN) {
				snake.setDirection(Direction.DOWN);
			} else if (e.getCode() == KeyCode.LEFT) {
				snake.setDirection(Direction.LEFT);
			} else if (e.getCode() == KeyCode.RIGHT) {
				snake.setDirection(Direction.RIGHT);
			} else if (e.getCode() == KeyCode.P) {
				if (isPaused) {
					task = createTimerTask();
					timer = new Timer("Timer");
					timer.scheduleAtFixedRate(task, TASK_UPDATE_DELAY_MS, TASK_UPDATE_PERIOD_MS);
					isPaused = false;
				} else {
					timer.cancel();
					isPaused = true;
				}
			}
		});

		primaryStage.setScene(scene);
		primaryStage.show();

		animationTimer = new AnimationTimer() {
			@Override
			public void handle(long timestamp) {
				if (isGameInProgress) {
					drawGrid();
					drawSnake();
					drawFood();
					showScore();
				} else if (isGameOver) {
					animationTimer.stop();
					showEndGameAlert();
					startButton.setVisible(true);
					grid.reset();
					snake = new Snake(WINDOW_WIDTH, WINDOW_HEIGHT, GRID_BLOCK_SIZE);
					snake.setHeadLocation(GRID_BLOCK_SIZE, GRID_BLOCK_SIZE);
				}
			}
		};
		animationTimer.start();

		task = createTimerTask();
		timer = new Timer();
		timer.scheduleAtFixedRate(task, TASK_UPDATE_DELAY_MS, TASK_UPDATE_PERIOD_MS);
	}

	private TimerTask createTimerTask() {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (isGameInProgress) {
					snake.snakeUpdate();

					if (snake.collidedWithWall()) {
						endGame("collided with wall");
					} else if (snake.collidedWithTail()) {
						endGame("collided with tail");
					}

					boolean foundFood = grid.foundFood(snake);
					if (foundFood) {
						snake.addTailSegment();
						grid.addFood();
						System.out.println("Snake length: " + snake.getTail().size());
						
					}
				}
			}
		};
		return task;
	}

	private void endGame(String reason) {
		timer.cancel();
		timer = null;
		isGameInProgress = false;
		isGameOver = true;
		System.out.println("Game over: " + reason + " - Score: " + (snake.getTail().size()));
	}
	
	//Game Over Meldung
	private void showEndGameAlert() {
		String gameOverText = "Game Over! Score: " + (snake.getTail().size());
		double textWidth = getTextWidth(gameOverText);

		graphicsContext.setFill(Color.WHITE);
		graphicsContext.fillText(gameOverText, (WINDOW_WIDTH / 2) - (textWidth / 2), WINDOW_HEIGHT / 2 - 24);
	}

	private void drawGrid() {
		graphicsContext.setFill(Color.BLACK);
		graphicsContext.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

		/*
		graphicsContext.setStroke(Color.BLACK);
		graphicsContext.setLineWidth(0.5);

		for (int x = 0; x < WINDOW_WIDTH; x += GRID_BLOCK_SIZE) {
			graphicsContext.strokeLine(x, 0, x, x + WINDOW_HEIGHT);
		}

		for (int y = 0; y < WINDOW_HEIGHT; y += GRID_BLOCK_SIZE) {
			graphicsContext.strokeLine(0, y, y + WINDOW_WIDTH, y);
		}*/
	}

	//grafische Darstellung Schlange
	private void drawSnake() {
		graphicsContext.setFill(Color.WHITE);
		graphicsContext.fillRect(snake.getHeadLocation().getX(), snake.getHeadLocation().getY(), snake.getBlockSize(),
				snake.getBlockSize());
		for (Point tailSegment : snake.getTail()) {
			graphicsContext.fillRect(tailSegment.getX(), tailSegment.getY(), snake.getBlockSize(),
					snake.getBlockSize());
		}
	}

	//grafische Darstellung Essen
	private void drawFood() {
		graphicsContext.setFill(Color.RED);
		graphicsContext.fillRect(grid.getFood().getLocation().getX(), grid.getFood().getLocation().getY(),
				GRID_BLOCK_SIZE, GRID_BLOCK_SIZE);
	}

	//Textzentrierung
	private double getTextWidth(String string) {
		Text text = new Text(string);
		new Scene(new Group(text));
		return text.getLayoutBounds().getWidth();
	}
	
	//Punktestand
	private void showScore() {
	    String gameOverText = "Score: " + (snake.getTail().size());
	    graphicsContext.setFill(Color.WHITE);
	    graphicsContext.fillText(gameOverText, 10, 15);
	    }
	
	}
