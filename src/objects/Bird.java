package objects;

import javafx.scene.canvas.GraphicsContext;

public class Bird extends SolidSprite {
	
	private double velX = 0;
	private double velY = 0;
	
	public Bird(double x, double y, double width, double height, String image){
		super(x, y, width, height, image);
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
	
	@Override
	public void draw(GraphicsContext gc){
		
		gc.drawImage(image, x, y, width, height);
		
	}

}
