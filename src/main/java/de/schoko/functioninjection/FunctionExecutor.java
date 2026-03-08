package de.schoko.functioninjection;

import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.Frame;

@FunctionalInterface
public interface FunctionExecutor<T> {
	void executeFunction(T commandSourceStack, ExecutionContext<T> context, Frame frame);
}
