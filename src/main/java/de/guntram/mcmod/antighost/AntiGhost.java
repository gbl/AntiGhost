package de.guntram.mcmod.antighost;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = AntiGhost.MODID, 
        version = AntiGhost.VERSION,
	clientSideOnly = true, 
	guiFactory = "de.guntram.mcmod.antighost.GuiFactory",
	acceptedMinecraftVersions = "[1.11.2]"
)

public class AntiGhost implements ICommand
{
    static final String MODID="antighost";
    static final String VERSION="1.1";
    static KeyBinding showGui;
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(this);
        ClientRegistry.registerKeyBinding(showGui = new KeyBinding("key.reveal", Keyboard.KEY_G, "key.categories.antighost"));
    }

    @EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        ConfigurationHandler confHandler = ConfigurationHandler.getInstance();
        confHandler.load(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(confHandler);
    }

    @SubscribeEvent
    public void keyPressed(final InputEvent.KeyInputEvent e) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (showGui.isPressed()) {
            this.execute(null, player, null);
            player.sendMessage(new TextComponentString(I18n.format("msg.request", (Object[]) null)));
        }
    }
    
    @Override
    public String getName() {
        return "ghost";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/ghost";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        Minecraft mc=Minecraft.getMinecraft();
        NetHandlerPlayClient conn = mc.getConnection();
        if (conn==null)
            return;
        BlockPos pos=sender.getPosition();
        for (int dx=-4; dx<=4; dx++)
            for (int dy=-4; dy<=4; dy++)
                for (int dz=-4; dz<=4; dz++) {
                    CPacketPlayerDigging packet=new CPacketPlayerDigging(
                            CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, 
                            new BlockPos(pos.getX()+dx, pos.getY()+dy, pos.getZ()+dz),
                            EnumFacing.UP       // with ABORT_DESTROY_BLOCK, this value is unused
                    );
                    conn.sendPacket(packet);
                }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        return new ArrayList<>();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return getName().compareTo(o.getName());
    }
}
