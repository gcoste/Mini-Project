public class Bombe extends Objet {

	final float GRAVITE = (float) 0.1;
	int dommage;
	Tank tank;

	int nbJoueurActifs;
	Joueur[] JoueursActifs;
	static String NomImage = "Bombe.png";

	public Bombe(Tank atank, double avitesse, String nom, Joueur[] arrayJoueurs) {

		super(0, 0, 0, 0, avitesse, NomImage, atank.limitesframe, atank.map,
				nom, atank.joueur);

		// on place la bombe en sortie du canon
		double a = Math.toRadians(atank.angle);
		x = (float) (atank.canon.x + Math.cos(a) * 40) - 2;
		y = (float) (atank.canon.y - Math.sin(a) * 40);

		// on regle les dommages en fonction de la bombe
		if (nom.equals("bombe")) {
			dommage = 50;
		} else if (nom.equals("obus")) {
			dommage = 100;
		}

		nbJoueurActifs = 0;
		JoueursActifs = new Joueur[arrayJoueurs.length];

		for (int i = 0; i < arrayJoueurs.length; i++) {
			if (arrayJoueurs[i].enVie) {
				JoueursActifs[nbJoueurActifs] = arrayJoueurs[i];
				nbJoueurActifs++;
				
			}
		}

		// on regle dx et dy en fonction de l'angle
		// le 0 est a droite, le 180 est a gauche
		dx = (float) (Math.cos(a) * vitesse);
		dy = (float) (Math.sin(a) * vitesse);
	}

	public void move(long t) {
		x = x + dx;
		y = y - dy;
		dy = dy - GRAVITE;

		for (int i = 0; i < nbJoueurActifs; i++) {
			if (this.Collision(JoueursActifs[i].tank)) {
				this.actif = false;
				JoueursActifs[i].touche(this);
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
