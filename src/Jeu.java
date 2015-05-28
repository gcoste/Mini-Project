import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

public class Jeu extends JFrame {
	int nombreJoueurs = 5;

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

	// ces differents boolean servent au passage de tours
	boolean finJeu;
	boolean finTourParTir;
	boolean attenteJoueur;
	boolean passageJoueur;

	// deux paramètres pour gerer les tours
	int joueurQuiJoue;
	Bombe bombeActive;

	// parametres IA
	boolean listeAnglesTermine;
	double angleIA;

	double vent;

	// le JPanel d'affichage des informations du joueur
	Bandeau bandeau;
	// couleur du bandeau et de la carte
	Color bleu = new Color(2, 13, 23);

	boolean rainbows;

	public Jeu() {
		// on regle le layout pour le bandeau
		this.setLayout(new BorderLayout());

		// initialisation des parametres
		finJeu = false;
		finTourParTir = false;
		attenteJoueur = false;
		passageJoueur = false;

		temps = 0;
		tempsTour = 0;
		joueurQuiJoue = 0;

		listeAnglesTermine = false;

		// On place le vent à 0 pour l'instant
		// le vent oscille entre 0.05 et -0.05;
		vent = 0;// (0.1 * Math.random() - 0.05);

		setTitle("Tanks");

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

		Joueurs[0] = new Joueur(0, placement[0], nombreJoueurs, null, null,
				true, map, Ecran, bandeau, JoueursActifs);

		// on ajoute le tank et son canon a la liste d'objets
		Objets.add(Joueurs[0].canon);
		Objets.add(Joueurs[0].tank);

		// on ajoute le joueur a liste des joueurs en vie
		JoueursActifs.add(Joueurs[0]);

		// on cree les joueurs (et ainsi leurs tanks et leurs canons)
		for (int i = 1; i < nombreJoueurs; i++) {
			Joueurs[i] = new Joueur(i, placement[i], nombreJoueurs, null, null,
					false, map, Ecran, bandeau, JoueursActifs);

			// on ajoute le tank et son canon a la liste d'objets
			Objets.add(Joueurs[i].canon);
			Objets.add(Joueurs[i].tank);

			// on ajoute le joueur a liste des joueurs en vie
			JoueursActifs.add(Joueurs[i]);
		}

		// on cree le bandeau
		bandeau = new Bandeau(Ecran.width, bleu);
		// on ajoute notre bandeau a la fenetre
		this.getContentPane().add(bandeau, BorderLayout.NORTH);

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

		// On lance les timers
		timer.start();
		timerTour.start();

		// on balaye la liste et on fait bouger tout les objets avec la
		// classe move qui leur est propre
		Iterator<Objet> k = Objets.iterator();

		while (k.hasNext()) {
			Objet O = (Objet) k.next();
			O.move(temps);
		}

		bandeau.setAngle(Joueurs[0].tank.angle);

		// on affiche la fenetre enfin prete
		setVisible(true);
	}

	public void paint(Graphics g) {
		// on dessine le bandeau
		bandeau.repaint();

		// on dessine d'abord le fond
		map.draw(temps, buffer);
		// la carte possede sa propre methode d'affichage
		map.drawHorizon(Ecran, buffer, bleu);

		// from here

		Font comic = new Font("Comic Sans MS", 1, 20);
		Font comicLarge = new Font("Comic Sans MS", 1, 30);

		buffer.setFont(comic);
		buffer.setColor(Color.black);
		buffer.drawString("Temps : " + (30 - (int) (tempsTour / 10)), 20, 30);

		if (attenteJoueur && !finJeu) {
			buffer.setFont(comicLarge);
			buffer.setColor(Color.red);
			buffer.drawString("En attente du joueur " + joueurQuiJoue,
					Ecran.width / 2 - 160, 80);
			buffer.drawString("Appuyez sur Entrée", Ecran.width / 2 - 135, 120);
		}

		if (finJeu) {
			Iterator<Joueur> k = JoueursActifs.iterator();

			Joueur O = null;

			while (k.hasNext()) {
				O = (Joueur) k.next();
			}

			buffer.setFont(comicLarge);
			buffer.setColor(Color.red);
			buffer.drawString("Game Over", Ecran.width / 2 - 70, 100);
			if (O != null) {
				buffer.drawString("Le joueur " + O.n + " a gagné !",
						Ecran.width / 2 - 135, 150);
			}
		}

		// to here : ca vire

		// dessine tous les objets dans le buffer
		Iterator<Objet> k = Objets.iterator();

		while (k.hasNext()) {
			Objet O = (Objet) k.next();

			O.draw(temps, buffer);

			// les canons sont dessines a part puisque leur definition n'est pas
			// la meme que pour les autres objets (pas d'image)
			if (O instanceof Tank) {
				O.joueur.canon.draw(buffer);
			}
		}

		// On dessine l'image associee au buffer dans le JFrame
		g.drawImage(ArrierePlan, 0, 150, this);
	}

	public void boucle_principale_jeu() {

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

				if (ToucheEspace) {
					bombeActive = Joueurs[j].tire(vent);
					Objets.add(0, bombeActive);
					finTourParTir = true;
				}

				// si jamais le tank est detruit pendant son tour, on accelere
				// le temps du tour afin que le tour se termine
				if (!Joueurs[j].actif) {
					tempsTour = 1000;
				}

			} else if (!finTourParTir && tempsTour / 10 < 30
					&& !Joueurs[j].estHumain) {

				// si jamais le tank est detruit pendant son tour, on accelere
				// le temps du tour afin que le tour se termine
				if (!Joueurs[j].actif) {
					tempsTour = 1000;
				}

				if (!listeAnglesTermine) {
					double[] angleTanks = new double[nombreJoueurs];
					angleIA = -1;

					Iterator<Joueur> k = JoueursActifs.iterator();
					Joueurs[j].tank.force = (int) (50 * Math.random() + 50);

					while (k.hasNext()) {
						Joueur J = (Joueur) k.next();

						if (J.n != j) {
							double petitAngle = Joueurs[j].prevision(J.tank)[0];
							double grandAngle = Joueurs[j].prevision(J.tank)[1];

							if (petitAngle >= 0 && petitAngle <= 180
									&& Joueurs[j].testTir(petitAngle) == J.n) {
								angleTanks[J.n] = petitAngle;

							} else if (grandAngle >= 0 && grandAngle <= 180
									&& Joueurs[j].testTir(grandAngle) == J.n) {
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
						while (angleIA <= 0 | angleIA > 180) {
							int p = (int) (nombreJoueurs * Math.random());

							if (p != Joueurs[j].n) {
								angleIA = angleTanks[p];
							}
						}

						Joueurs[j].fixe();
						listeAnglesTermine = true;
					}

				} else {
					if ((int) angleIA != ((int) (Joueurs[j].tank.angle))) {
						if (angleIA > Joueurs[j].tank.angle) {
							Joueurs[j].anglePlus();
						} else if (angleIA < Joueurs[j].tank.angle) {
							Joueurs[j].angleMoins();
						}
					} else if (angleIA != Joueurs[j].tank.angle) {
						Joueurs[j].tank.angle = angleIA;
					} else {
						bombeActive = Joueurs[j].tire(vent);
						Objets.add(0, bombeActive);
						finTourParTir = true;
					}
				}
			} else if (finTourParTir && !passageJoueur && !attenteJoueur) {
				// la premiere condition arrete le joueur et verifie que la
				// bombe
				// tire a bien explose avant de changer de joueur
				Joueurs[joueurQuiJoue].fixe();

				if (!bombeActive.actif) {
					passageJoueur = true;
					tempsTour = 0;
				}

				timerTour.stop();

			} else if (tempsTour / 10 >= 30) {
				// la deuxieme condition arrete le joueur dans le cas ou le tour
				// ce
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
				}

				bandeau.setAngle(Joueurs[joueurQuiJoue].tank.angle);
				bandeau.setForce(Joueurs[joueurQuiJoue].tank.force);

				/*
				 * // on modifie le vent pour le tour a venir vent = vent +
				 * (0.05 * Math.random() - 0.025);
				 * 
				 * if (vent > 0.05) { vent = 0.05; } else if (vent < -0.05) {
				 * vent = -0.05; }
				 */

				passageJoueur = false;
				attenteJoueur = true;

			} else if (attenteJoueur) {
				// on attend enfin que le joueur ait appuye sur entre pour
				// continuer
				if (ToucheEntre) {
					finTourParTir = false;
					attenteJoueur = false;
					listeAnglesTermine = false;

					timerTour.start();
				}
			}

			// on balaye la liste et on fait bouger tout les objets avec la
			// classe move qui leur est propre
			Iterator<Objet> k = Objets.iterator();

			while (k.hasNext()) {
				Objet O = (Objet) k.next();
				O.move(temps);
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

	public void miseAJourBandeau(int j) {
		if (Joueurs[j].estHumain) {
			Joueurs[j].tank.force = bandeau.getForce();
		}

		bandeau.setFuel(Joueurs[j].tank.fuel);
		bandeau.setVie(Joueurs[j].tank.vie);

		bandeau.setForce((int) Joueurs[j].tank.force);
		bandeau.setForceLabel();

		if (Math.abs(Joueurs[j].tank.angle - bandeau.getAngle()) >= 1.1
				&& Joueurs[j].estHumain) {
			Joueurs[j].tank.angle = bandeau.getAngle();
		}

		bandeau.setAngle((int) Joueurs[j].tank.angle);
		bandeau.setAngleLabel();
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
				timerTour.stop();
			} else {
				timer.start();
				timerTour.start();
			}
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
