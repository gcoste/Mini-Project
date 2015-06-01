import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import java.util.Iterator;
import java.util.LinkedList;

public class Jeu extends JFrame implements ActionListener {
	private int nombreJoueurs;

	private Font Captain;
	private Font CaptainSmall;

	// Liste de tous les objets du jeu (tanks, bombes, canon)
	private LinkedList<Objet> Objets;
	private LinkedList<Joueur> JoueursActifs;
	private Joueur[] Joueurs;

	private LinkedList<Caisse> Caisses;

	// timer qui regit le jeu
	private Timer timer;
	// timer qui regit le passage des tours
	private Timer timerTour;
	// timer qui lance la musique en boucle
	private Timer timerMusique;

	// compteurs de temps associés aux timers
	private long temps;
	private long tempsTour;

	private int nTour;
	private double nCycle;

	private BufferedImage ArrierePlan;
	private Graphics buffer;
	// on cree un objet du type message afin de pouvoir donner des
	// informations a l'utilisateur de maniere simple a n'importe quelle
	// moment du jeu
	private Message message;

	private JPanel centralPanel;
	private JButton quitter;
	private JProgressBar forceBar;

	private boolean ToucheHaut;
	private boolean ToucheBas;
	private boolean ToucheGauche;
	private boolean ToucheDroite;
	private boolean ToucheEspace;
	private boolean ToucheEntre;

	private Rectangle limitesFrame;
	private Carte map;

	// ces differents boolean servent au passage de tours
	private boolean joueurFiring;
	private boolean joueurATire;
	private boolean finJeu;
	private boolean finTour;
	private boolean finTourParTir;
	private boolean caisseEnVol;
	private boolean passageJoueur;
	private boolean attenteJoueur;

	// parametres pour gerer les tours
	private int joueurQuiJoue;
	private Bombe[] bombesActives;
	private Caisse caisseActive;

	// parametres de selection des bombes
	private final String[] BOMBES = new String[] { "gun", "roquette", "obus",
			"v2", "ogive", "patate", "Attaque aerienne" };

	// parametres IA
	private boolean angleChoisi;
	private double angleIA;
	private double forceIA;

	// difficulte de 1 a 5
	private int difficulte;

	private double vent;

	// le JPanel d'affichage des informations du joueur
	private Bandeau bandeau;
	// couleur du bandeau et de la carte
	private Color bleu = new Color(2, 13, 23);

	private boolean rainbows;

	public Jeu(int nbJoueurs, int diffic, String[] nomsHerites,
			String[] couleursHerites, int nombreJoueursHumains, Font Cap,
			Font CapSmall, boolean musiqueOn) {
		nombreJoueurs = nbJoueurs;
		difficulte = diffic;

		Captain = Cap;
		CaptainSmall = CapSmall;

		// initialisation des parametres de tours
		joueurFiring = false;
		joueurATire = false;
		finJeu = false;
		finTour = false;
		finTourParTir = false;
		caisseEnVol = false;
		passageJoueur = false;
		attenteJoueur = false;

		tempsTour = 0;
		joueurQuiJoue = 0;

		angleChoisi = false;

		temps = 0;

		nTour = 0;
		nCycle = 0;

		// Aucune touche n'est appuyee, donc tout est false
		ToucheHaut = false;
		ToucheBas = false;
		ToucheGauche = false;
		ToucheDroite = false;
		ToucheEspace = false;
		ToucheEntre = false;

		// le vent varie entre 0.01 et -0.01
		vent = (1 / (double) difficulte) * (0.02 * Math.random() - 0.01);

		// on cree la fenetre
		this.setTitle("Tanks");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// on regle le layout pour le bandeau
		this.setLayout(new BorderLayout());

		this.setIconImage(Toolkit.getDefaultToolkit().getImage("Icone.png"));

		// on recupere la taille exploitable de l'ecran
		GraphicsEnvironment env = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		Rectangle bounds = env.getMaximumWindowBounds();

		// et on regle notre fenetre a cette taille
		this.setSize(bounds.width, bounds.height);

		// On interdit de changer la taille de la fenetre
		this.setResizable(false);
		this.setUndecorated(true);

		// On ajoute l'ecouteur de clavier qui se refere a cette classe meme
		this.addKeyListener(new Jeu_this_keyAdapter(this));

		// l'ecran est notre fenetre de je, on enleve 100 pour laisser la place
		// au bandeauu
		limitesFrame = new Rectangle(0, 0, getSize().width,
				getSize().height - 100);

		// On met l'arriere plan fixe pour eviter de scintiller quand on
		// redessinera a chaque fois tout
		ArrierePlan = new BufferedImage(getSize().width,
				getSize().height - 100, BufferedImage.TYPE_INT_RGB);
		// On indique que buffer contient les dessins de arriere plan, si on
		// modifie buffer, on modifie arriere plan
		buffer = ArrierePlan.getGraphics();
		// on cree le message qui s'affichera lorqu'on aura besoin de donner un
		// message au joueur
		message = new Message(buffer, limitesFrame, Captain);

		// Cree la liste chainee de tous les objets
		Objets = new LinkedList<Objet>();
		// Cree la liste chainee de tous les joueurs en vie
		JoueursActifs = new LinkedList<Joueur>();
		// Cree la liste chainee de toutes les caisses
		Caisses = new LinkedList<Caisse>();

		Joueurs = new Joueur[nombreJoueurs];

		// On initialise la map
		map = new Carte(limitesFrame);

		creationJoueurs(nomsHerites, couleursHerites, nombreJoueursHumains);

		creationBouttons();

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

		if (musiqueOn) {
			// timer qui relance la musique toute les 5 minutes et 5 secondes
			timerMusique = new Timer(1000 * (5 * 60 + 5),
					new TimerMusiqueAction());

			// on lance la musique une fois, les fois suivante seront lancees
			// par le
			// timer
			Thread musique = new Son("Musique.wav");
			musique.start();

			timerMusique.start();
		}

		// On lance les timers
		timer.start();
		timerTour.start();

		// on fait bouger tout les objets une fois pour les plcaer tous sur la
		// map
		Iterator<Objet> k = Objets.iterator();

		while (k.hasNext()) {
			Objet O = (Objet) k.next();
			O.move();
		}

		// on affiche la fenetre enfin prete
		this.setVisible(true);
	}

	private void creationJoueurs(String[] noms, String[] couleurs,
			int nombreJoueursHumains) {
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

		boolean estHumain;

		// on cree les joueurs (et ainsi leurs tanks et leurs canons)
		for (int i = 0; i < nombreJoueurs; i++) {

			if (i < nombreJoueursHumains) {
				estHumain = true;
			} else {
				estHumain = false;
			}

			Joueurs[i] = new Joueur(i, placement[i], nombreJoueurs, difficulte,
					noms[i], couleurs[i], estHumain, BOMBES.length, map,
					limitesFrame, bandeau, JoueursActifs, Objets);

			// on ajoute le tank et son canon a la liste d'objets
			Objets.add(Joueurs[i].canon);
			Objets.add(Joueurs[i].tank);

			// on ajoute le joueur a liste des joueurs en vie
			JoueursActifs.add(Joueurs[i]);
		}
	}

	private void creationBouttons() {
		// on cree le bandeau
		bandeau = new Bandeau(limitesFrame.width, bleu, CaptainSmall, Captain);
		bandeau.setVent(vent);
		bandeau.setNom(Joueurs[0].nom, Joueurs[0].couleur);
		bandeau.bombePrev.addActionListener(this);
		bandeau.bombeNext.addActionListener(this);

		// on ajoute notre bandeau a la fenetre
		this.getContentPane().add(bandeau, BorderLayout.NORTH);

		// on cree un panel pour placer le bouton quitter et la barre de force
		centralPanel = new JPanel();
		centralPanel.setLayout(null);
		centralPanel.setOpaque(false);
		this.getContentPane().add(centralPanel, BorderLayout.CENTER);

		// on cree le bouton quitter
		quitter = new JButton("Quitter");
		quitter.setFont(Captain);
		quitter.setForeground(bleu);
		quitter.setFocusable(false);
		quitter.addActionListener(this);
		quitter.setVisible(false);

		// on cree la barre de force
		forceBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
		forceBar.setPreferredSize(new Dimension(400, 30));
		forceBar.setOpaque(false);
		forceBar.setBorder(null);
		forceBar.setVisible(false);

		// et on les place dans le panel
		quitter.setBounds(limitesFrame.width / 2 - 120,
				limitesFrame.height / 2 - 230, 240, 100);
		forceBar.setBounds(limitesFrame.width / 2 - 300, 80, 600, 50);

		// et on les ajoute au panel
		centralPanel.add(forceBar);
		centralPanel.add(quitter);
	}

	public void paint(Graphics g) {
		// on dessine le bandeau
		bandeau.repaint();

		// la carte possede sa propre methode d'affichage
		map.drawHorizon(limitesFrame, buffer, bleu);

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

				if (O.joueur.n != joueurQuiJoue | attenteJoueur | caisseEnVol) {
					buffer.setFont(CaptainSmall);
					drawStringCentre("" + (int) t.joueur.vie,
							(int) (O.getCenterX()), (int) (O.y - 25));
				}
			}
		}

		// on dessine tout les messages
		drawInfos();

		if (Joueurs[joueurQuiJoue].bombeArmee < 6) {
			drawViseur();
		} else if (!joueurFiring) {
			drawViseur((int) Joueurs[joueurQuiJoue].xVisee,
					Joueurs[joueurQuiJoue].viseeDroite);
		}

		// On dessine finalement l'image associee au buffer dans le JFrame
		if (!timer.isRunning()) {
			quitter.repaint();
		} else if (!joueurFiring) {
			g.drawImage(ArrierePlan, 0, 100, this);
		} else {
			forceBar.setVisible(true);
			forceBar.setValue((int) Joueurs[joueurQuiJoue].force);

			int red = Math.min(255,
					(int) (Joueurs[joueurQuiJoue].force * 510 / 100));
			int green = Math.min(255,
					(int) (-Joueurs[joueurQuiJoue].force * 510 / 100) + 510);
			forceBar.setForeground(new Color(red, green, 0));
		}
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
			if (!finTour) {
				if ((!finTourParTir | tempsTour / 10 < 30)
						&& Joueurs[j].estHumain) {
					boucleHumain(j);
				} else if (!finTourParTir && tempsTour / 10 < 30
						&& !Joueurs[j].estHumain) {
					boucleIA(j);
				}
			} else {
				// on gere le passage de tour dans une methode separee afin
				// d'alleger la boucle principale
				passageTour();
			}

			// si jamais le tank est detruit pendant son tour, on accelere
			// le temps du tour afin que le tour se termine
			if (!Joueurs[j].actif) {
				tempsTour = 299;
				timerTour.stop();
			}

			Joueurs[joueurQuiJoue].move();

			Iterator<Caisse> k = Caisses.iterator();

			while (k.hasNext()) {
				Caisse C = (Caisse) k.next();

				C.actionCaisse(JoueursActifs, temps);

				if (!C.actif) {
					k.remove();
				}
			}

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
		if (!joueurFiring) {
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
		}

		if (!finTourParTir) {
			if (ToucheEspace) {
				if (Joueurs[joueurQuiJoue].bombeArmee >= 6) {
					joueurATire = true;
				} else if (Joueurs[joueurQuiJoue].force < 100) {
					Joueurs[j].force++;
					joueurFiring = true;
					timerTour.stop();
				}
			} else if (Joueurs[j].force >= 100) {
				joueurATire = true;
			}

			if (joueurATire) {
				forceBar.setVisible(false);

				bombesActives = Joueurs[j].tire(vent);

				for (int u = 0; u < bombesActives.length; u++) {
					Objets.add(0, bombesActives[u]);
				}

				joueurFiring = false;
				finTourParTir = true;

				tempsTour = 250;
				timerTour.start();
			}
		} else {
			for (int u = 0; u < bombesActives.length; u++) {
				bombesActives[u].move();
			}
		}

		if (tempsTour >= 299) {
			finTour = true;
			timerTour.stop();
		}
	}

	private void boucleIA(int j) {

		if (!angleChoisi) {
			double[] angleTanks = new double[nombreJoueurs];
			Joueurs[j].bombeArmee = BOMBES.length - 1;

			while (Joueurs[j].arsenal[Joueurs[j].bombeArmee] == 0) {
				Joueurs[j].bombeArmee--;
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

				forceIA = 80 + (Joueurs[j].dico[0] + Joueurs[j].dico[1]) / 2;

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
				if (Joueurs[j].force < forceIA) {
					Joueurs[j].force++;
					joueurFiring = true;

				} else {
					Joueurs[j].force = forceIA;

					timerTour.stop();
					joueurATire = true;
				}

				if (joueurATire) {
					forceBar.setVisible(false);

					bombesActives = Joueurs[j].tire(vent);
					Objets.add(0, bombesActives[0]);

					joueurFiring = false;
					finTourParTir = true;
					finTour = true;
				}
			}

			if (Joueurs[j].getYCanon() > map.getY(Joueurs[j].getXCanon())) {
				angleChoisi = false;
			}
		}
	}

	private void passageTour() {
		finTour = true;
		timerTour.stop();

		if (finTourParTir && !passageJoueur && !attenteJoueur && !caisseEnVol) {
			// la premiere condition arrete le joueur et verifie que la
			// bombe tire a bien explose avant de changer de joueur
			Joueurs[joueurQuiJoue].fixe();
			int sum = 0;

			for (int u = 0; u < bombesActives.length; u++) {
				bombesActives[u].move();

				if (!bombesActives[u].actif) {
					sum++;
				}
			}

			if (sum == bombesActives.length) {
				if (3 * Math.random() < 1) {
					caisseActive = new Caisse(map, limitesFrame, JoueursActifs,
							Objets, Caisses, message);
					caisseEnVol = true;
				}

				passageJoueur = true;
				finTourParTir = false;
				tempsTour = 0;
			}

		} else if (tempsTour / 10 >= 29) {
			// la deuxieme condition arrete le joueur dans le cas ou le tour
			// ce serait termine a cause du temps
			Joueurs[joueurQuiJoue].fixe();

			finTourParTir = false;
			passageJoueur = true;

			tempsTour = 0;

		} else if (!attenteJoueur) {
			if (passageJoueur) {
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

				// on reinitialise les parametres pour le tour a venir
				Joueurs[joueurQuiJoue].force = 0;

				vent = (1 / (double) difficulte)
						* (0.02 * Math.random() - 0.01);

				angleChoisi = false;

				nTour++;
				nCycle = (nTour / (double) nombreJoueurs);

				bandeau.setNom(Joueurs[joueurQuiJoue].nom,
						Joueurs[joueurQuiJoue].couleur);
				bandeau.setVent(vent);

				passageJoueur = false;
			} else if ((nCycle % 5 == 0 | nCycle % 10 == 0) && !message.isDrawn) {
				int ars = 0;

				if (nCycle % 3 == 0) {
					message.setMessage(temps, new Color(200, 0, 0), 2,
							"Vous recevez tous un missile v2", null);

					ars = 3;
				} else if (nCycle % 5 == 0) {
					message.setMessage(temps, new Color(200, 0, 0), 2,
							"Vous recevez tous une attaque aerienne", null);

					ars = 6;
				}

				Iterator<Joueur> k = JoueursActifs.iterator();

				while (k.hasNext()) {
					Joueur J = (Joueur) k.next();

					if (J.estHumain | (ars == 3 && difficulte > 4)) {
						J.arsenal[ars]++;
					}
				}

				// on met -1 pour que la condition ne soit pas revalidee
				// apres l'effacage du message
				nCycle = -1;

			} else if (!caisseEnVol && !message.isDrawn) {
				// on gere le cas ou on est sur un tour particulier pour valider
				// le passage de tour (voir 20 lignes plus haut)
				attenteJoueur = true;

				if (Joueurs[joueurQuiJoue].estHumain) {

					message.setMessage(temps, bleu, 5, "Au tour de "
							+ Joueurs[joueurQuiJoue].nom, "Preparez vous !");

				} else {
					message.setMessage(
							temps,
							bleu,
							2,
							Joueurs[joueurQuiJoue].nom + " va prendre son tour",
							null);
				}

			} else if (caisseEnVol) {
				caisseActive.move();

				if (caisseActive.estPose) {
					caisseEnVol = false;
				}
			}

		} else if (attenteJoueur) {
			// on attend enfin que le joueur ait appuye sur entre pour
			// continuer ou que le delai soit ecoule
			if (!message.isDrawn | ToucheEntre) {
				if (ToucheEntre) {
					message.isDrawn = false;
				}

				finTour = false;
				joueurATire = false;
				attenteJoueur = false;

				timerTour.start();
			}
		} else {
			System.out.println("Erreur sur le passage des tours");
			System.exit(0);
		}
	}

	private void miseAJourBandeau(int j) {
		bandeau.setFuel(Joueurs[j].fuel);
		bandeau.setVie(Joueurs[j].vie);

		while (Joueurs[j].arsenal[Joueurs[j].bombeArmee] <= 0) {
			Joueurs[j].bombeArmee--;
		}

		bandeau.setBombe(BOMBES[Joueurs[j].bombeArmee],
				Joueurs[j].arsenal[Joueurs[j].bombeArmee]);

		bandeau.setTemps(30 - (int) (tempsTour / 10));
	}

	private void drawInfos() {
		buffer.setFont(Captain);
		buffer.setColor(bleu);

		if (finJeu) {
			buffer.setColor(new Color(200, 0, 0));

			Iterator<Joueur> k = JoueursActifs.iterator();

			Joueur O = null;

			while (k.hasNext()) {
				O = (Joueur) k.next();
			}

			drawStringCentre("Game Over", limitesFrame.width / 2, 120);

			if (O != null) {
				drawStringCentre(O.nom + " a gagne !", limitesFrame.width / 2,
						200);
			}
		}

		if (!finJeu) {
			message.drawMessage(temps);
		}
	}

	private void drawViseur() {
		buffer.setColor(Joueurs[joueurQuiJoue].couleur);

		double a = Math.toRadians(Joueurs[joueurQuiJoue].angle);

		int xV = (int) (Joueurs[joueurQuiJoue].tank.canon.x + Math.cos(a) * 120);
		int yV = (int) (Joueurs[joueurQuiJoue].tank.canon.y - Math.sin(a) * 120);

		buffer.drawOval(xV - 10, yV - 10, 20, 20);

	}

	private void drawViseur(int x1, boolean droite) {
		buffer.setColor(Joueurs[joueurQuiJoue].couleur);

		Graphics2D g = (Graphics2D) buffer.create();

		double deltaX;
		double deltaY = 50;

		if (droite) {
			deltaX = 20;
		} else {
			deltaX = -20;
		}

		double angle = Math.atan2(deltaY, deltaX);
		int longueur = (int) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

		AffineTransform at = AffineTransform.getTranslateInstance(x1, 20);
		at.concatenate(AffineTransform.getRotateInstance(angle));
		g.transform(at);

		g.setStroke(new BasicStroke(15));

		g.drawLine(0, 0, longueur, 0);
		g.fillPolygon(new int[] { longueur, longueur, longueur + 50 },
				new int[] { -20, 20, 0 }, 3);
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
		} else if (code == 32 && !finTour && Joueurs[joueurQuiJoue].force < 100
				&& timer.isRunning()) {
			ToucheEspace = true;
		} else if (code == 10) {
			ToucheEntre = true;
		} else if (code == 27) {
			// Si c'est la touche echape on fait pause
			if (timer.isRunning()) {
				timer.stop();
				timerTour.stop();

				quitter.setVisible(true);
				quitter.repaint();
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

			if (Joueurs[joueurQuiJoue].estHumain) {
				joueurATire = true;
			}

		} else if (code == 10) {
			ToucheEntre = false;
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

		if (Joueurs[joueurQuiJoue].estHumain && !finTour) {
			if (source == bandeau.bombePrev) {
				Joueurs[joueurQuiJoue].bombeArmee--;

				if (Joueurs[joueurQuiJoue].bombeArmee == -1) {
					Joueurs[joueurQuiJoue].bombeArmee = BOMBES.length - 1;
				}
			} else if (source == bandeau.bombeNext) {
				do {
					Joueurs[joueurQuiJoue].bombeArmee++;

					if (Joueurs[joueurQuiJoue].bombeArmee == BOMBES.length) {
						Joueurs[joueurQuiJoue].bombeArmee = 0;
					}
				} while (Joueurs[joueurQuiJoue].arsenal[Joueurs[joueurQuiJoue].bombeArmee] <= 0);
			}
		}

		if (source == quitter) {
			System.exit(0);
		}
	}
}
