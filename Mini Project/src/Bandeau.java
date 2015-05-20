import java.awt.*;

import javax.swing.*;

public class Bandeau extends JPanel {
	public JSlider vitesseInitiale;
	public JSlider angle;
	public JLabel labelvit;
	public JLabel labelangle;
	public JSlider forceSlider;
	public JLabel forceCanon;
	public JLabel vie;
	public JLabel fuel;

	public Bandeau() {
		this.setLayout(new FlowLayout());
		this.setPreferredSize(new Dimension(0, 100));
		this.setBackground(Color.black);

		forceCanon = new JLabel("Force");

		forceSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 1);
		forceSlider.setBackground(Color.white);
		forceSlider.setFocusable(false);
		forceSlider.setValue(50);

		forceSlider.setMajorTickSpacing(20);
		forceSlider.setLabelTable(forceSlider.createStandardLabels(20));
		forceSlider.setPaintTicks(true);
		forceSlider.setPaintLabels(true);

		// prend la vie du tank actif
		vie = new JLabel();
		// pareil avec fuel
		fuel = new JLabel();

		this.add(forceCanon);
		this.add(forceSlider);
		this.add(vie);
		this.add(fuel);
	}

	public int getForce() {
		return (int) forceSlider.getValue();
	}
}
