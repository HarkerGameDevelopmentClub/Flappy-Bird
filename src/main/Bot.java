package main;

import objects.Bird;
import objects.SolidSprite;

public class Bot {
	
	Game game;
	Bird bird;
	
	public Bot(Game game, Bird bird){
		
		this.game = game;
		this.bird = bird;
		
	}
	
	public void tick(int i){
		
		for(int j = 0; j < game.getPipes().size() - 1; j++){
			
			SolidSprite topPipe = game.getPipes().get(j);
			
			if(topPipe.getYPosition() == 0 && bird.getXPosition() <= topPipe.getXPosition() + topPipe.getWidth()){
				
				SolidSprite bottomPipe = game.getPipes().get(j+1);
				double y1 = topPipe.getYPosition() + topPipe.getHeight();
				double y2 = bottomPipe.getYPosition();
				double desY = (y2 - y1) / 3 * 2 + y1 - bird.getHitbox().height / 2;
				
				if(bird.getYPosition() > desY)
					bird.tryJump(game, i);
				return;
				
			}
		
		}
	}
	
}
