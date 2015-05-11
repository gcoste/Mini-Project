import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;

import javax.swing.*;

import java.util.LinkedList;
import java.util.Random;

import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class Jeu extends JFrame {
    Timer timer;
    long temps;
    BufferedImage ArrierePlan;
    Graphics buffer;
    boolean ToucheHaut;
    boolean ToucheBas;
    boolean ToucheGauche;
    boolean ToucheDroite;
    boolean ToucheEspace;
    Rectangle Ecran;
    Tank Tank1;

    LinkedList<Object> Objets;
    int score;
    boolean finjeu;

    Jeu() {
        score = 0;
        finjeu = false;

        setTitle("Space Invader");
        //Taille de l'écran de jeu
        setSize(700, 480);
        //On interdit de changer la taille de la fenêtre
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //On ajoute l'écouteur de clavier qui se réfère à cette classe même
        this.addKeyListener(new Jeu_this_keyAdapter(this));

        temps = 0;
        //Aucune touche n'est appuyée, donc tout est false
        ToucheHaut = false;
        ToucheBas = false;
        ToucheGauche = false;
        ToucheDroite = false;
        ToucheEspace = false;

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

    public static class Bandeau extends JFrame {

        private JPanel cadre;
        private JSlider vitesseInitiale;
        private JSlider angle;
        private JLabel labelvit;
        private JLabel labelangle;
        private JLabel vie;
        private JLabel fuel;

        static final int FPS_MIN = 0;
        static final int FPS_MAX = 10;
        static final int FPS_INIT = 5;

        public Bandeau() {
            this.setTitle("Gestion des paramètres");
            this.cadre = new JPanel();
            cadre.setLayout(new FlowLayout());

            labelvit = new JLabel("Vitesse");
            vitesseInitiale = new JSlider(JSlider.HORIZONTAL, FPS_MIN, FPS_MAX, FPS_INIT);
            labelangle = new JLabel("Angle");
            angle = new JSlider(JSlider.HORIZONTAL, FPS_MIN, FPS_MAX, FPS_INIT);
            vie = new JLabel("Vie = variablevie");
            fuel = new JLabel("Fuel = variablefuel");

            JPanel buttonPane = new JPanel();

            cadre.add(labelvit);
            cadre.add(vitesseInitiale);
            cadre.add(labelangle);
            cadre.add(angle);
            cadre.add(vie);
            cadre.add(fuel);

            this.setContentPane(cadre);
            this.setSize(250, 150);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setVisible(true);

        }

        public static void main(String[] args) {
            Bandeau fenetre = new Bandeau();

        }

    }

    public static void main(String[] args) {
        Jeu Monjeu = new Jeu();
    }

    public void this_keyPressed(KeyEvent e) {
        //code correspond à la touche appuyée, stock un nombre pour une touche
        int code = e.getKeyCode();
        //Suivant la touche appuyée, on prévient jeu que celle-ci est appuyée
        if (code == 37) {
            ToucheGauche = true;
        } else if (code == 39) {
            ToucheDroite = true;
        } else if (code == 38) {
            ToucheHaut = true;
        } else if (code == 40) {
            ToucheBas = true;
        } else if (code == 32) {
            ToucheEspace = true;
        }

        //Si c'est la touche entrée on fait pause
        else if (code == 10) {
            if (timer.isRunning()) {
                timer.stop();
            } else
                timer.start();
        }
        //Si c'est la touche échape on quitte
        else if (code == 27) {
            System.exit(0);
        }
    }

    public void this_keyReleased(KeyEvent e) {
        //code correspond à la touche relachée, stock un nombre pour une touche
        int code = e.getKeyCode();
        if (code == 37) {
            ToucheGauche = false;
        } else if (code == 39) {
            ToucheDroite = false;
        } else if (code == 38) {
            ToucheHaut = false;
        } else if (code == 40) {
            ToucheBas = false;
        } else if (code == 32) {
            ToucheEspace = false;
        }
    }

    //Classe interne à la classe jeu, elle peut modifier les attributs de la classe jeu (ici temps nous intéresse)

    private class TimerAction implements ActionListener {
        // ActionListener appelee toutes les 100 millisecondes comme demandé à l'initialisation du timer
        public void actionPerformed(ActionEvent e) {
            //Lance boucle_principale_jeu toutes les 100 ms
            boucle_principale_jeu();
            temps++;
        }
    }


    private class Jeu_this_keyAdapter extends KeyAdapter {
        //Jeu contenu dans notre écouteur, quand on créé un objet Jeu_this_keyAdapter il stock l'objet Jeu qu'il doit écouter
        //Ainsi l'objet jeu à un écouteur de clavier et l'écouteur de clavier a le Jeu auquel il se réfèrent (ils s'écoutent mutuellement)
        private Jeu NotreCombinaison;

        Jeu_this_keyAdapter(Jeu adaptee) {
            this.NotreCombinaison = adaptee;
        }

        // Quand on appuie une touche, on créé un KeyEvent, cette méthode est appelée
        //elle demande ensuite au Jeu auquelle elle se réfère de lancer la méthode this_keyPressed(e)
        public void keyPressed(KeyEvent e) {
            NotreCombinaison.this_keyPressed(e);
        }
BOBOBOBOBOBOBVOBOOOOOS
        //Même remarque
        public void keyReleased(KeyEvent e) {
            NotreCombinaison.this_keyReleased(e);
        }

    }
}
