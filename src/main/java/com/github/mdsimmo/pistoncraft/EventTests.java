package com.github.mdsimmo.pistoncraft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PistonBaseMaterial;
import org.bukkit.scheduler.BukkitTask;

public class EventTests implements Listener {

    @EventHandler
    public void onBlockBreak( BlockBreakEvent e ) {
        Bukkit.getLogger().info( "Block break event" );
    }

    @EventHandler
    public void onEntityChangeBlock( EntityChangeBlockEvent e ) {
        Bukkit.getLogger().info( "entity changed block" );
    }

    @EventHandler
    public void onHangingBreakEvent( HangingBreakEvent e ) {
        Bukkit.getLogger().info( "Hanging block broken" );
    }

    @EventHandler
    public void onPistonExtend( BlockPistonExtendEvent e ) {
        Bukkit.getLogger().info( "piston extend event" );
    }

    @EventHandler
    public void onPlayerClick( PlayerInteractEvent e ) {
        /*Block block = e.getClickedBlock();
        if ( block == null )
            return;
        MaterialData data = block.getState().getData();
        if ( data instanceof PistonBaseMaterial ) {
            if ( e.getAction() == Action.LEFT_CLICK_BLOCK ) {
                Collection<Block> pushing = PistonHelper.pushBlocks( block );
                PistonHelper.shiftBlocks( Bukkit.getPluginManager().getPlugin( "PistonCraft" ),
                        pushing, ((PistonBaseMaterial) data).getFacing() );
            } else {
                Collection<Block> pushing = PistonHelper.retractBlocks( block );
                PistonHelper.shiftBlocks( Bukkit.getPluginManager().getPlugin( "PistonCraft" ),
                        pushing, ((PistonBaseMaterial) data).getFacing().getOppositeFace() );
            }
        }*/
        if ( e.getClickedBlock() == null )
            return;

        for( BukkitTask task : Bukkit.getScheduler().getPendingTasks() ) {
            System.out.println( task );
        }

        MaterialData data = e.getClickedBlock().getState().getData();
        if ( data instanceof PistonBaseMaterial ) {
            Bukkit.getLogger().info( "data:  " + ((PistonBaseMaterial) data).isPowered() );
            Bukkit.getLogger().info( "block: " + e.getClickedBlock().isBlockPowered() );
            Bukkit.getLogger().info( "indir: " + e.getClickedBlock().isBlockIndirectlyPowered() );
        }
    }

    @EventHandler
    public void onPhysicsEvent( BlockPhysicsEvent e ) {
        if ( e.getBlock().getType() == Material.PISTON_BASE ) {
            if ( e.getBlock().isBlockPowered() ) {
                Bukkit.getLogger().info( "piston powered" );
            } else {
                Bukkit.getLogger().info( "piston not powered" );
            }
        }
    }

    @EventHandler
    public void onRedstone( BlockRedstoneEvent e ) {
        //Bukkit.getLogger().info( e.getBlock().getType().toString() );
    }
}
