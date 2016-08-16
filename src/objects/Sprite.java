package objects;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class Sprite {

	double x;
	double y;
	double width;
	double height;
	Image image;
	String imageName;
	
	public Sprite(double x, double y, double width, double height, String image){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.image = new Image("/images/" + image);
		this.imageName = image;
	}
	
	public double getXPosition(){return x;}
	public double getYPosition(){return y;}
	public void setXPosition(double v){x = v;}
	public void setYPosition(double v){y = v;}
	public double getWidth(){return width;}
	public double getHeight(){return height;}
	public Image getImage(){return image;}
	public String getImageName(){return imageName;}
	public void setImage(String to)
	{
		if (to == imageName) return; //don't lag plz
		imageName = to;
		image = new Image("/images/" + to);
	}
	
	public void draw(GraphicsContext gc){
		
		gc.drawImage(image, x, y, width, height);
		
	}
	
}
