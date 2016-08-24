package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import objects.Background;
import objects.Bird;
import objects.SolidSprite;
import objects.Sprite;
import objects.TileSprite;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import listeners.KeyboardListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class Game {
	
	public Main main;
	public KeyboardListener keyListener;
	
	Background background;
	
	public double SPEED = -1; // 1 speed = 60 px/sec
	public double SPEED_MIN = 2;
	public double SPEED_MAX = 12;
	public double GRAVITY = 0.35;
	public double JUMP_INTERVAL = 40;
	public double TOGGLE_INTERVAL = 30;
	public double JUMP_SPEED = 10;
	
	public double PIPE_MIN_GAP_X = 300;
	public double PIPE_MIN_GAP_Y = 250;
	public double PIPE_MAX_DEVIATION_X = 400;
	public double PIPE_MAX_DEVIATION_Y = 300;
	
	List<Bird> birds = new ArrayList<Bird>();
	List<SolidSprite> pipes = new ArrayList<SolidSprite>();
	
	boolean running = true;
	
	int timeUntilRestart = 0;
	
	Game game;

	MediaPlayer player;
	
	public Game(Main main){
		this.main = main;
		this.game = this;

		// Play music
		URL path = getClass().getResource("../music/bgm2.mp3");
		Media music = new Media(path.toString());
		player = new MediaPlayer(music);
		player.setCycleCount(MediaPlayer.INDEFINITE);
		player.setAutoPlay(true);		
		
		keyListener = new KeyboardListener(main.getScene());
		background = new Background(0, 0, main.WIDTH, main.HEIGHT, "background.png");
		
		start();
	}
	
	public List<SolidSprite> getPipes(){return pipes;}
	
	public void start(){

		SPEED = SPEED_MIN;

		this.addBird(new Bird(200, main.HEIGHT / 2.0 - 25, 50, 50, "bird.png", "Q"));
		this.addBird(new Bird(150, main.HEIGHT / 2.0 - 50, 50, 50, "bird.png", "R"));
		this.addBird(new Bird(100, main.HEIGHT / 2.0 - 75, 50, 50, "bird.png", "U"));
		this.addBird(new Bird(50, main.HEIGHT / 2.0 - 100, 50, 50, "bird.png", "P"));
		
		this.addColumn(1000, 300, 480);
		this.addColumn(1500, 600, 400);
		this.addColumn(2000, 200, 450);
		
		running = true;
	}
	
	private void addBird(Bird bird){
		birds.add(bird);
		bird.setBot(new Bot(this, bird));
	}
	
	public void removeBird(Bird bird){
		birds.remove(bird);
	}
	
	private void addColumn(double gapX, double gapY, double gapHeight){
		TileSprite pipe = new TileSprite(gapX, 0, 100, gapY, "green.png");
		pipes.add(pipe);
		
		pipe = new TileSprite(gapX, gapY + gapHeight , 100, main.HEIGHT - gapY - gapHeight, "green.png");
		pipes.add(pipe);
	}
	
	public void tick(int i){ // Calls every 1/60th of a second
		
		SPEED = Math.min(SPEED_MAX, SPEED+0.001);		

		if(keyListener.isKeyPressed("ESCAPE")){
			System.err.println("GAME TERMINATED");
			System.exit(0);
			return;
		}
		else if(!running)
			return;
		else if(keyListener.isKeyPressed("ENTER")){
			this.stop();
			return;
		}
		
		background.setXPosition(background.getXPosition() - SPEED);
		
		for(int j = pipes.size() - 1; j >= 0; j--){
			SolidSprite pipe = pipes.get(j);
			pipe.setXPosition(pipe.getXPosition() - SPEED);
			if(pipe.getXPosition() + pipe.getWidth() <= 0)
				pipes.remove(j);
		}
		
		for(Bird bird : birds){
			bird.handleTick(this, i);
			
			if(bird.getYPosition() < 0 || bird.getYPosition() + bird.getHeight() > main.HEIGHT){
				if(this.killBird(bird)){
					this.stop();
					return;
				}
			}
			
			for(SolidSprite pipe : pipes){
				if(bird.isColliding(pipe)){
					if(this.killBird(bird)){
						this.stop();
						return;
					}
				}
			}
		}
			
		if(pipes.size() > 0){
			SolidSprite lastPipe = pipes.get(pipes.size() - 1);
			if(lastPipe.getXPosition() + lastPipe.getWidth() + SPEED >= main.WIDTH && lastPipe.getXPosition() + lastPipe.getWidth() < main.WIDTH){
				double height = PIPE_MIN_GAP_Y + Math.random() * PIPE_MAX_DEVIATION_Y;
				this.addColumn(lastPipe.getXPosition() + lastPipe.getWidth() + PIPE_MIN_GAP_X + Math.random() * PIPE_MAX_DEVIATION_X, Math.random() * (main.HEIGHT - height), height);
			}
		}
	}
	
	private boolean killBird(Bird bird){
		// True if all dead
		
		bird.kill();
		
		for(Bird berd : birds)
			if(!berd.isDead())
				return false;
			
		return true;
	}
	
	private void stop(){
		running = false;
		
		timeUntilRestart = 3;
		
		new Timer().schedule(new TimerTask(){
			@Override
			public void run(){
				
				timeUntilRestart--;
				
				if(timeUntilRestart <= 0){
					this.cancel();
					pipes.clear();
					birds.clear();
					game.start();
					return;
				}
			}
				
		}, 500, 500);
	}
	
	public void draw(GraphicsContext gc){
		
		gc.clearRect(0, 0, main.WIDTH, main.HEIGHT);
		
		background.draw(gc);
		for(SolidSprite pipe : pipes)
			pipe.draw(gc);
		for(Bird bird : birds)
			bird.draw(gc);
		
		gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Comic Sans", FontWeight.BOLD, 48));

		//gc.fillText(points+"", 100, 100);
		
		if(!running)
			gc.fillText("Try again in "+timeUntilRestart, main.WIDTH/2-96, main.HEIGHT/2);
	}

}
