import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.Iterator;
import java.util.LinkedList;

public class Bombe extends Objet {

	private double gravite;
	private double vent;

	private Tank tank;

	public int dommage;

	// on verifie que la bombe a quitte le rectangle du tank qui l'a tiree afin
	// d'etre sur de ne pas entrainer une collision au debut du tir
	private boolean bombePartie;
	private boolean test;

	LinkedList<Joueur> JoueursActifs;

	public Bombe(Tank atank, double avent, double aforce, double angle,
			int dom, String nom, String nomImage,
			LinkedList<Joueur> ListJoueurs, double grav) {
		super(0, 0, 0, 0, aforce * 0.15, nomImage, atank.limitesFrame,
				atank.map, nom, atank.joueur);

		tank = atank;
		dommage = dom;

		gravite = grav;

		// on place la bombe en entree du canon
		x = joueur.canon.x - 1;
		y = joueur.canon.y - 1;

		bombePartie = false;
		test = true;

		// on regle dx et dy en fonction de l'angle
		// le 0 est a droite, le 180 est a gauche
		double a = Math.toRadians(angle);
		dx = Math.cos(a) * vitesse;
		dy = Math.sin(a) * vitesse;

		// le vent ne change pas pendant la course d'une bombe, on peut donc
		// le
		// stocker comme un attribut
		vent = avent;

		// on stocke la liste des joueurs encore presents afin de verifier si la
		// bombe tombe sur l'un d'eux
		JoueursActifs = ListJoueurs;
	}

	// constructeur pour l'attaque aerienne
	public Bombe(Tank atank, double ax, boolean droite,
			LinkedList<Joueur> ListJoueurs, double grav) {
		super(ax, 0, 1.5, 0, 0, "Bombe.png", atank.limitesFrame, atank.map,
				"roquette", atank.joueur);

		if (!droite) {
			dx = -1.5;
		}

		tank = atank;
		dommage = 20;

		gravite = grav;

		bombePartie = true;
		test = true;

		vent = 0;

		// on stocke la liste des joueurs encore presents afin de verifier si la
		// bombe tombe sur l'un d'eux
		JoueursActifs = ListJoueurs;
	}

	// constructeur pour la caisse piegee
	public Bombe(Tank atank, LinkedList<Joueur> ListJoueurs) {
		super(atank.getCenterX(), 0, 0, 0, 0, "Bombe.png", atank.limitesFrame,
				atank.map, "roquette", atank.joueur);
		tank = atank;
		dommage = 20;

		gravite = 0;

		bombePartie = true;
		test = true;

		vent = 0;

		// on stocke la liste des joueurs encore presents afin de verifier si la
		// bombe tombe sur l'un d'eux
		JoueursActifs = ListJoueurs;

		this.actif = false;
		rayonDegats(x);
		map.destructionMap(25, (int) x);

		Thread explosion = new Son("Explosion_roquette.wav");
		explosion.start();
	}

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
		}

		Iterator<Joueur> k = JoueursActifs.iterator();

		while (k.hasNext() && bombePartie && actif) {
			Joueur J = (Joueur) k.next();

			if (this.Collision(J.tank)) {
				this.actif = false;

				Thread explosion = new Son("Explosion_" + nom + ".wav");
				explosion.start();

				J.touche(this);
			}
		}

		// on verifie que la bombe a quitte le rectangle du tank qui l'a
		// tiree afin d'etre sur de ne pas entrainer une collision au debut
		// du tir
		if (!bombePartie
				&& !(new Rectangle((int) x, (int) y, l, h))
						.intersects(tank.limites)) {
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
					2 * rayon).intersects(J.tank.x, J.tank.y, J.tank.l,
					J.tank.h)) {
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

				if (cercle.intersects(J.tank.x, J.tank.y, J.tank.l, J.tank.h)) {
					J.degats((int) ((1 - a) * dommage));
					k2.remove();
				}
			}
		}
	}
}
