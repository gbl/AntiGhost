package de.guntram.mcmod.antighost;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

// This is just a wrapper class to make sure the Grid class itself, which
// needed to be 99% @OnlyIn(Dist.CLIENT) otherwise, doesn't need to get loaded 
// on dedicated servers
@Mod.EventBusSubscriber(modid = AntiGhostMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AntiGhostMain {
    public static final String MODID = "antighost";

    public static LocalPlayer player;
    public static ClientPacketListener conn;

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

    // Cache the player and connection so we don't have to get them every time we refresh blocks
    @SubscribeEvent
    private static void onClientSetup(FMLClientSetupEvent e) {
        var mc = Minecraft.getInstance();
        player = mc.player;
        conn = mc.getConnection();
    }

    // Register the key binding for refreshing
    @SubscribeEvent
    private static void registerBindings(RegisterKeyMappingsEvent e) {
        e.register(REFRESH_BLOCKS_MAPPING.get());
    }
}
