package com.github.mdsimmo.pistoncraft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class StickyComponents implements Listener {

    private final Plugin plugin;

    public StickyComponents( Plugin plugin ) {
        this.plugin = plugin;
    }

    private List<Block> getBlocksAttachedTo( Block block ) {
        List<Block> attached = new ArrayList<>(4);

        BlockFace[] directions = {
                BlockFace.DOWN, BlockFace.UP,
                BlockFace.NORTH, BlockFace.SOUTH,
                BlockFace.EAST,  BlockFace.WEST
        };
        // move things attached to the blocks
        for ( BlockFace d : directions ) {
            Block b = block.getRelative( d );
            MaterialData data = b.getState().getData();
            if ( data instanceof Attachable ) {
                Attachable attachable = (Attachable) data;
                if ( attachable.getAttachedFace() == d.getOppositeFace() ) {
                    attached.add( b );
                }
            }
        }

        // move things sitting on top
        Block over = block.getRelative( BlockFace.UP );
        if ( over.getPistonMoveReaction() == PistonMoveReaction.BREAK
                || over.getType() == Material.CARPET ) {
            // don't move attachable items that are not attached
            // attahed items will already have been added
            MaterialData data = block.getState().getData();
            if ( !(data instanceof Attachable) ) {
                attached.add( over );
            }
        }

        return attached;
    }

    private void update( Block block ) {
        BlockState original = block.getState();

        // in order for state.update() to update, it needs to change the block,
        // so we change the block just so we can change it back again
        BlockState hackState = block.getState();
        if ( hackState.getType() == Material.AIR )
            hackState.setType( Material.DIRT );
        else
            hackState.setType( Material.AIR );
        hackState.update( true, false );

        // update the block
        original.update( true, true );
    }

    private void shiftBlocks( final List<Block> blocks, final BlockFace direction ) {
        // save what the blocks would drop in case we need it later
        final HashMap<BlockState, Collection<ItemStack>> dropLookup = new HashMap<>( blocks.size());
        for ( Block b : blocks )
            dropLookup.put( b.getState(), b.getDrops() );

        // destroy all blocks
        for ( Block b : blocks )
            b.setType( Material.AIR, false );
        for ( Block b : blocks )
            update( b );

        // make the blocks appear the next tick
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask( plugin, new Runnable() {
            @Override
            public void run() {
                for ( BlockState from : dropLookup.keySet() ) {
                    BlockState to = from.getBlock().getRelative( direction ).getState();
                    if ( to.getType().isSolid() ) {
                        // break the item
                        for ( ItemStack item : dropLookup.get( from ) )
                            to.getWorld().dropItemNaturally( to.getLocation(),item );
                    } else {
                        // destroy the pushed into item
                        for ( ItemStack stack : to.getBlock().getDrops() )
                            to.getWorld().dropItemNaturally( to.getLocation(), stack );

                        // move the item
                        to.setType( from.getType() );
                        to.setData( from.getData() );

                        /*// Buttons will get stuck on - just turn them off
                        if ( to.getData() instanceof Button )
                            ((Button) to.getData()).setPowered( false );*/

                        // don't update yet - that's done below
                        to.update( true, false );
                    }
                }

                // update all blocks together with all neighbours correct
                for ( BlockState from : dropLookup.keySet() ) {
                    update( from.getBlock() );
                }
            }
        }, 3 );
        // delay of three because it's the amount of ticks a piston takes to move
    }

    private void handlePistonMove( List<Block> blocks, BlockFace direction ) {
        List<Block> allBlocks = new ArrayList<>();
        for ( Block b : blocks ) {
            List<Block> attached = getBlocksAttachedTo( b );
            allBlocks.addAll( attached );
        }
        shiftBlocks( allBlocks, direction );
    }

    @EventHandler( priority = EventPriority.HIGH )
    public void onPistonExtendEvent( BlockPistonExtendEvent e ) {
        if ( e.isCancelled() )
            return;
        handlePistonMove( e.getBlocks(), e.getDirection() );
    }

    @EventHandler( priority = EventPriority.HIGH )
    public void onPistonRetractEvent( BlockPistonRetractEvent e ) {
        if ( e.isCancelled() )
            return;
        handlePistonMove( e.getBlocks(), e.getDirection() );
    }

}
