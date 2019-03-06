package swarmsim.animals;

import swarmsim.App;
import swarmsim.util.ForceVector;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.List;

public abstract class Predator extends Animal {

    public enum BehaviorState {
        HUNTING, SEEKING, EATING,
    }

    protected BehaviorState behaviorState;

    protected ForceVector hunting_behavior = new ForceVector(0, 0);
    protected ForceVector competing_behavior = new ForceVector(0, 0);  // todo rename --> this makes predators somewhat avoid each other

    public abstract void timeStep(List<Prey> prey_animals, List<Predator> other_predators);

    public BehaviorState getCurrState() {
        return this.behaviorState;
    }

    protected void incrementDirection(double dtheta) {

        if (dtheta < 0) {

            direction = (direction + dtheta < 0) ?  // check if abs val dtheta > curr direction value
                    max_angle + direction + dtheta : direction + dtheta;
        } else {

            direction = (direction + dtheta > max_angle) ?
                    direction + dtheta - max_angle : direction + dtheta;
        }
    }


}
