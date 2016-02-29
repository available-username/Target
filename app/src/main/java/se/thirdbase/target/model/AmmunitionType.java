package se.thirdbase.target.model;

/**
 * Created by alexp on 2/26/16.
 */
public enum AmmunitionType {
    ROUND_NOSE("Round nose"),
    SEMI_WAD_CUTTER("Semi wad cutter"),
    JACKED("Jacked"),
    WAD_CUTTER("Wad cutter");

    String mName;

    AmmunitionType(String name) {
        mName = name;
    }

    public String toString() {
        return mName;
    }
}
