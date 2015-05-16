import java.awt.*;

public class Joueur {
	// limites de l'ecran
	Rectangle limitesframe;
	// carte sur lequel evolue l'objet
	Carte map;
	// permet d'acceder a certaines variables de jeu
	Jeu jeu;

	// identifiant du joueur
	int n;
	String nom;
	boolean estHumain;
	int score;
	Color couleur;
	boolean enVie;

	Tank tank;
	Canon canon;

	public Joueur(int num, String anom, String acouleur, boolean Humain,
			Jeu ajeu) {
		jeu = ajeu;
		map = jeu.map;
		limitesframe = jeu.Ecran;

		n = num;
		nom = anom;
		estHumain = Humain;
		enVie = true;

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

		tank = new Tank(this, 2 * jeu.TEMPS, "Tank_" + acouleur + ".png");

		canon = tank.canon;
	}

	public void tire(long force, long t) {
		Bombe obus = new Bombe(tank, force * jeu.TEMPS * 1.3, "obus",
				jeu.Joueurs);
		jeu.Objets.add(obus);
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
			tank.angle += 5 * jeu.TEMPS;
		}
	}

	public void angleMoins() {
		if (tank.angle > 0) {
			tank.angle -= 5 * jeu.TEMPS;
		}
	}

	public void touche(Bombe bombe) {
		tank.vie -= bombe.dommage / 10;

		if (tank.vie <= 0) {
			tank.actif = false;
			enVie = false;
		}
	}

}
