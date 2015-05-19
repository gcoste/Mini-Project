import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;

public class Joueur {
	// limites de l'ecran
	Rectangle limitesframe;
	// carte sur lequel evolue l'objet
	Carte map;
	// permet d'acceder a certaines variables de jeu
	final float TEMPS;

	// identifiant du joueur
	int n;
	String nom;
	boolean estHumain;
	int score;
	Color couleur;
	boolean actif;

	final float GRAVITE = (float) 0.1;

	LinkedList<Joueur> JoueursActifs;
	Tank tank;
	Canon canon;

	public Joueur(int num, int placement, int nombreJoueurs, String anom,
			String acouleur, boolean Humain, Carte amap, Rectangle aframe,
			final float ATEMPS, LinkedList<Joueur> JoueursEnVie) {
		map = amap;
		limitesframe = aframe;
		TEMPS = ATEMPS;

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

		tank = new Tank(this, placement, nombreJoueurs, 2 * TEMPS, "Tank_"
				+ acouleur + ".png");

		canon = tank.canon;

		JoueursActifs = JoueursEnVie;
	}

	public Bombe tire(float force, float vent) {
		// ON DEVRA A TERME REMPLACER "obus" PAR LE TYPE DE BOMBE ARME
		Bombe obus = new Bombe(tank, force * TEMPS * 1.3, vent, "obus",
				JoueursActifs, GRAVITE);
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
			tank.angle += 5 * TEMPS;
		}
	}

	public void angleMoins() {
		if (tank.angle > 0) {
			tank.angle -= 5 * TEMPS;
		}
	}

	public void touche(Bombe bombe, Iterator k) {
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
			tank.vie = -1;
		}

		if (tank.vie <= 0) {
			JoueursActifs.remove(n);
			actif = false;
			tank.actif = false;
			canon.actif = false;
		}
	}

	public float prevision(float force, float vent) {
		Bombe obus = new Bombe(tank, force * TEMPS * 1.3, vent, "obus",
				JoueursActifs, GRAVITE);

		float retour = (float) 0.5;
		boolean test = true;

		while (retour == 0.5) {
			obus.x = obus.x + obus.dx;
			obus.y = obus.y - obus.dy;
			obus.dy = obus.dy - GRAVITE;
			obus.dx = obus.dx + vent;

			obus.limites.setLocation((int) obus.x, (int) obus.y);

			if (obus.x < 0 | obus.x >= limitesframe.width) {
				float xTest = obus.x;
				float yTest = obus.y;
				float dxTest = obus.dx;
				float dyTest = obus.dy;

				while (test) {
					xTest = xTest + dxTest;
					yTest = yTest - dyTest;
					dyTest = dyTest - GRAVITE;
					dxTest = dxTest + vent;

					if ((xTest >= 0 && xTest < limitesframe.width)
							&& yTest < map.getY(xTest)) {
						test = false;
					} else if (yTest > limitesframe.height) {
						retour = -1;
						test = false;
					}
				}
			} else if (obus.y >= map.getY(obus.x)) {
				retour = (int) obus.x;
			}

			Iterator k = JoueursActifs.iterator();

			while (k.hasNext()) {
				Joueur J = (Joueur) k.next();

				if (obus.Collision(J.tank)) {
					retour = (float) ((int) obus.x + (J.n+1) * 0.01);
				}
			}
		}

		obus.actif = false;
		return retour;
	}

}
