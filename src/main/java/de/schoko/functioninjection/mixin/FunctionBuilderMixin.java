package de.schoko.functioninjection.mixin;

import de.schoko.functioninjection.FunctionInjectionMain;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.commands.functions.MacroFunction;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.commands.functions.FunctionBuilder;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(FunctionBuilder.class)
public class FunctionBuilderMixin {
	@Shadow private @Nullable List<MacroFunction.Entry<?>> macroEntries;

	@Inject(method = "build(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/commands/functions/CommandFunction;", at = @At("HEAD"), cancellable = true)
	public void handleFunctionCreation(Identifier identifier, CallbackInfoReturnable<CommandFunction<?>> cir) {
		if (this.macroEntries == null) {
			CommandFunction<?> function = FunctionInjectionMain.consumeFunction(identifier);
			if (function != null) cir.setReturnValue(function);
		}
	}
}
