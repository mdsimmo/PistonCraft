package com.github.mdsimmo.pistoncraft;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Config {

    public static boolean moveLiquid = false;

    static void load( Plugin plugin ) {
        FileConfiguration config = plugin.getConfig();
        if ( config.contains( "move-liquid" ) )
            moveLiquid = config.getBoolean( "move-liquid" );
    }

}
