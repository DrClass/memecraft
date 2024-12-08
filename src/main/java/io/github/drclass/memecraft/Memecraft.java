package io.github.drclass.memecraft;

import com.mojang.logging.LogUtils;

import io.github.drclass.memecraft.commands.IgniteCommand;
import io.github.drclass.memecraft.commands.ReplaceItemCommand;
import io.github.drclass.memecraft.component.MemecraftDataComponentTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.slf4j.Logger;

@Mod(Memecraft.MODID)
public class Memecraft {
	public static final String MODID = "memecraft";
	public static final Logger LOGGER = LogUtils.getLogger();

	public Memecraft() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		modEventBus.addListener(this::commonSetup);

		MinecraftForge.EVENT_BUS.register(this);
		
		MemecraftDataComponentTypes.register(modEventBus);

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {

	}

	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {

	}
	
	@SubscribeEvent
	public void onServerStarting(RegisterCommandsEvent event) {
        event.getDispatcher().register(IgniteCommand.register());
        event.getDispatcher().register(ReplaceItemCommand.register(event.getBuildContext()));
    }
	
	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents {
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {

		}
	}
}
