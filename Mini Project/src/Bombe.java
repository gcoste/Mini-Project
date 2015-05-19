import java.util.Iterator;
import java.util.LinkedList;

public class Bombe extends Objet {

	float gravite;
	float vent;

	int dommage;
	Tank tank;

	LinkedList<Joueur> JoueursActifs;

	public Bombe(Tank atank, double avitesse, float avent, String nom,
			LinkedList<Joueur> ListJoueurs, float grav) {
		super(0, 0, 0, 0, avitesse, "Bombe.png", atank.limitesframe, atank.map,
				nom, atank.joueur);

		gravite = grav;

		// on place la bombe en sortie du canon
		double a = Math.toRadians(atank.angle);
		x = (float) (atank.canon.x + Math.cos(a) * 40) - 2;
		y = (float) (atank.canon.y - Math.sin(a) * 40);

		// on regle les dommages en fonction du type de bombe
		if (nom.equals("canon")) {
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

	boolean test = true;

	public void move(long t) {
		x = x + dx;
		y = y - dy;
		dy = dy - gravite;
		dx = dx + vent;

		Iterator k = JoueursActifs.iterator();

		while (k.hasNext()) {
			Joueur J = (Joueur) k.next();

			if (this.Collision(J.tank)) {
				this.actif = false;
				
				Thread explosion=new Son("Explosion_" + nom + ".wav");
				explosion.start();
				
				J.touche(this, k);
			}
		}

		// on test si la bombe touche la carte ou les bords du jeu
		// on la desactive et la bombe sera supprimee apres
		if (x < 0 | x >= limitesframe.width) {
			float xTest = x;
			float yTest = y;
			float dxTest = dx;
			float dyTest = dy;
			
			while (test) {
				xTest = xTest + dxTest;
				yTest = yTest - dyTest;
				dyTest = dyTest - gravite;
				dxTest = dxTest + vent;

				if ((xTest >= 0 && xTest < limitesframe.width)
						&& yTest < map.getY(xTest)) {
					test = false;
				} else if (yTest > limitesframe.height) {
					this.actif = false;
					test = false;
				} 
			}
		} else if (y >= map.getY(x)) {
			this.actif = false;
			map.destructionMap(this.dommage, (int) x);
			
			Thread explosion=new Son("Explosion_" + nom + ".wav");
			explosion.start();
		}

		limites.setLocation((int) x, (int) y);
	}
}
