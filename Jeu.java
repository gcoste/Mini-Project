import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;

import javax.swing.*;

import java.util.LinkedList;


public class Jeu extends JFrame {
    Timer timer;
    long temps;
    BufferedImage ArrierePlan;
    Graphics buffer;
    boolean ToucheHaut;
    boolean ToucheBas;
    boolean ToucheGauche;
    boolean ToucheDroit;
    boolean ToucheEspace;
    Rectangle Ecran;
    tanks Tank1;

    LinkedList<Objet> Objets;
    int score;
    boolean finjeu;
    int nombreAliensVivants;
    int nombreViesRestantes;

    Jeu() {
        setSize(700, 480);
        setResizable(false);
        setVisible(true);

        Ecran =
            new Rectangle(getInsets().left, getInsets().top, getSize().width - getInsets().right - getInsets().left,
                          getSize().height - getInsets().bottom - getInsets().top);

        ArrierePlan = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_RGB);
        buffer = ArrierePlan.getGraphics();

        timer = new Timer(100, new TimerAction());
        timer.start();
    }

    public void paint(Graphics g) {
        // remplir le buffer de noir
        buffer.setColor(Color.black);
        buffer.fillRect(Ecran.x, Ecran.y, Ecran.x + Ecran.width, Ecran.y + Ecran.height);
        // on dessine l'image de l'objet en mouvement dans le buffer
        tanks.draw(temps, buffer);
        // on dessine l'image associée au buffer dans le JFrame
        g.drawImage(ArrierePlan, 0, 0, this);
    }

    void boucle_principale_jeu() {

    }

    private class TimerAction implements ActionListener {
        // ActionListener appelee toutes les 100 millisecondes
        public void actionPerformed(ActionEvent e) {
            boucle_principale_jeu();
            temps++;
        }
    }

    public static void main(String[] args) {
        Jeu Monjeu = new Jeu();
    }
}
