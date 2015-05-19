import java.awt.*;

public class Canon extends Objet {
	Tank tank;

	public Canon(Tank atank) {
		// l'image ne sera pas utilisée mais obligation de la mettre si on veut
		// créer le canon
		// (x,y) represente le point du bas du rectangle representant le
		// canon (le point fixé au tank)
		super(atank.x + 40, atank.y + 14, 0, 0, 0, "Canon.png",
				atank.limitesframe, atank.map, ("Canon_" + atank.nom),
				atank.joueur);

		tank = atank;
	}

	public void move(long t) {
		x = tank.x + 40;
		y = tank.y + 14;

		double a = Math.toRadians(tank.angle);
		float xAngle = (float) (x + Math.cos(a) * 40) - 2;
		float yAngle = (float) (y - Math.sin(a) * 40);

		while (yAngle > map.getY(xAngle) && tank.angle != 90) {
			if (tank.angle > 90) {
				tank.angle = (int) tank.angle - 1;
			} else if (tank.angle < 90) {
				tank.angle = (int) tank.angle + 1;
			}
			
			a = Math.toRadians(tank.angle);
			xAngle = (float) (x + Math.cos(a) * 40) - 2;
			yAngle = (float) (y - Math.sin(a) * 40);
		}
	}

	public void draw(Graphics buffer) {
		double a = Math.toRadians(tank.angle);

		// Pour accéder aux fonctions graphiques avancées il suffit de
		// transtyper g
		Graphics2D g = (Graphics2D) buffer;
		// g2 permet de définir l’épaisseur des traits ce que ne peut pas faire
		// g
		g.setStroke(new BasicStroke(5));
		g.setColor(tank.joueur.couleur);

		g.drawLine((int) (x + Math.cos(a) * 15), (int) (y - Math.sin(a) * 15),
				(int) (x + Math.cos(a) * 40), (int) (y - Math.sin(a) * 40));
	}
}