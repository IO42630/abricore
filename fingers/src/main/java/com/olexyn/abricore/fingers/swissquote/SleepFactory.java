package com.olexyn.abricore.fingers.swissquote;

import java.util.Random;

public class SleepFactory {

    private static final Random random = new Random();

    private static int baseLevel1 = 50;
    private static int baseLevel2 = 500;
    private static int baseLevel3 = 5000;

    public static void sleep(int level){
        int base;
        switch (level) {
            case 1:
                base = baseLevel1;
                break;
            case 3:
                base = baseLevel3;
                break;
            default:
                base = baseLevel2;
        }
        int increment = random.nextInt(base * 2);
        try {
            Thread.sleep(base + increment);
        } catch (InterruptedException ignored) {}

    }
}
