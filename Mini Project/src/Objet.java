import java.awt.*;
import java.io.File;

import javax.imageio.ImageIO;

public abstract class Objet {
	// cadre représentant la fenetre totale (avec les barres)
	Rectangle aframe;
	// position haut gauche de l'objet (on prend des float afin que le mouvement
	// des objets ne soit pas gene par le rafraichissement rapide du jeu)
	float x, y;
	// dimensions de l'objet
	int h, l;
	// direction de deplacement
	float dx, dy;
	double vitesse;
	Image image;
	// limites de l'objet lui-meme
	Rectangle limites;
	// limites de l'ecran
	Rectangle limitesframe;
	String nom;
	// l'objet est-il actif
	boolean actif;
	// joueur auquel l'objet appartient
	Joueur joueur;
	// carte sur lequel evolue l'objet
	Carte map;
	// angle du canon
	double angle;
	// force du tir
	float force;

	// Constructeur nul mais faut le mettre
	public Objet() {
		System.out.println("je suis nul");
	}

	// Constructeur initialise les attributs
	public Objet(float ax, float ay, float adx, float ady, double avitesse,
			float aforce, double angleToSet, String NomImage, Rectangle aframe,
			Carte amap, String anom, Joueur ajoueur) {
		x = ax;
		y = ay;
		dx = adx;
		dy = ady;
		vitesse = avitesse;
		angle = angleToSet;
		force = aforce;
		

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
		limitesframe = aframe;
		nom = anom;
		actif = true;
		joueur = ajoueur;
		map = amap;
	}

	// Dessine l'image, est dans la classe abstraite pour pouvoir se repercuter
	// au classe filles
	// On a plus qu'a  parcourir la liste d'elements et tous les dessiner
	public void draw(long t, Graphics buffer) {
		buffer.drawImage(image, (int) x, (int) y, null);
	}

	// Methode qui test si deux objets sont en collision
	public boolean Collision(Objet O) {
		return limites.intersects(O.limites);
	}

	// Methode qui gere le mouvement des objets
	// Abstraite car le mouvement depend du type d'objet
	abstract void move(long t);
}
