package mods.natura.worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenerator;

import mods.natura.common.NContent;
import mods.natura.common.PHNatura;

public class WillowGen extends WorldGenerator {

    public final boolean seekHeight;

    public WillowGen(boolean notify) {
        super(notify);
        seekHeight = !notify;
    }

    int findGround(World world, int x, int y, int z) {
        int ret = -1;
        int height = y;
        do {
            Block blockAtHeight = world.getBlock(x, height, z);
            if ((blockAtHeight == Blocks.dirt || blockAtHeight == Blocks.grass || blockAtHeight == Blocks.sand)
                    && !world.getBlock(x, height + 1, z).func_149730_j()) {
                ret = height + 1;
                break;
            }
            height--;
        } while (height > PHNatura.seaLevel);
        return ret;
    }

    public boolean generate(World world, Random random, int x, int y, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

        if(!chunk.isChunkLoaded) {
            return false;
        }
        if (seekHeight) {
            y = findGround(world, x, y, z);
            if (y == -1) return false;
        }
        int l;

        for (l = random.nextInt(4) + 5; world.getBlock(x, y - 1, z).getMaterial() == Material.water; --y) {
            ;
        }

        boolean flag = true;

        if (y >= 1 && y + l + 1 <= 128) {
            int i1;
            int j1;
            int k1;
            int l1;

            for (i1 = y; i1 <= y + 1 + l; ++i1) {
                byte b0 = 1;

                if (i1 >= y + 1 + l - 2) {
                    b0 = 3;
                } else if (i1 == y) {
                    b0 = 0;
                }

                for (j1 = x - b0; j1 <= x + b0 && flag; ++j1) {
                    for (k1 = z - b0; k1 <= z + b0 && flag; ++k1) {
                        if (i1 >= 0 && i1 < 128) {
                            Block block = world.getBlock(j1, i1, k1);
                            if (block != Blocks.air && !block.isLeaves(world, j1, i1, k1) && i1 > y) {
                                flag = false;
                            }
                        } else {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag) {
                return false;
            } else {
                Block block = world.getBlock(x, y - 1, z);

                if ((block == Blocks.grass || block == Blocks.dirt) && y < 128 - l - 1) {
                    this.setBlockAndNotifyAdequately(world, x, y - 1, z, Blocks.dirt, 0);
                    int i2;
                    int j2;

                    for (j2 = y - 3 + l; j2 <= y + l; ++j2) {
                        j1 = j2 - (y + l);
                        k1 = 2 - j1 / 2;

                        for (l1 = x - k1; l1 <= x + k1; ++l1) {
                            i2 = l1 - x;
                            int chunkXForL1 = l1 >> 4;
                            if (chunkXForL1 == chunkX) {
                                for (int k2 = z - k1; k2 <= z + k1; ++k2) {
                                    int l2 = k2 - z;
                                    int chunkZForK2 = k2 >> 4;
                                    if (chunkZForK2 == chunkZ) {
                                        block = world.getBlock(l1, j2, k2);

                                        if ((Math.abs(i2) != k1 || Math.abs(l2) != k1 || random.nextInt(2) != 0 && j1 != 0)
                                                && block.canBeReplacedByLeaves(world, l1, j2, k2)) {
                                            world.setBlock(l1, j2, k2, NContent.floraLeavesNoColor, 3, 2);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    for (j2 = 0; j2 < l; ++j2) {
                        block = world.getBlock(x, y + j2, z);

                        if (block == Blocks.air || block.isLeaves(world, x, y + j2, z)
                                || block.canBeReplacedByLeaves(world, x, y + j2, z)
                                || block == Blocks.water) {
                            this.setBlockAndNotifyAdequately(world, x, y + j2, z, NContent.willow, 0);
                        }
                    }

                    for (j2 = y - 3 + l; j2 <= y + l; ++j2) {
                        j1 = j2 - (y + l);
                        k1 = 2 - j1 / 2;

                        for (l1 = x - k1; l1 <= x + k1; ++l1) {
                            for (i2 = z - k1; i2 <= z + k1; ++i2) {
                                block = world.getBlock(l1, j2, i2);
                                if (block.isLeaves(world, l1, j2, i2)) {
                                    if (random.nextInt(4) == 0 && world.getBlock(l1 - 1, j2, i2) == Blocks.air) {
                                        this.generateVines(world, l1 - 1, j2, i2, 3);
                                    }

                                    if (random.nextInt(4) == 0 && world.getBlock(l1 + 1, j2, i2) == Blocks.air) {
                                        this.generateVines(world, l1 + 1, j2, i2, 3);
                                    }

                                    if (random.nextInt(4) == 0 && world.getBlock(l1, j2, i2 - 1) == Blocks.air) {
                                        this.generateVines(world, l1, j2, i2 - 1, 3);
                                    }

                                    if (random.nextInt(4) == 0 && world.getBlock(l1, j2, i2 + 1) == Blocks.air) {
                                        this.generateVines(world, l1, j2, i2 + 1, 3);
                                    }
                                }
                            }
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    /**
     * Generates vines at the given position until it hits a block.
     */
    private void generateVines(World world, int x, int y, int z, int par5) {
        this.setBlockAndNotifyAdequately(world, x, y, z, NContent.floraLeavesNoColor, par5);
        int i1 = 4;

        while (true) {
            --y;
            if (world.getBlock(x, y, z) != Blocks.air || i1 <= 0) {
                return;
            }

            this.setBlockAndNotifyAdequately(world, x, y, z, NContent.floraLeavesNoColor, par5);
            --i1;
        }
    }
}
