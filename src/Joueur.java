import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;

public class Joueur {
	// limites de l'ecran
	private Rectangle limitesFrame;
	// carte sur lequel evolue l'objet
	private Carte map;

	// identifiant du joueur
	public int n;
	public String nom;
	public boolean estHumain;
	public Color couleur;
	public boolean actif;

	public int[] arsenal;
	public int bombeArmee;

	public double vie;
	public double fuel;
	// angle du canon
	public double angle;
	// force du tir
	public double force;

	// parametres de visee pour l'attaque aerienne
	public double xVisee;
	public boolean viseeDroite;

	// parametre IA pour la dichotomie
	public double dico[];
	public double defaut;

	public final double GRAVITE = 0.1;

	LinkedList<Objet> Objets;
	LinkedList<Joueur> JoueursActifs;
	Tank tank;
	Canon canon;

	public Joueur(int num, int placement, int nombreJoueurs, int difficulte,
			String anom, String acouleur, boolean Humain, int nombreBombes,
			Carte amap, Rectangle aframe, LinkedList<Joueur> JoueursEnVie,
			LinkedList<Objet> Obj) {
		limitesFrame = aframe;
		map = amap;

		arsenal = new int[nombreBombes];
		arsenal[0] = 100000;
		bombeArmee = 0;

		n = num;
		nom = anom;
		estHumain = Humain;
		actif = true;

		angle = 0;
		xVisee = limitesFrame.width / 2;
		viseeDroite = true;
		force = 0;

		vie = 100;
		fuel = 100;

		if (estHumain) {
			arsenal[1] = 25;
			arsenal[2] = 5;

			defaut = 0;
			dico = new double[] { 0, 0 };
		} else {
			switch (difficulte) {
			case (1):
				break;
			case (2):
				arsenal[1] = 5;

				break;
			case (3):
				arsenal[1] = 10;
				arsenal[2] = 1;
				break;
			case (4):
				arsenal[1] = 10;
				arsenal[2] = 3;
				break;
			case (5):
				arsenal[1] = 25;
				arsenal[2] = 5;
				break;
			default:
				System.out.println("Erreur sur la difficulte");
				System.exit(0);
			}

			double ran = 200 * Math.random() - 100;

			// renvoit un nombre aleatoire entre 0 et 25, avec une probabilite
			// forte d'etre proche de 25
			defaut = 5 * (ran / Math.abs(ran)) * Math.pow(Math.abs(ran), 0.35);
			dico = new double[] { -25, 25 };
		}

		// on tranforme la couleur en texte en une couleur Java
		switch (acouleur) {
		case ("vert"):
			couleur = new Color(77, 153, 5);
			break;
		case ("rouge"):
			couleur = new Color(153, 22, 5);
			break;
		case ("bleu"):
			couleur = new Color(125, 241, 244);
			break;
		case ("jaune"):
			couleur = new Color(222, 176, 0);
			break;
		case ("violet"):
			couleur = new Color(140, 40, 139);
			break;
		case ("gris"):
			couleur = new Color(144, 143, 144);
			break;
		case ("marron"):
			couleur = new Color(96, 78, 34);
			break;
		case ("rose"):
			couleur = new Color(234, 64, 209);
			break;
		default:
			System.out.println("Erreur sur la couleur");
			System.exit(0);
		}

		tank = new Tank(this, placement, nombreJoueurs, "Tank_" + acouleur
				+ ".png", limitesFrame, map);

		canon = tank.canon;

		JoueursActifs = JoueursEnVie;
		Objets = Obj;
	}

	public Bombe[] tire(double vent) {
		// si l'attaque est declenchee par une caisse
		if (vent == 10) {
			int e = (int) (limitesFrame.width / 100);

			Bombe[] piege = new Bombe[e];

			for (int u = 0; u < e; u++) {
				piege[u] = new Bombe(tank, u * 100, true, JoueursActifs,
						GRAVITE);
			}

			Thread tir = new Son("Chute.wav");
			tir.start();

			return piege;
		} else if (bombeArmee < 6) {
			arsenal[bombeArmee]--;
			String nomBombe = new String();
			String nomImage = "Bombe.png";
			int dommage = 0;

			// on regle les dommages en fonction du type de bombe
			switch (bombeArmee) {
			case 0:
				nomBombe = "gun";
				dommage = 10;
				break;
			case 1:
				nomBombe = "roquette";
				dommage = 25;
				break;
			case 2:
				nomBombe = "obus";
				dommage = 50;
				break;
			case 3:
				nomBombe = "v2";
				dommage = 80;
				break;
			case 4:
				nomBombe = "ogive";
				nomImage = "Ogive.png";
				dommage = 100;
				break;
			case 5:
				nomBombe = "patate";
				nomImage = "Patate.png";
				dommage = 200;
				break;
			default:
				System.out.println("Erreur sur le choix de la bombe");
				System.exit(0);
			}

			Bombe obus = new Bombe(tank, vent, force + defaut, angle, dommage,
					nomBombe, nomImage, JoueursActifs, GRAVITE);

			if (bombeArmee < 4) {
				Thread tir = new Son("Tir.wav");
				tir.start();
			} else {
				Thread tir = new Son("Tir_ogive.wav");
				tir.start();
			}

			return new Bombe[] { obus };
		}

		else {
			arsenal[bombeArmee]--;
			Bombe[] attaqueAerienne = new Bombe[5];

			for (int u = 0; u < 5; u++) {
				attaqueAerienne[u] = new Bombe(tank, xVisee + (u - 2) * 40,
						viseeDroite, JoueursActifs, GRAVITE);
			}

			Thread tir = new Son("Chute.wav");
			tir.start();

			return attaqueAerienne;
		}

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
		if (bombeArmee > 5) {
			viseeDroite = false;

			if (xVisee > 50) {
				xVisee -= 3;
			}
		} else if (angle < 180) {
			angle += 0.5;
		}
	}

	public void angleMoins() {
		if (bombeArmee > 5) {
			viseeDroite = true;

			if (xVisee < limitesFrame.width - 50) {
				xVisee += 3;
			}
		} else if (angle > 0) {
			angle -= 0.5;
		}
	}

	public void touche(Bombe bombe) {
		double vieApresCoup = vie - bombe.dommage;
		bombe.rayonDegats(tank.getCenterX());

		// on corrige les degats afin que le tank ne se prenne pas deux fois le
		// coup (du fait du rayon de degats)
		vie = vieApresCoup;
		actif = true;
		tank.actif = true;
		canon.actif = true;

		map.destructionMap(bombe.dommage, (int) (tank.getCenterX()));

		if (vie <= 0) {			
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
		Bombe obus = new Bombe(tank, 0, forceTest, angleTest, 0, "obus",
				"Bombe.png", JoueursActifs, GRAVITE);

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
			if ((obus.x < 0 | obus.x >= limitesFrame.width) && !dichot) {
				double xTest = obus.x;
				double yTest = obus.y;
				double dxTest = obus.dx;
				double dyTest = obus.dy;

				while (test) {
					xTest = xTest + dxTest;
					yTest = yTest - dyTest;
					dyTest = dyTest - GRAVITE;

					if ((xTest >= 0 && xTest < limitesFrame.width)
							&& yTest < map.getY(xTest)) {
						test = false;
					} else if (yTest > limitesFrame.height) {
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
