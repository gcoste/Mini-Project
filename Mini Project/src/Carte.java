import java.awt.*;

public class Carte {
	//hauteur et largeur de la carte
	int h, l;
	int n;
	
	//horizon de la carte pour l'instant
	int horizon;
	

	public Carte(Graphics g, Rectangle aframe) {
		int h = (int) aframe.getHeight();
		int l = (int) aframe.getWidth();
		int n = l;

		g.setColor(Color.blue);
		g.fillRect(0, 0, l, h);

		g.setColor(Color.green);
		int horizon = 2 * h / 3;

		/*
		 * METHODE DE STANY, A REVOIR
		 * 
		 * for (int i = 0; i < l / n; i++) { Random rand = new Random(); int
		 * randomNum = rand.nextInt(10) - 5;
		 * 
		 * int horizon2 = horizon + randomNum; int[] xpoints = new int[] { n *
		 * i, n * i, n * (i + 1), n * (i + 1) }; int[] ypoints = new int[] { h,
		 * horizon, horizon2, h }; g.fillPolygon(xpoints, ypoints, 4);
		 * 
		 * horizon = horizon2;
		 */
	}
	
	public int getY (int x) {
		return horizon;
	}

}
