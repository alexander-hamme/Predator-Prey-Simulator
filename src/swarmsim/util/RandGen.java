package swarmsim.util;

import java.util.Random;

public class RandGen {

//    static Random generator = new Random(12345L);
    public Random generator = new Random();

    /**
     * Rand int between min and max, both inclusive
     * Note that this works with negative bounds too.
     * @param min
     * @param max
     * @return
     */
    public int getRandInt(int min, int max) {
        return generator.nextInt((max-min) + 1) + min;
    }
}
