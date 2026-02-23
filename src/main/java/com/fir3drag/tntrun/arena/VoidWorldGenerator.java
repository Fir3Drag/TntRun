package com.fir3drag.tntrun.arena;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.World;
import org.bukkit.Material;

import java.util.Random;

public class VoidWorldGenerator extends ChunkGenerator {

    @Override
    public ChunkData generateChunkData(World world, Random random, int x, int z, ChunkGenerator.BiomeGrid biome) {
        return createChunkData(world);
    }
}

