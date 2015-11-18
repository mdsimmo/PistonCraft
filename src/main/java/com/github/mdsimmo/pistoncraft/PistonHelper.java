package com.github.mdsimmo.pistoncraft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PistonBaseMaterial;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class PistonHelper {

    private static final BlockFace[] SURROUNDING = {
            BlockFace.DOWN, BlockFace.UP,
            BlockFace.NORTH, BlockFace.SOUTH,
            BlockFace.EAST,  BlockFace.WEST
    };

    public static boolean isPiston( BlockState block ) {
        return block.getData() instanceof PistonBaseMaterial;
    }

    public static PistonBaseMaterial getData( BlockState piston ) {
        MaterialData data = piston.getData();
        if ( data instanceof PistonBaseMaterial )
            return (PistonBaseMaterial)data;
        else
            throw new RuntimeException( "Not a piston" );
    }

    public static boolean isPistonExtended( BlockState piston ) {
        // isPowered() for pistons seems to mean is extended
        return getData( piston ).isPowered();
    }

    public static Collection<Block> pushBlocks( Block piston ) {
        PistonBaseMaterial data = getData( piston.getState() );
        Block first = piston.getRelative( data.getFacing(), 1 );
        Set<Block> connected = new HashSet<>();
        search( first, connected, data.getFacing() );

        // remove the original piston
        connected.remove( piston );

        return connected;
    }

    /**
     * Gets the blocks that will be moved when the piston is retracted. This method
     * will return junk if the piston is already retracted.
     * @param piston the piston block
     * @return the set of blocks that the piston will retract
     */
    public static Set<Block> retractBlocks( Block piston ) {
        PistonBaseMaterial data = getData( piston.getState() );

        if ( !data.isSticky() )
            return Collections.emptySet();

        Block first = piston.getRelative( data.getFacing(), 2 );
        Set<Block> connected = new HashSet<>();
        search( first, connected, data.getFacing().getOppositeFace() );

        // remove the original piston in case it got added
        connected.remove( piston );

        return connected;
    }

    private static void search( Block target, Set<Block> connected, BlockFace direction ) {
        if ( target.getType() == Material.SLIME_BLOCK ) {
            connected.add( target );
            // add all blocks around sticky blocks
            for ( BlockFace dir : SURROUNDING ) {
                Block b = target.getRelative( dir );
                if ( !connected.contains( b ) )
                    search( b, connected, direction );
            }
        } else if ( target.getType().isSolid() ) {
            connected.add( target );
            // add the block that is blocking the other block
            Block blockedBy = target.getRelative( direction );
            if ( !connected.contains( blockedBy ) )
                search( blockedBy, connected, direction );
        }
    }

    public static void shiftBlocks( final Plugin plugin, final Collection<Block> blocks, final BlockFace direction ) {
        // save the state of all the blocks
        final Collection<BlockState> blockStates = new ArrayList<>();
        for( Block block : blocks )
            blockStates.add( block.getState() );

        // destroy all blocks
        for ( Block b : blocks )
            b.setType( Material.AIR, false );

        // make the blocks appear the next tick
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask( plugin, new Runnable() {
            @Override
            public void run() {
                for ( BlockState from : blockStates ) {
                    Block to = from.getBlock().getRelative( direction );

                    // destroy the pushed into item
                    for ( ItemStack stack : to.getDrops() )
                        to.getWorld().dropItemNaturally( to.getLocation(), stack );

                    // move the item
                    BlockState toState = to.getState();
                    toState.setType( from.getType() );
                    toState.setData( from.getData() );
                    toState.update( true, true );
                }
            }
        }, 3 );
        // delay of three because it's the amount of ticks a piston takes to move
    }

}
