package se.thirdbase.target.model.precision;

import se.thirdbase.target.model.BulletHole;

/**
 * Created by alexp on 2/23/16.
 */
public final class PrecisionTarget {
    public static final int MAX_NBR_BULLETS = 5;
    public static final int MAX_SCORE = 10;
    public static final float RADIUS_INCREMENT = 2.5f;

    public static int getBulletScore(BulletHole hole) {
        float diameter = hole.getCaliber().getDiameter();
        float radius = Math.abs(hole.getRadius() - diameter / 2);

        return (int)Math.ceil(PrecisionTarget.MAX_SCORE - radius / PrecisionTarget.RADIUS_INCREMENT);
    }
}
