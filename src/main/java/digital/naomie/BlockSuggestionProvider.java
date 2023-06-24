package digital.naomie;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;
// Written by Xpple at https://raw.githubusercontent.com/xpple/BetterConfig/master/src/testmod/java/dev/xpple/betterconfig/BlockSuggestionProvider.java
class BlockSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(Registries.BLOCK.getIds(), builder);
    }
}