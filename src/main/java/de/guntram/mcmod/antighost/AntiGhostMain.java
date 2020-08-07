package de.guntram.mcmod.antighost;

import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

@Mod("antighost") 
// This is just a wrapper class to make sure the Grid class itself, which
// needed to be 99% @OnlyIn(Dist.CLIENT) otherwise, doesn't need to get loaded 
// on dedicated servers

public class AntiGhostMain {
    
    static final String MODNAME=AntiGhost.MODNAME;

    public AntiGhost antiGhost;
    
    public AntiGhostMain() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        if (FMLEnvironment.dist.isClient()) {
            antiGhost = new AntiGhost();
        } else {
            System.err.println(MODNAME+" detected a dedicated server. Not doing anything.");
        }
    }
}
