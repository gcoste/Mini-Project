import java.awt.*;

import javax.swing.*;

public class Bandeau extends JPanel {
	public final Font arial = new Font("Arial", Font.BOLD, 20);

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
		this.setPreferredSize(new Dimension(0, 150));
		this.setBackground(bleu);

		bars = new JPanel(new BorderLayout());
		bars.setPreferredSize(new Dimension((int) ((0.25) * largeur), 100));
		bars.setBackground(bleu);

		central = new JPanel(new GridLayout(2, 1));
		central.setPreferredSize(new Dimension((int) ((0.45) * largeur), 100));
		central.setBackground(bleu);

		sliders = new JPanel(new GridLayout(4, 1));
		sliders.setPreferredSize(new Dimension((int) ((0.25) * largeur), 150));
		sliders.setAlignmentY(0);
		sliders.setBackground(bleu);

		this.add(bars);
		this.add(central);
		this.add(sliders);

		vieBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
		vieBar.setPreferredSize(new Dimension((int) ((0.25) * largeur), 40));
		vieBar.setStringPainted(true);
		vieBar.setFont(arial);
		vieBar.setBackground(bleu);
		vieBar.setForeground(new Color(200, 0, 0));

		fuelBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
		fuelBar.setPreferredSize(new Dimension((int) ((0.25) * largeur), 40));
		fuelBar.setStringPainted(true);
		fuelBar.setFont(arial);
		fuelBar.setBackground(bleu);
		fuelBar.setForeground(new Color(28, 142, 62));

		forceLabel = new JLabel();
		forceLabel.setHorizontalAlignment(0);
		forceLabel.setFont(arial);
		forceLabel.setForeground(Color.white);

		forceSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 1);
		forceSlider.setBackground(bleu);
		forceSlider.setFocusable(false);
		forceSlider.setValue(50);
		forceSlider.setForeground(Color.white);

		angleLabel = new JLabel();
		angleLabel.setHorizontalAlignment(0);
		angleLabel.setFont(arial);
		angleLabel.setForeground(Color.white);

		angleSlider = new JSlider(JSlider.HORIZONTAL, 0, 180, 1);
		angleSlider.setBackground(bleu);
		angleSlider.setFocusable(false);
		angleSlider.setForeground(Color.white);
		angleSlider.setPaintTicks(true);

		bars.add(vieBar, BorderLayout.NORTH);
		bars.add(fuelBar, BorderLayout.SOUTH);

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
}
