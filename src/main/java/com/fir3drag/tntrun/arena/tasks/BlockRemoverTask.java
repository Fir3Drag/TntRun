package com.fir3drag.tntrun.arena.tasks;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class BlockRemoverTask {
    private final TntRun plugin;
    private Map<Block, Material> rollbackList = new HashMap<>();

    public BlockRemoverTask(TntRun plugin) {
        this.plugin = plugin;
    }

    private void removeBlock(Block under){
        Block secondUnder = under.getRelative(0, -1, 0);

        if (!under.getType().equals(Material.AIR) && !secondUnder.getType().equals(Material.AIR)){
            // add to rollback
            rollbackList.put(under, under.getType());
            rollbackList.put(secondUnder, secondUnder.getType());

            // remove blocks
            under.setType(Material.AIR);
            secondUnder.setType(Material.AIR);
        }
    }

    public void triggerBlockRemove(String arenaName, Location playerLocation){
        if (this.plugin.isShuttingDown){  // prevents bukkit task errors
            return;
        }

        int tntRemoveDelay = this.plugin.defaultValues.getTntRemoveDelay();

        new BukkitRunnable() {
            @Override
            public void run(){
                rollbackList = plugin.rollbackMap.get(arenaName);

                // block variables
                // -x = left, +x = right, north -z, south +z
                Block under = playerLocation.subtract(0, 1, 0).getBlock();
                Block leftUnder = under.getRelative(-1, 0, 0);
                Block leftTopUnder = under.getRelative(-1, 0, -1);
                Block leftBottomUnder = under.getRelative(-1, 0, 1);
                Block rightUnder = under.getRelative(1, 0, 0);
                Block rightTopUnder = under.getRelative(1, 0, -1);
                Block rightBottomUnder = under.getRelative(1, 0, 1);
                Block topUnder = under.getRelative(0, 0, -1);
                Block bottomUnder = under.getRelative(0, 0, 1);

                // Based off facing North

                // checks top left side of the block (remove current block + left block + top + top left)
                if (playerLocation.getX() >= under.getX() && playerLocation.getX() < under.getX() + 0.3 &&
                        playerLocation.getZ() >= under.getZ() && playerLocation.getZ() < under.getZ() + 0.3){
                    removeBlock(leftUnder);
                    removeBlock(topUnder);
                    removeBlock(leftTopUnder);
                }

                // checks top right side of the block (remove current block + right block + top + top right)
                else if (playerLocation.getX() >= under.getX() + 0.7 && playerLocation.getX() < under.getX() + 1 &&
                        playerLocation.getZ() >= under.getZ() && playerLocation.getZ() < under.getZ() + 0.3){
                    removeBlock(rightUnder);
                    removeBlock(topUnder);
                    removeBlock(rightTopUnder);
                }

                // checks bottom left side of the block (remove current block + left block + bottom + bottom left)
                else if (playerLocation.getX() >= under.getX() && playerLocation.getX() < under.getX() + 0.3 &&
                        playerLocation.getZ() >= under.getZ() + 0.7 && playerLocation.getZ() < under.getZ() + 1){
                    removeBlock(leftUnder);
                    removeBlock(bottomUnder);
                    removeBlock(leftBottomUnder);
                }

                // checks bottom right side of the block (remove current block + bottom block + bottom + bottom right)
                else if (playerLocation.getX() >= under.getX() + 0.7 && playerLocation.getX() < under.getX() + 1 &&
                        playerLocation.getZ() >= under.getZ() + 0.7 && playerLocation.getZ() < under.getZ() + 1){
                    removeBlock(rightUnder);
                    removeBlock(bottomUnder);
                    removeBlock(rightBottomUnder);
                }

                // checks left side of the block (remove current block + left block)
                else if (playerLocation.getX() >= under.getX() && playerLocation.getX() < under.getX() + 0.3){
                    removeBlock(leftUnder);
                }

                // checks right side of the block (remove current block + right block)
                else if (playerLocation.getX() >= under.getX() + 0.7 && playerLocation.getX() < under.getX() + 1){
                    removeBlock(rightUnder);
                }

                // checks top side of the block (remove current block + top block)
                else if (playerLocation.getZ() >= under.getZ() && playerLocation.getZ() < under.getZ() + 0.3){
                    removeBlock(topUnder);
                }

                // checks bottom side of the block (remove current block + bottom block)
                else if (playerLocation.getZ() >= under.getZ() + 0.7 && playerLocation.getZ() < under.getZ() + 1){
                    removeBlock(bottomUnder);
                }
                removeBlock(under);
                plugin.rollbackMap.replace(arenaName, rollbackList);
            }
        }.runTaskLater(plugin, tntRemoveDelay);
    }
}
