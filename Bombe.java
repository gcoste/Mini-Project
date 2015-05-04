public class Bombe extends Object {

    public double puissance; //pour les point de vie du tank
    public static String NomImage = "Bombe.png";

    public Bombe(String nom, int l, int h, float x, float y, float angle, int force, int joueur, double puissance){
        
        super(nom, l, h, x, y, angle, force, joueur, NomImage);
        
        this.puissance = puissance;
        
    }

    public vvoid move(long t) {  //paramètre temps à utiliser
        
        x += Math.cos(angle)*force;
        y += Math.sin(angle)*force - 3; //gravité à tester
               
    }
}
