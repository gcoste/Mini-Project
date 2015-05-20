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

	static final int FPS_MIN = 0;
	static final int FPS_MAX = 10;
	static final int FPS_INIT = 5;

	public Bandeau(boolean isDoubleBuffered) {
		this.setPreferredSize(new Dimension(0, 100));
		this.setLayout(new FlowLayout());
		this.setBackground(Color.white);

		forceCanon = new JLabel("Force");
		forceSlider = new JSlider(JSlider.HORIZONTAL, FPS_MIN, FPS_MAX,
				FPS_INIT);

		// prend la vie du tank actif
		vie = new JLabel();
		// pareil avec fuel
		fuel = new JLabel();

		this.add(forceCanon);
		forceSlider.setBackground(Color.pink);
		this.add(forceSlider);
		forceSlider.setFocusable(false);
		this.add(vie);
		this.add(fuel);

		forceSlider.setMajorTickSpacing(100);
		forceSlider.setMinorTickSpacing(0);
		forceSlider.setPaintTicks(true);
		forceSlider.setPaintLabels(true);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// ici le dessin de tes rectangles
	}

	/*
	 * public void stateChanged(ChangeEvent e) { JSlider source =
	 * (JSlider)e.getSource(); if (!source.getValueIsAdjusting()) { int fps =
	 * (int)source.getValue();
	 * 
	 * } }
	 */

}
