package digital.naomie;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.function.Supplier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import dev.xpple.betterconfig.api.ModConfigBuilder;
import dev.xpple.betterconfig.util.CheckedBiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VeinMine implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("serverveinmine");
    @Override
    public void onInitialize() {
        LOGGER.info("Server Vein Mine is initializing!");

        // Adapted from
        // https://raw.githubusercontent.com/xpple/BetterConfig/master/src/testmod/java/dev/xpple/betterconfig/TestMod.java

        CheckedBiFunction<CommandContext<? extends CommandSource>, String, Block, CommandSyntaxException> biFunc = (
                ctx, name) -> {
            String blockString = ctx.getArgument(name, String.class);
            Identifier blockId = Identifier.tryParse(blockString);
            if (blockId == null) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
            }
            if (Registries.BLOCK.containsId(blockId)) {
                return (Block) Registries.BLOCK.get(blockId);
            }
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
        };
        Supplier<SuggestionProvider<? extends CommandSource>> suggestionProviderSupplier = () -> new BlockSuggestionProvider();
        Pair<Supplier<SuggestionProvider<? extends CommandSource>>, CheckedBiFunction<CommandContext<? extends CommandSource>, String, Block, CommandSyntaxException>> pair = 
            new Pair<>(suggestionProviderSupplier, biFunc);
    

        new ModConfigBuilder("serverveinmine", VeinMineConfig.class)
                .registerTypeHierarchyWithSuggestor(
                        Block.class,
                        new BlockAdapter(),
                        pair)
                .build();
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            mineVein(world, pos, state, player);
        });
    LOGGER.info("Server Vein Mine's config has been loaded");
    }

    private void mineVein(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        ArrayList<Block> block_list = VeinMineConfig.veinMineableBlocks;
        if (!(world instanceof ServerWorld) || !(block_list.contains(state.getBlock()))
                || !(player.isSneaking() == VeinMineConfig.ShiftToActivate)) {
            return;
        }
        LOGGER.info("World is a server world, block is vein mineable, and player is matching sneak value");
        if (player.getMainHandStack().isSuitableFor(state)) {
            LOGGER.info("Player has suitable tool");
            ArrayList<BlockPos> blocks = new ArrayList<>();
            blocks.add(pos);
            int i = 0;
            while (i < blocks.size()) {
                BlockPos currentPos = blocks.get(i);
                for (BlockPos neighbor : new BlockPos[] {
                        currentPos.north(),
                        currentPos.south(),
                        currentPos.east(),
                        currentPos.west(),
                        currentPos.up(),
                        currentPos.down()
                }) {
                    if (world.getBlockState(neighbor).equals(state) && !blocks.contains(neighbor)) {
                        blocks.add(neighbor);
                    }
                }
                i++;
            }
            LOGGER.info("Blocks to be broken: {}", blocks.size());
            blocks.forEach(block -> {
                if (!PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(world, player, pos,
                        world.getBlockState(pos), world.getBlockEntity(pos))) {
                            LOGGER.info("Block ineligible for vein mine");
                    return;
                } else {
                    ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
                    serverPlayerEntity.interactionManager.tryBreakBlock(block);
                }
            });
        }

    }
}