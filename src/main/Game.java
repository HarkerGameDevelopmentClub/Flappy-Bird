package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import objects.Background;
import objects.Bird;
import objects.SolidSprite;
import objects.Sprite;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import listeners.KeyboardListener;

public class Game {
	
	Main main;
	List<Sprite> sprites = new ArrayList<Sprite>();
	KeyboardListener keyListener;
	int lastJump = -10000;
	
	double SPEED = 2; //  120 pixels a second
	double GRAVITY = 0.25;
	double JUMP_INTERVAL = 60;
	double JUMP_SPEED = 10;
	
	boolean running = true;
	
	int timeUntilRestart = 0;
	int points = 0;
	
	List<SolidSprite> scoringPipes = new ArrayList<SolidSprite>();
	
	Game game;
	
	public Game(Main main){
		this.main = main;
		this.game = this;
		
		start();
	}
	
	public void start(){
		
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
		
		SolidSprite pipe = new SolidSprite(gapX, 0, 100, gapY, "green.png");
		sprites.add(pipe);
		scoringPipes.add(pipe);
		sprites.add(new SolidSprite(gapX, gapY + gapHeight , 100, main.HEIGHT - gapY - gapHeight, "green.png"));
		
	}
	
	public void tick(int i){ // Calls every 1/60th of a second
		
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
		
		for(Sprite sprite : sprites){
			if(!(sprite instanceof Bird))
				sprite.setXPosition(sprite.getXPosition() - SPEED);
			else {
				Bird bird = (Bird)sprite;
				bird.setVelocityY(bird.getVelocityY() - GRAVITY);
				
				if(jump)
					bird.setVelocityY(JUMP_SPEED);
				
				bird.setYPosition(bird.getYPosition() - bird.getVelocityY());
				
				if(bird.getYPosition() < 0 || bird.getYPosition() > main.HEIGHT){
					this.stop();
					return;
				}
			}
		}
			
		for(int j = 0; j < sprites.size(); j++){
			Sprite sprite = sprites.get(j);
			if(!(sprite instanceof SolidSprite))
				continue;
			for(int k = j+1; k < sprites.size(); k++){
				Sprite other = sprites.get(k);
				if(!(other instanceof SolidSprite))
					continue;
				if(((SolidSprite)sprite).isColliding((SolidSprite)other))
					this.stop();
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
				double height = 150 + Math.random() * 300;
				this.addColumn(lastPipe.getXPosition() + lastPipe.getWidth() + 200 + Math.random() * 400, Math.random() * (main.HEIGHT - height), height);
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
					game.start();
					return;
				}
			}
				
		}, 1000, 1000);
	}
	
	public void draw(GraphicsContext gc){
		
		gc.clearRect(0, 0, main.WIDTH, main.HEIGHT);
		
		for(Sprite sprite : sprites)
			sprite.draw(gc);
		
		gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Comic Sans", FontWeight.BOLD, 48));
        
		if(!running)
			gc.fillText("Nope. " + timeUntilRestart, main.WIDTH/2-96, main.HEIGHT/2);
		
		gc.fillText(points+"", 100, 100);
	}

}
