import java.awt.*;


public class Tank extends Objet {
	Canon canon;
	double angle;
	float vie;
	float fuel;

	// Constructeur nul mais faut le mettre
	public Tank() {
		System.out.println("je suis nul");
	}

	public Tank(Joueur joueur, double avitesse, String NomImage) {
		super(0, 0, 0, 0, avitesse, NomImage, joueur.limitesframe, joueur.map,
				("Tank_" + joueur.n), joueur);

		// on place le tank aleatoirement mais sur le terrain
		this.x = (float) ((joueur.limitesframe.width - 100) * Math.random() + 50)
				- limites.width / 2;
		this.y = map.getY(x + limites.width / 2) - limites.height;

		this.angle = 0;

		// on cree le canon du tank
		canon = new Canon(this);

		this.vie = 100;
		this.fuel = 100;
	}

	public void move(long t) {
		if (fuel > 0) {
			x = x + (float) (vitesse * dx);
			y = map.getY(x + limites.width / 2) - limites.height;

			fuel -= 0.1 * Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2))
					* vitesse;

			// On test si on a pas atteint les bords de l'ecran, si c'est le cas
			// on se remet sur le bord
			if (x < limitesframe.x) {
				x = limitesframe.x;
			} else if (x + l > limitesframe.x + limitesframe.width) {
				x = limitesframe.x + limitesframe.width - l;
			}
		}

		// On place le rectangle de limites sur l'image
		limites.setLocation((int) x, (int) y);
	}

	public void touche(Bombe obus) {
		vie -= obus.dommage; // A REVOIR
	}
}
