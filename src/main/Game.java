package main;

import java.util.ArrayList;
import java.util.List;

import objects.Background;
import objects.Bird;
import objects.Sprite;
import javafx.scene.canvas.GraphicsContext;
import listeners.KeyboardListener;

public class Game {
	
	Main main;
	List<Sprite> sprites = new ArrayList<Sprite>();
	KeyboardListener keyListener;
	int lastJump = -10000;
	
	double SPEED = 1; //  60 pixels a second
	double GRAVITY = 0.25;
	double JUMP_INTERVAL = 60;
	double JUMP_SPEED = 10;
	
	public Game(Main main){
		this.main = main;
		
		start();
	}
	
	public void start(){
		
		keyListener = new KeyboardListener(main.getScene());
		
		sprites.add(new Background(0, 0, main.WIDTH, main.HEIGHT, "background.png"));
		sprites.add(new Bird(200, main.HEIGHT / 2.0 - 25, 50, 50, "bird.png"));
	}
	
	public void tick(int i){ // Calls every 1/60th of a second
		
		boolean jump = false;
		
		if(keyListener.isKeyPressed("ESCAPE")){
			System.err.println("GAME TERMINATED");
			System.exit(0);
			return;
		}
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
			}
		}
		
	}
	
	public void draw(GraphicsContext gc){
		
		gc.clearRect(0, 0, main.WIDTH, main.HEIGHT);
		
		for(Sprite sprite : sprites)
			sprite.draw(gc);
	}

}
