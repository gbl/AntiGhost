package de.guntram.mcmod.antighost;

import net.minecraft.client.option.KeyBinding;

import static net.minecraft.client.util.InputUtil.Type.KEYSYM;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;

public class Constants {
    public static final String MOD_ID = "antighost";
    public static final String MOD_NAME = "AntiGhost";
    // The x, y, and z range to refresh blocks at
    public static final int REFRESH_RANGE = 4;
    // The delay between executes in seconds
    public static final int REFRESH_DELAY = 10;
    public static final KeyBinding REFRESH_KEY_BINDING = new KeyBinding(
            "key." + MOD_ID + ".reveal",
            KEYSYM,
            GLFW_KEY_G,
            "key.categories." + MOD_ID
    );
    public static final String REFRESH_COMMAND_NAME = "ghost";
}
