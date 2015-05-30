import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import java.util.Iterator;
import java.util.LinkedList;

public class Jeu extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int nombreJoueurs = 5;
	boolean IA = !true;

	final String[] nomsIA = new String[] { "Cewen", "Kekin", "Mib", "Loll",
			"Dreney", "Ann", "Hager", "Gurwan", "Mik", "Nudar", "Grinik",
			"Thanb", "Rudam", "Lanba", "Deggs", "Deedge", "Sak", "Yolik",
			"Dundatt", "Rulek", "Dub", "Cecan", "Manran", "Orek", "Paul",
			"Hell", "Diney", "Haty", "Leggs", "Tank", "Kunty", "Ruger",
			"Bondam", "Rono", "Nury", "Gunrell", "Thanl", "Ceggs", "Thanvan",
			"Gurke", "Bonran", "Minik", "Vanl", "Deke", "Lowan", "Sak", "Sano",
			"Kodar", "Gurcan", "Tanran", "Olo", "Thanr", "Nelo", "Tek",
			"Deeran", "Rogan", "Thando", "Bagan", "Maba", "Haba", "Dawan",
			"Ok", "Neney", "Rento", "Hary", "Danlek", "Thanl", "Anato", "Kunl",
			"Obirek", "Drenik", "Kovan", "Ranke", "Blainar", "Lel", "Gunry",
			"Brirek", "Anaty", "Direll", "Dedge", "Rangan", "Kundar", "Bonlik",
			"Lob", "Nudatt", "Tegan", "Hadar", "Bridatt", "Kelo", "Lodatt",
			"Yodo", "Thancus", "Bocan", "Oke", "Iaty", "Nun", "Dunvan", "Roty",
			"Thanno", "Dar" };

	final Font Captain = creerFont(60, "Captain");
	final Font CaptainSmall = creerFont(30, "Captain");

	// Liste de tous les objets du jeu (tanks, bombes, canon)
	LinkedList<Objet> Objets;
	LinkedList<Joueur> JoueursActifs;
	Joueur[] Joueurs = new Joueur[nombreJoueurs];

	// timer qui regit le jeu
	Timer timer;
	// timer qui regit le passage des tours
	Timer timerTour;
	// timer qui lance la musique en boucle
	Timer timerMusique;

	// compteurs de temps associés aux timers
	long temps;
	long tempsTour;

	BufferedImage ArrierePlan;
	Graphics buffer;
	// on cree un objet du type message afin de pouvoir donner des
	// informations a l'utilisateur de maniere simple a n'importe quelle
	// moment du jeu
	Message message;

	JPanel centralPanel;
	JPanel milieuPanel;
	JButton quitter;
	JProgressBar forceBar;

	boolean ToucheHaut;
	boolean ToucheBas;
	boolean ToucheGauche;
	boolean ToucheDroite;
	boolean ToucheEspace;
	boolean ToucheEntre;
	boolean ToucheEchap;

	Rectangle Ecran;
	Carte map;

	// ces differents boolean servent au passage de tours
	boolean finJeu;
	boolean finTourParTir;
	boolean attenteJoueur;
	boolean attenteIA;
	boolean passageJoueur;

	// deux parametres pour gerer les tours
	int joueurQuiJoue;
	Bombe bombeActive;

	// parametres de selection des bombes
	final String[] bombes = new String[] { "gun", "rpg", "obus", "v2", "ogive",
			"tsar bomba" };
	int bombeArmee;

	// parametres IA
	boolean angleChoisi;
	double angleIA;
	// difficulte de 1 a 5
	int difficulte = 1;

	double vent;

	// le JPanel d'affichage des informations du joueur
	Bandeau bandeau;
	// couleur du bandeau et de la carte
	Color bleu = new Color(2, 13, 23);

	boolean rainbows;

	public Jeu() {
		// on regle le layout pour le bandeau
		this.setLayout(new BorderLayout());

		// initialisation des parametres de tours
		finJeu = false;
		finTourParTir = false;
		attenteJoueur = false;
		attenteIA = false;
		passageJoueur = false;

		tempsTour = 0;
		joueurQuiJoue = 0;

		bombeArmee = 0;

		temps = 0;

		// le vent varie entre 0.01 et -0.01
		vent = (1 / (double) difficulte) * 0.02 * Math.random() - 0.01;

		angleChoisi = false;

		setTitle("Tanks");
		setIconImage(Toolkit.getDefaultToolkit().getImage("Icone.png"));

		// on recupere la taille exploitable de l'ecran
		GraphicsEnvironment env = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		Rectangle bounds = env.getMaximumWindowBounds();

		// et on regle notre fenetre a cette taille
		setSize(bounds.width, bounds.height);

		// On interdit de changer la taille de la fenetre
		setResizable(false);
		setUndecorated(true);
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

		// l'ecran est notre fenetre de je, on enleve 100 pour laisser la place
		// au bandeauu
		Ecran = new Rectangle(0, 0, getSize().width, getSize().height - 150);

		// On met l'arriere plan fixe pour eviter de scintiller quand on
		// redessinera a chaque fois tout
		ArrierePlan = new BufferedImage(getSize().width,
				getSize().height - 150, BufferedImage.TYPE_INT_RGB);
		// On indique que buffer contient les dessins de arriere plan, si on
		// modifie buffer, on modifie arriere plan
		buffer = ArrierePlan.getGraphics();
		// on cree le message qui s'affichera lorqu'on aura besoin de donner un
		// message au joueur
		message = new Message(buffer, Ecran, Captain);

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

		Joueurs[0] = new Joueur(0, placement[0], nombreJoueurs, "Eloi", null,
				true, bombes.length, map, Ecran, bandeau, JoueursActifs);

		// on ajoute le tank et son canon a la liste d'objets
		Objets.add(Joueurs[0].canon);
		Objets.add(Joueurs[0].tank);

		// on ajoute le joueur a liste des joueurs en vie
		JoueursActifs.add(Joueurs[0]);

		// on cree les joueurs (et ainsi leurs tanks et leurs canons)
		for (int i = 1; i < nombreJoueurs; i++) {
			Joueurs[i] = new Joueur(i, placement[i], nombreJoueurs,
					nomsIA[(int) (100 * Math.random())], null, IA,
					bombes.length, map, Ecran, bandeau, JoueursActifs);

			// on ajoute le tank et son canon a la liste d'objets
			Objets.add(Joueurs[i].canon);
			Objets.add(Joueurs[i].tank);

			// on ajoute le joueur a liste des joueurs en vie
			JoueursActifs.add(Joueurs[i]);
		}

		// on cree le bandeau
		bandeau = new Bandeau(Ecran.width, bleu, CaptainSmall, Captain);
		bandeau.setVent(vent);
		bandeau.setNom(Joueurs[0].nom, Joueurs[0].couleur);
		bandeau.bombePrev.addActionListener(this);
		bandeau.bombeNext.addActionListener(this);
		// on ajoute notre bandeau a la fenetre
		this.getContentPane().add(bandeau, BorderLayout.NORTH);

		centralPanel = new JPanel();
		centralPanel
				.setLayout(new BoxLayout(centralPanel, BoxLayout.PAGE_AXIS));
		centralPanel.setOpaque(false);
		centralPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		centralPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
		this.getContentPane().add(centralPanel, BorderLayout.CENTER);

		milieuPanel = new JPanel(
				new BoxLayout(milieuPanel, BoxLayout.LINE_AXIS));
		milieuPanel.setOpaque(false);

		quitter = new JButton("Quitter");
		quitter.setPreferredSize(new Dimension(250, 100));
		quitter.setFont(Captain);
		quitter.setForeground(bleu);
		quitter.setFocusable(false);
		quitter.addActionListener(this);
		quitter.setVisible(false);

		forceBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
		forceBar.setPreferredSize(new Dimension(400, 30));
		forceBar.setBackground(new Color(0, 255, 0));
		forceBar.setVisible(false);

		centralPanel.add(forceBar);
		centralPanel.add(quitter);

		// On initialise le timer du jeu afin d'avoir un jeu fluide (il bat
		// idealement toute les 100*TEMPS millisecondes)
		timer = new Timer((int) (10), new TimerAction());

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

		timerMusique = new Timer(1000 * (5 * 60 + 5), new TimerMusiqueAction());

		// on lance la musique une fois, les fois suivante seront lancees par le
		// timer
		Thread musique = new Son("Musique.wav");
		musique.start();

		// On lance les timers
		timer.start();
		timerTour.start();
		timerMusique.start();

		// on balaye la liste et on fait bouger tout les objets avec la
		// classe move qui leur est propre
		Iterator<Objet> k = Objets.iterator();

		while (k.hasNext()) {
			Objet O = (Objet) k.next();
			O.move();
		}

		bandeau.setForce(Joueurs[0].force);
		bandeau.setAngle(Joueurs[0].angle);

		// on affiche la fenetre enfin prete
		setVisible(true);
	}

	public void paint(Graphics g) {
		// on dessine les boutons meme s'ils ne sont pas visibles;
		quitter.repaint();
		forceBar.repaint();

		// on dessine le bandeau
		bandeau.repaint();

		// la carte possede sa propre methode d'affichage
		map.drawHorizon(Ecran, buffer, bleu);

		// dessine tous les objets dans le buffer
		Iterator<Objet> k = Objets.iterator();

		while (k.hasNext()) {
			Objet O = (Objet) k.next();

			O.draw(buffer);

			// les canons sont dessines a part puisque leur definition n'est pas
			// la meme que pour les autres objets (pas d'image)
			if (O instanceof Tank) {
				Tank t = (Tank) O;

				t.canon.draw(buffer);

				if (O.joueur.n != joueurQuiJoue) {
					buffer.setFont(CaptainSmall);
					drawStringCentre("" + (int) t.joueur.vie,
							(int) (O.getCenterX()), (int) (O.y - 25));
				}
			}
		}

		// on dessine tout les messages
		drawInfos();

		if (!Joueurs[joueurQuiJoue].isMoving) {
			drawViseur();
		}

		// On dessine finalement l'image associee au buffer dans le JFrame
		g.drawImage(ArrierePlan, 0, 150, this);
	}

	private void boucle_principale_jeu() {

		if (!finJeu) {
			int j = joueurQuiJoue;

			// mise a jour des parametres du bandeau
			miseAJourBandeau(j);

			/*
			 * on gere le passage des tours avec une condition qui devient
			 * fausse a la fin de chaque tour. Le tour se termine lorsque le
			 * joueur tire ou lorsqu'il s'est ecoule 30 secondes. Alors, on
			 * passe a la trasition entre les tours.
			 */
			if (!finTourParTir && tempsTour / 10 < 30 && Joueurs[j].estHumain) {
				boucleHumain(j);
			} else if (!finTourParTir && tempsTour / 10 < 30
					&& !Joueurs[j].estHumain) {
				boucleIA(j);
			} else {
				// on gere le passage de tour dans une methode separee afin
				// d'alleger la boucle principale
				passageTour();
			}

			Joueurs[joueurQuiJoue].move();

			// on balaye la liste et supprime tous les objets inactifs
			// ainsi on ne paindra que les objets encore actifs
			Iterator<Objet> k1 = Objets.iterator();

			while (k1.hasNext()) {
				Objet O = (Objet) k1.next();

				if (O.actif == false) {
					k1.remove();
				}

			}

			// on balaye la liste et supprime tous les joueurs inactifs
			// ainsi on ne paindra que les objets encore actifs
			Iterator<Joueur> k2 = JoueursActifs.iterator();

			while (k2.hasNext()) {
				Joueur O = (Joueur) k2.next();

				if (O.actif == false) {
					k2.remove();
				}
			}
		}

		// cheats code
		String cheat = "";

		switch (cheat) {
		case ("rainbows"):
			rainbows = true;
			break;
		}

		if (rainbows) {
			bleu = new Color((int) (Math.random() * 255),
					(int) (Math.random() * 255), (int) (Math.random() * 255));
		}

		// force le rafraichissement de l'image et le dessin de l'objet
		repaint();
	}

	private void boucleHumain(int j) {
		if (ToucheGauche) {
			Joueurs[j].moveGauche();
		} else if (ToucheDroite) {
			Joueurs[j].moveDroite();
		} else {
			Joueurs[j].fixe();
		}

		if (ToucheHaut) {
			Joueurs[j].anglePlus();
		} else if (ToucheBas) {
			Joueurs[j].angleMoins();
		}

		while (ToucheEspace) {

			Joueurs[j].force++;

			bombeActive = Joueurs[j].tire(vent, bombes[bombeArmee], bombeArmee);
			Objets.add(0, bombeActive);
			finTourParTir = true;
		}

		// si jamais le tank est detruit pendant son tour, on accelere
		// le temps du tour afin que le tour se termine
		if (!Joueurs[j].actif) {
			tempsTour = 300;
		}
	}

	private void boucleIA(int j) {

		// si jamais le tank est detruit pendant son tour, on accelere
		// le temps du tour afin que le tour se termine
		if (!Joueurs[j].actif) {
			tempsTour = 300;
		}

		if (!angleChoisi) {
			double[] angleTanks = new double[nombreJoueurs];
			bombeArmee = bombes.length - 1;

			while (Joueurs[j].arsenal[bombeArmee] == 0) {
				bombeArmee--;
			}

			angleIA = -1;

			Iterator<Joueur> k = JoueursActifs.iterator();
			while (k.hasNext()) {
				Joueur J = (Joueur) k.next();

				if (J.n != j) {
					double petitAngle = Joueurs[j].prevision(J.tank, 80)[0];
					double grandAngle = Joueurs[j].prevision(J.tank, 80)[1];

					if (petitAngle >= 0 && petitAngle <= 180
							&& Joueurs[j].testTir(80, petitAngle, null) == J.n) {
						angleTanks[J.n] = petitAngle;

					} else if (grandAngle >= 0 && grandAngle <= 180
							&& Joueurs[j].testTir(80, grandAngle, null) == J.n) {
						angleTanks[J.n] = grandAngle;

					} else {
						angleTanks[J.n] = 0;
					}
				}
			}

			double sum = 0;

			// on verifie que le tank a au moins un tank atteignable
			for (int m = 0; m < nombreJoueurs; m++) {
				sum += angleTanks[m];
			}

			// on traite le cas ou le tank ne pourrait toucher personne
			if (sum == 0) {
				Joueur J;

				if (((Joueur) JoueursActifs.get(0)).n != j) {
					J = (Joueur) JoueursActifs.get(0);
				} else {
					J = (Joueur) JoueursActifs.get(1);
				}

				if (J.tank.x >= Joueurs[j].tank.x) {
					Joueurs[j].moveDroite();
				} else {
					Joueurs[j].moveGauche();
				}

			} else {
				// on gere aussi la difficulte
				int joueurVise = 0;

				while (angleIA <= 0 | angleIA > 180) {
					joueurVise = (int) (nombreJoueurs * Math.random());

					if (joueurVise != Joueurs[j].n) {
						angleIA = angleTanks[joueurVise];
					}
				}

				double d = Joueurs[j].testTir(80
						+ (Joueurs[j].dico[0] + Joueurs[j].dico[1]) / 2
						+ Joueurs[j].defaut, angleIA, Joueurs[joueurVise].tank) / 10;

				if (Joueurs[joueurVise].tank.getCenterX() < Joueurs[j].tank
						.getCenterX()) {
					if (d >= 0) {
						Joueurs[j].dico[0] = (Joueurs[j].dico[0] + Joueurs[j].dico[1]) / 2;
					} else {
						Joueurs[j].dico[1] = (Joueurs[j].dico[0] + Joueurs[j].dico[1]) / 2;
					}
				} else {
					if (d >= 0) {
						Joueurs[j].dico[1] = (Joueurs[j].dico[0] + Joueurs[j].dico[1]) / 2;
					} else {
						Joueurs[j].dico[0] = (Joueurs[j].dico[0] + Joueurs[j].dico[1]) / 2;
					}
				}

				Joueurs[j].force = 80 + (Joueurs[j].dico[0] + Joueurs[j].dico[1]) / 2;

				Joueurs[j].fixe();
				angleChoisi = true;
			}

		} else {
			if ((int) angleIA != ((int) (Joueurs[j].angle))) {
				if (angleIA > Joueurs[j].angle) {
					Joueurs[j].anglePlus();
				} else if (angleIA < Joueurs[j].angle) {
					Joueurs[j].angleMoins();
				}
			} else if (angleIA != Joueurs[j].angle) {
				Joueurs[j].angle = angleIA;
			} else {
				bombeActive = Joueurs[j].tire(vent, bombes[bombeArmee],
						bombeArmee);
				Objets.add(0, bombeActive);
				finTourParTir = true;
			}

			if (Joueurs[j].getYCanon() > map.getY(Joueurs[j].getXCanon())) {
				angleChoisi = false;
			}
		}
	}

	private void passageTour() {
		if (finTourParTir && !passageJoueur && !attenteJoueur && !attenteIA) {
			// la premiere condition arrete le joueur et verifie que la
			// bombe
			// tire a bien explose avant de changer de joueur
			Joueurs[joueurQuiJoue].fixe();
			bombeActive.move();

			if (!bombeActive.actif) {
				passageJoueur = true;
				tempsTour = 0;
			}

			timerTour.stop();

		} else if (tempsTour / 10 >= 30) {
			// la deuxieme condition arrete le joueur dans le cas ou le tour
			// ce serait termine a cause du temps
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
			}

			vent = (1 / (double) difficulte) * 0.02 * Math.random() - 0.01;

			bandeau.setNom(Joueurs[joueurQuiJoue].nom,
					Joueurs[joueurQuiJoue].couleur);
			bandeau.setVent(vent);
			bandeau.setAngle(Joueurs[joueurQuiJoue].angle);
			bandeau.setForce(Joueurs[joueurQuiJoue].force);

			passageJoueur = false;

			if (Joueurs[joueurQuiJoue].estHumain) {
				attenteJoueur = true;
			} else {
				attenteIA = true;
				message.setMessage(temps, bleu, 2, Joueurs[joueurQuiJoue].nom
						+ " va prendre son tour", null);
			}

			for (int u = 0; u < 3; u++) {
				// on balaye la liste et on fait bouger tout les objets avec la
				// classe move qui leur est propre
				Iterator<Objet> k = Objets.iterator();

				while (k.hasNext()) {
					Objet O = (Objet) k.next();
					O.move();
				}
			}

		} else if (attenteJoueur) {
			// on balaye la liste et on fait bouger tout les objets avec la
			// classe move qui leur est propre
			Iterator<Objet> k = Objets.iterator();

			while (k.hasNext()) {
				Objet O = (Objet) k.next();
				O.move();
			}

			// on attend enfin que le joueur ait appuye sur entre pour
			// continuer
			if (ToucheEntre) {
				finTourParTir = false;
				attenteJoueur = false;
				angleChoisi = false;

				timerTour.start();
			}
		} else if (attenteIA) {

			if (!message.isDrawn | ToucheEntre) {
				if (ToucheEntre) {
					message.isDrawn = false;
				}

				finTourParTir = false;
				attenteIA = false;
				angleChoisi = false;

				timerTour.start();
			}
		}
	}

	private void miseAJourBandeau(int j) {
		if (Joueurs[j].estHumain && !attenteJoueur) {
			Joueurs[j].force = bandeau.getForce();
		}

		bandeau.setFuel(Joueurs[j].fuel);
		bandeau.setVie(Joueurs[j].vie);

		while (Joueurs[j].arsenal[bombeArmee] <= 0) {
			bombeArmee--;
		}

		bandeau.setBombe(bombes[bombeArmee], Joueurs[j].arsenal[bombeArmee]);

		bandeau.setTemps(30 - (int) (tempsTour / 10));

		bandeau.setForce((int) Joueurs[j].force);
		bandeau.setForceLabel();

		if (Math.abs(Joueurs[j].angle - bandeau.getAngle()) >= 1.1
				&& Joueurs[j].estHumain && !attenteJoueur) {
			Joueurs[j].angle = bandeau.getAngle();
		}

		bandeau.setAngle((int) Joueurs[j].angle);
		bandeau.setAngleLabel();
	}

	public void drawInfos() {
		buffer.setFont(Captain);
		buffer.setColor(bleu);

		if (attenteJoueur && !finJeu) {
			drawStringCentre("En attente de " + Joueurs[joueurQuiJoue].nom,
					Ecran.width / 2, 130);
			drawStringCentre("Appuyez sur Entree", Ecran.width / 2, 190);

		} else if (finJeu) {
			buffer.setColor(new Color(200, 0, 0));

			Iterator<Joueur> k = JoueursActifs.iterator();

			Joueur O = null;

			while (k.hasNext()) {
				O = (Joueur) k.next();
			}

			drawStringCentre("Game Over", Ecran.width / 2, 120);

			if (O != null) {
				drawStringCentre(O.nom + " a gagne !", Ecran.width / 2, 200);
			}
		}

		if (!finJeu) {
			message.drawMessage(temps);
		}
	}

	public void drawViseur() {
		buffer.setColor(Joueurs[joueurQuiJoue].couleur);

		double a = Math.toRadians(Joueurs[joueurQuiJoue].angle);

		int xV = (int) (Joueurs[joueurQuiJoue].tank.canon.x + Math.cos(a) * 100);
		int yV = (int) (Joueurs[joueurQuiJoue].tank.canon.y - Math.sin(a) * 100);

		buffer.drawOval(xV - 10, yV - 10, 20, 20);
	}

	private Font creerFont(int taille, String font) {
		try {
			// la police creer est de taille 1
			Font police = Font.createFont(Font.TRUETYPE_FONT, new File(font
					+ ".ttf"));

			// on change alors la taille de la police
			return police.deriveFont((float) taille);

		} catch (IOException | FontFormatException e) {
			System.out.println(font + " introuvable !");
			System.out.println("Mettre la police dans le repertoire source");
			System.exit(0);
			return null;
		}
	}

	private void drawStringCentre(String s, int xPos, int yPos) {
		int stringLength = (int) buffer.getFontMetrics()
				.getStringBounds(s, buffer).getWidth();

		buffer.drawString(s, xPos - stringLength / 2, yPos);
	}

	private void this_keyPressed(KeyEvent e) {
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
				timerTour.stop();

				quitter.setVisible(true);
			} else {
				timer.start();
				timerTour.start();

				quitter.setVisible(false);
			}
		}
	}

	private void this_keyReleased(KeyEvent e) {
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

	private class TimerMusiqueAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Lance la musique toute les 5 minutes et 10 secondes
			Thread musique = new Son("Musique.wav");
			musique.start();
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

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (Joueurs[joueurQuiJoue].estHumain) {
			if (source == bandeau.bombePrev) {
				bombeArmee--;

				if (bombeArmee == -1) {
					bombeArmee = bombes.length - 1;
				}
			} else if (source == bandeau.bombeNext) {
				do {
					bombeArmee++;

					if (bombeArmee == bombes.length) {
						bombeArmee = 0;
					}
				} while (Joueurs[joueurQuiJoue].arsenal[bombeArmee] <= 0);
			} else if (source == quitter) {
				System.exit(0);
			}
		}
	}
}
