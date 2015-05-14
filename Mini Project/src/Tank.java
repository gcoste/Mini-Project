import java.awt.Rectangle;

public class Tank extends Objet {

	double vie;
	double fuel;
	boolean estHumain;
	static String NomImage = "Tank.png";
	
	//Constructeur nul mais faut le mettre
	public Tank(){
		System.out.println("je suis nul");
	}

	public Tank(Rectangle aframe, Carte map, String nom, int joueur, boolean Humain) {
		super(0, 0, 0, 0, 10, NomImage, aframe, map, nom, joueur);
		
		//on place le tank aleatoirement mais sur le terrain
		this.x = (int) (400*Math.random() + 100) - limites.width/2;
		this.y = map.getY(x)-limites.height;

		this.vie = 100;
		this.fuel = 100;
		this.estHumain = Humain;
	}

	public void move(long t) {
		if (fuel > 0) {
			x = x + (int) (vitesse * dx);
			y = y + (int) (vitesse * dy);

			// PREVOIR UN COEFFICIENT
			fuel -= Math.sqrt(dx + dy) * vitesse;

			// On test si on a pas atteint les bords de l'ecran, si c'est le cas
			// on se remet sur le bord
			if (x < limitesframe.x) {
				x = limitesframe.x;
			} else if (x + l > limitesframe.x + limitesframe.width) {
				x = limitesframe.x + limitesframe.width - l;
			}

			if (y < limitesframe.y) {
				y = limitesframe.y;
			} else if (y + h > limitesframe.y + limitesframe.height) {
				y = limitesframe.y + limitesframe.height - h;
			}
		}

		// On place le rectangle de limites sur l'image
		limites.setLocation(x, y);
	}

	public void touche(Bombe obus) {
		vie -= obus.dommage; // A REVOIR
	}
}
