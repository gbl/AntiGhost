package de.guntram.mcmod.antighost;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = AntiGhost.MODID, 
        version = AntiGhost.VERSION,
	clientSideOnly = true, 
	guiFactory = "de.guntram.mcmod.antighost.GuiFactory",
	acceptedMinecraftVersions = "[1.12]"
)

public class AntiGhost
{
    static final String MODID="antighost";
    static final String VERSION="1.0";
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        ConfigurationHandler confHandler = ConfigurationHandler.getInstance();
        confHandler.load(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(confHandler);
    }
}
