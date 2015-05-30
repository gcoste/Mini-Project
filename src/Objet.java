import java.awt.*;
import java.io.File;

import javax.imageio.ImageIO;

public abstract class Objet {
	// cadre représentant la fenetre totale (avec les barres)
	Rectangle aframe;
	// position haut gauche de l'objet (on prend des double afin que le
	// mouvement
	// des objets ne soit pas gene par le rafraichissement rapide du jeu)
	double x, y;
	// dimensions de l'objet
	int h, l;
	// direction de deplacement
	double dx, dy;
	double vitesse;
	Image image;
	// limites de l'objet lui-meme
	Rectangle limites;
	// limites de l'ecran
	Rectangle limitesFrame;
	String nom;
	// l'objet est-il actif
	boolean actif;
	// joueur auquel l'objet appartient
	Joueur joueur;
	// carte sur lequel evolue l'objet
	Carte map;

	// Constructeur nul mais faut le mettre
	public Objet() {
		System.out.println("je suis nul");
	}

	// Constructeur initialise les attributs
	public Objet(double ax, double ay, double adx, double ady, double avitesse,
			String NomImage, Rectangle aframe, Carte amap, String anom,
			Joueur ajoueur) {
		x = ax;
		y = ay;
		dx = adx;
		dy = ady;
		vitesse = avitesse;

		// Test si l'image est bien presente
		try {
			image = ImageIO.read(new File(NomImage));
		} catch (Exception err) {
			System.out.println(NomImage + " introuvable !");
			System.out.println("Mettre les images dans le repertoire source");
			System.exit(0);
		}

		// Contient la hauteur et largeur de l'image
		h = image.getHeight(null);
		l = image.getWidth(null);
		limites = new Rectangle((int) ax, (int) ay, l, h);
		limitesFrame = aframe;
		nom = anom;
		actif = true;
		joueur = ajoueur;
		map = amap;
	}

	// Dessine l'image, est dans la classe abstraite pour pouvoir se repercuter
	// au classe filles
	// On a plus qu'a  parcourir la liste d'elements et tous les dessiner
	public void draw(Graphics buffer) {
		buffer.drawImage(image, (int) x, (int) y, null);
	}

	// Methode qui test si deux objets sont en collision
	public boolean Collision(Objet O) {
		return limites.intersects(O.limites);
	}

	public double getCenterX() {
		return x + limites.width / 2;
	}

	public double getCenterY() {
		return y + limites.height / 2;
	}

	// Methode qui gere le mouvement des objets
	// Abstraite car le mouvement depend du type d'objet
	abstract void move();
}
