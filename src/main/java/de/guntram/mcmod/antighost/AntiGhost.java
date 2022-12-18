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

// Subscribes to mod lifecycle events
@Mod(AntiGhost.MODID)
@Mod.EventBusSubscriber(modid = AntiGhost.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AntiGhost {
    public static final String MODID = "antighost";

    // Lazy initialize the key mapping to refresh blocks
    // Only conflicts in-game
    public static final Lazy<KeyMapping> REFRESH_BLOCKS_MAPPING = Lazy.of(
            () -> new KeyMapping(
                    "key." + MODID + ".reveal",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_G,
                    "key.categories." + MODID
            )
    );

    // Register the key binding for refreshing
    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent e) {
        e.register(REFRESH_BLOCKS_MAPPING.get());
    }
}
