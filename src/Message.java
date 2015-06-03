import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Message {
	private Graphics buffer;
	private Rectangle limitesFrame;
	private Font Captain;

	private Color couleur;
	// duree du message en seconde
	private int duree;
	private long temps;
	private String ligne1, ligne2;

	public boolean isDrawn;

	public Message(Graphics b, Rectangle aframe, Font Cap) {
		buffer = b;
		limitesFrame = aframe;
		Captain = Cap;

		isDrawn = false;

		setMessage(0, null, 0, null, null);
	}

	public void setMessage(long t, Color col, int dur, String l1, String l2) {
		temps = t;
		couleur = col;
		duree = dur * 100;
		ligne1 = l1;
		ligne2 = l2;

		isDrawn = true;
	}

	public void drawMessage(long atemps) {
		buffer.setColor(couleur);

		if (atemps - duree < temps && ligne1 != null && isDrawn) {
			if (ligne2 == null) {
				buffer.drawString(ligne1,
						limitesFrame.width
								/ 2
								- (int) buffer.getFontMetrics()
										.getStringBounds(ligne1, buffer)
										.getWidth() / 2, 160);
			} else {
				buffer.drawString(ligne1,
						limitesFrame.width
								/ 2
								- (int) buffer.getFontMetrics()
										.getStringBounds(ligne1, buffer)
										.getWidth() / 2, 130);
				buffer.drawString(ligne2,
						limitesFrame.width
								/ 2
								- (int) buffer.getFontMetrics()
										.getStringBounds(ligne2, buffer)
										.getWidth() / 2, 190);
			}
		} else {
			isDrawn = false;
		}
	}
}
