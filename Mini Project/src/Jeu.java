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
	Carte map;

	public Jeu() {
		score = 0;
		finjeu = false;

		setTitle("Tanks");
		// Taille de l'ecran de jeu
		setSize(700, 480);
		// On interdit de changer la taille de la fenetre
		setResizable(false);
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
		// L'ecran est notre fenetre de jeu, on doit donc prendre la taille de
		// la fenetre moins le superflue
		// (comme les bordures de fenetre, le truc en haut avec le nom du
		// programme et la croix pour fermer)
		// getInsets.left(ou top,right,bottom) recupere le taille de ces
		// bordures. Il est utilise dans une classe reprenant
		// Jframe donc s'applique a cette JFrame, et on creer notre rectangle
		// avec par exemple une hauteur qui vaut:
		// hauteur=hauteur de fenetre - marge en haut - marge en bas
		Ecran = new Rectangle(getInsets().left, getInsets().top,
				getSize().width - getInsets().right - getInsets().left,
				getSize().height - getInsets().bottom - getInsets().top);

		// On met l'arriere plan fixe pour eviter de scintiller quand on
		// redessinera a chaque fois tout
		ArrierePlan = new BufferedImage(getSize().width, getSize().height,
				BufferedImage.TYPE_INT_RGB);
		// On indique que buffer contient les dessins de arrier plan, si on
		// modifie buffer, on modifie arriere plan
		buffer = ArrierePlan.getGraphics();

		// Creer la liste chainee en memoire
		Objects = new LinkedList<Object>();
		// Creer la liste chainee de tanks en memoire
		Tanks = new LinkedList<Tank>();

		for (int i = 0; i < nombreJoueurs; i++) {
			Tank n = new Tank(("Tank_" + i), i, true, 0);
			// Ajouter le tank dans les listes d'objets
			Tanks.add(n);
			Objects.add(n);
		}

		String NomsImages = null;

		// On initialise le timer a une action toutes les 100 ms
		timer = new Timer(100, new TimerAction());
		// On lance le timer
		timer.start();

		setVisible(true);
	}

	public void paint(Graphics g) {
		// On remplit le buffer en noir, ca se repercute sur l'arriere plan
		// (toujours definir la couleur avant de dessiner)
		buffer.setColor(Color.black);
		buffer.fillRect(Ecran.x, Ecran.y, Ecran.x + Ecran.width, Ecran.y
				+ Ecran.height);
		// On rajoute le score dans le buffer
		buffer.setColor(Color.white);
		buffer.drawString("SCORE : " + score, 50, Ecran.height - 20);

		// On initialise la map
		map = new Carte(g, Ecran);

		// dessine TOUS les objets dans le buffer
		for (int k = 0; k < Objects.size(); k++) {
			Object O = (Object) Objects.get(k);
			O.draw(temps, buffer);
		}
		// On dessine l'image associee au buffer dans le JFrame
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
		 * = 0; vaisseau.dy = 0; } // deplace tous les objets par Polymorphisme
		 * for (int k = 0; k < Objets.size(); k++) { Objet O = (Objet)
		 * Objets.get(k); O.move(temps); } //On ajoute le missile si il est tire
		 * if (ToucheEspace) { Missile M = new Missile(vaisseau.x + vaisseau.l /
		 * 2, vaisseau.y, Ecran, "MISSILE"); Objets.add(M); }
		 * 
		 * //On rajoute une bombe qui tombe du haut de l'ecran //3/80 de chance
		 * de faire tomber une bombe toutes des 100 ms int t = (int) (80 *
		 * Math.random()); if (t <= 2) { t = 2; } //On ajoute aux 3/80 chances
		 * 1/2 chances d'etre reellement lance, donc 3/160 chances par 100 ms //
		 * on va simplifier le jeu en lancant un missile au hasard du haut du
		 * frame if (temps % t == 0) { Bombe B = new Bombe((int) (Math.random()
		 * * Ecran.width), 0, Ecran, "BOMBE"); Objets.add(B); }
		 * 
		 * //On parcours tous nos objets a la recherche des missiles for (int k1
		 * = 0; k1 < Objets.size(); k1++) { Objet O1 = (Objet) Objets.get(k1);
		 * //On trouve un missile if ((O1.actif) && (O1.nom_objet == "MISSILE"))
		 * { for (int k2 = 0; k2 < Objets.size(); k2++) { Objet O2 = (Objet)
		 * Objets.get(k2); //On cherche les aliens pour faire des couples
		 * miisiles/aliens if ((O2.actif) && (O2.nom_objet == "ALIEN")) { //On
		 * regarde si l'alien et le missile sont en collision if
		 * (O1.Collision(O2)) { //On verifie si il n'y a plus d'alien if
		 * (nombreAliensVivants > 1) { nombreAliensVivants--; } else {
		 * nombreAliensVivants--; finjeu = true; } // mis a jour du score score
		 * += ((Alien) O2).score; // on supprimera plus tard ces objets O1.actif
		 * = false; O2.actif = false; } } } } }
		 * 
		 * //On recherche maintenant les bombes for (int k = 0; k <
		 * Objets.size(); k++) { Objet O = (Objet) Objets.get(k); //On trouve
		 * une bombe if (O.nom_objet == "BOMBE") { //Il y a bien collision entre
		 * une bombe et le vaisseau if ((O.actif) && (vaisseau.Collision(O))) {
		 * //On eteint la bombe, elle disparait O.actif = false; //On verifie
		 * qu'il reste des vies au joueur if (nombreViesRestantes > 0) {
		 * nombreViesRestantes--; } else { finjeu = true; } } } }
		 * 
		 * // On verifie qu'un alien actif ne touche pas le vaisseau for (int k1
		 * = 0; k1 < Objets.size(); k1++) { Objet O1 = (Objet) Objets.get(k1);
		 * if ((O1.actif) && (O1.nom_objet == "ALIEN")) { if
		 * (vaisseau.Collision(O1)) { O1.actif = false; //Si il touche le
		 * vaisseau, on perd une vie sauf si l'on en a plus if
		 * (nombreViesRestantes > 0) { nombreViesRestantes--; } else { finjeu =
		 * true; } } } //Si un alien est en bas, le jeu est termine if ((O1.y >=
		 * this.getSize().width) && (O1.nom_objet == "ALIEN")) { finjeu = true;
		 * } }
		 * 
		 * //Gere la fin de partie if (finjeu) { //On desactive tous les objets
		 * for (int k1 = 0; k1 < Objets.size(); k1++) { Objet O1 = (Objet)
		 * Objets.get(k1); O1.actif = false; } //On voit si c'est victoire ou
		 * defaite //On cree ensuite l'objet message correspondant //L'objet
		 * message sera traite comme un objet par le Paint //On a donc peu de
		 * choses a modifier if (nombreAliensVivants == 0) { Objet m = new
		 * Message(true, score, Ecran); Objets.add(m); } else { Objet m = new
		 * Message(false, score, Ecran); Objets.add(m); } }
		 * 
		 * // accelere le jeu toutes les 10 secondes if (temps % 100 == 0) {
		 * timer.setDelay((int) (timer.getDelay() * 0.9)); }
		 * 
		 * // balaye la liste et supprime tous les objets inactifs // Ainsi on
		 * ne paindra que les objets encore actifs for (int k = 0; k <
		 * Objets.size(); k++) { Objet O = (Objet) Objets.get(k); if (O.actif ==
		 * false) { Objets.remove(k); k--; } }
		 * 
		 * // force le rafraichissement de l'image et le dessin de l'objet
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

		public static void main(String[] args) {
			Bandeau fenetre = new Bandeau();

		}
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
		}

		// Si c'est la touche entree on fait pause
		else if (code == 10) {
			if (timer.isRunning()) {
				timer.stop();
			} else
				timer.start();
		}
		// Si c'est la touche echape on quitte
		else if (code == 27) {
			System.exit(0);
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
		 * Jeu_this_keyAdapter il stock l'objet Jeu qu'il doit ecouter Ainsi
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
