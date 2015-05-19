
import java.awt.Rectangle;

public class Canon {
    
    public int x1,y1;
    public int x2,y2;
    public Rectangle canon;
    int angle;
    
    public Canon(tank t, Rectangle canon, int angle ){
        
        this.x1 = x.tank/2;
        this.y1 = y.tank/2;
        this.canon = canon;
        this.angle = angle;
        this.x2 = height.canon*Math.cos(angle);
        this.y2 = height.canon*Math.sin(angle);
        
        }
    
    public void deplacement(){ //dépend de l'angle peut etre pas utile ?//
        
        
        }
    
    
}