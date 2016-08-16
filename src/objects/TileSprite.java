package objects;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class TileSprite extends SolidSprite {
	
	public TileSprite(double x, double y, double width, double height, String image) {
		super(x, y, width, height, image);
	}

	@Override
	public void draw(GraphicsContext gc){
		double x = getXPosition();
		double y = getYPosition();
		double width = getWidth();
		double height = getHeight();	
		Image image = getImage();

		double imageWidth = image.getWidth();
		double imageHeight = image.getHeight();
		double numDrawsY = (int) height / (int) imageHeight;
		double numDrawsX = (int) width / (int) imageWidth;
		double heightLeftOver = (int) height % (int) imageHeight;
		double widthLeftOver = (int) width % (int) imageWidth;
		for (int i = 0; i < numDrawsY; i++)
			for (int j = 0; j < numDrawsX; j++)
				gc.drawImage(image, x + j*imageWidth, y + i*imageHeight, imageWidth, imageHeight);
		gc.drawImage(image, x, y+numDrawsY*imageHeight, width, heightLeftOver);
		gc.drawImage(image, x+numDrawsX*imageWidth, y, widthLeftOver, height);
	}
}
