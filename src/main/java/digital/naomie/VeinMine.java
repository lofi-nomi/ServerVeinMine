package digital.naomie;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.xpple.betterconfig.api.ModConfigBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class VeinMine implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("serverveinmine");
    public static HashMap<Block, Block> equivalentBlocks = new HashMap<>();

    @Override
    public void onInitialize() {
        LOGGER.info("Server Vein Mine is initializing!");
        new ModConfigBuilder("serverveinmine", VeinMineConfig.class)
                .registerTypeHierarchy(
                        Block.class,
                        new BlockAdapter(),
                        new BlockSuggestionProvider(),
                        (ctx, name) -> {
                            String blockString = ctx.getArgument(name, String.class);
                            Identifier blockId = Identifier.tryParse(blockString);
                            if (blockId == null) {
                                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
                            }
                            if (!Registries.BLOCK.containsId(blockId)) {
                                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
                            }
                            return Registries.BLOCK.get(blockId);
                        }).build();
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            if (world.isClient()) return;
            if (!VeinMineConfig.veinMineableBlocks.contains(state.getBlock())) return;
            if (player.isSneaking() != VeinMineConfig.ShiftToActivate) return;
            if (!player.getMainHandStack().isSuitableFor(state)) return;
            this.mineVein(world, pos, state, player);
        });
    }

    private void mineVein(World world, BlockPos pos, @NotNull BlockState state, PlayerEntity player) {

        Block targetBlock = equivalentBlocks.getOrDefault(state.getBlock(), state.getBlock());
        HashSet<BlockPos> checked = new HashSet<>();
        HashSet<BlockPos> remaining = new HashSet<>();
        HashSet<BlockPos> targets = new HashSet<>();
        checked.add(pos);
        remaining.add(pos);
        int total = 0;
        while (total < VeinMineConfig.maxBlocks && !remaining.isEmpty()) {
            BlockPos currentPos = remaining.iterator().next();
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (Math.abs(x) + Math.abs(y) + Math.abs(z) == 0) continue;
                        BlockPos neighbor = currentPos.add(x, y, z);
                        if (checked.contains(neighbor)) continue;
                        Block neighborType = world.getBlockState(neighbor).getBlock();
                        Block neighbourBlock = equivalentBlocks.getOrDefault(neighborType, neighborType);
                        if (Objects.equals(targetBlock, neighbourBlock)) {
                            targets.add(neighbor);
                            remaining.add(neighbor);
                            checked.add(neighbor);
                            total++;
                        }
                    }
                }
            }
            remaining.remove(currentPos);
        }
        if (targets.size() > VeinMineConfig.maxBlocks){
            player.sendMessage(Text.of("You're trying to vein mine more blocks than allowed"), true);
            return;
        }
        targets.forEach(block -> {
            if (PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(world, player, pos, world.getBlockState(pos), world.getBlockEntity(pos))) {
                ((ServerPlayerEntity) player).interactionManager.tryBreakBlock(block);
            }
        });
    }

    static {
        equivalentBlocks.put(Blocks.DEEPSLATE_COAL_ORE, Blocks.COAL_ORE);
        equivalentBlocks.put(Blocks.DEEPSLATE_COPPER_ORE, Blocks.COPPER_ORE);
        equivalentBlocks.put(Blocks.DEEPSLATE_IRON_ORE, Blocks.IRON_ORE);
        equivalentBlocks.put(Blocks.DEEPSLATE_GOLD_ORE, Blocks.GOLD_ORE);
        equivalentBlocks.put(Blocks.DEEPSLATE_LAPIS_ORE, Blocks.LAPIS_ORE);
        equivalentBlocks.put(Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.REDSTONE_ORE);
        equivalentBlocks.put(Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DIAMOND_ORE);
        equivalentBlocks.put(Blocks.DEEPSLATE_EMERALD_ORE, Blocks.EMERALD_ORE);
    }
}