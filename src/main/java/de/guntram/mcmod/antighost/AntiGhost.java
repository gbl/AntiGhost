package de.guntram.mcmod.antighost;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommandSource;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;



@Mod("antighost") 

public class AntiGhost 
{
    static final String MODID="antighost";
    static final String VERSION="1.1.1";
    static KeyBinding showGui;
    
    public AntiGhost() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::init);        
    }
    
    public void init(FMLCommonSetupEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
        // ClientCommandHandler.instance.registerCommand(this); How the F in forge?
        ClientRegistry.registerKeyBinding(showGui = 
                new KeyBinding("key.reveal", 'G', "key.categories.antighost"));
    }

    @SubscribeEvent
    public void keyPressed(final InputEvent.KeyInputEvent e) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (showGui.isPressed()) {
            this.execute(null, player, null);
            player.sendStatusMessage(new TranslationTextComponent("msg.request"), false);
        }
    }
    
    @SubscribeEvent
    public void chatEvent(final ClientChatEvent e) {
        if (e.getOriginalMessage().equals("/ghost")) {
            this.execute(null, Minecraft.getInstance().player, null);
            e.setCanceled(true);
        }
    }

/*
    public String getName() {
        return "ghost";
    }

    public String getUsage(ICommandSender sender) {
        return "/ghost";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>();
    }

    @Override
*/
    
    public void execute(MinecraftServer server, ICommandSource sender, String[] args) {
        Minecraft mc=Minecraft.getInstance();
        ClientPlayNetHandler conn = mc.getConnection();
        if (conn==null)
            return;
        if (!(sender instanceof ClientPlayerEntity)) {
            return;
        }
        ClientPlayerEntity player = (ClientPlayerEntity) sender;
        BlockPos pos=player.func_233580_cy_();
        for (int dx=-4; dx<=4; dx++)
            for (int dy=-4; dy<=4; dy++)
                for (int dz=-4; dz<=4; dz++) {
                    CPlayerDiggingPacket packet=new CPlayerDiggingPacket(
                            CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK, 
                            new BlockPos(pos.getX()+dx, pos.getY()+dy, pos.getZ()+dz),
                            Direction.UP       // with ABORT_DESTROY_BLOCK, this value is unused
                    );
                    conn.sendPacket(packet);
                }
    }
/*
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
*/
}
