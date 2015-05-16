import java.awt.*;
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

	public Bombe tire(float force, float vent, long t) {
		Bombe obus = new Bombe(tank, force * TEMPS * 1.3, vent, "obus",
				JoueursActifs);

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

	public void touche(Bombe bombe, int k) {
		tank.vie -= bombe.dommage;
		map.destructionMap(bombe.dommage, (int) (tank.x+tank.limites.width/2));

		if (tank.vie <= 0) {
			JoueursActifs.remove(k);
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

}
