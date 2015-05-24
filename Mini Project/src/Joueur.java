import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;

public class Joueur {
	// limites de l'ecran
	Rectangle limitesframe;
	// carte sur lequel evolue l'objet
	Carte map;
	// bandeau du jeu pour acceder � la force
	Bandeau bandeau;

	// identifiant du joueur
	int n;
	String nom;
	boolean estHumain;
	int score;
	Color couleur;
	boolean actif;

	final double GRAVITE = 0.1;

	LinkedList<Joueur> JoueursActifs;
	Tank tank;
	Canon canon;

	public Joueur(int num, int placement, int nombreJoueurs, String anom,
			String acouleur, boolean Humain, Carte amap, Rectangle aframe,
			Bandeau abandeau, LinkedList<Joueur> JoueursEnVie) {
		limitesframe = aframe;
		map = amap;
		bandeau = abandeau;

		n = num;
		nom = anom;
		estHumain = Humain;
		actif = true;

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
			couleur = new Color(14, 1, 67);
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
		default:
			acouleur = "vert";
			couleur = new Color(77, 153, 5);
			break;
		}

		tank = new Tank(this, placement, nombreJoueurs, 0.2, "Tank_" + acouleur
				+ ".png");

		canon = tank.canon;

		JoueursActifs = JoueursEnVie;
	}

	public Bombe tire(double vent) {
		// ON DEVRA A TERME REMPLACER "obus" PAR LE TYPE DE BOMBE ARME
		Bombe obus = new Bombe(tank, vent, tank.angle, "obus", JoueursActifs,
				GRAVITE);
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
		if (tank.angle < 180) {
			tank.angle += 0.5;
		}
	}

	public void angleMoins() {
		if (tank.angle > 0) {
			tank.angle -= 0.5;
		}
	}

	public void touche(Bombe bombe, Iterator<Joueur> k) {
		tank.vie -= bombe.dommage;
		map.destructionMap(bombe.dommage,
				(int) (tank.x + tank.limites.width / 2));

		if (tank.vie <= 0) {
			k.remove();
			actif = false;
			tank.actif = false;
			canon.actif = false;
		}
	}

	public void degats(int k) {
		tank.vie -= k;

		if (k == 0) {
			tank.vie = 0;
		}

		if (tank.vie <= 0) {
			actif = false;
			tank.actif = false;
			canon.actif = false;
		}
	}

	public double[] prevision(Tank t) {
		double x = t.x + t.limites.getWidth()/2 - getXCanon();
		double z = getYCanon() - t.y - t.limites.getHeight()/2;

		double h = (Math.pow(0.15 * tank.force, 2)) / (2 * GRAVITE);
		double delta = x * x - 4 * (z + ((x * x) / (4 * h)))
				* ((x * x) / (4 * h));
		double theta1 = Math.atan((-x + Math.sqrt(delta)) / (-2 * (x * x) / (4 * h)));
		double theta2 = Math.atan((-x - Math.sqrt(delta)) / (-2 * (x * x) / (4 * h)));

		if (x < 0) {
			theta1 += Math.PI;
			theta2 += Math.PI;
			
			double echange = theta1;
			theta1 = theta2;
			theta2 = echange;
		}
		
		if (delta < 0) {
			theta1 = -1;
		}
		
		return new double[] {Math.toDegrees(theta1), Math.toDegrees(theta2)};
	}

	public int testTir(double angle) {
		Bombe obus = new Bombe(tank, 0, angle, "obus", JoueursActifs,
				GRAVITE);

		boolean test = true;

		while (true) {
			obus.x = obus.x + obus.dx;
			obus.y = obus.y - obus.dy;
			obus.dy = obus.dy - GRAVITE;

			obus.limites.setLocation((int) obus.x, (int) obus.y);

			if (obus.x < 0 | obus.x >= limitesframe.width) {
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
			} else if (obus.y >= map.getY(obus.x)) {
				obus.actif = false;
				return -1;
			}

			Iterator<Joueur> k = JoueursActifs.iterator();

			while (k.hasNext()) {
				Joueur J = (Joueur) k.next();

				if (obus.Collision(J.tank)) {
					obus.actif = false;
					return J.n;
				}
			}
		}
	}

	public double getX() {
		return (tank.x + tank.limites.width / 2);
	}

	public double getXCanon() {
		double a = Math.toRadians(tank.angle);
		return (tank.canon.x + Math.cos(a) * 40);
	}

	public double getYCanon() {
		double a = Math.toRadians(tank.angle);
		return (tank.canon.y - Math.sin(a) * 40);
	}
}
