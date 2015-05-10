import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import java.io.File;

import javax.imageio.ImageIO;

public abstract class Object {

	protected String nom;
	protected int l, h;
	protected int x, y;
	protected float dx, dy;
	protected float vitesse;
	protected Image image;
	protected Rectangle limites;
	protected Rectangle limitesframe;
	protected int angle;
	protected int vitesseInitiale;
	protected int joueur;

	public Object(String nom, int ax, int ay, float adx, float ady,
			double vitesse, int vitesseInitiale, int angle, int joueur,
			String NomImage) {
		try {
			image = ImageIO.read(new File(NomImage));
		}

		catch (Exception err) {
			System.out.println(NomImage + " introuvable !");
			System.out.println("Mettre les images dans le repertoire : "
					+ getClass().getClassLoader().getResource(NomImage));
			System.exit(0);
		}

		// récupère une fois pour toute la hauteur et largeur de l'image
		h = image.getHeight(null);
		l = image.getWidth(null);

		// définir les limites de l'objet pour les collisions et les sorties
		limites = new Rectangle(ax, ay, l, h);
	}

	void draw(long t, Graphics g) {
		g.drawImage(image, x, y, null);
	}

	boolean Collision(Object O) {
		return limites.intersects(O.limites);
	}

	abstract class move {
		int x;
		int y;

		abstract public void simulate();

		abstract public void paint(Graphics g);
	}
}
