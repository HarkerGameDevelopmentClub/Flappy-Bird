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
	double BOT_TOGGLE_INTERVAL = 30;
	double JUMP_SPEED = 10;
	
	Bot bot = null;
	
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
		
		bot = new Bot(this);
	}
	
	public void start(){

		SPEED = SPEED_MIN;

		points = 0;
		
		keyListener = new KeyboardListener(main.getScene());
		
		sprites.add(new Background(0, 0, main.WIDTH, main.HEIGHT, "background.png"));
		sprites.add(new Bird(200, main.HEIGHT / 2.0 - 25, 50, 50, "bird.png"));

		/*SolidSprite pipe = new SolidSprite(1000, 0, 100, 300, "green.png");
		sprites.add(pipe);
		scoringPipes.add(pipe);
		sprites.add(new SolidSprite(1000, main.HEIGHT - 300, 100, 300, "green.png"));
		
		pipe = new SolidSprite(1500, 0, 100, 600, "green.png");
		sprites.add(pipe);
		scoringPipes.add(pipe);
		sprites.add(new SolidSprite(1500, main.HEIGHT - 200, 100, 200, "green.png"));
		
		sprites.add(new SolidSprite(3000, 0, 100, main.HEIGHT, "green.png"));*/
		
		this.addColumn(1000, 300, 480);
		this.addColumn(1500, 600, 280);
		this.addColumn(2000, 100, 200);
		//this.addColumn(3000, main.HEIGHT / 2, 0);
		
		running = true;
		
	}
	
	private void addColumn(double gapX, double gapY, double gapHeight){
		TileSprite pipe = new TileSprite(gapX, 0, 100, gapY, "green.png");
		sprites.add(pipe);
		scoringPipes.add(pipe);
		sprites.add(new TileSprite(gapX, gapY + gapHeight , 100, main.HEIGHT - gapY - gapHeight, "green.png"));
		
	}
	
	public void tick(int i){ // Calls every 1/60th of a second
		
		if(running && bot != null)
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
			if(i - lastToggle >= BOT_TOGGLE_INTERVAL){
				if(bot == null)
					bot = new Bot(this);
				else
					bot = null;
				
				lastToggle = i;
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
					if(!(other instanceof SolidSprite))
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
				double height = 200 + Math.random() * 300;
				this.addColumn(lastPipe.getXPosition() + lastPipe.getWidth() + 250 + Math.random() * 400, Math.random() * (main.HEIGHT - height), height);
			}
		}
		
		if(sprites.size() >= 4){
			if(sprites.get(2).getXPosition() + sprites.get(2).getWidth() <= 0){
				sprites.remove(2);
				sprites.remove(2);
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
		else if(bot != null){
			gc.setFont(Font.font("Comic Sans", FontWeight.EXTRA_LIGHT, 24));
			gc.fillText("Press B to toggle bot mode", main.WIDTH/2-96, main.HEIGHT/2);
		}
	}

}
