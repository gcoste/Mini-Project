import java.awt.Rectangle;

public class Bombe extends Objet {

	static final double GRAVITE = 0.2;
	static int dommage;
	static String NomImage = "Bombe.png";

	public Bombe(int ax, int ay, double angle, int avitesse, Carte map, Rectangle aframe, String nom, int joueur) {

		super(ax, ay, 0, 0, avitesse, NomImage, aframe, map, nom, joueur);

		//on regle les dommages en fonction de la bombe
		if (nom.equals("bombe")) {
			dommage = 50;
		} else if (nom.equals("obus")) {
			dommage = 100;
		}
		
		//on régle dx et dy en fonction de l'angle
		// le 0 est à droite, le 180 est à gauche
		angle = Math.toRadians(angle);
		dx = (float) Math.cos(angle) * vitesse;
		dy = (float) Math.sin(angle) * vitesse;
	}

	public void move(long t) {
		
		x = x + (int) dx;
		y = y + (int) dy;
		dy = (float) (dy - GRAVITE);
		
		// on test si la bombe touche la carte
		// La bombe sera supprimee apres
		if (y <= map.getY(x)) {
			this.actif = false;
		}

		limites.setLocation(x, y);
	}
}
