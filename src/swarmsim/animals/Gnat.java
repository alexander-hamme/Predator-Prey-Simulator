package swarmsim.animals;


import swarmsim.App;
import swarmsim.util.ForceVector;
import swarmsim.util.RandGen;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Gnat extends Fly {

//    private final int predator_avoid_thresh = 4;   // body lengths

    private static final double movement_speed = 5.0;


    private final int direction_variance = 10;  // bound (in degrees) for how much Gnat randomly varies its direction while moving


    // midges swarm together

    public Gnat() throws IOException {

        this.x = random_generator.getRandInt(0, App.DISPL_WDTH);
        this.y = random_generator.getRandInt(0, App.DISPL_HGHT);

        this.direction = 0.0;

        this.image = ImageIO.read(new File("images/gnat.png"));

        this.img_width = this.image.getWidth();
        this.img_height = this.image.getHeight();

        // TODO replace this with just moving forward all the time?
        forward_motion = new ForceVector(movement_speed, this.direction);

        this.forces = new ForceVector[]{
                forward_motion, obstacle_avoidance, predator_avoidance,   //TODO  swarm_behavior
        };

//        this.forces = new ForceVector[]{swarm_behavior, };
    }
}


