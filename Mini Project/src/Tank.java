import java.awt.*;
import java.io.File;

import javax.imageio.ImageIO;

public class Tank extends Objet {
	Canon canon;
	double angle;
	float vie;
	float fuel;
	boolean estHumain;
	Color couleur;
	static String NomImage = "Tank_bleu.png";

	// Constructeur nul mais faut le mettre
	public Tank() {
		System.out.println("je suis nul");
	}

	public Tank(Rectangle aframe, Carte map, String nom, int joueur,
			String acouleur, boolean Humain) {
		super(0, 0, 0, 0, 0.05, NomImage, aframe, map, nom, joueur);

		// on place le tank aleatoirement mais sur le terrain
		this.x = (float) ((aframe.width-100) * Math.random() + 50) - limites.width / 2;
		this.y = map.getY(x+limites.width/2) - limites.height;
		
		this.angle = 90;

		switch (joueur) {
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

		// on r�atribue une image en fonction de la couleur du joueur
		NomImage = ("tank_" + acouleur + ".png");

		try {
			image = ImageIO.read(new File(NomImage));
		} catch (Exception err) {
			System.out.println(NomImage + " introuvable !");
			System.out.println("Mettre les images dans le repertoire source");
			System.exit(0);
		}

		// on cree le canon du tank
		canon = new Canon(this);

		this.vie = 100;
		this.fuel = 100;
		this.estHumain = Humain;
	}

	public void move(long t) {
		if (fuel > 0) {
			x = x + (float) (vitesse * dx);
			y = map.getY(x+limites.width/2) - limites.height;

			fuel -= 0.1*Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)) * vitesse;

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
