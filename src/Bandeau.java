import java.awt.*;

import javax.swing.*;

public class Bandeau extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Font Calibri = new Font("Calibri", Font.BOLD, 25);
	public Font Captain;

	public JPanel bars;

	public JProgressBar vieBar;
	public JProgressBar fuelBar;

	public JPanel central;

	public JPanel bombe;
	public JButton bombeNext;
	public JLabel bombeLabel;
	public JButton bombePrev;

	public JPanel infos;
	public JLabel tempsLabel;
	public JLabel nomLabel;
	public JLabel ventLabel;

	public JPanel sliders;

	public JLabel forceLabel;
	public JSlider forceSlider;
	public JLabel angleLabel;
	public JSlider angleSlider;

	// largeur est la largeur de l'ecran
	public Bandeau(int largeur, Color bleu, Font cap, Font capBig) {
		Captain = cap.deriveFont((float) 50);

		this.setLayout(new FlowLayout());
		this.setPreferredSize(new Dimension(0, 150));
		this.setBackground(bleu);

		bars = new JPanel(new BorderLayout());
		bars.setPreferredSize(new Dimension((int) ((0.25) * largeur), 100));
		bars.setBackground(bleu);

		central = new JPanel(new GridLayout(2, 1));
		central.setPreferredSize(new Dimension((int) ((0.45) * largeur), 150));
		central.setBackground(bleu);

		sliders = new JPanel(new GridLayout(4, 1));
		sliders.setPreferredSize(new Dimension((int) ((0.25) * largeur), 150));
		sliders.setAlignmentY(0);
		sliders.setBackground(bleu);

		this.add(bars);
		this.add(central);
		this.add(sliders);

		createBars(largeur, bleu);
		createCentral(largeur, bleu);
		createSliders(largeur, bleu);
	}

	public void createBars(int largeur, Color bleu) {
		vieBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
		vieBar.setPreferredSize(new Dimension((int) ((0.25) * largeur), 40));
		vieBar.setStringPainted(true);
		vieBar.setFont(Calibri);
		vieBar.setBackground(bleu);
		vieBar.setForeground(new Color(200, 0, 0));

		fuelBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
		fuelBar.setPreferredSize(new Dimension((int) ((0.25) * largeur), 40));
		fuelBar.setStringPainted(true);
		fuelBar.setFont(Calibri);
		fuelBar.setBackground(bleu);
		fuelBar.setForeground(new Color(28, 142, 62));

		bars.add(vieBar, BorderLayout.NORTH);
		bars.add(fuelBar, BorderLayout.SOUTH);
	}

	public void createCentral(int largeur, Color bleu) {
		ImageIcon Moins = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				"moins.png"));
		ImageIcon Plus = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				"plus.png"));

		bombe = new JPanel(new FlowLayout());
		bombe.setBackground(bleu);

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

		infos = new JPanel(new FlowLayout());
		infos.setBackground(bleu);

		tempsLabel = new JLabel();
		tempsLabel
				.setPreferredSize(new Dimension((int) ((0.05) * largeur), 60));
		tempsLabel.setHorizontalAlignment(0);
		tempsLabel.setFont(Captain);
		tempsLabel.setForeground(Color.white);

		nomLabel = new JLabel();
		nomLabel.setPreferredSize(new Dimension((int) ((0.25) * largeur), 60));
		nomLabel.setHorizontalAlignment(0);
		nomLabel.setFont(Captain);

		ventLabel = new JLabel();
		ventLabel.setPreferredSize(new Dimension((int) ((0.05) * largeur), 60));
		ventLabel.setHorizontalAlignment(0);
		ventLabel.setFont(Captain);
		ventLabel.setForeground(new Color(153, 217, 234));

		infos.add(tempsLabel);
		infos.add(nomLabel);
		infos.add(ventLabel);

		central.add(bombe);
		central.add(infos);
	}

	public void createSliders(int largeur, Color bleu) {
		forceLabel = new JLabel();
		forceLabel.setHorizontalAlignment(0);
		forceLabel.setFont(Calibri);
		forceLabel.setForeground(Color.white);

		forceSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 1);
		forceSlider.setBackground(bleu);
		forceSlider.setFocusable(false);
		forceSlider.setForeground(Color.white);

		angleLabel = new JLabel();
		angleLabel.setHorizontalAlignment(0);
		angleLabel.setFont(Calibri);
		angleLabel.setForeground(Color.white);

		angleSlider = new JSlider(JSlider.HORIZONTAL, 0, 180, 1);
		angleSlider.setBackground(bleu);
		angleSlider.setFocusable(false);
		angleSlider.setForeground(Color.white);
		angleSlider.setPaintTicks(true);
		angleSlider.setInverted(true);

		sliders.add(forceLabel);
		sliders.add(forceSlider);
		sliders.add(angleLabel);
		sliders.add(angleSlider);
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

	public int getForce() {
		return forceSlider.getValue();
	}

	public void setForce(double f) {
		forceSlider.setValue((int) f);
		forceLabel.setText("Force : " + (int) f);
	}

	public void setForceLabel() {
		forceLabel.setText("Force : " + getForce());
	}

	public int getAngle() {
		return angleSlider.getValue();
	}

	public void setAngle(double a) {
		angleSlider.setValue((int) a);
		angleLabel.setText("Angle : " + (int) a);
	}

	public void setAngleLabel() {
		angleLabel.setText("Angle : " + getAngle());
	}

	protected ImageIcon creerIcone(String NomImage) {
		java.net.URL imgURL = getClass().getResource(NomImage);
		if (imgURL != null) {
			return new ImageIcon(imgURL, NomImage);
		} else {
			System.out.println(NomImage + " introuvable !");
			System.out.println("Mettre les images dans le repertoire source");
			System.exit(0);
			return null;
		}
	}
}
