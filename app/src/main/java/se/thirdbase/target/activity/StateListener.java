package se.thirdbase.target.activity;

/**
 * Created by alexp on 2/12/16.
 */
public interface StateListener {

    void onStartup();

    void onPrecision();

    void onWeapons();

    void onAmmunition();

    void onStatistics();
}
