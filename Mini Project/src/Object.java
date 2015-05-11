import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import java.io.File;

import javax.imageio.ImageIO;

public abstract class Object {

    private static Rectangle aframe;
    public String nom;
    //dimensions de l'objet
    int h, l;
    //Position haut gauche de l'objet
    int x, y;
    //direction de déplacement
    float dx, dy;
    public float vitesse;
    public static Image image;
    //limites de l'objet lui-même
    public Rectangle limites;
    //limites de l'écran
    public Rectangle limitesframe;
    //L'objet est-il actif
    boolean actif;

    public int angle;
    public int joueur;

    //Constructeur nul mais faut le mettre
    public Object() {
        System.out.println("je suis nul");
    }

    // Constructeur initialise les attributs
    public Object(String nom_object, int ax, int ay, float adx, float ady, double avitesse, int vitesseInitiale,
                  int angle, int joueur, String NomImage) {

        x = ax;
        y = ay;
        dx = adx;
        dy = ady;
        vitesse = (float) avitesse;
        // Test si l'image est bien présente
        try {
            image = ImageIO.read(new File(NomImage));
        } catch (Exception err) {
            System.out.println(NomImage + " introuvable !");
            System.out.println("Mettre les images dans le repertoire :" +
                               getClass().getClassLoader().getResource(NomImage));
            System.exit(0);
        }
        //Contient la hauteur et largeur de l'image
        h = image.getHeight(null);
        l = image.getWidth(null);
        limites = new Rectangle(ax, ay, l, h);
        limitesframe = aframe;
        nom = nom_object;
        actif = true;
    }

    // Dessine l'image, est dans la classe abstraite pour pouvoir se répercuter au classe filles
    //On a plus qu'à parcourir la liste d'éléments et tous les dessiner
    public void draw(long t, Graphics g) {
        g.drawImage(image, x, y, null);
    }

    //Méthode qui test si deux objets sont en collision
    public boolean Collision(Object O) {
        return limites.intersects(O.limites);
    }
}

//Méthode qui gère le mouvement des objets
//Abstraite car le mouvement dépend du type d'objet
abstract class move {
    int x;
    int y;

    abstract public void simulate();

    abstract public void paint(Graphics g);
}
