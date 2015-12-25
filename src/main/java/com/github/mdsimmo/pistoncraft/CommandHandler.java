package com.github.mdsimmo.pistoncraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private final Plugin plugin;

    public CommandHandler( Plugin plugin ) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand( CommandSender sender, Command command, String alias, String[] args ) {
        if ( command.getName().equalsIgnoreCase( "pcreload" ) ) {
            Config.load( plugin );
            sender.sendMessage( "PistonCraft config reloaded" );
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete( CommandSender commandSender, Command command, String s, String[] strings ) {
        return Collections.emptyList();
    }
}
