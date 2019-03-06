package swarmsim.animals;

import swarmsim.App;
import swarmsim.util.ForceVector;
import swarmsim.util.RandGen;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public abstract class Animal {


    protected static final int WINDOW_BOUNDS_X = App.DISPL_WDTH;
    protected static final int WINDOW_BOUNDS_Y = App.DISPL_HGHT;
    protected static final int max_angle = 359;

    protected int x, y;
    protected double direction;

    protected BufferedImage image;
    protected int img_width, img_height;

    protected AffineTransform transformer = new AffineTransform();
    protected AffineTransformOp transform_op = null;

    protected ForceVector obstacle_avoidance = new ForceVector(0, 0);
    protected ForceVector forward_motion = new ForceVector(0, 0);

    protected ForceVector[] forces;

    protected RandGen random_generator = new RandGen();

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int centerX() {
        return (int) Math.round(this.x + img_width / 2.0);
    }

    public int centerY() {
        return (int) Math.round(this.y + img_height / 2.0);
    }

    public ForceVector[] getForces() {
        return this.forces;
    }

    // each class has a specific implementation of this method
    protected abstract void updatePosition();


    protected void incrementDirection(double dtheta) {

        if (dtheta < 0) {

            direction = (direction + dtheta < 0) ?  // check if abs val dtheta > curr direction value
                    max_angle + direction + dtheta : direction + dtheta;
        } else {

            direction = (direction + dtheta > max_angle) ?
                    direction + dtheta - max_angle : direction + dtheta;
        }
    }

    protected void avoidWalls() {

        double prox_x = img_width / 2.0;
        double prox_y = img_height / 2.0;

        int centerx = this.x + (int) Math.round(prox_x);
        int centery = this.y + (int) Math.round(prox_y);

        int x_multiplier = (centerx < prox_x) ? 1 : (centerx > WINDOW_BOUNDS_X - prox_x) ? -1 : 0;
        int y_multiplier = (centery < prox_y) ? 1 : (centery > WINDOW_BOUNDS_Y - prox_y) ? -1 : 0;


        if (x_multiplier == 0 && y_multiplier == 0) {
            obstacle_avoidance.setMagnitude(0.0);
            return;
        }

        // x component of Force increases as Gnat gets closer to obstacle
        double x_component = (x_multiplier == 1) ? prox_x - centerx : (x_multiplier == -1) ?
                (prox_x - (WINDOW_BOUNDS_X - centerx))
                : 0.0;

        // same with y component
        double y_component = (y_multiplier == 1) ? prox_y - centery : (y_multiplier == -1) ?
                (prox_y - (WINDOW_BOUNDS_Y - centery))
                : 0.0;

        x_component *= x_multiplier;
        y_component *= y_multiplier;

        // this sets the new theta value too.
        obstacle_avoidance.setComponents(x_component, y_component);
    }



    protected ForceVector getNetForce() {

        //double mySample = r.nextGaussian() * desiredStdDev + desiredMean;

        double net_force_x = 0.0;
        double net_force_y = 0.0;

        for (ForceVector f : this.forces) {
            net_force_x += f.getX_component();
            net_force_y += f.getY_component();
        }

        double theta = Math.toDegrees(Math.atan2(net_force_y, net_force_x));

        theta = (theta < 0) ? theta + max_angle : theta;

        // 0 degrees is up, so this must be factored into trig functions by subtracting theta from pi/2
        return new ForceVector(
                Math.pow(Math.pow(net_force_x, 2) + Math.pow(net_force_y, 2), 0.5),
                theta
        );
    }

    /**
     * @param dtheta degrees
     */
    protected void createImgTransformOp(double dtheta) {
        transformer.rotate(Math.toRadians(dtheta), img_width / 2.0, img_height / 2.0);
        transform_op = new AffineTransformOp(transformer, AffineTransformOp.TYPE_BICUBIC);
    }



    // TODO:  keep a reference stored of current Rotated image to avoid rotating the original image every single time
    // todo   this function is called!!!
    public BufferedImage getCurrImage() {

        if (transform_op == null) {
            return this.image;
        }
        return transform_op.filter(this.image, null);
    }

}
