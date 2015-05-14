import java.awt.Rectangle;

public class Bombe extends Object {

	public static int dommage;
	public static double vitesse;
	public static String NomImage = "Bombe.png";

	public Bombe(int ax, int ay, int angle, int avitesse, Rectangle aframe, String nom, int joueur) {

		super(ax, ay, 0, 0, 10, NomImage, aframe, nom, joueur);

		if (nom.equals("bombe")) {
			dommage = 50;
		} else if (nom.equals("obus")) {
			dommage = 100;
		}
	}

	double dx;
	double dy;
	public static double G;

	public void Shot(double x, double y, Object b) {
		this.dx = x * Math.cos(b.angle);
		this.dy = y * Math.sin(b.angle);
		this.dx = b.vitesse;
	}

	public void simulate() {
		dy += G;
		x += (int) Math.round(dx);
		y += (int) Math.round(dy);
		/*
		 * if(y<=sol){ besoin des coordonnees du sol dy=0; dx=0; y=sol; }
		 */
	}

	/*
	 * public void paint (Graphics g){ besoin image bombe
	 * g.drawImage(BombeImage,x,y,this); }
	 */

	public void move(long t) {
		x = x + (int) (vitesse * dx);
		y = y + (int) (vitesse * dy);
		// on test si la bombe montant sort du haut de l'écran
		if (y + h < limitesframe.y) {
			this.actif = false;
		}
		// La bombe sera supprimee apres
		limites.setLocation(x, y);
	}
}
