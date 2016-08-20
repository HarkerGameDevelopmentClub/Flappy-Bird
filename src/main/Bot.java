package main;

import objects.Bird;
import objects.Sprite;


public class Bot {
	
	Game game;
	
	public Bot(Game game){
		
		this.game = game;
		
	}
	
	public void tick(int i){
		
		if(game.scoringPipes.size() <= 0)
			return;
		
		Bird bird = null;
		for(Sprite sprite : game.sprites){
			if(sprite instanceof Bird){
				bird = (Bird)sprite;
				break;
			}
		}
		if(bird == null)
			return;
		
		double desY = game.sprites.get(game.sprites.indexOf(game.scoringPipes.get(0)) + 1).getYPosition() - bird.getHitbox().height * 3 / 2;
	//	desX = game.scoringPipes.get(0).getXPosition() + game.scoringPipes.get(0).getWidth();
		
		if(bird.getYPosition() > desY)
			game.keyListener.input.add("SPACE");
		else
			game.keyListener.input.remove("SPACE");
	}
	
}
