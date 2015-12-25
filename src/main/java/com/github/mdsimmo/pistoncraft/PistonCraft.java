package com.github.mdsimmo.pistoncraft;

import org.bukkit.plugin.java.JavaPlugin;

public class PistonCraft extends JavaPlugin {

    @Override
    public void onEnable() {
        super.onEnable();
        Config.load( this );

        CommandHandler handler = new CommandHandler( this );
        String[] commands = {
                "pcreload"
        };
        for ( String cmd : commands ) {
            getCommand( cmd ).setExecutor( handler );
            getCommand( cmd ).setTabCompleter( handler );
        }
        //getServer().getPluginManager().registerEvents( new PistonHandler( this ), this );
        //getServer().getPluginManager().registerEvents( new EventTests(), this );
        getServer().getPluginManager().registerEvents( new StickyComponents( this ), this );
    }

}
