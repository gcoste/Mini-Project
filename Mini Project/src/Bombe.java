import java.awt.Graphics;

public class Bombe extends Object {

	public static final int puissance = 0;
	public double vitesse;
	public static String NomImage = "Bombe.png";
	public boolean actif;

	public Bombe(String nom, int l, int h, float x, float y, int angle,
			int force, int joueur, double avitesse) {

		super(nom, l, h, x, y, avitesse, force, angle, joueur, NomImage);

		this.vitesse = avitesse;
		this.actif = true;
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
