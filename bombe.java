
public class bombe extends Object {

    public double puissance; //pour les point de vie du tank//

    public bombe(int id, int l, int h, int x, int y, int angle, int force, int joueur, double puissance){
        
        super(id, l, h, x, y, angle, force, joueur);
        
        this.puissance = puissance;
        
        }

    public void deplacement(){  //paramètre temps à utiliser//
        
        x += Math.cos(angle)*force;
        y += Math.sin(angle)*force - 3; //gravité à tester//
               
        }



}
