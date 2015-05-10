import java.awt.Rectangle;

public class Canon {

	public double x1, y1;
	public double x2, y2;
	public static int height = 0;
	public Rectangle canon;
	int angle;

	public Canon(Tank t, Rectangle canon, int angle) {

		this.x1 = t.y / 2;
		this.y1 = t.x / 2;
		this.canon = canon;
		this.angle = angle;
		this.x2 = height * Math.cos(angle);
		this.y2 = height * Math.sin(angle);

	}

	public void deplacement() { // dépend de l'angle peut etre pas utile ?//

	}

}