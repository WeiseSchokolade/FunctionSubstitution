package de.schoko.functioninjection;

import net.fabricmc.api.ModInitializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.ScoreHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionInjectionMain implements ModInitializer {
	private static final Map<Identifier, CommandFunction<CommandSourceStack>> providedFunctionMap = new HashMap<>();
	private static final List<CommandFunction<CommandSourceStack>> providedFunctions = new ArrayList<>();
	private static final List<CommandFunction<CommandSourceStack>> unconsumedFunctions = new ArrayList<>();

	private static final ScoreHolder inputHolder = ScoreHolder.forNameOnly("$math.isqrt.x");
	private static final ScoreHolder outputHolder = ScoreHolder.forNameOnly("$math.isqrt");

	@Override
	public void onInitialize() {
		addPlainFunction("test", "loop", (commandSourceStack, context, frame) -> {
			Vec3 position = commandSourceStack.getAnchor().apply(commandSourceStack);
			Vec3 offset = Vec3.directionFromRotation(commandSourceStack.getRotation()).scale(0.1);
			for (int i = 0; i < 500; i++) {
				BlockState state = commandSourceStack.getLevel().getBlockState(new BlockPos((int) position.x, (int) position.y, (int) position.z));
				if (!state.isAir()) {
					EntityType.LIGHTNING_BOLT.spawn(commandSourceStack.getLevel(), new BlockPos((int) position.x, (int) position.y, (int) position.z), EntitySpawnReason.COMMAND);
					break;
				}
				position = position.add(offset);

				for (ServerPlayer player : commandSourceStack.getLevel().players()) {
					commandSourceStack.getLevel().sendParticles(player, ParticleTypes.SMOKE, true, false, position.x(), position.y, position.z, 1, 0, 0,0, 0);
				}
				//commandSourceStack.getLevel().addParticle(ParticleTypes.SMOKE, true, false, position.x, position.y, position.z, 0, 0, 0);
			}

		});
		addPlainFunction("test", "nonvanilla_function", (commandSourceStack, context, frame) -> {
			if (commandSourceStack.isPlayer()) commandSourceStack.getPlayer().sendSystemMessage(Component.literal("Yoooo, you're a player! Here, have the current unix millisecond time: " + System.currentTimeMillis()));
			frame.returnSuccess(31415926);
		});
		addPlainFunction("bs.math", "isqrt", (commandSourceStack, context, frame) -> {
			//CompoundTag data = commandSourceStack.getServer().getCommandStorage().get(Identifier.fromNamespaceAndPath("$math.isqrt", ));
			ServerScoreboard scoreboard = commandSourceStack.getServer().getScoreboard();
			int value = (int) Math.floor(Math.sqrt(scoreboard.getOrCreatePlayerScore(inputHolder, scoreboard.getObjective("bs.in")).get()));
			scoreboard.getOrCreatePlayerScore(outputHolder, scoreboard.getObjective("bs.out")).set(value);
			frame.returnSuccess(value);
		});
	}

	public static void addPlainFunction(String namespace, String path, FunctionExecutor<CommandSourceStack> executor) {
		Identifier identifier = Identifier.fromNamespaceAndPath(namespace, path);
		ProvidedPlainTextCommandFunction<CommandSourceStack> function = new ProvidedPlainTextCommandFunction<>(identifier, executor);
		providedFunctionMap.put(identifier, function);
		providedFunctions.add(function);
	}

	public static CommandFunction<?> consumeFunction(Identifier identifier) {
		unconsumedFunctions.removeIf(function -> function.id().equals(identifier));
		return providedFunctionMap.get(identifier);
	}

	public static void clearConsumption() {
		unconsumedFunctions.clear();
		unconsumedFunctions.addAll(providedFunctions);
	}

	public static List<CommandFunction<CommandSourceStack>> getUnconsumedFunctions() {
		return unconsumedFunctions;
	}
}
