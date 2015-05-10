public class Tank extends Object {

    public int vie;
    public int fuel;
    public boolean estHumain;
    public double collision;
    public static float vitesse;
    public static String NomImage = "Tank.png";


    public Tank(String nom, int joueur, boolean estHumain, double collision) {

        super(nom, 0, 0, 1, 1, vitesse, 100, 90, joueur, NomImage);

        this.vie = 100;
        this.fuel = 100;
        this.estHumain = estHumain;
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


    public void collisionDetected(Bombe obus) {
        vie -= obus.puissance;
    }
}
