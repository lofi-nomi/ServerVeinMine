package digital.naomie;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import dev.xpple.betterconfig.api.Config;

import java.util.ArrayList;
import java.util.List;

public class VeinMineConfig {
    @Config public static boolean generateConfigJSON = false;
    @Config public static boolean ShiftToActivate = true;
    @Config public static int maxBlocks = 64;
    @Config public static ArrayList<Block> veinMineableBlocks = new ArrayList<>(
            List.of(
                    Blocks.COAL_ORE,
                    Blocks.IRON_ORE,
                    Blocks.GOLD_ORE,
                    Blocks.DIAMOND_ORE,
                    Blocks.EMERALD_ORE,
                    Blocks.LAPIS_ORE,
                    Blocks.REDSTONE_ORE,
                    Blocks.NETHER_QUARTZ_ORE,
                    Blocks.NETHER_GOLD_ORE,
                    Blocks.ANCIENT_DEBRIS,
                    Blocks.COPPER_ORE,
                    Blocks.DEEPSLATE_IRON_ORE,
                    Blocks.DEEPSLATE_GOLD_ORE,
                    Blocks.DEEPSLATE_COAL_ORE,
                    Blocks.DEEPSLATE_DIAMOND_ORE,
                    Blocks.DEEPSLATE_EMERALD_ORE,
                    Blocks.DEEPSLATE_LAPIS_ORE,
                    Blocks.DEEPSLATE_REDSTONE_ORE,
                    Blocks.DEEPSLATE_COPPER_ORE,
                    Blocks.GLOWSTONE
            ));
}