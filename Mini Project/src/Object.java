import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import java.io.File;

import javax.imageio.ImageIO;

public abstract class Object {

	public String nom;
	public int l, h;
	public static int x, y;
	public float dx, dy;
	public float vitesse;
	public static Image image;
	public Rectangle limites;
	public Rectangle limitesframe;
	public int angle;
	public int vitesseInitiale;
	public int joueur;

	public Object(String nom, int ax, int ay, float adx, float ady,	double vitesse, int vitesseInitiale, int angle, int joueur, String NomImage) {
		try {
			image = ImageIO.read(new File(NomImage));
		}

		catch (Exception err) {
			System.out.println(NomImage + " introuvable !");
			System.out.println("Mettre les images dans le repertoire : "
					+ getClass().getClassLoader().getResource(NomImage));
			System.exit(0);
		}

		// r�cup�re une fois pour toute la hauteur et largeur de l'image
		h = image.getHeight(null);
		l = image.getWidth(null);

		// d�finir les limites de l'objet pour les collisions et les sorties
		limites = new Rectangle(ax, ay, l, h);
	}

	static void draw(long t, Graphics g) {
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
