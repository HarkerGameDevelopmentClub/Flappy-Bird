package objects;

public class SolidSprite extends Sprite {
	
	public SolidSprite(double x, double y, double width, double height, String image) {
		super(x, y, width, height, image);
	}
	
	public boolean isColliding(SolidSprite other){
		double[] mtv = this.getMTV(other);
		return mtv[0] != 0 || mtv[1] != 0;
	}
	
	private double[] getMTV(SolidSprite other){ // Minimum Translation Vector
		// This is a modification for rectangles of the Separating Axis Theorem Algorithm.
		
		double[] mtv = {0.0, 0.0};
		
		// Total width - projected length = projected overlap
		double xOverlap = (this.width + other.width) - Math.max(other.x + other.width - this.x, this.x + this.width - other.x);
		double yOverlap = (this.height + other.height) - Math.max(other.y + other.height - this.y, this.y + this.height - other.y);
		 
		if(xOverlap > 0 && yOverlap > 0){
			if(xOverlap < yOverlap)
				mtv[0] = xOverlap;
			else
				mtv[1] = yOverlap;
		}
		
		return mtv;
		//return (between(t, other.t, other.b) || between(b, other.t, other.b)) && (between(l, other.l, other.r) || between(r, other.l, other.r));
	}

}
