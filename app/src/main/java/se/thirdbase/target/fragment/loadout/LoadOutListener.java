package se.thirdbase.target.fragment.loadout;

import se.thirdbase.target.model.Ammunition;
import se.thirdbase.target.model.Weapon;

/**
 * Created by alexp on 3/1/16.
 */
public interface LoadOutListener {

    void onLoadOut(Weapon weapon, Ammunition ammunition);
}
