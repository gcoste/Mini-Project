import java.awt.*;

import javax.swing.*;

public class Bandeau extends JPanel {
	public JPanel bars;
	public JProgressBar vieBar;
	public JProgressBar fuelBar;

	public JPanel central;

	public JPanel infos;
	public JLabel nom;
	public JLabel temps;
	public JLabel vent;

	public JPanel bombe;
	public JButton bombeNext;
	public JButton bombePrev;
	public JLabel bombeLabel;

	public JPanel sliders;
	public JLabel forceLabel;
	public JSlider forceSlider;
	public JLabel angleLabel;
	public JSlider angleSlider;

	// largeur est la largeur de l'ecran
	public Bandeau(int largeur, Color bleu) {
		this.setLayout(new FlowLayout());
		this.setPreferredSize(new Dimension(0, 100));
		this.setBackground(bleu);

		bars = new JPanel(new GridLayout(2, 1));
		bars.setPreferredSize(new Dimension((int) ((0.25) * largeur), 100));
		bars.setBackground(bleu);

		central = new JPanel(new GridLayout(2, 1));
		central.setPreferredSize(new Dimension((int) ((0.45) * largeur), 100));
		central.setBackground(bleu);

		sliders = new JPanel();
		sliders.setLayout(new BoxLayout(sliders, BoxLayout.LINE_AXIS));
		sliders.setPreferredSize(new Dimension((int) ((0.25) * largeur), 100));
		sliders.setBackground(bleu);

		this.add(bars, BorderLayout.WEST);
		this.add(central, BorderLayout.CENTER);
		this.add(sliders, BorderLayout.EAST);

		forceSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 1);
		forceSlider.setBackground(bleu);
		forceSlider.setFocusable(false);
		forceSlider.setValue(50);

		forceSlider.setMajorTickSpacing(20);
		forceSlider.setLabelTable(forceSlider.createStandardLabels(20));
		forceSlider.setForeground(Color.white);
		forceSlider.setPaintTicks(true);
		forceSlider.setPaintLabels(true);

		sliders.add(forceSlider);
	}

	public int getForce() {
		return (int) forceSlider.getValue();
	}

	public void setForce(int f) {
		forceSlider.setValue(f);
	}
}
