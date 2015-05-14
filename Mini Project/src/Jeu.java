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
	Timer timer;
	long temps;
	BufferedImage ArrierePlan;
	Graphics buffer;
	boolean ToucheHaut;
	boolean ToucheBas;
	boolean ToucheGauche;
	boolean ToucheDroite;
	boolean ToucheEspace;
	Rectangle Ecran;

	LinkedList<Tank> Tanks;
	LinkedList<Object> Objects;
	int nombreJoueurs = 2;
	int score;
	boolean finjeu;

	public Jeu() {
		score = 0;
		finjeu = false;

		setTitle("Tanks");
		// Taille de l'écran de jeu
		setSize(700, 480);
		// On interdit de changer la taille de la fenêtre
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// On ajoute l'écouteur de clavier qui se réfère à cette classe
		// même
		this.addKeyListener(new Jeu_this_keyAdapter(this));

		temps = 0;
		// Aucune touche n'est appuyée, donc tout est false
		ToucheHaut = false;
		ToucheBas = false;
		ToucheGauche = false;
		ToucheDroite = false;
		ToucheEspace = false;
		// L'écran est notre fenêtre de jeu, on doit donc prendre la taille de
		// la fenêtre moins le superflue
		// (comme les bordures de fenêtre, le truc en haut avec le nom du
		// programme et la croix pour fermer)
		// getInsets.left(ou top,right,bottom) récupère le taille de ces
		// bordures. Il est utilisé dans une classe reprenant
		// Jframe donc s'applique à cette JFrame, et on créer notre rectangle
		// avec par exemple une hauteur qui vaut:
		// hauteur=hauteur de fenêtre - marge en haut - marge en bas
		Ecran = new Rectangle(getInsets().left, getInsets().top,
				getSize().width - getInsets().right - getInsets().left,
				getSize().height - getInsets().bottom - getInsets().top);

		// On met l'arrière plan fixe pour éviter de scintiller quand on
		// redessinera à chaque fois tout
		ArrierePlan = new BufferedImage(getSize().width, getSize().height,
				BufferedImage.TYPE_INT_RGB);
		// On indique que buffer contient les dessins de arrier plan, si on
		// modifie buffer, on modifie arrière plan
		buffer = ArrierePlan.getGraphics();

		// Créer la liste chainée en mémoire
		Objects = new LinkedList<Object>();
		// Créer la liste chainée de tanks en mémoire
		Tanks = new LinkedList<Tank>();

		for (int i = 0; i < nombreJoueurs; i++) {
			Tank n = new Tank(("Tank_" + i), i, true, 0);
			// Ajouter le tank dans les listes d'objets
			Tanks.add(n);
			Objects.add(n);
		}

		String NomsImages = null;

		// On initialise le timer à une action toutes les 100 ms
		timer = new Timer(100, new TimerAction());
		// On lance le timer
		timer.start();

		setVisible(true);
	}

	public void paint(Graphics g) {
		// On remplit le buffer en noir, ça se répercute sur l'arrière plan
		// (toujours définir la couleur avant de dessiner)
		buffer.setColor(Color.black);
		buffer.fillRect(Ecran.x, Ecran.y, Ecran.x + Ecran.width, Ecran.y
				+ Ecran.height);
		// On rajoute le score dans le buffer
		buffer.setColor(Color.white);
		buffer.drawString("SCORE : " + score, 50, Ecran.height - 20);

		// On créé la map
		int h = getHeight();
		int w = getWidth();
		int n = 20;

		g.setColor(Color.blue);
		g.fillRect(0, 0, w, h);

		g.setColor(Color.green);
		int horizon1 = 2 * h / 3;

		for (int i = 0; i < w / n; i++) {
			Random rand = new Random();
			int randomNum = rand.nextInt(10) - 5;

			int horizon2 = horizon1 + randomNum;
			int[] xpoints = new int[] { n * i, n * i, n * (i + 1), n * (i + 1) };
			int[] ypoints = new int[] { h, horizon1, horizon2, h };
			g.fillPolygon(xpoints, ypoints, 4);

			horizon1 = horizon2;
		}

		// dessine TOUS les objets dans le buffer
		for (int k = 0; k < Objects.size(); k++) {
			Object O = (Object) Objects.get(k);
			O.draw(temps, buffer);
		}
		// On dessine l'image associée au buffer dans le JFrame
		g.drawImage(ArrierePlan, 0, 0, this);
	}

	/*
	 * public void Collision(){ if(Math.abs(x.Tank-x.Bombe)<=l.Tank ||
	 * Math.abs(y.Tank-y.Bombe)<= h.Tank){ vie.tank -= 25;
	 * 
	 * 
	 * } }
	 */

	public void boucle_principale_jeu() {
		/*
		 * RETRAVAILLER BOUCLE PRINCIPALE UNIQUEMENT QUAND TOUT LE RESTE
		 * FONCTIONNERA if (ToucheGauche) { vaisseau.dx = -1; vaisseau.dy = 0; }
		 * else if (ToucheDroite) { vaisseau.dx = +1; vaisseau.dy = 0; } else if
		 * (ToucheHaut) { vaisseau.dx = 0; vaisseau.dy = -1; } else if
		 * (ToucheBas) { vaisseau.dx = 0; vaisseau.dy = +1; } else { vaisseau.dx
		 * = 0; vaisseau.dy = 0; } // déplace tous les objets par Polymorphisme
		 * for (int k = 0; k < Objets.size(); k++) { Objet O = (Objet)
		 * Objets.get(k); O.move(temps); } //On ajoute le missile si il est
		 * tiré if (ToucheEspace) { Missile M = new Missile(vaisseau.x +
		 * vaisseau.l / 2, vaisseau.y, Ecran, "MISSILE"); Objets.add(M); }
		 * 
		 * //On rajoute une bombe qui tombe du haut de l'écran //3/80 de chance
		 * de faire tomber une bombe toutes des 100 ms int t = (int) (80 *
		 * Math.random()); if (t <= 2) { t = 2; } //On ajoute aux 3/80 chances
		 * 1/2 chances d'être réellement lancé, donc 3/160 chances par 100 ms
		 * // on va simplifier le jeu en lançant un missile au hasard du haut
		 * du frame if (temps % t == 0) { Bombe B = new Bombe((int)
		 * (Math.random() * Ecran.width), 0, Ecran, "BOMBE"); Objets.add(B); }
		 * 
		 * //On parcours tous nos objets à la recherche des missiles for (int
		 * k1 = 0; k1 < Objets.size(); k1++) { Objet O1 = (Objet)
		 * Objets.get(k1); //On trouve un missile if ((O1.actif) &&
		 * (O1.nom_objet == "MISSILE")) { for (int k2 = 0; k2 < Objets.size();
		 * k2++) { Objet O2 = (Objet) Objets.get(k2); //On cherche les aliens
		 * pour faire des couples miisiles/aliens if ((O2.actif) &&
		 * (O2.nom_objet == "ALIEN")) { //On regarde si l'alien et le missile
		 * sont en collision if (O1.Collision(O2)) { //On vérifie si il n'y a
		 * plus d'alien if (nombreAliensVivants > 1) { nombreAliensVivants--; }
		 * else { nombreAliensVivants--; finjeu = true; } // mis a jour du score
		 * score += ((Alien) O2).score; // on supprimera plus tard ces objets
		 * O1.actif = false; O2.actif = false; } } } } }
		 * 
		 * //On recherche maintenant les bombes for (int k = 0; k <
		 * Objets.size(); k++) { Objet O = (Objet) Objets.get(k); //On trouve
		 * une bombe if (O.nom_objet == "BOMBE") { //Il y a bien collision entre
		 * une bombe et le vaisseau if ((O.actif) && (vaisseau.Collision(O))) {
		 * //On éteint la bombe, elle disparait O.actif = false; //On vérifie
		 * qu'il reste des vies au joueur if (nombreViesRestantes > 0) {
		 * nombreViesRestantes--; } else { finjeu = true; } } } }
		 * 
		 * // On vérifie qu'un alien actif ne touche pas le vaisseau for (int
		 * k1 = 0; k1 < Objets.size(); k1++) { Objet O1 = (Objet)
		 * Objets.get(k1); if ((O1.actif) && (O1.nom_objet == "ALIEN")) { if
		 * (vaisseau.Collision(O1)) { O1.actif = false; //Si il touche le
		 * vaisseau, on perd une vie sauf si l'on en a plus if
		 * (nombreViesRestantes > 0) { nombreViesRestantes--; } else { finjeu =
		 * true; } } } //Si un alien est en bas, le jeu est terminé if ((O1.y
		 * >= this.getSize().width) && (O1.nom_objet == "ALIEN")) { finjeu =
		 * true; } }
		 * 
		 * //Gère la fin de partie if (finjeu) { //On désactive tous les
		 * objets for (int k1 = 0; k1 < Objets.size(); k1++) { Objet O1 =
		 * (Objet) Objets.get(k1); O1.actif = false; } //On voit si c'est
		 * victoire ou défaite //On créé ensuite l'objet message
		 * correspondant //L'objet message sera traité comme un objet par le
		 * Paint //On a donc peu de choses à modifier if (nombreAliensVivants
		 * == 0) { Objet m = new Message(true, score, Ecran); Objets.add(m); }
		 * else { Objet m = new Message(false, score, Ecran); Objets.add(m); } }
		 * 
		 * // accélère le jeu toutes les 10 secondes if (temps % 100 == 0) {
		 * timer.setDelay((int) (timer.getDelay() * 0.9)); }
		 * 
		 * // balaye la liste et supprime tous les objets inactifs // Ainsi on
		 * ne paindra que les objets encore actifs for (int k = 0; k <
		 * Objets.size(); k++) { Objet O = (Objet) Objets.get(k); if (O.actif ==
		 * false) { Objets.remove(k); k--; } }
		 * 
		 * // force le rafraîchissement de l'image et le dessin de l'objet
		 * repaint(); }
		 */
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
			this.setTitle("Gestion des paramètres");
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

		public static void main(String[] args) {
			Bandeau fenetre = new Bandeau();

		}
	}

	public void this_keyPressed(KeyEvent e) {
		// code correspond à la touche appuyée, stock un nombre pour une
		// touche
		int code = e.getKeyCode();
		// Suivant la touche appuyée, on prévient jeu que celle-ci est
		// appuyée
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
		}

		// Si c'est la touche entrée on fait pause
		else if (code == 10) {
			if (timer.isRunning()) {
				timer.stop();
			} else
				timer.start();
		}
		// Si c'est la touche échape on quitte
		else if (code == 27) {
			System.exit(0);
		}
	}

	public void this_keyReleased(KeyEvent e) {
		// code correspond à la touche relachée, stock un nombre pour une
		// touche
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
		}
	}

	// Classe interne à la classe jeu, elle peut modifier les attributs de la
	// classe jeu (ici temps nous intéresse)

	private class TimerAction implements ActionListener {
		// ActionListener appelee toutes les 100 millisecondes comme demandé à
		// l'initialisation du timer
		public void actionPerformed(ActionEvent e) {
			// Lance boucle_principale_jeu toutes les 100 ms
			boucle_principale_jeu();
			temps++;
		}
	}

	private class Jeu_this_keyAdapter extends KeyAdapter {
		// Jeu contenu dans notre écouteur, quand on créé un objet
		// Jeu_this_keyAdapter il stock l'objet Jeu qu'il doit écouter
		// Ainsi l'objet jeu à un écouteur de clavier et l'écouteur de
		// clavier a le Jeu auquel il se réfèrent (ils s'écoutent
		// mutuellement)
		private Jeu NotreCombinaison;

		Jeu_this_keyAdapter(Jeu adaptee) {
			this.NotreCombinaison = adaptee;
		}

		// Quand on appuie une touche, on créé un KeyEvent, cette méthode est
		// appelée
		// elle demande ensuite au Jeu auquelle elle se réfère de lancer la
		// méthode this_keyPressed(e)
		public void keyPressed(KeyEvent e) {
			NotreCombinaison.this_keyPressed(e);
		}

		// Même remarque
		public void keyReleased(KeyEvent e) {
			NotreCombinaison.this_keyReleased(e);
		}

	}
}
