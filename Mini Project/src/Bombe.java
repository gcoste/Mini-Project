import java.util.LinkedList;

public class Bombe extends Objet {

	final float GRAVITE = (float) 0.1;
	float vent;

	int dommage;
	Tank tank;

	LinkedList<Joueur> JoueursActifs;
	static String NomImage = "Bombe.png";

	public Bombe(Tank atank, double avitesse, float avent, String nom,
			LinkedList<Joueur> ListJoueurs) {
		super(0, 0, 0, 0, avitesse, NomImage, atank.limitesframe, atank.map,
				nom, atank.joueur);

		// on place la bombe en sortie du canon
		double a = Math.toRadians(atank.angle);
		x = (float) (atank.canon.x + Math.cos(a) * 40) - 2;
		y = (float) (atank.canon.y - Math.sin(a) * 40);

		// on regle les dommages en fonction du type de bombe
		if (nom.equals("bombe")) {
			dommage = 10;
		} else if (nom.equals("obus")) {
			dommage = 50;
		} else if (nom.equals("ogive")) {
			dommage = 100;
		}

		// on stocke la liste des joueurs encore presents afin de verifier si la
		// bombe tombe sur l'un d'eux
		JoueursActifs = ListJoueurs;

		// on regle dx et dy en fonction de l'angle
		// le 0 est a droite, le 180 est a gauche
		dx = (float) (Math.cos(a) * vitesse);
		dy = (float) (Math.sin(a) * vitesse);

		// le vent ne change pas pendant la course d'une bombe, on peut donc le
		// stocker comme un attribut
		vent = avent;
	}

	public void move(long t) {
		x = x + dx;
		y = y - dy;
		dy = dy - GRAVITE;
		dx = dx + vent;

		for (int k = 0; k < JoueursActifs.size(); k++) {
			Joueur J = (Joueur) JoueursActifs.get(k);

			if (this.Collision(J.tank)) {
				this.actif = false;
				J.touche(this, k);
			}
		}

		// on test si la bombe touche la carte ou les bords du jeu
		// on la desactive et la bombe sera supprimee apres
		if (x < 0 | x >= limitesframe.width) {
			this.actif = false;
		} else if (y >= map.getY(x)) {
			this.actif = false;
		}

		limites.setLocation((int) x, (int) y);
	}
}
