package se.thirdbase.target.util;

import java.util.Random;

/**
 * Created by alexp on 3/2/16.
 */
public final class PaletteGenerator {

    private static Random random = new Random();

    public static int[] generate(int base, int size) {
        int[] palette = new int[size];

        random.setSeed(0xdeadbeef);

        for (int i = 0; i < size; i++) {
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);

            r = (r + ((base & 0x0000ff) >> 0)) / 2;
            g = (g + ((base & 0x00ff00) >> 8)) / 2;
            b = (b + ((base & 0xff0000) >> 16)) / 2;

            palette[i] = 0xff000000 | (r << 16) | (g << 8) |b;
        }

        return palette;
    }
}
