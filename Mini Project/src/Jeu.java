import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.LinkedList;

public class Jeu extends JFrame {
	int nombreJoueurs = 4;

	// Liste de tous les objets du jeu (tanks, bombes, canon)
	LinkedList<Objet> Objets;
	LinkedList<Joueur> JoueursActifs;
	Joueur[] Joueurs = new Joueur[nombreJoueurs];

	// timer qui regit le jeu
	Timer timer;
	// timer qui regit le passage des tours
	Timer timerTour;
	// compteurs de temps associés aux timers
	long temps;
	long tempsTour;

	BufferedImage ArrierePlan;
	Graphics buffer;

	boolean ToucheHaut;
	boolean ToucheBas;
	boolean ToucheGauche;
	boolean ToucheDroite;
	boolean ToucheEspace;
	boolean ToucheEntre;
	boolean ToucheEchap;

	Rectangle Ecran;
	Carte map;

	/*
	 * le parametre de temps est utilise pour modifier le temps uniformement :
	 * on accelere le jeu en le diminuant. Par defaut, si TEMPS = 1, le timer
	 * sera regle sur 100 ms. On utilise un parametre afin de maintenir les
	 * different attributs du jeu (vitesse des tanks, vitesse dse bombes)
	 * toujours coherents. Seule la gravite sera a ajuster.
	 */

	final float TEMPS = (float) 0.1;

	// ces differents boolean servent au passage de tours
	boolean finJeu;
	boolean finTourParTir;
	boolean attenteJoueur;
	boolean passageJoueur;

	int joueurQuiJoue;
	Bombe bombeActive;

	float vent;
	float force;

	public Jeu() {
		finJeu = false;
		finTourParTir = false;
		attenteJoueur = false;
		passageJoueur = false;

		temps = 0;
		tempsTour = 0;
		joueurQuiJoue = 0;

		force = 100;
		vent = (float) (0.1 * Math.random() - 0.05);

		setTitle("Tanks");

		// on recupere la taille exploitable de l'ecran
		GraphicsEnvironment env = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		Rectangle bounds = env.getMaximumWindowBounds();

		// et on regle notre fenetre a cette taille
		setSize(bounds.width, bounds.height);

		// On interdit de changer la taille de la fenetre
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// On ajoute l'ecouteur de clavier qui se refere a cette classe meme
		this.addKeyListener(new Jeu_this_keyAdapter(this));

		// Aucune touche n'est appuyee, donc tout est false
		ToucheHaut = false;
		ToucheBas = false;
		ToucheGauche = false;
		ToucheDroite = false;
		ToucheEspace = false;
		ToucheEntre = false;
		ToucheEchap = false;
		/*
		 * L'ecran est notre fenetre de jeu, on doit donc prendre la taille de
		 * la fenetre moins le superflue (comme les bordures de fenetre, le truc
		 * en haut avec le nom du programme et la croix pour fermer)
		 * getInsets.left(ou top,right,bottom) recupere le taille de ces
		 * bordures. Il est utilise dans une classe reprenant Jframe donc
		 * s'applique a cette JFrame, et on creer notre rectangle avec par
		 * exemple une hauteur qui vaut: hauteur=hauteur de fenetre - marge en
		 * haut - marge en bas
		 */
		Insets k = getInsets();

		Ecran = new Rectangle(k.left, k.top,
				getSize().width - k.right - k.left, getSize().height - k.bottom
						- k.top);

		// On met l'arriere plan fixe pour eviter de scintiller quand on
		// redessinera a chaque fois tout
		ArrierePlan = new BufferedImage(getSize().width, getSize().height,
				BufferedImage.TYPE_INT_RGB);
		// On indique que buffer contient les dessins de arriere plan, si on
		// modifie buffer, on modifie arriere plan
		buffer = ArrierePlan.getGraphics();

		// Creer la liste chainee de tous les objets
		Objets = new LinkedList<Objet>();
		// Creer la liste chainee de tous les joueurs en vie
		JoueursActifs = new LinkedList<Joueur>();
		// On initialise la map
		map = new Carte(Ecran);

		// on cree deux tableau de nombres aléatoires pour le placement des
		// tanks
		int[] placement = new int[nombreJoueurs];
		// temp va referencer les positions deja utilisee par un tank
		double[] temp = new double[nombreJoueurs];

		// on rempli temp par les id des joueurs
		for (int i = 0; i < nombreJoueurs; i++) {
			temp[i] = i;
			placement[i] = (int) (nombreJoueurs * Math.random());
		}

		// on rempli ensuite placement aleatoirement mais en verifiant a chaque
		// fois que la place n'est pas deja prise
		for (int i = 0; i < nombreJoueurs; i++) {
			while (temp[placement[i]] == -1) {
				placement[i] = (int) (nombreJoueurs * Math.random());
			}

			temp[placement[i]] = -1;
		}

		// on cree les joueurs (et ainsi leurs tanks et leurs canons)
		for (int i = 0; i < nombreJoueurs; i++) {
			Joueurs[i] = new Joueur(i, placement[i], nombreJoueurs, null, null,
					true, map, Ecran, TEMPS, JoueursActifs);

			// on ajoute le tank et son canon a la liste d'objets
			Objets.add(Joueurs[i].canon);
			Objets.add(Joueurs[i].tank);

			// on ajoute le joueur a liste des joueurs en vie
			JoueursActifs.add(Joueurs[i]);
		}

		// On initialise le timer du jeu afin d'avoir un jeu fluide (il bat
		// idealement toute les 100*TEMPS millisecondes)
		timer = new Timer((int) (100 * TEMPS), new TimerAction());

		/*
		 * On initialise le timer des tours (qui bat tout les 100ms). On est
		 * oblige d'utilise un timer separe puisque que le timer principal ne
		 * bat plus exactement a l'interval demande une fois qu'on reduit cet
		 * interval pour obtenir un jeu fluide : en effet, le timer demande trop
		 * d'effort a l'ordinateur, et commence a battre a un temps un peu plus
		 * long que prevu. On cree donc un autre timer avec une unite de temps
		 * plus grosse (100ms) afin que l'ordinateur n'ai pas de mal a le garder
		 * au rythme demande meme quand celui-ci est tres sollicite.
		 */
		timerTour = new Timer(100, new TimerTourAction());

		// On lance les timers
		timer.start();
		timerTour.start();

		// on affiche la fenetre enfin prete
		setVisible(true);
	}

	public void paint(Graphics g) {
		// on dessine d'abord le fond
		map.draw(temps, buffer);
		// la carte possede sa propre methode d'affichage
		map.drawHorizon(Ecran, buffer);

		String sVent = new String("");

		for (int i = 0; i < (int) 100 * (Math.abs(vent)); i++) {
			if (Math.round(100 * vent) > 0) {
				sVent = sVent + ">";
			} else if (Math.round(100 * vent) < 0) {
				sVent = sVent + "<";
			} else {
				sVent = "-";
			}
		}

		Font comic = new Font("Comic Sans MS", 1, 20);
		Font comicLarge = new Font("Comic Sans MS", 1, 30);

		buffer.setFont(comic);
		buffer.setColor(Color.black);
		buffer.drawString("Joueur " + Joueurs[joueurQuiJoue].n, 20, 50);
		buffer.drawString("Vie : " + Joueurs[joueurQuiJoue].tank.vie, 20, 80);
		buffer.drawString("Fuel : " + (int) Joueurs[joueurQuiJoue].tank.fuel,
				20, 110);
		buffer.drawString("Vent : " + sVent, 20, 170);
		buffer.drawString("Temps : " + (30 - (int) (tempsTour / 10)), 20, 140);

		int xPrev = Joueurs[joueurQuiJoue].prevision(force, vent);

		buffer.setColor(Color.red);
		if (xPrev >= 0)
			buffer.fillOval(xPrev, (int) map.getY(xPrev), 10, 10);

		if (attenteJoueur) {
			buffer.setFont(comicLarge);
			buffer.setColor(Color.red);
			buffer.drawString("En attente du joueur " + joueurQuiJoue,
					Ecran.width / 2 - 160, 80);
			buffer.drawString("Appuyez sur Entrée", Ecran.width / 2 - 135, 120);
		}

		// dessine tous les objets dans le buffer
		for (int k = 1; k < Objets.size(); k++) {
			Objet O = (Objet) Objets.get(k);

			O.draw(temps, buffer);

			// les canons sont dessines a part puisque leur definition n'est pas
			// la meme que pour les autres objets (pas d'image)
			if (O instanceof Tank) {
				O.joueur.canon.draw(buffer);
			}
		}

		// On dessine l'image associee au buffer dans le JFrame
		g.drawImage(ArrierePlan, 0, 0, this);
	}

	public void boucle_principale_jeu() {
		/*
		 * on gere le passage des tours avec une condition qui devient fausse a
		 * la fin de chaque tour. Le tour se termine lorsque le joueur tire ou
		 * lorsqu'il s'est ecoule 30 secondes. Alors, on passe a la trasition
		 * entre les tours.
		 */
		if (!finTourParTir && tempsTour / 10 < 30) {
			int i = joueurQuiJoue;

			if (ToucheGauche) {
				Joueurs[i].moveGauche();
			} else if (ToucheDroite) {
				Joueurs[i].moveDroite();
			} else {
				Joueurs[i].fixe();
			}

			if (ToucheHaut) {
				Joueurs[i].anglePlus();
			} else if (ToucheBas) {
				Joueurs[i].angleMoins();
			}

			if (ToucheEspace) {
				bombeActive = Joueurs[i].tire(force, vent);
				Objets.add(bombeActive);
				finTourParTir = true;
			}

			// si jamais le tank est detruit pendant son tour, on accelere le
			// temps du tour afin que le tour se termine
			if (!Joueurs[i].actif) {
				tempsTour = 500;
			}

		} else if (finTourParTir && !passageJoueur && !attenteJoueur) {
			// la premiere condition arrete le joueur et verifie que la bombe
			// tire a bien explose avant de changer de joueur
			Joueurs[joueurQuiJoue].fixe();

			if (!bombeActive.actif) {
				passageJoueur = true;
				tempsTour = 0;
			}

			timerTour.stop();

		} else if (tempsTour / 10 >= 30) {
			// la deuxieme condition arrete le joueur dans le cas ou le tour ce
			// serait termine a cause du temps
			Joueurs[joueurQuiJoue].fixe();

			finTourParTir = true;
			passageJoueur = true;

			tempsTour = 0;
			timerTour.stop();

		} else if (passageJoueur) {
			// on parcourt ensuite la liste des joueurs encore vivants pour
			// trouver le joueur suivant
			if (JoueursActifs.size() > 1) {
				do {
					if (joueurQuiJoue + 1 == nombreJoueurs) {
						joueurQuiJoue = 0;
					} else {
						joueurQuiJoue++;
					}
				} while (!Joueurs[joueurQuiJoue].actif);
			}

			// si il ne reste plus qu'un seul joueur, le jeu est termine
			if (JoueursActifs.size() <= 1) {
				finJeu = true;
				System.exit(0);
			}

			// on modifie le vent pour le tour a venir
			vent = vent + (float) (0.05 * Math.random() - 0.025);

			if (vent > 0.05) {
				vent = (float) 0.05;
			} else if (vent < -0.05) {
				vent = (float) -0.05;
			}

			passageJoueur = false;
			attenteJoueur = true;

		} else if (attenteJoueur) {
			// on attend enfin que le joueur ait appuye sur entre pour continuer
			if (ToucheEntre) {
				finTourParTir = false;
				attenteJoueur = false;

				timerTour.start();
			}
		}

		// on balaye la liste et on fait bouger tout les objets avec la classe
		// move qui leur est propre
		for (int k = 0; k < Objets.size(); k++) {
			Objet O = (Objet) Objets.get(k);
			O.move(temps);
		}

		// on balaye la liste et supprime tous les objets inactifs
		// ainsi on ne paindra que les objets encore actifs
		for (int k = 0; k < Objets.size(); k++) {
			Objet O = (Objet) Objets.get(k);
			if (O.actif == false) {
				Objets.remove(k);
				k--;
			}
		}

		// force le rafraichissement de l'image et le dessin de l'objet
		repaint();
	}

	public static class Bandeau extends JFrame {

		private JPanel cadre;
		private JSlider vitesseInitiale;
		private JSlider angle;
		private JLabel labelvit;
		private JLabel labelangle;
		private JLabel vie;
		private JLabel fuel;

		static final int FPS_MIN = 0;
		static final int FPS_MAX = 10;
		static final int FPS_INIT = 5;

		public Bandeau() {
			this.setTitle("Gestion des parametres");
			this.cadre = new JPanel();
			cadre.setLayout(new FlowLayout());

			labelvit = new JLabel("Vitesse");
			vitesseInitiale = new JSlider(JSlider.HORIZONTAL, FPS_MIN, FPS_MAX,
					FPS_INIT);
			labelangle = new JLabel("Angle");
			angle = new JSlider(JSlider.HORIZONTAL, FPS_MIN, FPS_MAX, FPS_INIT);
			vie = new JLabel("Vie = variablevie");
			fuel = new JLabel("Fuel = variablefuel");

			JPanel buttonPane = new JPanel();

			cadre.add(labelvit);
			cadre.add(vitesseInitiale);
			cadre.add(labelangle);
			cadre.add(angle);
			cadre.add(vie);
			cadre.add(fuel);

			this.setContentPane(cadre);
			this.setSize(250, 150);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setVisible(true);

		}

		// public static void main(String[] args) {
		// Bandeau fenetre = new Bandeau();
		// }
	}

	public void this_keyPressed(KeyEvent e) {
		// code correspond a la touche appuyee, stock un nombre pour une touche
		int code = e.getKeyCode();
		// Suivant la touche appuyee, on previent jeu que celle-ci est appuyee
		if (code == 37) {
			ToucheGauche = true;
		} else if (code == 39) {
			ToucheDroite = true;
		} else if (code == 38) {
			ToucheHaut = true;
		} else if (code == 40) {
			ToucheBas = true;
		} else if (code == 32) {
			ToucheEspace = true;
		} else if (code == 10) {
			ToucheEntre = true;
		} else if (code == 27) {
			ToucheEchap = true;

			// Si c'est la touche echape on fait pause
			if (timer.isRunning()) {
				timer.stop();
			} else
				timer.start();
		}
	}

	public void this_keyReleased(KeyEvent e) {
		// code correspond a la touche relachee, stock un nombre pour une touche
		int code = e.getKeyCode();
		if (code == 37) {
			ToucheGauche = false;
		} else if (code == 39) {
			ToucheDroite = false;
		} else if (code == 38) {
			ToucheHaut = false;
		} else if (code == 40) {
			ToucheBas = false;
		} else if (code == 32) {
			ToucheEspace = false;
		} else if (code == 10) {
			ToucheEntre = false;
		} else if (code == 27) {
			ToucheEchap = false;
		}
	}

	private class TimerAction implements ActionListener {
		// ActionListener appelee toutes les 10 millisecondes comme demande a
		// l'initialisation du timer
		public void actionPerformed(ActionEvent e) {
			// Lance boucle_principale_jeu toute les 10 ms
			boucle_principale_jeu();
			temps++;
		}
	}

	private class TimerTourAction implements ActionListener {
		// ActionListener appelee toutes les 100 millisecondes comme demande a
		// l'initialisation du timer
		public void actionPerformed(ActionEvent e) {
			tempsTour++;
		}
	}

	private class Jeu_this_keyAdapter extends KeyAdapter {
		/*
		 * Jeu contenu dans notre ecouteur, quand on cree un objet
		 * Jeu_this_keyAdapter, il stock l'objet Jeu qu'il doit ecouter Ainsi
		 * l'objet jeu a un ecouteur de clavier et l'ecouteur de clavier a le
		 * Jeu auquel il se referent (ils s'ecoutent mutuellement)
		 */
		private Jeu NotreCombinaison;

		Jeu_this_keyAdapter(Jeu adaptee) {
			this.NotreCombinaison = adaptee;
		}

		/*
		 * Quand on appuie une touche, on cree un KeyEvent, cette methode est
		 * appelee elle demande ensuite au Jeu auquelle elle se refere de lancer
		 * la methode this_keyPressed(e)
		 */
		public void keyPressed(KeyEvent e) {
			NotreCombinaison.this_keyPressed(e);
		}

		// Meme remarque
		public void keyReleased(KeyEvent e) {
			NotreCombinaison.this_keyReleased(e);
		}
	}
}
