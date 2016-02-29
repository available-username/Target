package se.thirdbase.target;

import se.thirdbase.target.model.Ammunition;
import se.thirdbase.target.model.Weapon;

/**
 * Created by alexp on 2/29/16.
 */
public interface AmmunitionStateListener {

    void onOverview();

    void onAdd();

    void onAdded(Ammunition ammunition);
}
