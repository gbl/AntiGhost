package de.guntram.mcmod.antighost;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
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
	acceptedMinecraftVersions = "[1.8.9]"
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
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (showGui.isPressed()) {
            this.processCommand(player, null);
            player.addChatMessage(new ChatComponentText(I18n.format("msg.request", (Object[]) null)));
        }
    }
    
    @Override
    public String getCommandName() {
        return "ghost";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/ghost";
    }

    @Override
    public List<String> getCommandAliases() {
        return new ArrayList<String>();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        Minecraft mc=Minecraft.getMinecraft();
        NetHandlerPlayClient conn = mc.getNetHandler();
        if (conn==null)
            return;
        BlockPos pos=sender.getPosition();
        for (int dx=-4; dx<=4; dx++)
            for (int dy=-4; dy<=4; dy++)
                for (int dz=-4; dz<=4; dz++) {
                    C07PacketPlayerDigging packet=new C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, 
                            new BlockPos(pos.getX()+dx, pos.getY()+dy, pos.getZ()+dz),
                            EnumFacing.UP       // with ABORT_DESTROY_BLOCK, this value is unused
                    );
                    conn.addToSendQueue(packet);
                }
    }

    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return new ArrayList<String>();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return getCommandName().compareTo(o.getCommandName());
    }
}
