import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Menu extends JFrame implements ActionListener {
	private final Font Captain = creerFont(60, "Captain");
	private final Font CaptainSmall = creerFont(30, "Captain");
	private final Font CaptainSuperSmall = creerFont(20, "Captain");

	private final String[] nomsIA = new String[] { "Cewen", "Kekin", "Mib",
			"Loll", "Dreney", "Ann", "Hager", "Gurwan", "Mik", "Nudar",
			"Grinik", "Thanb", "Rudam", "Lanba", "Deggs", "Deedge", "Sak",
			"Yolik", "Dundatt", "Rulek", "Dub", "Cecan", "Manran", "Orek",
			"Paul", "Hell", "Diney", "Haty", "Leggs", "Tank", "Kunty", "Ruger",
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

	private int nombreJoueursHumains;
	private int nombreJoueursIA;

	private String[] noms;
	private String[] couleurs;

	private JPanel principalPanel;

	private JLabel labelHaut;
	private JComboBox<String> choixNbJoueurs;

	private JLabel labelBas;
	private JComboBox<String> choixNbIA;

	private JComboBox<String> choixCouleurs;

	private JTextField nomField;

	private JRadioButton debutant;
	private JRadioButton facile;
	private JRadioButton moyen;
	private JRadioButton difficile;
	private JRadioButton hardcore;

	private ButtonGroup difficulteGroupe = new ButtonGroup();
	private JPanel difficultePanel;

	private JButton musiqueButton;
	private boolean musiqueOn;

	private JButton next;

	private boolean selectionNbJoueurs;
	private boolean creationJoueurs;
	private boolean reglageParametres;

	private int i;

	public Menu() {
		selectionNbJoueurs = false;
		creationJoueurs = false;
		reglageParametres = false;
		musiqueOn = true;

		i = 0;

		this.setTitle("Tanks");
		this.setSize(600, 400);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage("Icone.png"));

		nomField = new JTextField();
		nomField.setFont(CaptainSmall);
		nomField.setHorizontalAlignment(0);

		choixCouleurs = new JComboBox<String>(new String[] { "vert", "rouge",
				"bleu", "jaune", "gris", "marron", "violet", "rose" });

		principalPanel = new JPanel();

		// on va positionner directement les boutons avec des coordonnes, plus
		// simple pour les centrer (parce que les layouts ne correspondent
		// pas avec ce que l'on cherche a obtenir) et la fenetre n'est pas
		// redimensionnable de toute facon
		principalPanel.setLayout(null);

		selectionNbJoueurs();

		this.getContentPane().add(principalPanel);

		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private void selectionNbJoueurs() {
		labelHaut = new JLabel("Choisissez le nombre de joueurs");
		labelHaut.setFont(CaptainSmall);
		labelHaut.setHorizontalAlignment(0);

		choixNbJoueurs = new JComboBox<String>(new String[] { "0", "1", "2",
				"3", "4", "5", "6", "7", "8" });
		choixNbJoueurs.setFont(CaptainSuperSmall);
		choixNbJoueurs.setSelectedIndex(2);

		labelBas = new JLabel("Choisissez le nombre d'Ordinateurs");
		labelBas.setFont(CaptainSmall);
		labelBas.setHorizontalAlignment(0);

		choixNbIA = new JComboBox<String>(new String[] { "0", "1", "2", "3",
				"4", "5", "6", "7", "8" });
		choixNbIA.setFont(CaptainSuperSmall);

		next = new JButton("Suivant");
		next.setFont(CaptainSuperSmall);
		next.setVerticalAlignment(SwingConstants.BOTTOM);
		next.addActionListener(this);

		principalPanel.add(labelHaut);
		principalPanel.add(choixNbJoueurs);
		principalPanel.add(labelBas);
		principalPanel.add(choixNbIA);

		labelHaut.setBounds(0, 50, 600, 30);
		choixNbJoueurs.setBounds(280, 90, 40, 30);
		labelBas.setBounds(0, 180, 600, 30);
		choixNbIA.setBounds(280, 220, 40, 30);

		principalPanel.add(next);
		next.setBounds(250, 300, 100, 35);

		selectionNbJoueurs = true;
	}

	private void creationJoueurs() {
		labelHaut.setText("Entrez le nom du joueur " + (i + 1));

		labelBas = new JLabel("Choisissez sa couleur");
		labelBas.setFont(CaptainSmall);
		labelBas.setHorizontalAlignment(0);

		choixCouleurs.setFont(CaptainSuperSmall);

		principalPanel.add(labelHaut);
		principalPanel.add(nomField);
		principalPanel.add(labelBas);
		principalPanel.add(choixCouleurs);

		labelHaut.setBounds(0, 50, 600, 30);
		nomField.setBounds(100, 90, 400, 40);
		labelBas.setBounds(0, 180, 600, 30);
		choixCouleurs.setBounds(250, 220, 100, 30);

		principalPanel.add(next);
		next.setBounds(250, 300, 100, 35);
	}

	private void reglageParametres() {
		labelHaut.setText("Choisissez la difficulte");

		debutant = new JRadioButton("debutant");
		debutant.setFont(CaptainSuperSmall);
		facile = new JRadioButton("facile");
		facile.setFont(CaptainSuperSmall);
		moyen = new JRadioButton("moyen");
		moyen.setFont(CaptainSuperSmall);
		difficile = new JRadioButton("difficile");
		difficile.setFont(CaptainSuperSmall);
		hardcore = new JRadioButton("hardcore");
		hardcore.setFont(CaptainSuperSmall);

		moyen.setSelected(true);

		difficulteGroupe = new ButtonGroup();
		difficulteGroupe.add(debutant);
		difficulteGroupe.add(facile);
		difficulteGroupe.add(moyen);
		difficulteGroupe.add(difficile);
		difficulteGroupe.add(hardcore);

		difficultePanel = new JPanel(new FlowLayout());

		difficultePanel.add(debutant);
		difficultePanel.add(facile);
		difficultePanel.add(moyen);
		difficultePanel.add(difficile);
		difficultePanel.add(hardcore);

		musiqueButton = new JButton("Musique : on");
		musiqueButton.setFont(CaptainSuperSmall);
		musiqueButton.setVerticalAlignment(SwingConstants.BOTTOM);
		musiqueButton.addActionListener(this);

		next.setText("Jouer");

		principalPanel.add(labelHaut);
		principalPanel.add(difficultePanel);
		principalPanel.add(musiqueButton);

		labelHaut.setBounds(0, 80, 600, 30);
		difficultePanel.setBounds(0, 120, 600, 50);
		musiqueButton.setBounds(230, 200, 140, 35);

		principalPanel.add(next);
		next.setBounds(250, 300, 100, 35);
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

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == next) {
			if (selectionNbJoueurs) {
				nombreJoueursHumains = choixNbJoueurs.getSelectedIndex();
				nombreJoueursIA = choixNbIA.getSelectedIndex();

				if ((nombreJoueursHumains + nombreJoueursIA) > 8) {
					JOptionPane
							.showMessageDialog(
									this,
									"Le nombre de joueurs total doit être inférieur ou égal à 8 !",
									"Trop de joueurs",
									JOptionPane.WARNING_MESSAGE);

				} else if ((nombreJoueursHumains + nombreJoueursIA) < 2) {
					JOptionPane
							.showMessageDialog(this,
									"Il faut au moins 2 joueurs pour jouer !",
									"Pas assez de joueurs",
									JOptionPane.WARNING_MESSAGE);
				} else {
					selectionNbJoueurs = false;

					noms = new String[nombreJoueursHumains + nombreJoueursIA];
					couleurs = new String[nombreJoueursHumains
							+ nombreJoueursIA];

					principalPanel.removeAll();
					repaint();

					if (nombreJoueursHumains > 0) {
						creationJoueurs = true;

						creationJoueurs();
					} else {
						reglageParametres = true;

						reglageParametres();
					}
				}

			} else if (creationJoueurs) {
				if (nomField.getText().length() > 8) {
					JOptionPane.showMessageDialog(this,
							"Choisissez un nom de 8 caractères maximum !",
							"Nom trop long", JOptionPane.WARNING_MESSAGE);

				} else if (nomField.getText().equals("")) {
					JOptionPane.showMessageDialog(this, "Choisissez un nom !",
							"You had one job !", JOptionPane.WARNING_MESSAGE);

				} else if (i < nombreJoueursHumains) {
					noms[i] = nomField.getText();
					couleurs[i] = choixCouleurs.getItemAt(choixCouleurs
							.getSelectedIndex());
					choixCouleurs
							.removeItemAt(choixCouleurs.getSelectedIndex());

					nomField.setText("");

					principalPanel.removeAll();
					repaint();

					i++;

					if (i < nombreJoueursHumains) {
						creationJoueurs();
					}
				}

				if (i == nombreJoueursHumains) {
					creationJoueurs = false;
					reglageParametres = true;

					principalPanel.removeAll();
					repaint();

					reglageParametres();
				}

			} else if (reglageParametres) {

				int d = 0;

				if (debutant.isSelected()) {
					d = 1;
				} else if (facile.isSelected()) {
					d = 2;
				} else if (moyen.isSelected()) {
					d = 3;
				} else if (difficile.isSelected()) {
					d = 4;
				} else if (hardcore.isSelected()) {
					d = 5;
				} else {
					System.out.println("Erreur sur la difficulte (selection)");
					System.exit(0);
				}

				if (d != 0) {
					for (int k = 0; k < nombreJoueursIA; k++) {
						noms[nombreJoueursHumains + k] = nomsIA[(int) (100 * Math
								.random())];
						couleurs[nombreJoueursHumains + k] = choixCouleurs
								.getItemAt(choixCouleurs.getSelectedIndex());
						choixCouleurs.removeItemAt(choixCouleurs
								.getSelectedIndex());
					}

					this.setVisible(false);

					Jeu MonJeu = new Jeu(
							nombreJoueursHumains + nombreJoueursIA, d, noms,
							couleurs, nombreJoueursHumains, Captain,
							CaptainSmall, musiqueOn);
				}
			}

		} else if (source == musiqueButton) {
			musiqueOn = !musiqueOn;

			if (musiqueOn) {
				musiqueButton.setText("Musique : on");
			} else {
				musiqueButton.setText("Musique : off");
			}
		}
	}
}
