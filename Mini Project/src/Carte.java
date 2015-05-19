import java.awt.*;
import java.awt.image.BufferedImage;

public class Carte extends Objet {
	static final Color bleu = new Color(2, 13, 23);

	// horizon de la carte
	int[] horizon;

	public Carte(Rectangle aframe) {
		// on utilise le constructeur de Objet afin que le fond de la carte soit
		// affiché avec les autres objets (plus simple)
		super(0, 0, 0, 0, 0, "Fond.png", aframe, null, null, null);

		// on redimmensionne l'image de fond afin qu'elle s'adapte a la
		// resolution de l'ecran
		image = scaleImage(image,
				image.getWidth(null) * aframe.height / image.getHeight(null),
				aframe.height);

		int h = aframe.height;
		int l = aframe.width+1;

		// degre du polynome qui represente la carte
		int degre = (int) (20 * Math.random() + 1);

		// on cree un tableau avec les coefficients du polynome a, b, c ... de
		// type (x - a)*(x - b)*(x - c) ...
		double[] polynome = new double[degre];

		/*
		 * la suite est un algorythme personnel pour calculer un polynome qui ne
		 * depasserait pas de la carte et qui en meme temps n'aurait pas de
		 * courbe trop pentue (qui soit joli a affiche en gros !). Trop long a
		 * expliquer !
		 */
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

		// donc la notre polynome de base est termine

		// on cree un autre tableau avec des petites variations aleatoires afin
		// de rendre la map plus realiste
		double[] alea = new double[l];
		alea[0] = 0;

		int n = 10;

		// on attribue les petites variations tout les n points
		for (int i = 0; i < alea.length; i = i + n) {
			double ecart = 2 * Math.random() - 1;

			for (int k = 0; k < n && (i + k + 1) < alea.length; k++) {
				alea[i + k + 1] = alea[i + k] + (ecart * (n + 1) / n);
			}
		}

		// on ajoute finalement le polynome et les petites variations
		for (int i = 0; i < alea.length; i++) {
			horizon[i] += (int) alea[i];
		}

		// la fin du constructeur sert a verifier que la definition ne sort
		// toujours pas des limites du jeu (+offset pour etre sur qu'on voit
		// bien les tanks)
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

	public void drawHorizon(Rectangle aframe, Graphics buffer) {
		// Color random = new Color((int)(Math.random()*255),
		// (int)(Math.random()*255), (int)(Math.random()*255));

		/*
		 * On remplit la carte en bleu avec la definition (toujours definir la
 		 * couleur avant de dessiner). Le fond est dessine directement dans la
		 * classe Objet.
		 */
		buffer.setColor(bleu);
		for (int i = 0; i < horizon.length - 1; i++) {
			buffer.fillRect(i, horizon[i], 1, aframe.height - horizon[i]);
		}
	}

	public void destructionMap(int rayon, int x) {
		rayon = (int) (1.5 * rayon);
		int min = Math.max(1, x - rayon);
		int max = Math.min(limitesframe.width - 1, x + rayon);

		int[] temp = new int[limitesframe.width];

		for (int i = min; i < max; i++) {
			if (Math.sqrt(Math.pow(x - i, 2) + Math.pow(getY(x) - getY(i), 2)) <= rayon) {
				double h = (Math
						.sqrt(Math.pow(rayon, 2)
								- (Math.pow(x - i, 2) + Math.pow(getY(x)
										- getY(i), 2))));
				temp[i] = (int) h;
			}
		}

		for (int i = min; i < max; i++) {
			horizon[i] += temp[i];

			// lissage du resultat
			for (int k = 0; k < 10; k++) {
				int d = (horizon[i - 1] + horizon[i]) / 2;
				horizon[i - 1] = (horizon[i - 1] + d) / 2;
				horizon[i] = (horizon[i] + d) / 2;
			}
		}
	}

	// methode qui donne le y de la carte pour n'importe quel x (permet de
	// placer les objets sur la carte)
	public float getY(float x) {
		return horizon[(int) x];
	}

	// methode pour calculer un polynome
	public double calculPolynome(double x, double[] polynome) {
		double sum = 1;
		for (int p = 0; p < polynome.length; p++) {
			sum += sum * (x - polynome[p]);
		}
		return sum;
	}

	// methode pour le redimmensionnement de l'image
	public static Image scaleImage(Image source, int width, int height) {
		BufferedImage img = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(source, 0, 0, width, height, null);
		g.dispose();
		return img;
	}

	// on est oblige de definir cette methode meme si la carte ne "bouge" pas
	// puisqu'elle extends de Objet
	public void move(long t) {
	}

}
