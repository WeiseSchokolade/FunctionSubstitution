package de.schoko.functioninjection.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import de.schoko.functioninjection.FunctionInjectionMain;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerFunctionLibrary;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ServerFunctionLibrary.class)
public class ServerFunctionLibraryMixin {
	@Inject(method = "reload", at = @At("HEAD"))
	public void forgetConsumption(PreparableReloadListener.SharedState sharedState, Executor executor, PreparableReloadListener.PreparationBarrier preparationBarrier, Executor executor2, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
		FunctionInjectionMain.clearConsumption();
	}

	@Inject(method = "method_29453(Lcom/mojang/datafixers/util/Pair;)V", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void handleReload(Pair pair, CallbackInfo ci, Map map, ImmutableMap.Builder<Identifier, CommandFunction<CommandSourceStack>> builder) {
		FunctionInjectionMain.getUnconsumedFunctions().forEach(function -> {
			builder.put(function.id(), function);
		});
	}
}
