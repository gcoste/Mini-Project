import java.awt.Color;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.LinkedList;

public class Caisse extends Objet {
	private Message message;
	
	public boolean estPose;

	private final double GRAVITE = 0.1;
	private boolean bombesLancees;

	private Joueur aPris;

	private double proba;
	private boolean piegeAerien;
	private boolean piegeAuSol;

	LinkedList<Objet> Objets;
	Bombe[] bombesActives;

	public Caisse(Carte amap, Rectangle aframe,
			LinkedList<Joueur> JoueursActifs, LinkedList<Objet> Obj,
			LinkedList<Caisse> Caisses, Message mes) {
		super(0, -50, 0, 0, 1, "Caisse.png", aframe, amap, null, null);

		message = mes;
		estPose = false;
		aPris = null;

		proba = 100 * Math.random();

		piegeAerien = false;
		piegeAuSol = false;

		if (proba >= 87 && proba < 92) {
			piegeAuSol = true;
		} else if (proba >= 92) {
			piegeAerien = true;
		}

		bombesLancees = false;
		bombesActives = null;

		boolean verif = true;

		// on verifie que la caisse n'apparait pas directement au-dessus d'un
		// tank ou d'une autre caisse
		while (verif) {
			x = (int) (aframe.width - l) * Math.random();

			Iterator<Joueur> k1 = JoueursActifs.iterator();
			Iterator<Caisse> k2 = Caisses.iterator();

			while (k1.hasNext() && verif) {
				Joueur J = (Joueur) k1.next();

				if (Math.abs(this.getCenterX() - J.tank.getCenterX()) < 50) {
					verif = false;
				}
			}

			while (k2.hasNext() && verif) {
				Caisse C = (Caisse) k2.next();

				if (Math.abs(this.getCenterX() - C.getCenterX()) < 50) {
					verif = false;
				}
			}

			verif = !verif;
		}

		Objets = Obj;

		Objets.add(this);
		Caisses.add(this);
	}

	public void move() {
		y += vitesse;
		vitesse += GRAVITE;

		if (y + h > map.getY(getCenterX())) {
			y = map.getY(getCenterX()) - h;

			estPose = true;
		}

		limites.setLocation((int) x, (int) y);
	}

	public void actionCaisse(LinkedList<Joueur> JoueursActifs, long temps) {
		if ((y != map.getY(getCenterX()) - h && estPose)
				&& aPris == null) {
			this.actif = false;
		}

		Iterator<Joueur> k = JoueursActifs.iterator();

		while (k.hasNext() && actif && aPris == null) {
			Joueur J = (Joueur) k.next();

			if (this.Collision(J.tank)) {
				aPris = J;
			}
		}

		if (aPris != null) {
			if (!piegeAerien && !piegeAuSol) {
				this.actif = false;

				giveTank(aPris, temps);
			} else if (piegeAuSol) {
				this.actif = false;

				message.setMessage(temps, new Color(200, 0, 0), 2,
						"Vous etes tombes dans un piege", "dommage !");

				Bombe piege = new Bombe(aPris.tank, JoueursActifs);
			} else {
				piegeTank(aPris, temps);
			}
		}
	}

	private void giveTank(Joueur J, long temps) {
		if (proba < 40) {
			if (proba < 25) {
				J.vie += 30;

				message.setMessage(temps, new Color(28, 142, 62), 2,
						"30 points de vie", "supplementaires");
			} else {
				J.vie += 50;

				message.setMessage(temps, new Color(28, 142, 62), 2,
						"50 points de vie", "supplementaires");
			}
		} else if (proba < 55) {
			if (proba < 50) {
				J.fuel += 30;

				if (J.fuel > 100) {
					J.fuel = 100;
				}

				message.setMessage(temps, new Color(128, 0, 128), 2,
						"50 litres de fioul", "supplementaires");
			} else {
				J.fuel = 100;

				message.setMessage(temps, new Color(128, 0, 128), 2,
						"Reservoir de fioul rempli", null);
			}
		} else if (proba < 87) {
			if (proba < 70) {
				J.arsenal[2] += 5;

				message.setMessage(temps, new Color(200, 0, 0), 2, "5 obus",
						null);
			} else if (proba < 75) {
				J.arsenal[6]++;

				message.setMessage(temps, new Color(200, 0, 0), 2,
						"une attaque aerienne", null);
			} else if (proba < 80) {
				J.arsenal[3]++;

				message.setMessage(temps, new Color(200, 0, 0), 2,
						"un missile v2", null);
			} else if (proba < 85) {
				J.arsenal[4]++;

				message.setMessage(temps, new Color(200, 0, 0), 2,
						"une ogive nucleaire", "soyez prudent");
			} else {
				J.arsenal[5]++;

				message.setMessage(temps, new Color(200, 0, 0), 2,
						"le lance patate", "faites en bon usage");

				Thread explosion = new Son("Lance_patate.wav");
				explosion.start();
			}
		} else {
			System.out.println("Erreur sur les probas caisse");
			System.exit(0);
		}
	}

	private void piegeTank(Joueur J, long temps) {
		if (!bombesLancees) {
			bombesLancees = true;
			Objets.remove(this);

			message.setMessage(temps, new Color(200, 0, 0), 2,
					"Pluie de l'apocalypse", "dommage !");

			bombesActives = J.tire(10);

			for (int u = 0; u < bombesActives.length; u++) {
				Objets.add(0, bombesActives[u]);
			}
		} else {
			int sum = 0;

			for (int u = 0; u < bombesActives.length; u++) {
				bombesActives[u].move();

				if (!bombesActives[u].actif) {
					sum++;
				}
			}

			if (sum == bombesActives.length) {
				this.actif = false;
			}
		}
	}
}
