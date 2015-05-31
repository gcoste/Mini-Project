import java.awt.Color;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.LinkedList;

public class Caisse extends Objet {
	Message message;

	final double GRAVITE = 0.1;
	boolean estPose;

	public Caisse(Carte amap, Rectangle aframe,
			LinkedList<Joueur> JoueursActifs, LinkedList<Caisse> Caisses,
			Message mes) {
		super(0, -50, 0, 0, 1, "Caisse.png", aframe, amap, null, null);

		message = mes;
		estPose = false;

		Iterator<Joueur> k1 = JoueursActifs.iterator();
		Iterator<Caisse> k2 = Caisses.iterator();

		boolean verif = true;

		// on verifie que la caisse n'apparait pas directement au-dessus d'un
		// tank ou d'une autre caisse
		while (verif) {
			x = (int) (aframe.width - limites.width) * Math.random();

			while (k1.hasNext()) {
				Joueur J = (Joueur) k1.next();

				if (Math.abs(this.getCenterX() - J.tank.getCenterX()) < 50) {
					verif = false;
					break;
				}
			}

			while (k2.hasNext()) {
				Caisse C = (Caisse) k2.next();

				if (Math.abs(this.getCenterX() - C.getCenterX()) < 50) {
					verif = false;
					break;
				}
			}

			verif = !verif;
		}
	}

	public void move() {
		y += vitesse;
		vitesse += GRAVITE;

		if (y + limites.height > map.getY(getCenterX())) {
			y = map.getY(getCenterX()) - limites.height;

			estPose = true;
		}

		limites.setLocation((int) x, (int) y);
	}

	public void actionCaisse(LinkedList<Joueur> JoueursActifs, long temps) {
		if (y != map.getY(getCenterX()) - limites.height && estPose) {
			this.actif = false;

		}

		Iterator<Joueur> k = JoueursActifs.iterator();

		while (k.hasNext()) {
			Joueur J = (Joueur) k.next();

			if (this.Collision(J.tank)) {
				this.actif = false;

				giveTank(J, temps);
			}
		}
	}

	public void giveTank(Joueur J, long temps) {
		double proba = 100 * Math.random();

		if (proba < 40) {
			if (proba < 25) {
				J.vie += 30;

				message.setMessage(temps, new Color(28, 142, 62), 2,
						"30 points de vie", "supplementaires");
			} else {
				J.vie += 50;

				message.setMessage(temps, new Color(28, 142, 62), 2,
						"50 points de vie", "supplementaires");
			}
		} else if (proba < 60) {
			if (proba < 55) {
				J.fuel += 30;

				if (J.fuel > 100) {
					J.fuel = 100;
				}

				message.setMessage(temps, new Color(128, 0, 128), 2,
						"50 litres de fioul", "supplementaires");
			} else {
				J.fuel = 100;

				message.setMessage(temps, new Color(128, 0, 128), 2,
						"Reservoir de fioul rempli", null);
			}
		} else {
			if (proba < 68) {
				J.arsenal[1] += 25;

				message.setMessage(temps, new Color(200, 0, 0), 2,
						"25 roquettes", null);
			} else if (proba < 83) {
				J.arsenal[2] += 5;

				message.setMessage(temps, new Color(200, 0, 0), 2, "5 obus",
						null);
			} else if (proba < 93) {
				J.arsenal[3]++;

				message.setMessage(temps, new Color(200, 0, 0), 2,
						"1 missile v2", null);
			} else if (proba < 98) {
				J.arsenal[4]++;

				message.setMessage(temps, new Color(200, 0, 0), 2,
						"1 ogive nucleaire", "soyez prudent");
			} else {
				J.arsenal[5]++;

				message.setMessage(temps, new Color(200, 0, 0), 2,
						"1 tsar bomba", "faites en bon usage");
			}
		}

	}
}
