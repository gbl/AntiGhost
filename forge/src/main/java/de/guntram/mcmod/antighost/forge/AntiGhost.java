package de.guntram.mcmod.antighost.forge;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

import static de.guntram.mcmod.antighost.Constants.MOD_ID;
import static de.guntram.mcmod.antighost.Constants.MOD_NAME;

@Mod(MOD_ID)
public class AntiGhost {

    public AntiGhost() {
        if (FMLEnvironment.dist.isDedicatedServer()) {
            System.err.println(MOD_NAME + " detected a dedicated server. Not doing anything.");
        }
    }
}
