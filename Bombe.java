public class Bombe extends Object {

    public double puissance; //pour les point de vie du tank
    public static String NomImage = "Bombe.png";
    public float vitesse;

    public Bombe(String nom, int l, int h, float x, float y, int angle, int force, int joueur, double puissance) {

        super(nom, l, h, x, y, vitesse, force, angle, joueur, NomImage);

        this.puissance = puissance;
    }

    public void move(long t) { //paramètre temps à utiliser
        x += Math.cos(angle) * force;
        y += Math.sin(angle) * force - 3; //gravité à tester
    }
}
