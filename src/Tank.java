public class Tank extends Objet {
	Canon canon;

	// Constructeur nul mais faut le mettre
	public Tank() {
		System.out.println("je suis nul");
	}

	public Tank(Joueur joueur, int placement, int nombreJoueurs,
			double avitesse, String NomImage) {
		super(0, 0, 0, 0, avitesse, NomImage, joueur.limitesframe, joueur.map,
				("Tank_" + joueur.n), joueur);

		// on divise la taile du terrain par le nombre de joueur afin de placer
		// chaque tank dans un secteur
		this.x = ((limitesFrame.width / nombreJoueurs - 100) * Math.random()
				+ 50 + placement * (limitesFrame.width / nombreJoueurs))
				- limites.width / 2;
		// le tank est place directement sur la map
		this.y = map.getY(getCenterX()) - limites.height;

		// on cree le canon du tank
		canon = new Canon(this);
	}

	public void move() {
		if (joueur.fuel > 0) {
			x = x + vitesse * dx;
			y = map.getY(getCenterX()) - limites.height;

			joueur.fuel -= 0.1 * Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2))
					* vitesse;

			// On test si on a pas atteint les bords de l'ecran, si c'est le cas
			// on se remet sur le bord
			if (x < limitesFrame.x) {
				x = limitesFrame.x;
			} else if (x + l > limitesFrame.x + limitesFrame.width) {
				x = limitesFrame.x + limitesFrame.width - l;
			}

			// si le tank atteint la limite basse du jeu, il est instantanement
			// detruit
			if (y > limitesFrame.height) {
				// on met 0 en parametre pour faire comprendre a la methode que
				// le tank es directement detruit
				joueur.degats(-1);
			}
		}

		// On place le rectangle de limites sur l'image
		limites.setLocation((int) x, (int) y);
	}
}
