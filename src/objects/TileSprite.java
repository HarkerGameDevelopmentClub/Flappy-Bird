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

		// All of the complete tiles
		for (int i = 0; i < numDrawsY; i++)
			for (int j = 0; j < numDrawsX; j++)
				gc.drawImage(image,x + j*imageWidth, y + i*imageHeight,imageWidth, imageHeight);

		// Vertically cropped tiles
		for (int i = 0; i < numDrawsX; i++)
			gc.drawImage(image, 0, 0, imageWidth, heightLeftOver,
					x + i*imageWidth, y + numDrawsY*imageHeight, imageWidth, heightLeftOver);
		// Horizontally cropped tiles
		for (int i = 0; i < numDrawsY; i++)
			gc.drawImage(image, 0, 0, widthLeftOver, imageHeight,
					x + numDrawsX*imageWidth, y + i*imageHeight, widthLeftOver, imageHeight);
		// Doubly cropped tile
		gc.drawImage(image, 0, 0, widthLeftOver, heightLeftOver,
				x + numDrawsX*imageWidth, y + numDrawsY*imageHeight, widthLeftOver, heightLeftOver);
	}
}
