public class Bombe extends Object {

    public double vitesseInitiale;
    public static String NomImage = "Bombe.png";
    public float vitesse;

    public Bombe(String nom, int l, int h, float x, float y, int angle, int force, int joueur, double vitesseInitiale) {

        super(nom, l, h, x, y, vitesse, force, angle, joueur, NomImage);

        this.vitesseInitiale = vitesseInitiale;
    }

}
   
   class Shot extends moveObject{  //paramètre temps à utiliser
        double dx;
        double dy;
        
        public Shot (int x, int y){ 
           this.x = x*Math.cos(angle);
           this.y = y*Math.sin(angle);
           this.dx = vitesseInitiale;
            }
        
        public void simulate(){
            dy += G;
            x +=(int)Math.round(dx);
            y += (int)Math.round(dy);
            /*if(y<=sol){ besoin des coordonnées du sol
                dy=0;
                dx=0;
                y=sol;
               }*/
         }
            
        /*public void paint (Graphics g){      besoin image bombe
            g.drawImage(BombeImage,x,y,this);                                 
            }*/
                    
    
    }
