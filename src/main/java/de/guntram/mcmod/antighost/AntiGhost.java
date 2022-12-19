package de.guntram.mcmod.antighost;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

import static de.guntram.mcmod.antighost.AntiGhost.MOD_ID;

@Mod(MOD_ID)
public class AntiGhost {
    public static final String MOD_ID = "antighost";
    public static final String MOD_NAME = "AntiGhost";

    public AntiGhost() {
        if (FMLEnvironment.dist.isDedicatedServer()) {
            System.err.println(MOD_NAME + " detected a dedicated server. Not doing anything.");
        }
    }
}
