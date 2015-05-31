import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.Iterator;
import java.util.LinkedList;

public class Bombe extends Objet {

	double gravite;
	double vent;

	Tank tank;

	int dommage;

	// on verifie que la bombe a quitte le rectangle du tank qui l'a tiree afin
	// d'etre sur de ne pas entrainer une collision au debut du tir
	boolean bombePartie;

	LinkedList<Objet> Objets;
	LinkedList<Joueur> JoueursActifs;

	public Bombe(Tank atank, double avent, double aforce, double angle,
			String nom, LinkedList<Joueur> ListJoueurs,
			LinkedList<Objet> Obj, double grav) {
		super(0, 0, 0, 0, aforce * 0.15, "Bombe.png", atank.limitesFrame,
				atank.map, nom, atank.joueur);

		tank = atank;

		gravite = grav;

		// on place la bombe en entree du canon
		x = joueur.canon.x - 1;
		y = joueur.canon.y - 1;

		// on regle les dommages en fonction du type de bombe
		if (nom.equals("gun")) {
			dommage = 10;
		} else if (nom.equals("roquettes")) {
			dommage = 25;
		} else if (nom.equals("obus")) {
			dommage = 50;
		} else if (nom.equals("v2")) {
			dommage = 80;
		} else if (nom.equals("ogive")) {
			dommage = 100;
		} else if (nom.equals("tsar bomba")) {
			dommage = 200;
		}

		bombePartie = false;

		// on stocke la liste des joueurs encore presents afin de verifier si la
		// bombe tombe sur l'un d'eux
		JoueursActifs = ListJoueurs;
		Objets = Obj;

		// on regle dx et dy en fonction de l'angle
		// le 0 est a droite, le 180 est a gauche
		double a = Math.toRadians(angle);
		dx = Math.cos(a) * vitesse;
		dy = Math.sin(a) * vitesse;

		// le vent ne change pas pendant la course d'une bombe, on peut donc le
		// stocker comme un attribut
		vent = avent;
	}

	boolean test = true;

	public void move() {
		x += dx;
		y -= dy;
		dy -= gravite;
		dx += vent;

		// on test si la bombe touche la carte ou les bords du jeu
		// on la desactive et la bombe sera supprimee apres
		if (x < 0 | x >= limitesFrame.width) {
			double xTest = x;
			double yTest = y;
			double dxTest = dx;
			double dyTest = dy;

			while (test) {
				xTest = xTest + dxTest;
				yTest = yTest - dyTest;
				dyTest = dyTest - gravite;
				dxTest = dxTest + vent;

				if ((xTest >= 0 && xTest < limitesFrame.width)
						&& yTest < map.getY(xTest)) {
					test = false;
				} else if (yTest > limitesFrame.height) {
					this.actif = false;
					test = false;
				}
			}
		} else if (y >= map.getY(x) && actif) {
			
			this.actif = false;
			rayonDegats(x);
			map.destructionMap(this.dommage, (int) x);

			Thread explosion = new Son("Explosion_" + nom + ".wav");
			explosion.start();
			
			// on balaye la liste et on fait bouger tout les objets avec
			// la classe move qui leur est propre, 3x pour placer les tanks
			// au sol apr�s explosion
			for (int u = 0; u < 3; u++) {

				Iterator<Objet> k = Objets.iterator();

				while (k.hasNext()) {
					Objet O = (Objet) k.next();
					O.move();
				}
			}
		}

		Iterator<Joueur> k = JoueursActifs.iterator();

		while (k.hasNext() && bombePartie && actif) {
			Joueur J = (Joueur) k.next();

			if (this.Collision(J.tank)) {
				this.actif = false;

				Thread explosion = new Son("Explosion_" + nom + ".wav");
				explosion.start();

				J.touche(this, k);
				
				// on balaye la liste et on fait bouger tout les objets avec
				// la classe move qui leur est propre, 3x pour placer les tanks
				// au sol apr�s explosion
				for (int u = 0; u < 3; u++) {

					Iterator<Objet> k1 = Objets.iterator();

					while (k1.hasNext()) {
						Objet O = (Objet) k1.next();
						O.move();
					}
				}
			}
		}

		// on verifie que la bombe a quitte le rectangle du tank qui l'a
		// tiree afin d'etre sur de ne pas entrainer une collision au debut
		// du tir
		if (!(new Rectangle((int) x, (int) y, l, h)).intersects(tank.limites)
				&& !bombePartie) {
			bombePartie = true;
		}

		limites.setLocation((int) x, (int) y);
	}

	public void rayonDegats(double x1) {
		int rayon = 2 * dommage;
		double y1 = map.getY(x1);

		LinkedList<Joueur> JoueursProches = new LinkedList<Joueur>();

		Iterator<Joueur> k = JoueursActifs.iterator();

		while (k.hasNext()) {
			JoueursProches.add((Joueur) k.next());
		}

		Iterator<Joueur> k1 = JoueursProches.iterator();

		while (k1.hasNext()) {
			Joueur J = (Joueur) k1.next();

			if (new Ellipse2D.Double(x1 - rayon, y1 - rayon, 2 * rayon,
					2 * rayon).intersects(J.tank.x, J.tank.y,
					J.tank.limites.width, J.tank.limites.height)) {
			} else {
				k1.remove();
			}
		}

		for (double a = 0; a <= 1; a += 0.05) {

			Ellipse2D.Double cercle = new Ellipse2D.Double(x1 - rayon * a, y1
					- rayon * a, a * 2 * rayon, a * 2 * rayon);

			Iterator<Joueur> k2 = JoueursProches.iterator();

			while (k2.hasNext()) {
				Joueur J = (Joueur) k2.next();
				// System.out.println(cercle.intersects(J.tank.limites));

				if (cercle.intersects(J.tank.x, J.tank.y, J.tank.limites.width,
						J.tank.limites.height)) {
					J.degats((int) ((1 - a) * dommage));
					k2.remove();
				}
			}
		}
	}
}
