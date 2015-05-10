import java.awt.Graphics;

public class Bombe extends Object {

	public static final int puissance = 0;
	public static double vitesse;
	public static String NomImage = "Bombe.png";

	public Bombe(String nom, int l, int h, float x, float y, int angle,
			int force, int joueur, double vitesse) {

		super(nom, l, h, x, y, vitesse, force, angle, joueur, NomImage);

		this.vitesse = vitesse;
	}
}

public class Shot extends Object.move { // paramètre temps à utiliser
	double dx;
	double dy;
	public static double G;

	public Shot(double x, double y, Object b) {
		this.x = x * Math.cos(b.angle);
		this.y = y * Math.sin(b.angle);
		this.dx = b.vitesse;
	}

	public void simulate() {
		dy += G;
		x += (int) Math.round(dx);
		y += (int) Math.round(dy);
		/*
		 * if(y<=sol){ besoin des coordonnées du sol dy=0; dx=0; y=sol; }
		 */
	}

	/*
	 * public void paint (Graphics g){ besoin image bombe
	 * g.drawImage(BombeImage,x,y,this); }
	 */

	public void paint(Graphics g) {

	}

}