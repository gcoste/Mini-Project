import java.awt.*;

import javax.swing.*;

public class Bandeau extends JPanel {
	private Font Calibri = new Font("Calibri", Font.BOLD, 25);
	private Font Captain;

	private JPanel bars;
	private JProgressBar vieBar;
	private JProgressBar fuelBar;

	private JPanel infos;
	private JLabel tempsLabel;
	private JLabel nomLabel;
	private JLabel ventLabel;

	private JPanel bombe;
	private JLabel bombeLabel;

	public JButton bombeNext;
	public JButton bombePrev;

	// largeur est la largeur de l'ecran
	public Bandeau(int largeur, Color bleu, Font cap, Font capBig) {
		Captain = cap.deriveFont((float) 50);

		this.setLayout(new FlowLayout());
		this.setPreferredSize(new Dimension(0, 100));
		this.setBackground(bleu);

		bars = new JPanel(new FlowLayout());
		bars.setPreferredSize(new Dimension((int) ((0.25) * largeur), 90));
		bars.setBackground(bleu);

		infos = new JPanel(new FlowLayout());
		infos.setPreferredSize(new Dimension((int) ((0.45) * largeur), 100));
		infos.setBackground(bleu);

		bombe = new JPanel(new FlowLayout());
		bombe.setPreferredSize(new Dimension((int) ((0.25) * largeur), 100));
		bombe.setBackground(bleu);

		this.add(bars);
		this.add(infos);
		this.add(bombe);

		createBars(largeur, bleu);
		createInfos(largeur, bleu);
		createBombe(largeur, bleu);
	}

	private void createBars(int largeur, Color bleu) {
		vieBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
		vieBar.setPreferredSize(new Dimension((int) ((0.25) * largeur), 30));
		vieBar.setBackground(bleu);
		vieBar.setForeground(new Color(28, 142, 62));

		fuelBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
		fuelBar.setPreferredSize(new Dimension((int) ((0.25) * largeur), 30));
		fuelBar.setBackground(bleu);
		fuelBar.setForeground(new Color(128, 0, 128));

		bars.add(vieBar, BorderLayout.NORTH);
		bars.add(fuelBar, BorderLayout.SOUTH);
	}

	private void createInfos(int largeur, Color bleu) {
		tempsLabel = new JLabel();
		tempsLabel
				.setPreferredSize(new Dimension((int) ((0.05) * largeur), 80));
		tempsLabel.setHorizontalAlignment(0);
		tempsLabel.setFont(Captain);
		tempsLabel.setForeground(Color.white);

		nomLabel = new JLabel();
		nomLabel.setPreferredSize(new Dimension((int) ((0.25) * largeur), 80));
		nomLabel.setHorizontalAlignment(0);
		nomLabel.setFont(Captain);

		ventLabel = new JLabel();
		ventLabel.setPreferredSize(new Dimension((int) ((0.05) * largeur), 80));
		ventLabel.setHorizontalAlignment(0);
		ventLabel.setFont(Captain);
		ventLabel.setForeground(new Color(153, 217, 234));

		infos.add(tempsLabel);
		infos.add(nomLabel);
		infos.add(ventLabel);
	}

	public void createBombe(int largeur, Color bleu) {
		ImageIcon Moins = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				"moins.png"));
		ImageIcon Plus = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				"plus.png"));

		bombePrev = new JButton(Moins);
		bombePrev.setBorder(null);
		bombePrev.setBackground(bleu);
		bombePrev.setFocusable(false);

		bombeLabel = new JLabel();
		bombeLabel.setPreferredSize(new Dimension((int) ((0.2) * largeur), 80));
		bombeLabel.setHorizontalAlignment(0);
		bombeLabel.setFont(Captain);
		bombeLabel.setForeground(Color.white);

		bombeNext = new JButton(Plus);
		bombeNext.setBorder(null);
		bombeNext.setBackground(bleu);
		bombeNext.setFocusable(false);

		bombe.add(bombePrev);
		bombe.add(bombeLabel);
		bombe.add(bombeNext);
	}

	public void setVie(double vie) {
		vieBar.setValue((int) vie);
	}

	public void setFuel(double fuel) {
		fuelBar.setValue((int) fuel);
	}

	public void setBombe(String bombe, int n) {
		if (bombe.equals("gun")) {
			bombe += "  -  INFINI";
		} else {
			bombe += "  -  " + n;
		}

		bombeLabel.setText(bombe);
	}

	public void setTemps(double temps) {
		// changement de la couleur du temps en fonction du temps ecoule
		int r = Math.min(255, (int) (-temps * 510 / 30) + 510);
		int g = Math.min(255, (int) (temps * 510 / 30));

		tempsLabel.setForeground(new Color(r, g, 0));

		tempsLabel.setText("" + (int) temps);
	}

	public void setNom(String nom, Color c) {
		nomLabel.setForeground(c);
		nomLabel.setText(nom);
	}

	public void setVent(double v) {
		String vent = new String("");

		for (int i = 0; i < (int) 300 * (Math.abs(v)); i++) {
			if (v > 0.001) {
				vent = vent + ">";
			} else if (v < -0.001) {
				vent = vent + "<";
			} else {
				vent = "-";
			}
		}

		ventLabel.setText(vent);
	}

	public void draw() {
		super.repaint();
	}
}
