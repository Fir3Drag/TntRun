package com.fir3drag.tntrun.arena;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;

public class TntRunBlockRemover {
    private final TntRun plugin;

    public TntRunBlockRemover(TntRun plugin) {
        this.plugin = plugin;
    }

    private void removeBlockIfTNT(Player player){

        new BukkitRunnable() {
            @Override
            public void run(){
                Map<Block, Material> rollbackList = plugin.rollbackMap.get(player.getWorld().getName());

                // block variables
                // -x = left, +x = right, north -z, south +z
                Block under = player.getLocation().subtract(0, 1, 0).getBlock();
                Block secondUnder = player.getLocation().subtract(0, 2, 0).getBlock();

                Block leftUnder = under.getRelative(-1, 0, 0);
                Block leftSecondUnder = secondUnder.getRelative(-1, 0, 0);

                Block leftTopUnder = under.getRelative(-1, 0, -1);
                Block leftTopSecondUnder = secondUnder.getRelative(-1, 0, -1);

                Block leftBottomUnder = under.getRelative(-1, 0, 1);
                Block leftBottomSecondUnder = secondUnder.getRelative(-1, 0, 1);

                Block rightUnder = under.getRelative(1, 0, 0);
                Block rightSecondUnder = secondUnder.getRelative(1, 0, 0);

                Block rightTopUnder = under.getRelative(1, 0, -1);
                Block rightTopSecondUnder = secondUnder.getRelative(1, 0, -1);

                Block rightBottomUnder = under.getRelative(1, 0, 1);
                Block rightBottomSecondUnder = secondUnder.getRelative(1, 0, 1);

                Block topUnder = under.getRelative(0, 0, -1);
                Block topSecondUnder = secondUnder.getRelative(0, 0, -1);

                Block bottomUnder = under.getRelative(0, 0, 1);
                Block bottomSecondUnder = secondUnder.getRelative(0, 0, 1);

                // Based off facing North
                if (under.getType().equals(Material.SAND) || under.getType().equals(Material.GRAVEL)){  // recheck the blocks in case they were turned to air in the last second
                    if (secondUnder.getType().equals(Material.TNT)){
                        // checks top left side of the block (remove current block + left block + top + top left)
                        if (player.getLocation().getX() >= under.getX() && player.getLocation().getX() < under.getX() + 0.3 &&
                                player.getLocation().getZ() >= under.getZ() && player.getLocation().getZ() < under.getZ() + 0.3){

                            // add to rollback
                            rollbackList.put(under, under.getType());
                            rollbackList.put(secondUnder, secondUnder.getType());

                            rollbackList.put(leftUnder, leftUnder.getType());
                            rollbackList.put(leftSecondUnder, leftSecondUnder.getType());

                            rollbackList.put(topUnder, topUnder.getType());
                            rollbackList.put(topSecondUnder, topSecondUnder.getType());

                            rollbackList.put(leftTopUnder, leftTopUnder.getType());
                            rollbackList.put(leftTopSecondUnder, leftTopSecondUnder.getType());

                            // remove certain blocks
                            under.setType(Material.AIR);
                            secondUnder.setType(Material.AIR);

                            leftUnder.setType(Material.AIR);
                            leftSecondUnder.setType(Material.AIR);

                            topUnder.setType(Material.AIR);
                            topSecondUnder.setType(Material.AIR);

                            leftTopUnder.setType(Material.AIR);
                            leftTopSecondUnder.setType(Material.AIR);

                        }

                        // checks top right side of the block (remove current block + right block + top + top right)
                        else if (player.getLocation().getX() >= under.getX() + 0.7 && player.getLocation().getX() < under.getX() + 1 &&
                                player.getLocation().getZ() >= under.getZ() && player.getLocation().getZ() < under.getZ() + 0.3){

                            // add to rollback
                            rollbackList.put(under, under.getType());
                            rollbackList.put(secondUnder, secondUnder.getType());

                            rollbackList.put(rightUnder, rightUnder.getType());
                            rollbackList.put(rightSecondUnder, rightSecondUnder.getType());

                            rollbackList.put(topUnder, topUnder.getType());
                            rollbackList.put(topSecondUnder, topSecondUnder.getType());

                            rollbackList.put(rightTopUnder, rightTopUnder.getType());
                            rollbackList.put(rightTopSecondUnder, rightTopSecondUnder.getType());

                            // remove certain blocks
                            under.setType(Material.AIR);
                            secondUnder.setType(Material.AIR);

                            rightUnder.setType(Material.AIR);
                            rightSecondUnder.setType(Material.AIR);

                            topUnder.setType(Material.AIR);
                            topSecondUnder.setType(Material.AIR);

                            rightTopUnder.setType(Material.AIR);
                            rightTopSecondUnder.setType(Material.AIR);
                        }

                        // checks bottom left side of the block (remove current block + left block + bottom + bottom left)
                        else if (player.getLocation().getX() >= under.getX() && player.getLocation().getX() < under.getX() + 0.3 &&
                                player.getLocation().getZ() >= under.getZ() + 0.7 && player.getLocation().getZ() < under.getZ() + 1){

                            // add to rollback
                            rollbackList.put(under, under.getType());
                            rollbackList.put(secondUnder, secondUnder.getType());

                            rollbackList.put(leftUnder, leftUnder.getType());
                            rollbackList.put(leftSecondUnder, leftSecondUnder.getType());

                            rollbackList.put(bottomUnder, bottomUnder.getType());
                            rollbackList.put(bottomSecondUnder, bottomSecondUnder.getType());

                            rollbackList.put(leftBottomUnder, leftBottomUnder.getType());
                            rollbackList.put(leftBottomSecondUnder, leftBottomSecondUnder.getType());

                            // remove certain blocks
                            under.setType(Material.AIR);
                            secondUnder.setType(Material.AIR);

                            leftUnder.setType(Material.AIR);
                            leftSecondUnder.setType(Material.AIR);

                            bottomUnder.setType(Material.AIR);
                            bottomSecondUnder.setType(Material.AIR);

                            leftBottomUnder.setType(Material.AIR);
                            leftBottomSecondUnder.setType(Material.AIR);
                        }

                        // checks bottom right side of the block (remove current block + bottom block + bottom + bottom right)
                        else if (player.getLocation().getX() >= under.getX() + 0.7 && player.getLocation().getX() < under.getX() + 1 &&
                                player.getLocation().getZ() >= under.getZ() + 0.7 && player.getLocation().getZ() < under.getZ() + 1){

                            // add to rollback
                            rollbackList.put(under, under.getType());
                            rollbackList.put(secondUnder, secondUnder.getType());

                            rollbackList.put(rightUnder, rightUnder.getType());
                            rollbackList.put(rightSecondUnder, rightSecondUnder.getType());

                            rollbackList.put(bottomUnder, bottomUnder.getType());
                            rollbackList.put(bottomSecondUnder, bottomSecondUnder.getType());

                            rollbackList.put(rightBottomUnder, rightBottomUnder.getType());
                            rollbackList.put(rightBottomSecondUnder, rightBottomSecondUnder.getType());

                            // remove certain blocks
                            under.setType(Material.AIR);
                            secondUnder.setType(Material.AIR);

                            rightUnder.setType(Material.AIR);
                            rightSecondUnder.setType(Material.AIR);

                            bottomUnder.setType(Material.AIR);
                            bottomSecondUnder.setType(Material.AIR);

                            rightBottomUnder.setType(Material.AIR);
                            rightBottomSecondUnder.setType(Material.AIR);
                        }

                        // checks left side of the block (remove current block + left block)
                        else if (player.getLocation().getX() >= under.getX() && player.getLocation().getX() < under.getX() + 0.3){

                            // add to rollback
                            rollbackList.put(under, under.getType());
                            rollbackList.put(secondUnder, secondUnder.getType());

                            rollbackList.put(leftUnder, leftUnder.getType());
                            rollbackList.put(leftSecondUnder, leftSecondUnder.getType());

                            // remove certain blocks
                            under.setType(Material.AIR);
                            secondUnder.setType(Material.AIR);

                            leftUnder.setType(Material.AIR);
                            leftSecondUnder.setType(Material.AIR);
                        }

                        // checks right side of the block (remove current block + right block)
                        else if (player.getLocation().getX() >= under.getX() + 0.7 && player.getLocation().getX() < under.getX() + 1){

                            // add to rollback
                            rollbackList.put(under, under.getType());
                            rollbackList.put(secondUnder, secondUnder.getType());

                            rollbackList.put(rightUnder, rightUnder.getType());
                            rollbackList.put(rightSecondUnder, rightSecondUnder.getType());

                            // remove certain blocks
                            under.setType(Material.AIR);
                            secondUnder.setType(Material.AIR);

                            rightUnder.setType(Material.AIR);
                            rightSecondUnder.setType(Material.AIR);
                        }

                        // checks top side of the block (remove current block + top block)
                        else if (player.getLocation().getZ() >= under.getZ() && player.getLocation().getZ() < under.getZ() + 0.3){

                            // add to rollback
                            rollbackList.put(under, under.getType());
                            rollbackList.put(secondUnder, secondUnder.getType());

                            rollbackList.put(topUnder, topUnder.getType());
                            rollbackList.put(topSecondUnder, topSecondUnder.getType());

                            // remove certain blocks
                            under.setType(Material.AIR);
                            secondUnder.setType(Material.AIR);

                            topUnder.setType(Material.AIR);
                            topSecondUnder.setType(Material.AIR);
                        }

                        // checks bottom side of the block (remove current block + bottom block)
                        else if (player.getLocation().getZ() >= under.getZ() + 0.7 && player.getLocation().getZ() < under.getZ() + 1){

                            // add to rollback
                            rollbackList.put(under, under.getType());
                            rollbackList.put(secondUnder, secondUnder.getType());

                            rollbackList.put(bottomUnder, bottomUnder.getType());
                            rollbackList.put(bottomSecondUnder, bottomSecondUnder.getType());

                            // remove certain blocks
                            under.setType(Material.AIR);
                            secondUnder.setType(Material.AIR);

                            bottomUnder.setType(Material.AIR);
                            bottomSecondUnder.setType(Material.AIR);
                        }
                        else {  // middle of the block

                            // add to rollback
                            rollbackList.put(under, under.getType());
                            rollbackList.put(secondUnder, secondUnder.getType());

                            // remove certain blocks
                            under.setType(Material.AIR);
                            secondUnder.setType(Material.AIR);
                        }
                        plugin.rollbackMap.replace(player.getWorld().getName(), rollbackList);
                    }
                }
            }
        }.runTaskLater(plugin, 1);
    }

    public void move(Player player){
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("Arenas");
        String arenaName = player.getWorld().getName();

        // checks the player is in an arena and is playing the game
        if (arenas.contains(arenaName) && this.plugin.playingMap.get(arenaName).contains(player)){
            if (this.plugin.gameStatusMap.get(arenaName).equals("playing")){  // checks the game has started
                Block blockUnderPlayer = player.getLocation().subtract(0, 1, 0).getBlock();
                Block secondBlockUnderPlayer = player.getLocation().subtract(0, 2, 0).getBlock();

                if (blockUnderPlayer.getY() + 1 == player.getLocation().getY()){
                    if (blockUnderPlayer.getType().equals(Material.SAND) || blockUnderPlayer.getType().equals(Material.GRAVEL)){
                        if (secondBlockUnderPlayer.getType().equals(Material.TNT)){
                            removeBlockIfTNT(player);
                        }
                    }
                }
            }
        }
    }
}
