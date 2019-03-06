package swarmsim.util;


// TODO:  use Radians for everything so you don't have to do any conversions!

public class ForceVector {

    private final int max_angle = 359;

    private double magnitude;
    private double theta;  // in degrees

    public ForceVector(double _magnitude, double _theta) {
        this.magnitude = _magnitude;
        this.theta = _theta;
    }

    public double getMagnitude() {
        return this.magnitude;
    }

    public void setMagnitude(final double mag) {
        this.magnitude = mag;
    }

    public void setComponents(final double x_component, final double y_component) {
        setMagnitude(Math.pow(
                Math.pow(x_component, 2) + Math.pow(y_component, 2), 0.5
        ));

        double theta = Math.toDegrees(Math.atan2(y_component, x_component));

        theta = (theta < 0) ? theta + max_angle : theta;

        setTheta(theta);
    }

    public double getTheta() {
        return this.theta;
    }

    public void incrementTheta(final double dtheta) {

        if (dtheta < 0) {

            theta = (theta + dtheta < 0) ?  // check if abs val dtheta > curr direction value
                    max_angle + theta + dtheta : theta + dtheta;
        } else {

            theta = (theta + dtheta > max_angle) ?
                    theta + dtheta - max_angle : theta + dtheta;
        }
    }

    public void setTheta(final double t) {
        this.theta = t;
    }

    public double getX_component() {
        return this.magnitude * Math.cos(Math.toRadians(this.theta));
    }

    // Y component values don't have their sign flipped here

    public double getY_component() {
        // don't flip sign of y axis here
        return this.magnitude * Math.sin(Math.toRadians(this.theta));
    }

    public String toString() {
        return String.format("Force Vector, %.5f at %.5f degrees", magnitude, theta);
    }
}

