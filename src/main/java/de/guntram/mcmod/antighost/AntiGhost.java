package de.guntram.mcmod.antighost;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmlclient.registry.ClientRegistry;

public class AntiGhost 
{
    static final String MODID="antighost";
    static final String MODNAME="AntiGhost";
    static KeyMapping showGui;
    
    public AntiGhost() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::init);        
    }
    
    public void init(FMLCommonSetupEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
        // ClientCommandHandler.instance.registerCommand(this); How the F in forge?
        ClientRegistry.registerKeyBinding(showGui = 
                new KeyMapping("key.reveal", 'G', "key.categories.antighost"));
    }

    @SubscribeEvent
    public void keyPressed(final InputEvent.KeyInputEvent e) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (showGui.consumeClick()) {
            this.execute(null, player, null);
            player.displayClientMessage(new TranslatableComponent("msg.request"), true);
        }
    }
    
    @SubscribeEvent
    public void chatEvent(final ClientChatEvent e) {
        if (e.getOriginalMessage().equals("/ghost")) {
            this.execute(null, Minecraft.getInstance().player, null);
            e.setCanceled(true);
        }
    }

    public void execute(MinecraftServer server, LocalPlayer player, String[] args) {
        Minecraft mc=Minecraft.getInstance();
        ClientPacketListener conn = mc.getConnection();
        if (conn==null)
            return;
        BlockPos pos=player.blockPosition();
        for (int dx=-4; dx<=4; dx++)
            for (int dy=-4; dy<=4; dy++)
                for (int dz=-4; dz<=4; dz++) {
                    ServerboundPlayerActionPacket packet=new ServerboundPlayerActionPacket(
                            ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, 
                            new BlockPos(pos.getX()+dx, pos.getY()+dy, pos.getZ()+dz),
                            Direction.UP       // with ABORT_DESTROY_BLOCK, this value is unused
                    );
                    conn.send(packet);
                }
    }
}
