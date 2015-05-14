import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;

import javax.swing.*;

import java.util.LinkedList;
import java.util.Random;
import java.awt.FlowLayout;
import java.io.ObjectStreamConstants;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class Jeu extends JFrame {
	// Liste de tous les objets du jeu (tanks, bombes, canon)
	LinkedList<Objet> Objets;
	LinkedList<Tank> Tanks;

	Timer timer;
	long temps;
	BufferedImage ArrierePlan;
	Graphics buffer;
	boolean ToucheHaut;
	boolean ToucheBas;
	boolean ToucheGauche;
	boolean ToucheDroite;
	boolean ToucheEspace;
	boolean ToucheEchap;
	Rectangle Ecran;

	int nombreJoueurs = 2;
	int score;
	int vent;

	boolean finjeu;
	Carte map;

	public Jeu() {
		score = 0;
		finjeu = false;

		setTitle("Tanks");
		// Taille de l'ecran de jeu
		setSize(1600, 900);
		// On interdit de changer la taille de la fenetre
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// On ajoute l'ecouteur de clavier qui se refere a cette classe meme
		this.addKeyListener(new Jeu_this_keyAdapter(this));

		temps = 0;
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
		Ecran = new Rectangle(getInsets().left, getInsets().top,
				getSize().width - getInsets().right - getInsets().left,
				getSize().height - getInsets().bottom - getInsets().top);

		// On met l'arriere plan fixe pour eviter de scintiller quand on
		// redessinera a chaque fois tout
		ArrierePlan = new BufferedImage(getSize().width, getSize().height,
				BufferedImage.TYPE_INT_RGB);
		// On indique que buffer contient les dessins de arriere plan, si on
		// modifie buffer, on modifie arriere plan
		buffer = ArrierePlan.getGraphics();

		// Creer la liste chainee en memoire
		Objets = new LinkedList<Objet>();
		// Creer la liste chainee de tanks en memoire
		Tanks = new LinkedList<Tank>();
		// On initialise la map
		map = new Carte(Ecran);

		Tank tank1 = new Tank(Ecran, map, "Tank_1", 1, true);
		// Ajouter le tank dans les listes d'objets
		Tanks.add(tank1);
		Objets.add(tank1);

		// On initialise le timer a une action toutes les 100 ms
		timer = new Timer(100, new TimerAction());
		// On lance le timer
		timer.start();

		setVisible(true);
	}

	public void paint(Graphics g) {
		// On ajoute le score dans le buffer
		buffer.setColor(Color.white);
		buffer.drawString("SCORE : " + score, 50, Ecran.height - 20);

		map.paint(Ecran, buffer);


		// dessine TOUS les objets dans le buffer
		for (int k = 0; k < Objets.size(); k++) {
			Objet O = (Objet) Objets.get(k);
			O.draw(temps, buffer);
		}
		// On dessine l'image associee au buffer dans le JFrame
		g.drawImage(ArrierePlan, 0, 0, this);
	}

	public void boucle_principale_jeu() {
		repaint();
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
