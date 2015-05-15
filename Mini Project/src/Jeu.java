import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.LinkedList;

public class Jeu extends JFrame {
	int nombreJoueurs = 7;

	// Liste de tous les objets du jeu (tanks, bombes, canon)
	LinkedList<Objet> Objets;
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

	final double TEMPS = 0.1;

	boolean finJeu;
	boolean finTour;
	int joueurActif;
	long debutTour;
	int vent;

	public Jeu() {
		finJeu = false;

		temps = 0;
		tempsTour = 0;

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
		// On initialise la map
		map = new Carte(Ecran);

		// on cree les joueurs (et ainsi leurs tanks et leurs canons)
		for (int i = 0; i < nombreJoueurs; i++) {
			Joueurs[i] = new Joueur(i, null, null, true, this);

			// on ajoute le tank et son canon a la liste d'objets
			Objets.add(Joueurs[i].canon);
			Objets.add(Joueurs[i].tank);
		}

		// On initialise le timer afin d'avoir 60 frames par seconde
		timer = new Timer((int) (100 * TEMPS), new TimerAction());

		// On initialise le timer des tours (qui bat tout les 100ms)
		timerTour = new Timer(100, new TimerTourAction());

		// On lance les timers
		timer.start();
		timerTour.start();

		setVisible(true);
	}

	public void paint(Graphics g) {
		map.draw(Ecran, buffer);

		// dessine tous les objets dans le buffer
		for (int k = 0; k < Objets.size(); k++) {
			Objet O = (Objet) Objets.get(k);

			O.draw(temps, buffer);

			if (O instanceof Tank) {
				O.joueur.canon.draw(buffer);
			}
		}

		// On dessine l'image associee au buffer dans le JFrame
		g.drawImage(ArrierePlan, 0, 0, this);
	}

	public void boucle_principale_jeu() {
		if (!finTour && tempsTour / 10 < 30) {

			int i = 2;

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
				Joueurs[i].tire(100, temps);
			}

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
		} else {
			// passage au joueur suivant
		}
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
		} else if (code == 27) {
			ToucheEchap = false;
		}
	}

	// Classe interne a la classe jeu, elle peut modifier les attributs de la
	// classe jeu (ici temps nous interesse)

	private class TimerAction implements ActionListener {
		// ActionListener appelee toutes les 100 millisecondes comme demande a
		// l'initialisation du timer
		public void actionPerformed(ActionEvent e) {
			// Lance boucle_principale_jeu toutes les 100 ms
			boucle_principale_jeu();
			temps++;
		}
	}

	private class TimerTourAction implements ActionListener {
		// ActionListener appelee toutes les 100 millisecondes comme demande a
		// l'initialisation du timer
		public void actionPerformed(ActionEvent e) {
			// Lance boucle_principale_jeu toutes les 100 ms
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
