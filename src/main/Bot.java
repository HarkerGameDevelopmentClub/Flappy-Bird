package main;

import objects.Bird;
import objects.SolidSprite;

public class Bot {
	
	Game game;
	Bird bird;
	
	double lastJump = -10000;
	
	public Bot(Game game, Bird bird){
		
		this.game = game;
		this.bird = bird;
		
	}
	
	public void tick(int i){
		
		for(SolidSprite pipe : game.pipes){
			
			if(pipe.getYPosition() > 0 && bird.getXPosition() <= pipe.getXPosition() + pipe.getWidth()){
				
				double desY = pipe.getYPosition() - bird.getHitbox().height * 3 / 2;
				
				if(bird.getYPosition() > desY){
					if(i - lastJump >= game.JUMP_INTERVAL){
						lastJump = i;
						bird.setVelocityY(game.JUMP_SPEED);
					}
				}
				
				return;
				
			}
		
		}
	}
	
}
