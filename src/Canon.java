import java.awt.*;

public class Canon extends Objet {
	Tank tank;

	double xCanon;
	double yCanon;

	public Canon(Tank atank) {
		// l'image ne sera pas utilisée mais obligation de la mettre si on veut
		// créer le canon
		// (x,y) represente le point du bas du recttank.angle representant le
		// canon (le point fixé au tank)
		super(atank.x + atank.limites.width/2, atank.y + 9, 0, 0, 0, "Canon.png",
				atank.limitesframe, atank.map, ("Canon_" + atank.nom),
				atank.joueur);

		tank = atank;

		double a = Math.toRadians(tank.angle);
		xCanon = x + Math.cos(a) * 40;
		yCanon = y - Math.sin(a) * 40;
	}

	public void move(long t) {
		x = tank.x + 25;
		y = tank.y + 9;

		xCanon = joueur.getXCanon();
		yCanon = joueur.getYCanon();

		while (yCanon > map.getY(xCanon) && tank.angle != 90) {
			if (tank.angle > 90) {
				tank.angle = (int) tank.angle - 1;
			} else if (tank.angle < 90) {
				tank.angle = (int) tank.angle + 1;
			}

			xCanon = joueur.getXCanon();
			yCanon = joueur.getYCanon();
		}
	}

	public void draw(Graphics buffer) {
		double a = Math.toRadians(tank.angle);

		// Pour accéder aux fonctions graphiques avancées il suffit de
		// transtyper g
		Graphics2D g = (Graphics2D) buffer;
		// g2 permet de définir l’épaisseur des traits ce que ne peut pas faire
		// g
		g.setStroke(new BasicStroke(3));
		g.setColor(joueur.couleur);

		g.drawLine((int) (x + Math.cos(a) * 8), (int) (y - Math.sin(a) * 8),
				(int) xCanon, (int) yCanon);
	}
}