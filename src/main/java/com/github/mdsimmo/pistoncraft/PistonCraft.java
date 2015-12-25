package com.github.mdsimmo.pistoncraft;

import org.bukkit.plugin.java.JavaPlugin;

public class PistonCraft extends JavaPlugin {

    @Override
    public void onEnable() {
        super.onEnable();
        Config.load( this );
        //getServer().getPluginManager().registerEvents( new PistonHandler( this ), this );
        //getServer().getPluginManager().registerEvents( new EventTests(), this );
        getServer().getPluginManager().registerEvents( new StickyComponents( this ), this );
    }

}
