package io.github.drclass.memecraft.component;

import java.util.function.UnaryOperator;

import io.github.drclass.memecraft.Memecraft;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MemecraftDataComponentTypes {
	public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Memecraft.MODID);
	
	public static final RegistryObject<DataComponentType<Integer>> SOULBIND = register("soulbind", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT)); 
	
	public static <T> RegistryObject<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> buildOperator) {
		return DATA_COMPONENT_TYPES.register(name, () -> buildOperator.apply(DataComponentType.builder()).build());
	}
	
	public static void register(IEventBus eventBus) {
		DATA_COMPONENT_TYPES.register(eventBus);
	}
}
