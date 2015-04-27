
public abstract class Object {
   
   protected int id; //~/.jdeveloper/mywork/Tank/Mini-Project $ //
   protected int l,h;
   protected int x,y;
   protected int angle;
   protected int force;
   protected int joueur;
   
   
   public Object(int id, int l, int h, int x, int y, int angle, int force, int joueur){
       this.id=id;
       this.l=l;
       this.h=h;
       this.x=x;
       this.y=y;
       this.angle=angle;
       this.force=force;
       this.joueur=joueur;
       }
   
}
