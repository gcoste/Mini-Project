import java.awt.*;

public class Carte {
	static final Color bleu = new Color(49, 103, 163);
	static final Color vert = new Color(0, 64, 0);

	// horizon de la carte
	int[] horizon;

	public Carte(Rectangle aframe) {
		int h = aframe.height;
		int l = aframe.width;

		int degre = (int) (20 * Math.random() + 1);

		double[] polynome = new double[degre];

		for (int i = 0; i < degre; i++) {
			polynome[i] = (l * 2) / degre * Math.random() + i * (l * 2) / degre;
		}

		double[] horizonTemp = new double[l];
		double max = 0;
		double min = 0;

		for (int i = 0; i < l; i++) {
			horizonTemp[i] = calculPolynome(i + (int) (l / 2), polynome);

			if (max < horizonTemp[i]) {
				max = horizonTemp[i];
			}

			if (min > horizonTemp[i]) {
				min = horizonTemp[i];
			}
		}

		for (int i = 0; i < horizonTemp.length; i++) {
			horizonTemp[i] -= min;
		}

		max -= min;

		for (int i = 0; i < horizonTemp.length; i++) {
			horizonTemp[i] = (horizonTemp[i] / max) * (h - 400) + 200;
		}

		horizon = new int[l];

		for (int i = 0; i < horizonTemp.length; i++) {
			horizon[i] = (int) horizonTemp[i];
		}

		double[] alea = new double[l];
		alea[0] = 0;

		int n = 10;

		for (int i = 0; i < alea.length; i = i + n) {
			double ecart = 2 * Math.random() - 1;

			for (int k = 0; k < n && (i + k + 1) < alea.length; k++) {
				alea[i + k + 1] = alea[i + k] + (ecart * (n + 1) / n);
			}
		}

		for (int i = 0; i < alea.length; i++) {
			horizon[i] += (int) alea[i];
		}

		max = min = horizon[0];

		for (int i = 0; i < l; i++) {
			if (max < horizon[i]) {
				max = horizon[i];
			}

			if (min > horizon[i]) {
				min = horizon[i];
			}
		}

		for (int i = 0; i < horizon.length; i++) {
			horizon[i] -= min;
		}

		max -= min;

		for (int i = 0; i < horizon.length; i++) {
			horizon[i] = (int) ((horizon[i] / max) * (h - 500) + 300);
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

	public double calculPolynome(double x, double[] polynome) {
		double sum = 1;
		for (int p = 0; p < polynome.length; p++) {
			sum += sum * (x - polynome[p]);
		}
		return sum;
	}

}
