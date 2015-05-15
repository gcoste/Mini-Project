import java.awt.*;

public class Carte {
	static final Color bleu = new Color(49, 103, 163);
	static final Color vert = new Color(0, 64, 0);

	// horizon de la carte
	int[] horizon;

	public Carte(Rectangle aframe) {
		int h = (int) aframe.height;

		horizon = new int[aframe.width];

		horizon[0] = (int) (7 * h / 10 + h / 10 * Math.random());

		for (int i = 0; i < horizon.length - 1; i++) {
			horizon[i + 1] = horizon[i] + (int) (4 * Math.random() - 2);

			if (horizon[i + 1] > 19 * h / 20) {
				horizon[i + 1] = 19 * h / 20;
			} else if (horizon[i + 1] < 100) {
				horizon[i + 1] = 100;
			}
		}
	}

	public void draw(Rectangle aframe, Graphics buffer) {
		// Color random = new Color((int)(Math.random()*255),
		// (int)(Math.random()*255), (int)(Math.random()*255));

		// On remplit le buffer en bleu, ca se repercute sur l'arriere plan
		// (toujours definir la couleur avant de dessiner)
		buffer.setColor(bleu);
		buffer.fillRect(aframe.x, aframe.y, aframe.width, aframe.height);

		buffer.setColor(vert);
		for (int i = 0; i < horizon.length - 1; i++) {
			buffer.fillRect(i, horizon[i], 1, aframe.height - horizon[i]);
		}
	}

	public float getY(float x) {
		return horizon[(int) x];
	}

}
