package swarmsim;

import javax.imageio.ImageIO;
import javax.swing.*;
//import java.awt.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import swarmsim.animals.*;
import swarmsim.util.ForceVector;
import swarmsim.util.SimKeyAdapter;


import javax.swing.Timer;

/**
 * TODO:  make another version of this that is 2D but from the side - frog can climb on the walls and ceiling,
 * todo    but has to turn or jump to shoot tongue
 */


/**
 * This is where all the animation code goes
 */

public class Simulation extends JPanel implements ActionListener {


    private boolean GAME_OVER = false;
    private boolean DRAW_FORCE_VECTORS = false;

    private AffineTransform transformer = new AffineTransform();
    private AffineTransformOp transform_op;
    private SimKeyAdapter keyAdapter = new SimKeyAdapter();
    private Timer timer;

    private java.util.List<Prey> prey_animals;
    private java.util.List<Prey> prey_eaten;
    private java.util.List<Predator> predators;

    private Image background_img;
    private String bg_img_path = "images/background.png";

    private final Font text_font = new Font("TimesRoman", Font.BOLD, 17);
    //                                                         x                        y
    private final int[] tongue_power_text_pos = {App.DISPL_WDTH / 2 - 150, App.DISPL_HGHT - 40};
    private final int[] jump_power_text_pos = {App.DISPL_WDTH / 2 + 50, App.DISPL_HGHT - 40};
    private final int[] tongue_bar_dims = {130, 5};
    private final int[] jump_bar_dims = {110, 5};  // width, height


    private int refresh_delay = 30;  // ms

    public Simulation() throws IOException {
        setup();
        start();
    }

    // choose what prey_animals to create based on current level / difficulty setting?
    private void createAnimals() throws IOException {
        // check level first...

        int num_prey = 60;
        prey_animals = new ArrayList<>();

        while (num_prey-- > 0) {
            prey_animals.add(new Fly());
        }


        int num_predators = 2;
        predators = new ArrayList<>();

        while (num_predators-- > 0) {
            predators.add(new DragonFly());
        }

        prey_eaten = new ArrayList<>();
    }

    private void setup() throws IOException {
        addKeyListener(keyAdapter);
        setBackground(Color.LIGHT_GRAY);
        background_img = ImageIO.read(new File(bg_img_path));
        setFocusable(true);
        setPreferredSize(new Dimension(App.DISPL_WDTH, App.DISPL_HGHT));



        createAnimals();
    }

    private void start() {
        timer = new Timer(refresh_delay, this);
        timer.start();
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        long time = System.currentTimeMillis();

        /*if (player.isEating()) {

            final int[] tongue = player.getTongueLine();

            //todo make this more intelligent? don't loop through all of them
            for (Bug bug : prey_animals) {

//                if (bug instanceof Gnat) {
//                    bug.timeStep(playerx, playery, prey_animals);

                bug.timeStep(playerx, playery);

                // TODO  draw the tongue only up to the *farthest hit bug*
                if (bug.gotHit(tongue[2], tongue[3])) {
                    System.out.println("Got eaten: " + bug.toString());
                    bug.setEaten(true);
                    prey_eaten.add(bug);
                }
            }

            repaint();

            for (Bug bug : prey_eaten) {
                prey_animals.remove(bug);
            }

            return;

        } else {*/

        for (Prey prey : prey_animals) {

            if (prey.gotEaten()) {
                prey_eaten.add(prey);
                continue;
            }

            prey.timeStep(this.prey_animals, this.predators);
        }

        for (Predator predator : predators) {
            predator.timeStep(this.prey_animals, this.predators);
        }

        if (!prey_eaten.isEmpty()) {System.out.println(prey_eaten.size() + " prey eaten");}

        for (Prey prey : prey_eaten) {
            prey_animals.remove(prey);
        }

        repaint();

        prey_eaten.clear();

//        System.out.print("\rLoop latency: " + (System.currentTimeMillis() - time) + " ms");
    }



    @Override
    public void paintComponent(Graphics g) {

        long time = System.currentTimeMillis();

        super.paintComponent(g);

        g.drawImage(background_img, 0, 0, this);

        /*if (player.isEating()) {

            g.setColor(player.tongue_color);

            final int[] tongue = player.getTongueLine();

            g.drawLine(tongue[0], tongue[1], tongue[2], tongue[3]);

            //todo try to do this in a smarter way?   i.e. don't loop through all of them
        }*/


        for (Prey prey : prey_animals) {

            if (prey.gotEaten()) {
            }

            g.drawImage(prey.getCurrImage(), prey.getX(), prey.getY(), this);

            g.setColor(Color.GREEN);

            if (DRAW_FORCE_VECTORS) {
                for (ForceVector f : prey.getForces()) {
                    g.drawLine(prey.centerX(), prey.centerY(),
                            (int) Math.round(prey.centerX() + 20 * f.getX_component()),
                            (int) Math.round(prey.centerY() + 20 * f.getY_component()));
                }
            }
        }

        for (Predator predator : predators) {
            if (DRAW_FORCE_VECTORS) {
                for (ForceVector f : predator.getForces()) {
                    g.drawLine(predator.centerX(), predator.centerY(),
                            (int) Math.round(predator.centerX() + 20 * f.getX_component()),
                            (int) Math.round(predator.centerY() + 20 * f.getY_component()));
                }
            }
            g.drawImage(predator.getCurrImage(), predator.getX(), predator.getY(), this);
        }

        Toolkit.getDefaultToolkit().sync();

//        System.out.print("\rGraphics latency: " + (System.currentTimeMillis() - time) + " ms");

    }
}
