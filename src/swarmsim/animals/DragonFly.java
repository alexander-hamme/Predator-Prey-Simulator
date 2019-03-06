package swarmsim.animals;

import swarmsim.App;
import swarmsim.util.ForceVector;
import swarmsim.util.RandGen;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class DragonFly extends Predator {

    private int other_predator_avoid_thresh = 3;
    private final int direction_variance = 5;  // bound (in degrees) for how much Fly randomly varies its direction while moving

    private static final double movement_speed = 3.5;
    private int prey_swarm_thresh = 5;

    private Prey currently_hunting = null;

    private final int hunting_ticks = 15;
    private int hunting_tick = 1;

    private final int hunting_risk = 2;   // 2 body lengths

    private long time_since_last_seek = 0L;
    private final long seeking_frequency = 5000L;

    private long time_since_last_meal = 0L;
    private final long eating_frequency = 5000L;

    private BufferedImage seeking_image;
    private BufferedImage hunting_image;


    public DragonFly() throws IOException {

        this.x = random_generator.getRandInt(0, App.DISPL_WDTH);
        this.y = random_generator.getRandInt(0, App.DISPL_HGHT);

        this.direction = 0.0;

        this.behaviorState = BehaviorState.HUNTING;

//        this.image = ImageIO.read(new File("images/fly.png"));
        hunting_image = ImageIO.read(new File("images/dragonfly_hunt.png"));
        seeking_image = ImageIO.read(new File("images/dragonfly_seek.png"));

        this.image = hunting_image;

        this.img_width = this.image.getWidth();
        this.img_height = this.image.getHeight();

        // TODO replace this with just moving forward all the time?
        forward_motion = new ForceVector(movement_speed, this.direction);

        this.forces = new ForceVector[]{
                forward_motion, obstacle_avoidance, hunting_behavior, competing_behavior
        };
    }

    private boolean canEat() {
        return System.currentTimeMillis() - time_since_last_meal > eating_frequency;
    }

    private boolean canSeek() {
        return System.currentTimeMillis() - time_since_last_seek > seeking_frequency;
    }

    // force_calculation_nums : {x_component, y_component, this.centerx, this.centery, prey_x, prey_y, dx, dy}
    private void startSeek(final Prey prey) {
        this.hunting_tick = 1;
        this.currently_hunting = prey;
        this.behaviorState = BehaviorState.SEEKING;
    }

    private void seek() {

        int centerx = this.centerX();
        int centery = this.centerY();

        int prey_x = currently_hunting.centerX();
        int prey_y = currently_hunting.centerY();

        double dx = prey_x - centerx;
        double dy = prey_y - centery;

        // threshold for when the prey has been caught
        if (Math.pow(Math.pow(dx, 2) + Math.pow(dy, 2), 0.5) <= img_width/2.0) {

            this.currently_hunting.setEaten(true);
            endSeek(true);

        } else {

            if (++hunting_tick > hunting_ticks) {

                endSeek(false);

            } else {

                double x_component = -0.05 * ((centerx - prey_x) - dx);
                double y_component = -0.05 * ((centery - prey_y) - dy);

                hunting_behavior.setComponents(x_component, y_component);
            }
        }
    }

    private void endSeek(boolean caughtPrey) {
        this.behaviorState = (caughtPrey) ? BehaviorState.EATING : BehaviorState.HUNTING;
        this.currently_hunting = null;
        this.time_since_last_seek = System.currentTimeMillis();
    }


    private void hunt(final List<Prey> prey_animals) {

        this.behaviorState = BehaviorState.HUNTING;

        int centerx = this.centerX();
        int centery = this.centerY();

        double x_component = 0.0;
        double y_component = 0.0;

//        double hunt_force_multiplier = 1.0 / (10 * (prey_animals.size() + 1));
        double hunt_force_multiplier = 1.0 / 1000;

        for (Prey prey : prey_animals) {

            int prey_x = prey.centerX();
            int prey_y = prey.centerY();

            double dx = prey_x - centerx;
            double dy = prey_y - centery;

            // if predator is within a distance threshold
            if (Math.pow(Math.pow(dx, 2) + Math.pow(dy, 2), 0.5) <= img_width * prey_swarm_thresh) {

                // TODO: OR:  if abs(0.01 * ((centerx - prey_x) - dx)) > abs(x_component) ... ;

                // move closer to other animals if you're more than a body's length away
                if (Math.pow(Math.pow(dx, 2) + Math.pow(dy, 2), 0.5) <= img_width * hunting_risk) {

                    if (canSeek()) {
                        startSeek(prey);
                        return;
                    }

                } else {

                    x_component = -hunt_force_multiplier * ((centerx - prey_x) - dx);
                    y_component = -hunt_force_multiplier * ((centery - prey_y) - dy);


                }
            }
        }
        hunting_behavior.setComponents(x_component, y_component);
    }


    private void compete(List<Predator> other_predators) {

        int centerx = this.centerX();
        int centery = this.centerY();

        double x_component = 0.0;
        double y_component = 0.0;

        for (Predator predator : other_predators) {

            if (predator == this) {continue;}

            int predator_x = predator.centerX();
            int predator_y = predator.centerY();

            double dx = predator_x - centerx;
            double dy = predator_y - centery;

            // if predator is within a distance threshold
            if (Math.pow(Math.pow(dx, 2) + Math.pow(dy, 2), 0.5) < img_width * other_predator_avoid_thresh) {

                // TODO: OR:  if abs(0.01 * ((centerx - predator_x) - dx)) > abs(x_component) ... ;

                x_component = 0.002 * ((centerx - predator_x) - dx);
                y_component = 0.002 * ((centery - predator_y) - dy);
            }
        }

        competing_behavior.setComponents(x_component, y_component);
    }

    @Override
    public void timeStep(List<Prey> prey_animals, List<Predator> other_predators) {

        forward_motion.setMagnitude(movement_speed);   // reset forward motion value at start of each timestep

        forward_motion.incrementTheta(random_generator.getRandInt(-direction_variance, direction_variance));  // add direction variance



        if (this.behaviorState == BehaviorState.SEEKING) {

            seek();

        } else if (this.canEat()){

            hunt(prey_animals);
        }

        avoidWalls();

        compete(other_predators);

        final ForceVector netForce = getNetForce();

        // add direction variance again for realistic wobble in flying
//        netForce.incrementTheta(RandGen.getRandInt(-direction_variance, direction_variance));

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


    @Override
    public BufferedImage getCurrImage() {

        switch(this.behaviorState) {
            case HUNTING: this.image = hunting_image; break;
            case SEEKING: this.image = seeking_image; break;
            case EATING: this.image = hunting_image; break;
        }

        if (transform_op == null) {
            return this.image;
        }
        return transform_op.filter(this.image, null);
    }
}
