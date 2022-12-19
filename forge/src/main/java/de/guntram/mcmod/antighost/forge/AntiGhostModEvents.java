package de.guntram.mcmod.antighost.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static de.guntram.mcmod.antighost.Constants.*;

// Subscribes to mod lifecycle events
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AntiGhostModEvents {
    // Register the key binding for refreshing
    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent e) {
        // Set the keybind to only conflict in-game
        REFRESH_KEY_BINDING.setKeyConflictContext(KeyConflictContext.IN_GAME);
        e.register(REFRESH_KEY_BINDING);
    }
}
