package com.github.mdsimmo.pistoncraft;

import org.bukkit.Material;

public class Utils {

    public static boolean isLiquid( Material material ) {
        return material == Material.LAVA
                || material == Material.WATER
                || material == Material.STATIONARY_LAVA
                || material == Material.STATIONARY_WATER;
    }

}
