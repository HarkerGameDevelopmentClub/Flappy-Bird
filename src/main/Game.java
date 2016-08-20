package main;

import java.util.ArrayList;
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
	
	Main main;
	List<Sprite> sprites = new ArrayList<Sprite>();
	KeyboardListener keyListener;
	int lastJump = -10000;
	int lastToggle = -10000;
	
	double SPEED = -1; // 1 speed = 60 px/sec
	double SPEED_MIN = 2;
	double SPEED_MAX = 6;
	double GRAVITY = 0.35;
	double JUMP_INTERVAL = 40;
	double TOGGLE_INTERVAL = 30;
	double JUMP_SPEED = 10;
	
	double PIPE_MIN_GAP_X = 400;
	double PIPE_MIN_GAP_Y = 300;
	double PIPE_MAX_DEVIATION_X = 400;
	double PIPE_MAX_DEVIATION_Y = 300;
	
	Bird leader;
	
	List<Bot> bots = new ArrayList<Bot>();
	List<SolidSprite> pipes = new ArrayList<SolidSprite>();
	
	boolean running = true;
	
	int timeUntilRestart = 0;
	int points = 0;
	
	List<SolidSprite> scoringPipes = new ArrayList<SolidSprite>();
	
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
		
		start();
	}
	
	public void start(){

		SPEED = SPEED_MIN;

		points = 0;
		
		keyListener = new KeyboardListener(main.getScene());
		
		sprites.add(new Background(0, 0, main.WIDTH, main.HEIGHT, "background.png"));
		
		/* TOP BIRDS */
		sprites.add(new Bird(100, main.HEIGHT / 2.0 - 125, 50, 50, "bird.png"));
		sprites.add(new Bird(150, main.HEIGHT / 2.0 - 75, 50, 50, "bird.png"));
		/* CENTRAL BIRD */
		leader = new Bird(200, main.HEIGHT / 2.0 - 25, 50, 50, "bird.png");
		sprites.add(leader);
		/* BOTTOM BIRDS */
		sprites.add(new Bird(150, main.HEIGHT / 2.0 + 25, 50, 50, "bird.png"));
		sprites.add(new Bird(100, main.HEIGHT / 2.0 + 75, 50, 50, "bird.png"));
		
		this.addColumn(1000, 300, 480);
		this.addColumn(1500, 600, 480);
		this.addColumn(2000, 200, 450);
		
		running = true;
		
		this.setBots();
	}
	
	private void addCompanions(){
		sprites.add(new Bird(leader.getXPosition() - 100, leader.getYPosition() - 100, 50, 50, "bird.png"));
		sprites.add(new Bird(leader.getXPosition() - 50, leader.getYPosition() - 50, 50, 50, "bird.png"));
		
		sprites.add(new Bird(leader.getXPosition() - 50, leader.getYPosition() + 50, 50, 50, "bird.png"));
		sprites.add(new Bird(leader.getXPosition() - 100, leader.getYPosition() + 100, 50, 50, "bird.png"));
		
		if(bots.size() > 0)
			this.setBots();
	}
	
	private void setBots(){
		bots.clear();
		
		for(Sprite sprite : sprites)
			if(sprite instanceof Bird)
				bots.add(new Bot(this, (Bird)sprite));
	}
	
	private void addColumn(double gapX, double gapY, double gapHeight){
		TileSprite pipe = new TileSprite(gapX, 0, 100, gapY, "green.png");
		sprites.add(pipe);
		scoringPipes.add(pipe);
		pipes.add(pipe);
		
		pipe = new TileSprite(gapX, gapY + gapHeight , 100, main.HEIGHT - gapY - gapHeight, "green.png");
		sprites.add(pipe);
		pipes.add(pipe);
	}
	
	public void tick(int i){ // Calls every 1/60th of a second
		
		if(running)
			for(Bot bot : bots)
				bot.tick(i);
		
		SPEED = Math.min(SPEED_MAX, SPEED+0.001);		

		boolean jump = false;
		
		if(keyListener.isKeyPressed("ESCAPE")){
			System.err.println("GAME TERMINATED");
			System.exit(0);
			return;
		}
		else if(!running)
			return;
		else if(keyListener.isKeyPressed("SPACE")){
			if(i - lastJump >= JUMP_INTERVAL){
				lastJump = i;
				jump = true;
			}
		}
		else if(keyListener.isKeyPressed("B")){
			if(i - lastToggle >= TOGGLE_INTERVAL){
				if(bots.size() > 0)
					bots.clear();
				else
					this.setBots();
				
				lastToggle = i;
			}
		}
		else if(keyListener.isKeyPressed("V")){
			if(i - lastToggle >= TOGGLE_INTERVAL){
				
				lastToggle = i;
				
				boolean found = false;
				for(int j = sprites.size() - 1; j >= 0; j--){
					Sprite sprite = sprites.get(j);
					if(sprite instanceof Bird && !sprite.equals(leader)){
						found = true;
						sprites.remove(j);
					}
				}
				
				if(!found)
					this.addCompanions();
			}
		}
			
		
		for(int j = 0; j < sprites.size(); j++){
			Sprite sprite = sprites.get(j);
			if(!(sprite instanceof Bird))
				sprite.setXPosition(sprite.getXPosition() - SPEED);
			else {
				Bird bird = (Bird)sprite;
				bird.setVelocityY(bird.getVelocityY() - GRAVITY);
				
				if(jump)
					bird.setVelocityY(JUMP_SPEED);

				if (bird.getVelocityY() > 0)
					bird.setImage("birdup.png");
				else
					bird.setImage("bird.png");				
				bird.setYPosition(bird.getYPosition() - bird.getVelocityY());
				
				if(bird.getYPosition() < 0 || bird.getYPosition() > main.HEIGHT){
					this.stop();
					return;
				}

				for(int k = j+1; k < sprites.size(); k++){
					Sprite other = sprites.get(k);
					if(!(other instanceof SolidSprite) || other instanceof Bird)
						continue;
					if(bird.isColliding((SolidSprite)other))
						this.stop();
				}
			}
		}
		
		for(Sprite sprite : sprites){
			if(sprite instanceof Bird){
				Bird bird = (Bird) sprite;
				for(int j = scoringPipes.size() - 1; j >= 0; j--){
					SolidSprite pipe = scoringPipes.get(j);
					if(bird.getXPosition() >= pipe.getXPosition() + pipe.getWidth()){
						this.scorePoint();
						scoringPipes.remove(j);
					}
				}
			}
		}
		
		if(scoringPipes.size() > 0){
			SolidSprite lastPipe = scoringPipes.get(scoringPipes.size() - 1);
			if(lastPipe.getXPosition() + lastPipe.getWidth() + SPEED >= main.WIDTH && lastPipe.getXPosition() + lastPipe.getWidth() < main.WIDTH){
				double height = PIPE_MIN_GAP_Y + Math.random() * PIPE_MAX_DEVIATION_Y;
				this.addColumn(lastPipe.getXPosition() + lastPipe.getWidth() + PIPE_MIN_GAP_X + Math.random() * PIPE_MAX_DEVIATION_X, Math.random() * (main.HEIGHT - height), height);
			}
		}
		
		if(pipes.size() >= 2){
			if(pipes.get(0).getXPosition() + pipes.get(0).getWidth() <= 0){
				sprites.remove(pipes.remove(0));
				sprites.remove(pipes.remove(0));
			}
		}
	}
	
	private void scorePoint(){
		points++;
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
					sprites.clear();
					scoringPipes.clear();
					pipes.clear();
					game.start();
					return;
				}
			}
				
		}, 500, 500);
	}
	
	public void draw(GraphicsContext gc){
		
		gc.clearRect(0, 0, main.WIDTH, main.HEIGHT);
		
		for(Sprite sprite : sprites)
			sprite.draw(gc);
		
		gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Comic Sans", FontWeight.BOLD, 48));

		gc.fillText(points+"", 100, 100);
		
		if(!running)
			gc.fillText("Try again in "+timeUntilRestart, main.WIDTH/2-96, main.HEIGHT/2);
		else if(bots.size() > 0){
			gc.setFont(Font.font("Comic Sans", FontWeight.EXTRA_LIGHT, 24));
			gc.fillText("Press B to toggle bot mode", main.WIDTH/2-96, main.HEIGHT/2);
		}
	}

}
