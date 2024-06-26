package digital.naomie;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.xpple.betterconfig.api.ModConfigBuilder;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VeinMine implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("serverveinmine");
    public static HashMap<Block, Block> equivalentBlocks = new HashMap<>();

    @Override
    public void onInitialize() {
        LOGGER.info("Server Vein Mine is initializing!");

        // Adapted from
        // https://raw.githubusercontent.com/xpple/BetterConfig/master/src/testmod/java/dev/xpple/betterconfig/TestMod.java
//
//        CheckedBiFunction<CommandContext<? extends CommandSource>, String, Block, CommandSyntaxException> biFunc = (
//                ctx, name) -> {
//            String blockString = ctx.getArgument(name, String.class);
//            Identifier blockId = Identifier.tryParse(blockString);
//            if (blockId == null) {
//                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
//            }
//            if (Registries.BLOCK.containsId(blockId)) {
//                return (Block) Registries.BLOCK.get(blockId);
//            }
//            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
//        };
//        Supplier<SuggestionProvider<? extends CommandSource>> suggestionProviderSupplier = BlockSuggestionProvider::new;
//        Pair<Supplier<SuggestionProvider<? extends CommandSource>>, CheckedBiFunction<CommandContext<? extends CommandSource>, String, Block, CommandSyntaxException>> pair =
//            new Pair<>(suggestionProviderSupplier, biFunc);
//

        new ModConfigBuilder("serverveinmine", VeinMineConfig.class)
                .registerTypeHierarchy(
                        Block.class,
                        new BlockAdapter(),
                        new BlockSuggestionProvider(), (ctx, name) -> {
                            String blockString = ctx.getArgument(name, String.class);
                            Identifier blockId = Identifier.tryParse(blockString);
                            if (blockId == null) {
                                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
                            }
                            if (Registries.BLOCK.containsId(blockId)) {
                                return Registries.BLOCK.get(blockId);
                            }
                            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
                        }).build();
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            mineVein(world, pos, state, player);
        });
//        TODO Implement Bidirectional Hashmap
        equivalentBlocks.put(Blocks.DEEPSLATE_COAL_ORE, Blocks.COAL_ORE);
        equivalentBlocks.put(Blocks.COAL_ORE, Blocks.COAL_ORE);
        equivalentBlocks.put(Blocks.DEEPSLATE_COPPER_ORE, Blocks.COPPER_ORE);
        equivalentBlocks.put(Blocks.COPPER_ORE, Blocks.COPPER_ORE);
        equivalentBlocks.put(Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DIAMOND_ORE);
        equivalentBlocks.put(Blocks.DIAMOND_ORE, Blocks.DIAMOND_ORE);
        equivalentBlocks.put(Blocks.DEEPSLATE_EMERALD_ORE, Blocks.EMERALD_ORE);
        equivalentBlocks.put(Blocks.EMERALD_ORE, Blocks.EMERALD_ORE);
        equivalentBlocks.put(Blocks.DEEPSLATE_GOLD_ORE, Blocks.GOLD_ORE);
        equivalentBlocks.put(Blocks.GOLD_ORE, Blocks.GOLD_ORE);
        equivalentBlocks.put(Blocks.DEEPSLATE_IRON_ORE, Blocks.IRON_ORE);
        equivalentBlocks.put(Blocks.IRON_ORE, Blocks.IRON_ORE);
        equivalentBlocks.put(Blocks.DEEPSLATE_LAPIS_ORE, Blocks.LAPIS_ORE);
        equivalentBlocks.put(Blocks.LAPIS_ORE, Blocks.LAPIS_ORE);
        equivalentBlocks.put(Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE);
        equivalentBlocks.put(Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE);
    LOGGER.info("Server Vein Mine's config has been loaded");
    }

    private void mineVein(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        ArrayList<Block> block_list = VeinMineConfig.veinMineableBlocks;
        if (!(world instanceof ServerWorld) || !(block_list.contains(state.getBlock()))
                || !(player.isSneaking() == VeinMineConfig.ShiftToActivate)) {
            return;
        }
        if (player.getMainHandStack().isSuitableFor(state)) {
            ArrayList<BlockPos> blocks = new ArrayList<>();
            blocks.add(pos);
            int i = 0;
            while (i < blocks.size() && i < VeinMineConfig.maxBlocks){
                BlockPos currentPos = blocks.get(i);
                for (BlockPos neighbor : new BlockPos[] {
                        currentPos.add(1, 0, 0), // right
                        currentPos.add(-1, 0, 0), // left
                        currentPos.add(0, 1, 0), // up
                        currentPos.add(0, -1, 0), // down
                        currentPos.add(0, 0, 1), // front
                        currentPos.add(0, 0, -1), // back
                        currentPos.add(1, 1, 0), // right-up
                        currentPos.add(-1, 1, 0), // left-up
                        currentPos.add(1, -1, 0), // right-down
                        currentPos.add(-1, -1, 0), // left-down
                        currentPos.add(1, 0, 1), // right-front
                        currentPos.add(-1, 0, 1), // left-front
                        currentPos.add(1, 0, -1), // right-back
                        currentPos.add(-1, 0, -1), // left-back
                        currentPos.add(0, 1, 1), // up-front
                        currentPos.add(0, -1, 1), // down-front
                        currentPos.add(0, 1, -1), // up-back
                        currentPos.add(0, -1, -1), // down-back
                        currentPos.add(1, 1, 1), // right-up-front
                        currentPos.add(-1, 1, 1), // left-up-front
                        currentPos.add(1, -1, 1), // right-down-front
                        currentPos.add(-1, -1, 1), // left-down-front
                        currentPos.add(1, 1, -1), // right-up-back
                        currentPos.add(-1, 1, -1), // left-up-back
                        currentPos.add(1, -1, -1), // right-down-back
                        currentPos.add(-1, -1, -1) // left-down-back
                }) {
                    Block neighbourBlock = world.getBlockState(neighbor).getBlock();
                    if ((Objects.equals(neighbourBlock, state.getBlock()) || Objects.equals(equivalentBlocks.getOrDefault(neighbourBlock,null),state.getBlock())) && !blocks.contains(neighbor))
                    {
                        blocks.add(neighbor);
                    }
                }
                i++;
            }
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
            if (blocks.size() > VeinMineConfig.maxBlocks){
                serverPlayerEntity.sendMessage(Text.of("You're trying to vein mine more blocks than allowed"), true);
                return;
            }
            blocks.forEach(block -> {
                if (!PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(world, player, pos,
                        world.getBlockState(pos), world.getBlockEntity(pos))) {
                            LOGGER.info("Block ineligible for vein mine");
                    return;
                } else {

                    serverPlayerEntity.interactionManager.tryBreakBlock(block);
                }
            });
        }

    }
}