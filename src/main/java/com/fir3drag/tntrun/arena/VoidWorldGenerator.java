package com.fir3drag.tntrun.arena;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.World;
import org.bukkit.Material;

import java.util.Random;

public class VoidWorldGenerator extends ChunkGenerator {

    @Override
    public ChunkData generateChunkData(World world, Random random, int x, int z, ChunkGenerator.BiomeGrid biome) {
        ChunkData chunkData = this.createChunkData(world);

        // Fill all blocks with air
        for (int xOffset = 0; xOffset < 16; xOffset++) {
            for (int zOffset = 0; zOffset < 16; zOffset++) {
                for (int yOffset = 0; yOffset < world.getMaxHeight(); yOffset++) {
                    chunkData.setBlock(xOffset, yOffset, zOffset, Material.AIR);
                }
            }
        }

        return chunkData;
    }
}

