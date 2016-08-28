package objects;

import main.Bot;
import main.Game;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Bird extends SolidSprite {
	
	private double velX = 0;
	private double velY = 0;
	private Bot bot = null;
	private int lastJump = -10000;
	private String key = "";
	private boolean dead = false;
	private int points = 0;
	private String color = "red";
	
	public Bird(double x, double y, double width, double height, String key, String color){
		super(x, y, width, height, color+"bird.png");
		
		this.key = key;
		this.color = color;
	}

	public void setVelocityX(double v){
		velX = v;
	}
	public void setVelocityY(double v){
		velY = v;
	}
	public double getVelocityX(){
		return velX;
	}
	public double getVelocityY(){
		return velY;
	}
	
	public Bot getBot(){return bot;}
	public void setBot(Bot bot){this.bot = bot;}
	public String getKey(){return key;}
	public boolean isDead(){return dead;}
	public void kill(){this.dead = true;}
	
	public void handleTick(Game game, int i){
		
		this.setVelocityY(this.getVelocityY() - game.GRAVITY);
		
		if(dead){
			this.setXPosition(this.getXPosition() - game.SPEED);
			this.setYPosition(this.getYPosition() - this.getVelocityY());
			return;
		}
		
		if(game.keyListener.isKeyPressed(key)){
			this.tryJump(game, i);
			
			if(bot != null)
				bot = null;
		}
		
		if(bot != null)
			bot.tick(i);
		
		if (this.getVelocityY() > 0)
			this.setImage(color+"birdup.png");
		else
			this.setImage(color+"bird.png");	
		
		this.setYPosition(this.getYPosition() - this.getVelocityY());
		
		for(SolidSprite pipe : game.getPipes()){
			double diff = this.getXPosition() - pipe.getXPosition() - pipe.getYPosition();
			if(diff < game.SPEED && diff >= 0){
				points++;
				return;
			}
		}
	}
	
	public boolean tryJump(Game game, int i){
		
		if(i - lastJump >= game.JUMP_INTERVAL){
			lastJump = i;
			this.setVelocityY(game.JUMP_SPEED);
			return true;
		}
		
		return false;
		
	}
	
	@Override
	public Hitbox getHitbox(){
		if(this.getVelocityY() > 0)
			return new Hitbox(x + width / 20, y + height / 20 * 3, 9 * width / 10, 15 * height / 20);
		else
			return new Hitbox(x + width / 10, y + height / 20, 9 * width / 10, 13 * height / 20);
	}
	
	@Override
	public void draw(GraphicsContext gc){

		gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Comic Sans", FontWeight.BOLD, 24));
        gc.fillText(points+"", x + width / 3, y - width / 2);
        gc.fillText(key, x+ width / 3, y + width * 3 / 2);
        
		gc.drawImage(image, x, y, width, height);
		
		
	}

}
