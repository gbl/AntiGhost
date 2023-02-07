package de.guntram.mcmod.antighost;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class AntiGhost 
{
    static final String MODID="antighost";
    static final String MODNAME="AntiGhost";
    static final KeyMapping ON_REVEAL = new KeyMapping("key.antighost.reveal", GLFW.GLFW_KEY_G, "key.categories.antighost");
    static boolean handled = false;
    
    public AntiGhost() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::init);
        bus.addListener(this::registerBindings);
    }
    
    public void init(FMLCommonSetupEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(ON_REVEAL);
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (ON_REVEAL.consumeClick()) {
                Minecraft mc = Minecraft.getInstance();
                this.execute(null, mc.player, null);
                mc.player.displayClientMessage(Component.translatable("msg.request"), true);
            }
        }
    } 

    @SubscribeEvent
    public void chatEvent(final ClientChatEvent e) {
        if (e.getOriginalMessage().equals("/ghost")) {
            Minecraft mc = Minecraft.getInstance();
            this.execute(null, mc.player, null);
            e.setCanceled(true);
        }
    }

    public void execute(MinecraftServer server, LocalPlayer player, String[] args) {
        Minecraft mc = Minecraft.getInstance();
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
