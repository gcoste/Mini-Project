import java.awt.event.KeyEvent;

import javax.swing.*;

public class Curseur extends JSlider {
	Jeu MonJeu;
	
	public Curseur(int FPS_MIN, int FPS_MAX, int FPS_INIT) {
		super (JSlider.HORIZONTAL, FPS_MIN, FPS_MAX,
				FPS_INIT);
	}
}
