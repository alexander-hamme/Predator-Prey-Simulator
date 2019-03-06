package swarmsim.util;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;


/**
 * TODO: add support for key combinations, EG Forward + Direction should Turn and Walk at the same time
 * <p>
 * TODO:   This can be accomplished by keeping track of which keys are currently pressed
 * <p>
 * TODO:   so instead of returning a single PreyMovement, this should return a Set<PreyMovement>
 */

public class SimKeyAdapter extends KeyAdapter {

    private final int JUMP_KEY = KeyEvent.VK_SPACE;
    private final int TONGUE_KEY = KeyEvent.VK_SHIFT;


    // WALKING CURRENTLY DISABLED  -->  Remove altogether?
    private final int WALK_KEY = -1; //KeyEvent.VK_UP;

    private final int TURN_UP_KEY = KeyEvent.VK_UP;
    private final int TURN_DOWN_KEY = KeyEvent.VK_DOWN;   // todo get rid of this?

    private final int TURN_LEFT_KEY = KeyEvent.VK_LEFT;
    private final int TURN_RIGHT_KEY = KeyEvent.VK_RIGHT;

    private PreyMovement movement = PreyMovement.NONE;

    private final HashSet<PreyMovement> movement_inputs = new HashSet<>();
    private HashSet<PreyMovement> to_return = movement_inputs;

    @Override
    public void keyReleased(KeyEvent e) {

        int key = e.getKeyCode();

        switch (key) {

            // release Jump key to jump
            case JUMP_KEY:
                setMovement(PreyMovement.JUMP);
//                movement_inputs.remove(PreyMovement.JUMP_HOLD);
                movement_inputs.clear();
                movement_inputs.add(PreyMovement.JUMP);
                break; // todo get rid of break?

            // release Tongue key to shoot tongue
            case TONGUE_KEY:
                setMovement(PreyMovement.TONGUE);
//                movement_inputs.remove(PreyMovement.TONGUE_HOLD);
                movement_inputs.clear();
                movement_inputs.add(PreyMovement.TONGUE);
                break;


            case WALK_KEY:
                setMovement(PreyMovement.NONE);
                movement_inputs.remove(PreyMovement.WALK);
                break;

            case TURN_UP_KEY:
                setMovement(PreyMovement.TURN_UP);
                movement_inputs.remove(PreyMovement.TURN_UP);
                break;


            case TURN_DOWN_KEY:
                setMovement(PreyMovement.TURN_DOWN);
                movement_inputs.remove(PreyMovement.TURN_DOWN);
                break;

            case TURN_LEFT_KEY:
                setMovement(PreyMovement.NONE);
                movement_inputs.remove(PreyMovement.TURN_LEFT);
                break;

            case TURN_RIGHT_KEY:
                setMovement(PreyMovement.NONE);
                movement_inputs.remove(PreyMovement.TURN_RIGHT);
                break;
        }
    }


    @Override
    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();

        switch (key) {

            case WALK_KEY:
                movement_inputs.add(PreyMovement.WALK);
                setMovement(PreyMovement.WALK);
                break;

            case TURN_UP_KEY:
                movement_inputs.add(PreyMovement.TURN_UP);
                setMovement(PreyMovement.TURN_UP);
                break;

            case TURN_DOWN_KEY:
                movement_inputs.add(PreyMovement.TURN_DOWN);
                setMovement(PreyMovement.TURN_DOWN);
                break;

            case TURN_LEFT_KEY:
                movement_inputs.add(PreyMovement.TURN_LEFT);
                setMovement(PreyMovement.TURN_LEFT);
                break;

            case TURN_RIGHT_KEY:
                movement_inputs.add(PreyMovement.TURN_RIGHT);
                setMovement(PreyMovement.TURN_RIGHT);
                break;

            case JUMP_KEY:
                movement_inputs.add(PreyMovement.JUMP_HOLD);
                // TODO : change this to Store up Jumping power,
                // todo   and then the KeyReleased() for Space actually makes the Frog jump
                setMovement(PreyMovement.JUMP_HOLD);
                break;

            case TONGUE_KEY:
                movement_inputs.add(PreyMovement.TONGUE_HOLD);
                setMovement(PreyMovement.TONGUE_HOLD);
                break;

            /*default: {
                setMovement(PreyMovement.NONE);
                break;
            }*/
        }
    }

    private void setMovement(final PreyMovement d) {
        this.movement = d;
    }

    public PreyMovement getMovement() {

        // releasing the special hold keys above does not reset the movement state to NONE, so that must be done here
        switch (this.movement) {

            case JUMP: {
                this.movement = PreyMovement.NONE;
                return PreyMovement.JUMP;
            }

            case TONGUE: {
                this.movement = PreyMovement.NONE;
                return PreyMovement.TONGUE;
            }

            default: {
                return this.movement;
            }
        }
    }

    public void reset_special_keys() {
        // releasing the special hold keys above adds these values to the set, but they are not removed above, so that must be done here
        this.movement_inputs.remove(PreyMovement.JUMP);
        this.movement_inputs.remove(PreyMovement.TONGUE);
    }

/*    public void reset() {
        this.movement_inputs.clear();
    }*/

    public HashSet<PreyMovement> getMovementInputs() {

        if (this.movement_inputs.isEmpty()) {
            this.movement_inputs.add(PreyMovement.NONE);
        }
        return this.movement_inputs;
    }
}