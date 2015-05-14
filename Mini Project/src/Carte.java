import java.awt.*;

public class Carte {
	// hauteur et largeur de la carte
	static int h, l;

	// horizon de la carte pour l'instant
	int horizon;

	public Carte(Rectangle aframe) {
		h = (int) aframe.height;
		l = (int) aframe.width;
		horizon = 2 * h / 3;
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

	public void paint(Rectangle aframe, Graphics buffer) {
		h = (int) aframe.height;
		l = (int) aframe.width;
		
		// On remplit le buffer en bleu, ca se repercute sur l'arriere plan
		// (toujours definir la couleur avant de dessiner)
		buffer.setColor(Color.blue);
		buffer.fillRect(aframe.x, aframe.y, l, h);

		horizon = 2 * h / 3;
		buffer.setColor(Color.green);
		buffer.fillRect(aframe.x, horizon, l, h);
	}

	public int getY(int x) {
		return horizon;
	}

}
