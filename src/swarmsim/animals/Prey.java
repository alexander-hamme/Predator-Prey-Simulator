package swarmsim.animals;

import swarmsim.util.ForceVector;
import java.util.List;

/**
 * Parent class, to be extended by individual insects
 */
public abstract class Prey extends Animal {


    protected boolean eaten = false;

    protected ForceVector predator_avoidance = new ForceVector(0, 0);

    protected ForceVector swarm_behavior = new ForceVector(0, 0);

    ForceVector food_attraction = new ForceVector(0, 0);
    ForceVector need_to_land = new ForceVector(0, 0);  // or just make this a timed thing?

    public abstract void timeStep(List<Prey> other_prey_animals, List<Predator> predators);

    public void setEaten(final boolean b) {
        eaten = b;
    }

    public boolean gotEaten() {
        return eaten;
    }

    //    public boolean gotHit(int tongue_tip_x, int tongue_tip_y, int x2, int y2) {
    public boolean gotHit(int tongue_tip_x, int tongue_tip_y) {

        return this.x <= tongue_tip_x && tongue_tip_x <= this.x + img_width
                && this.y <= tongue_tip_y && tongue_tip_y <= this.y + img_height;

    }

    protected abstract void avoidPredators(List<Predator> predators);
}
