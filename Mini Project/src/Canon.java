import java.awt.*;

public class Canon extends Objet {
	Tank tank;

	float xCanon;
	float yCanon;

	public Canon(Tank atank) {
		// l'image ne sera pas utilisée mais obligation de la mettre si on veut
		// créer le canon
		// (x,y) represente le point du bas du rectangle representant le
		// canon (le point fixé au tank)
		super(atank.x + 40, atank.y + 14, 0, 0, 0, 0, atank.angle,
				"Canon.png", atank.limitesframe, atank.map,
				("Canon_" + atank.nom), atank.joueur);

		tank = atank;

		double a = Math.toRadians(tank.angle);
		xCanon = (float) (x + Math.cos(a) * 40);
		yCanon = (float) (y - Math.sin(a) * 40);
	}

	public void move(long t) {
		x = tank.x + 40;
		y = tank.y + 14;

		xCanon = joueur.getXCanon();
		yCanon = joueur.getYCanon();

		while (yCanon > map.getY(xCanon) && angle != 90) {
			if (angle > 90) {
				angle = (int) angle - 1;
			} else if (angle < 90) {
				angle = (int) angle + 1;
			}

			xCanon = joueur.getXCanon();
			yCanon = joueur.getYCanon();
		}
		
		tank.angle = angle;
	}

	public void draw(Graphics buffer) {
		double a = Math.toRadians(angle);

		// Pour accéder aux fonctions graphiques avancées il suffit de
		// transtyper g
		Graphics2D g = (Graphics2D) buffer;
		// g2 permet de définir l’épaisseur des traits ce que ne peut pas faire
		// g
		g.setStroke(new BasicStroke(5));
		g.setColor(joueur.couleur);

		g.drawLine((int) (x + Math.cos(a) * 15), (int) (y - Math.sin(a) * 15),
				(int) xCanon, (int) yCanon);
	}
}