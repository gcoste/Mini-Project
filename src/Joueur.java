import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;

public class Joueur {
	// limites de l'ecran
	Rectangle limitesframe;
	// carte sur lequel evolue l'objet
	Carte map;
	// bandeau du jeu pour acceder à la force
	Bandeau bandeau;

	// identifiant du joueur
	int n;
	String nom;
	boolean estHumain;
	int score;
	Color couleur;
	boolean actif;

	int[] arsenal;

	double vie;
	double fuel;
	// angle du canon
	double angle;
	// force du tir
	double force;

	// parametre IA pour la dichotomie
	double dico[];
	double defaut;

	final double GRAVITE = 0.1;

	LinkedList<Objet> Objets;
	LinkedList<Joueur> JoueursActifs;
	Tank tank;
	Canon canon;

	public Joueur(int num, int placement, int nombreJoueurs, String anom,
			String acouleur, boolean Humain, int nombreBombes, Carte amap,
			Rectangle aframe, Bandeau abandeau,
			LinkedList<Joueur> JoueursEnVie, LinkedList<Objet> Obj) {
		limitesframe = aframe;
		map = amap;
		bandeau = abandeau;

		arsenal = new int[nombreBombes];
		arsenal[0] = 100000;

		n = num;
		nom = anom;
		estHumain = Humain;
		actif = true;

		angle = 0;
		force = 0;

		vie = 100;
		fuel = 100;

		if (estHumain) {
			arsenal[1] = 25;
			arsenal[2] = 5;
			arsenal[3] = 1;

			defaut = 0;
			dico = new double[] { 0, 0 };
		} else {
			arsenal[1] = 10;

			defaut = 40 * Math.random() - 20;
			dico = new double[] { -20, 20 };
		}

		// on tranforme la couleur en texte en une couleur Java
		switch (n) {
		case (0):
			acouleur = "vert";
			couleur = new Color(77, 153, 5);
			break;
		case (1):
			acouleur = "rouge";
			couleur = new Color(153, 22, 5);
			break;
		case (2):
			acouleur = "bleu";
			couleur = new Color(125, 241, 244);
			break;
		case (3):
			acouleur = "jaune";
			couleur = new Color(222, 176, 0);
			break;
		case (4):
			acouleur = "violet";
			couleur = new Color(140, 40, 139);
			break;
		case (5):
			acouleur = "gris";
			couleur = new Color(144, 143, 144);
			break;
		case (6):
			acouleur = "marron";
			couleur = new Color(96, 78, 34);
			break;
		case (7):
			acouleur = "rose";
			couleur = new Color(234, 64, 209);
			break;
		default:
			acouleur = "vert";
			couleur = new Color(77, 153, 5);
			break;
		}

		tank = new Tank(this, placement, nombreJoueurs, "Tank_" + acouleur
				+ ".png");

		canon = tank.canon;

		JoueursActifs = JoueursEnVie;
		Objets = Obj;
	}

	public Bombe tire(double vent, String bombe, int n) {
		arsenal[n]--;

		Bombe obus = new Bombe(tank, vent, force + defaut, angle, bombe,
				JoueursActifs, Objets, GRAVITE);

		Thread tir = new Son("Tir.wav");
		tir.start();

		return obus;
	}

	public void moveGauche() {
		tank.dx = -1;
	}

	public void moveDroite() {
		tank.dx = 1;
	}

	public void fixe() {
		tank.dx = 0;
	}

	public void anglePlus() {
		if (angle < 180) {
			angle += 0.5;
		}
	}

	public void angleMoins() {
		if (angle > 0) {
			angle -= 0.5;
		}
	}

	public void touche(Bombe bombe, Iterator<Joueur> k) {
		double vieApresCoup = vie - bombe.dommage;
		bombe.rayonDegats(tank.getCenterX());

		// on corrige les degats afin que le tank ne se prenne pas deux fois le
		// coup
		vie = vieApresCoup;
		actif = true;
		tank.actif = true;
		canon.actif = true;

		map.destructionMap(bombe.dommage, (int) (tank.getCenterX()));

		if (vie <= 0) {
			k.remove();
			actif = false;
			tank.actif = false;
			canon.actif = false;
		}
	}

	public void degats(int k) {
		vie -= k;

		if (k == -1) {
			vie = 0;
		}

		if (vie <= 0) {
			actif = false;
			tank.actif = false;
			canon.actif = false;
		}
	}

	public double[] prevision(Tank t, double aforce) {
		double x = t.x + t.limites.getWidth() / 2 - (canon.x - 2);
		double z = (canon.y - 2) - t.y - t.limites.getHeight() / 2;

		double h = (Math.pow(0.15 * aforce, 2)) / (2 * GRAVITE);
		double delta = x * x - 4 * (z + ((x * x) / (4 * h)))
				* ((x * x) / (4 * h));
		double theta1 = Math.atan((-x + Math.sqrt(delta))
				/ (-2 * (x * x) / (4 * h)));
		double theta2 = Math.atan((-x - Math.sqrt(delta))
				/ (-2 * (x * x) / (4 * h)));

		if (x < 0) {
			theta1 += Math.PI;
			theta2 += Math.PI;

			double echange = theta1;
			theta1 = theta2;
			theta2 = echange;
		}

		if (delta < 0) {
			theta1 = -1;
			theta2 = -1;
		}

		return new double[] { Math.toDegrees(theta1), Math.toDegrees(theta2) };
	}

	public double testTir(double forceTest, double angleTest, Tank tankVise) {
		Bombe obus = new Bombe(tank, 0, forceTest, angleTest, "obus",
				JoueursActifs, Objets, GRAVITE);

		boolean dichot;
		boolean cestBon = false;

		if (tankVise == null) {
			dichot = false;
		} else {
			dichot = true;
		}

		// on verifie que la bombe a quitte le rectangle du tank qui l'a tiree
		// afin d'etre sur de ne pas entrainer une collision au debut du tir
		boolean bombePartie = false;

		boolean test = true;

		while (true) {
			obus.x += obus.dx;
			obus.y -= obus.dy;
			obus.dy -= GRAVITE;

			if (obus.dy < 0) {
				cestBon = true;
			}

			// on verifie que la bombe a quitte le rectangle du tank qui l'a
			// tiree afin d'etre sur de ne pas entrainer une collision au debut
			// du tir
			if (!(new Rectangle((int) obus.x, (int) obus.y, obus.l, obus.h))
					.intersects(tank.limites) && !bombePartie) {
				bombePartie = true;
			}

			obus.limites.setLocation((int) obus.x, (int) obus.y);

			// on test si la bombe touche la carte ou les bords du jeu
			if ((obus.x < 0 | obus.x >= limitesframe.width) && !dichot) {
				double xTest = obus.x;
				double yTest = obus.y;
				double dxTest = obus.dx;
				double dyTest = obus.dy;

				while (test) {
					xTest = xTest + dxTest;
					yTest = yTest - dyTest;
					dyTest = dyTest - GRAVITE;

					if ((xTest >= 0 && xTest < limitesframe.width)
							&& yTest < map.getY(xTest)) {
						test = false;
					} else if (yTest > limitesframe.height) {
						obus.actif = false;
						return -1;
					}
				}

			} else if (!dichot && obus.y >= map.getY(obus.x)) {
				obus.actif = false;
				return -1;
			} else if (dichot && obus.y >= tankVise.getCenterY() && cestBon) {
				return (obus.x - tankVise.getCenterX());
			}

			if (!dichot) {
				Iterator<Joueur> k = JoueursActifs.iterator();

				while (k.hasNext() && bombePartie) {
					Joueur J = (Joueur) k.next();

					if (obus.Collision(J.tank)) {
						obus.actif = false;
						return J.n;
					}
				}
			}
		}
	}

	public double getXCanon() {
		double a = Math.toRadians(angle);
		return (tank.canon.x + Math.cos(a) * 28);
	}

	public double getYCanon() {
		double a = Math.toRadians(angle);
		return (tank.canon.y - Math.sin(a) * 28);
	}

	public void move() {
		tank.move();
		canon.move();
	}
}
