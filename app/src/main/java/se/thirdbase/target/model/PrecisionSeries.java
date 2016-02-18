package se.thirdbase.target.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import se.thirdbase.target.db.BulletHoleContract;
import se.thirdbase.target.db.PrecisionContract;
import se.thirdbase.target.db.PrecisionDBHelper;
import se.thirdbase.target.db.PrecisionSeriesContract;

/**
 * Created by alexp on 2/17/16.
 */
public class PrecisionSeries {

    private static final float RING_RADIUS_INCREMENT = 2.5f;

    private List<BulletHole> mBulletHoles;
    private int mScore;

    public PrecisionSeries() {
        mBulletHoles = new ArrayList<>();
    }

    public PrecisionSeries(List<BulletHole> bulletHoles) {
        mBulletHoles = bulletHoles;
        mScore = calculateScore(bulletHoles);
    }

    public void addBulletHole(BulletHole bulletHole) {
        mBulletHoles.add(bulletHole);
        mScore += calculateScore(bulletHole);
    }

    public void setBulletHoles(List<BulletHole> bulletHoles) {
        mBulletHoles = bulletHoles;
        mScore = calculateScore(bulletHoles);
    }

    public List<BulletHole> getBulletHoles() {
        return mBulletHoles;
    }

    private int calculateScore(List<BulletHole> bulletHoles) {
        int score = 0;

        for (BulletHole hole : bulletHoles) {
            score += calculateScore(hole);
        }

        return score;
    }

    private int calculateScore(BulletHole hole) {
        float diameter = hole.getCaliber().getDiameter();
        float radius = Math.abs(hole.getRadius() - diameter / 2);

        return (int) Math.ceil(10 - radius / RING_RADIUS_INCREMENT);
    }

    public int getScore() {
        return mScore;
    }
}

