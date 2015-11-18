package com.github.mdsimmo.pistoncraft;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.material.PistonBaseMaterial;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

public class PistonHandler implements Listener {

    private Plugin plugin;

    public PistonHandler( Plugin plugin ) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPistonExtend( BlockPistonExtendEvent e ) {
        plugin.getLogger().info( "piston retract event" );
        //e.setCancelled( true );
    }

    @EventHandler
    public void onPistonRetract( BlockPistonRetractEvent e ) {
        plugin.getLogger().info( "piston retract event" );
        //e.setCancelled( true );
    }

    @EventHandler( priority = EventPriority.LOWEST)
    public void onPistonPowered( BlockPhysicsEvent e ) {
        if ( e.isCancelled() )
            return;
        Block b = e.getBlock();
        if ( !PistonHelper.isPiston( b.getState() ) )
            return;

        System.out.println( "piston update" );

        if ( b.isBlockIndirectlyPowered() ) {
            extendPiston( b );
        } else {
            retractPiston( b );
        }
    }

    private void extendPiston( Block piston ) {
        if ( PistonHelper.isPistonExtended( piston.getState() ) )
            return;
        System.out.println( "piston extending" );
        Collection<Block> blocks = PistonHelper.pushBlocks( piston );
        PistonBaseMaterial data = PistonHelper.getData( piston.getState() );
        PistonHelper.shiftBlocks( plugin, blocks, data.getFacing() );
    }

    private void retractPiston( Block piston ) {
        if ( !PistonHelper.isPistonExtended( piston.getState() ) )
            return;
        System.out.println( "piston retracting" );
        Collection<Block> blocks = PistonHelper.retractBlocks( piston );
        PistonBaseMaterial data = PistonHelper.getData( piston.getState() );
        PistonHelper.shiftBlocks( plugin, blocks, data.getFacing().getOppositeFace() );

    }

}