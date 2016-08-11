package objects;

import javafx.scene.canvas.GraphicsContext;

public class Background extends Sprite {

	public Background(double x, double y, double width, double height, String image) {
		super(x, y, width, height, image);
	}
	
	@Override
	public void draw(GraphicsContext gc){
		if(this.getXPosition() <= this.width * -1)
			this.setXPosition(0);
		if(this.getXPosition() != 0)
			gc.drawImage(this.image, this.getXPosition() + this.width, this.getYPosition(), this.width, this.height);
		
		gc.drawImage(this.image, this.getXPosition(), this.getYPosition(), this.width, this.height);
	}

}
