package se.thirdbase.target.activity;

import se.thirdbase.target.model.Weapon;

/**
 * Created by alexp on 2/29/16.
 */
public interface WeaponsStateListener {

    void onOverview();

    void onAdd();

    void onAdded(Weapon weapon);
}
