package mods.natura.worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenerator;

public class CloudGen extends WorldGenerator {

    private Block cloudBlock;
    private int cloudMeta;
    private int numberOfBlocks;
    private boolean flatCloud;

    public CloudGen(Block cloud, int metadata, int size, boolean isCloudFlat) {
        cloudBlock = cloud;
        cloudMeta = metadata;
        numberOfBlocks = size;
        flatCloud = isCloudFlat;
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
        int l = random.nextInt(3) - 1;
        int i1 = random.nextInt(3) - 1;

        int numberOfBlocks = 10;

        for (int j1 = 0; j1 < numberOfBlocks; j1++) {
            x += (random.nextInt(3) - 1) + l;
            z += (random.nextInt(3) - 1) + i1;

            if (random.nextBoolean() && !flatCloud || flatCloud && random.nextInt(10) == 0) {
                y += random.nextInt(3) - 1;
            }

            if (!chunk.isChunkLoaded) {
                return false;
            }

            int maxCloudSize = 4 * (flatCloud ? 3 : 1) + random.nextInt(2);
            int xStart = Math.max(x - maxCloudSize, chunk.xPosition * 16);
            int xEnd = Math.min(x + maxCloudSize, (chunk.xPosition * 16) + 16);
            int yEnd = Math.min(y + random.nextInt(1) + 2, 255);
            int zStart = Math.max(z - maxCloudSize, chunk.zPosition * 16);
            int zEnd = Math.min(z + maxCloudSize, (chunk.zPosition * 16) + 16);

            for (int xIter = xStart; xIter < xEnd; xIter++) {
                for (int yIter = y; yIter < yEnd; yIter++) {
                    for (int zIter = zStart; zIter < zEnd; zIter++) {
                        if (world.getBlock(xIter, yIter, zIter) == Blocks.air
                                && Math.abs(xIter - x) + Math.abs(yIter - y) + Math.abs(zIter - z) < maxCloudSize) {
                            setBlockAndNotifyAdequately(world, xIter, yIter, zIter, cloudBlock, cloudMeta);
                        }
                    }
                }
            }
        }
        return true;
    }
}
