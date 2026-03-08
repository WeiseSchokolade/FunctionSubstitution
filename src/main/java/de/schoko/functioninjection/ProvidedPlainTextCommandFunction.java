package de.schoko.functioninjection;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.commands.functions.InstantiatedFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record ProvidedPlainTextCommandFunction<T>(Identifier identifier, FunctionExecutor<CommandSourceStack> functionExecutor) implements CommandFunction<T>, InstantiatedFunction<T> {
	@Override
	public @NotNull Identifier id() {
		return identifier;
	}

	@Override
	public @NotNull InstantiatedFunction<T> instantiate(@Nullable CompoundTag compoundTag, @NotNull CommandDispatcher<T> commandDispatcher) {
		return this;
	}

	@Override
	public @NotNull List<UnboundEntryAction<T>> entries() {
		return List.of((object, executionContext, frame) -> functionExecutor.executeFunction((CommandSourceStack) object, (ExecutionContext<CommandSourceStack>) executionContext, frame));
	}

}
