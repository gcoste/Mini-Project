import java.awt.Rectangle;
import java.util.Iterator;
import java.util.LinkedList;

public class Caisse extends Objet {
	LinkedList<Joueur> JoueursActifs;

	double gravite;

	public Caisse(Carte amap, Rectangle aframe,
			LinkedList<Joueur> JoueursEnVie, double grav) {
		super(0, -50, 0, 0, 1, "Caisse.png", aframe, amap, null, null);

		JoueursActifs = JoueursEnVie;
		gravite = grav;

		Iterator<Joueur> k = JoueursActifs.iterator();

		boolean verif = true;

		// on verifie que la caisse n'apparait pas directement au-dessus d'un
		// tank
		while (verif) {
			x = aframe.width * Math.random();

			while (k.hasNext()) {
				Joueur J = (Joueur) k.next();

				if (Math.abs(x + limites.width / 2 - J.tank.getCenterX()) < 50) {
					verif = false;
					break;
				}
			}

			verif = !verif;
		}
	}

	public void move() {
		y += vitesse;
		vitesse += gravite;
	}
}
