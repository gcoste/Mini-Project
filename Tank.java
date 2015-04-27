

import java.awt.Color;
import java.awt.Rectangle;


public class tanks extends Object {
    
    public int vie;
    public int fuel;
    public int vitesse;
    public boolean estHumain;
    public double collision;
    
    
    public tanks (int id, int l, int h, int x, int y, int angle, int force, int joueur,int vie, int fuel, int vitesse, boolean estHumain, Rectangle Tank, double collision){

         super(id, l, h, x, y, angle, force, joueur);
         
         this.vie = 100;
         this.fuel = 100;
         this.vitesse = 1;
         this.estHumain = estHumain;
         this.Tank = Tank;
         this.collision = collision;   

    }
    
    void move(long t) {
        x = x + (int) (vitesse * dx);
        y = y + (int) (vitesse * dy);

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

        limites.setLocation(x, y);
    }
    
    
    public void collisionDetected(bombe){ //problème d'identification de la bombe//
        vie -= bombe.puissance;
        }
    
    
   
         
        
}
