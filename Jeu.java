import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;

import javax.swing.*;

import java.util.LinkedList;
import java.util.Random;


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
    Tank Tank1;

    LinkedList<Object> Objets;
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
    
    public void map(Graphics g) {
        int h = getHeight();
        int w = getWidth();
        int n = 20;
        
        g.setColor(Color.blue);
        g.fillRect(0, 0, w, h);
        
        g.setColor(Color.green);
        int horizon1 = 2 * h / 3;
        
        for (int i = 0; i < w / n; i++) {
            Random rand = new Random();
            int randomNum = rand.nextInt(10) - 5;
            
            int horizon2 = horizon1 + randomNum;
            int[] xpoints = new int[] { n * i, n * i, n * (i + 1), n * (i + 1) };
            int[] ypoints = new int[] { h, horizon1, horizon2, h };
            g.fillPolygon(xpoints, ypoints, 4);
            
            horizon1 = horizon2;
        }
    }

    public void paint(Graphics g) {
        // remplir le buffer de noir
        buffer.setColor(Color.black);
        buffer.fillRect(Ecran.x, Ecran.y, Ecran.x + Ecran.width, Ecran.y + Ecran.height);
        // on dessine l'image de l'objet en mouvement dans le buffer
        Tank.draw(temps, buffer);
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
