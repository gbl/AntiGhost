package de.guntram.mcmod.antighost;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import static de.guntram.mcmod.antighost.AntiGhost.MOD_ID;

// Subscribes to mod lifecycle events
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AntiGhostModEvents {
    // Lazy initialize the key mapping to refresh blocks
    // Only conflicts in-game
    public static final Lazy<KeyMapping> REFRESH_BLOCKS_MAPPING = Lazy.of(
            () -> new KeyMapping(
                    "key." + MOD_ID + ".reveal",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_G,
                    "key.categories." + MOD_ID
            )
    );

    // Register the key binding for refreshing
    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent e) {
        e.register(REFRESH_BLOCKS_MAPPING.get());
    }
}
