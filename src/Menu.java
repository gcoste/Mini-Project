import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Menu extends JFrame {
	final Font Captain = creerFont(60, "Captain");
	final Font CaptainSmall = creerFont(30, "Captain");

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

	int nombreJoueurs = 5;
	int difficulte = 5;

	String[] noms;
	String[] couleurs;
	boolean[] estHumain;

	JPanel principalPanel;

	JLabel nbJoueur;
	JComboBox selecNbJoueur;

	JLabel nbIA;
	JComboBox selecNbIA;

	public Menu() {
		this.setTitle("Tanks");
		this.setSize(600, 400);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage("Icone.png"));

		principalPanel = new JPanel();
		// on va positionner directement les boutons avec des coordonnes, plus
		// simple pour les centrer et la fenetre n'est pas redimensionnable de
		// toute facon
		principalPanel.setLayout(null);

		selectionNbJoueurs();

		this.getContentPane().add(principalPanel);

		this.setLocationRelativeTo(null);
		this.setVisible(true);

		// Jeu MonJeu = new Jeu(nombreJoueurs, difficulte, noms, couleurs, ia);
	}

	public void selectionNbJoueurs() {
		nbJoueur = new JLabel("Choisissez le nombre de joueurs");
		nbJoueur.setFont(CaptainSmall);
		nbJoueur.setHorizontalAlignment(0);

		selecNbJoueur = new JComboBox(new String[] { "2", "3", "4", "5", "6",
				"7", "8" });

		nbIA = new JLabel("Choisissez le nombre d'Ordinateurs");
		nbIA.setFont(CaptainSmall);
		nbIA.setHorizontalAlignment(0);

		selecNbJoueur = new JComboBox(new String[] { "2", "3", "4", "5", "6",
				"7", "8" });

		principalPanel.add(nbJoueur);
		principalPanel.add(selecNbJoueur);
		principalPanel.add(nbIA);
		principalPanel.add(selecNbIA);

		nbJoueur.setBounds(0, 100 - 30, 600, 30);
		selecNbJoueur.setBounds(0, 150 - 30, 600, 30);
		nbIA.setBounds(0, 250 - 30, 600, 30);
		selecNbJoueur.setBounds(0, 300 - 30, 600, 30);
	}

	public void creationJoueurs() {

	}

	public void creationIA() {

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

		if (false) {
			System.exit(0);
		}
	}
}
