package de.guntram.mcmod.antighost.forge;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import static de.guntram.mcmod.antighost.Constants.*;

// Subscribes to mod lifecycle events
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AntiGhostModEvents {
    // Lazy initialize the key mapping to refresh blocks
    // Only conflicts in-game
    public static final Lazy<KeyBinding> REFRESH_BLOCKS_MAPPING = Lazy.of(
            () -> new KeyBinding(
                    REFRESH_DESCRIPTION_KEY,
                    KeyConflictContext.IN_GAME,
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_G,
                    CONTROLS_CATEGORY_KEY
            )
    );

    // Register the key binding for refreshing
    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent e) {
        e.register(REFRESH_BLOCKS_MAPPING.get());
    }
}
