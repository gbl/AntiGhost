package de.guntram.mcmod.antighost.forge;

import static net.minecraft.server.command.CommandManager.literal;

import de.guntram.mcmod.antighost.BlockRefresher;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.common.Mod;

import static de.guntram.mcmod.antighost.Constants.*;

// Subscribes to forge events
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class AntiGhostForgeEvents {
    // Register the /ghost command
    @SubscribeEvent
    public static void registerCommands(RegisterClientCommandsEvent e) {
        e.getDispatcher().register(
            literal(REFRESH_COMMAND_NAME)
                .executes(c -> {
                    BlockRefresher.refreshBlocks();
                    return 0;
                }));
    }

    // Event for checking key press in-game
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent e) {
        // Only check on the second tick event
        if (e.phase == TickEvent.Phase.END) {
            // Consume all the key presses
            while (REFRESH_KEY_BINDING.wasPressed()) {
                BlockRefresher.refreshBlocks();
            }
        }
    }
}
