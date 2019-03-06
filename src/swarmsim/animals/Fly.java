package swarmsim.animals;

import swarmsim.App;
import swarmsim.util.ForceVector;
import swarmsim.util.RandGen;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Fly extends Prey {

    protected final int predator_avoid_thresh = 5;   // body lengths
    protected final int prey_swarm_thresh = 5;   // body lengths

    private static final double movement_speed = 5.0;

    // simulate quasi-brownian forward motion with a force vector from behind that has a theta with randomized variance.
    // This propels the object in a generally forward direction, but with somewhat realistic flying patterns.
    private final int direction_variance = 10;  // bound (in degrees) for how much Fly randomly varies its direction while moving

    public Fly() throws IOException {

        this.x = random_generator.getRandInt(0, App.DISPL_WDTH);
        this.y = random_generator.getRandInt(0, App.DISPL_HGHT);

        this.direction = 0.0;

//        this.image = ImageIO.read(new File("images/fly.png"));
        this.image = ImageIO.read(new File("images/gnat.png"));

        this.img_width = this.image.getWidth();
        this.img_height = this.image.getHeight();

        // TODO replace this with just moving forward all the time?
        forward_motion = new ForceVector(movement_speed, this.direction);

        this.forces = new ForceVector[]{
                forward_motion, obstacle_avoidance, predator_avoidance, swarm_behavior
        };
    }


    private void swarmTogether(final List<Prey> other_prey_animals) {

        int centerx = this.centerX();
        int centery = this.centerY();

        double x_component = 0.0;
        double y_component = 0.0;

        double swarm_force_multiplier = 1.0 / (10 * (other_prey_animals.size() + 1));

        for (Prey prey : other_prey_animals) {

            if (prey == this) {
                continue;
            }

            int prey_x = prey.centerX();
            int prey_y = prey.centerY();

            double dx = prey_x - centerx;
            double dy = prey_y - centery;

            // if predator is within a distance threshold
            if (Math.pow(Math.pow(dx, 2) + Math.pow(dy, 2), 0.5) <= img_width * prey_swarm_thresh) {

                // TODO: OR:  if abs(0.01 * ((centerx - prey_x) - dx)) > abs(x_component) ... ;

                // move closer to other animals if you're more than a body's length away
                if (Math.pow(Math.pow(dx, 2) + Math.pow(dy, 2), 0.5) > img_width) {

                    x_component = -swarm_force_multiplier * ((centerx - prey_x) - dx);
                    y_component = -swarm_force_multiplier * ((centery - prey_y) - dy);
//                    x_component = -0.005 * ((centerx - prey_x) - dx);
//                    y_component = -0.005 * ((centery - prey_y) - dy);

                } else {

                    // don't overlap bodies with other animals in your swarm, keep your own space
                    x_component += 0.1 * swarm_force_multiplier * ((centerx - prey_x) - dx);
                    y_component += 0.1 * swarm_force_multiplier * ((centery - prey_y) - dy);
                }
            }
        }
        swarm_behavior.setComponents(x_component, y_component);
    }

    protected void avoidPredators(List<Predator> predators) {

        int centerx = this.centerX();
        int centery = this.centerY();

        double x_component = 0.0;
        double y_component = 0.0;

        for (Predator predator : predators) {

            int predator_x = predator.centerX();
            int predator_y = predator.centerY();

            double dx = predator_x - centerx;
            double dy = predator_y - centery;

            // if predator is within a distance threshold
            if (Math.pow(Math.pow(dx, 2) + Math.pow(dy, 2), 0.5) < img_width * predator_avoid_thresh) {

                // TODO: OR:  if abs(0.01 * ((centerx - predator_x) - dx)) > abs(x_component) ... ;

                x_component += 0.01 * ((centerx - predator_x) - dx);
                y_component += 0.01 * ((centery - predator_y) - dy);
            }
        }

        predator_avoidance.setComponents(x_component, y_component);
    }

    @Override
    public void timeStep(List<Prey> other_prey_animals, List<Predator> predators) {

        forward_motion.setMagnitude(movement_speed);   // reset forward motion value at start of each timestep

        forward_motion.incrementTheta(random_generator.getRandInt(-direction_variance, direction_variance));  // add direction variance

        avoidWalls();

        swarmTogether(other_prey_animals);

        avoidPredators(predators);

//        System.out.println(forward_motion.toString());

        final ForceVector netForce = getNetForce();

        // add direction variance again for realistic wobble in flying
        netForce.incrementTheta(random_generator.getRandInt(-direction_variance, direction_variance));

        double f_theta = netForce.getTheta();

        createImgTransformOp(f_theta - this.direction);

        this.direction = f_theta;

        forward_motion.setComponents(netForce.getX_component(), netForce.getY_component());

        updatePosition();
    }

    @Override
    protected void updatePosition() {
        this.x += forward_motion.getMagnitude() * Math.cos(Math.toRadians(forward_motion.getTheta()));
        this.y += forward_motion.getMagnitude() * Math.sin(Math.toRadians(forward_motion.getTheta()));
    }
}
