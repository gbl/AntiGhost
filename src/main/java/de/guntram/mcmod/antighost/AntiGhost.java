package de.guntram.mcmod.antighost;

import com.mojang.brigadier.CommandDispatcher;
import static io.github.cottonmc.clientcommands.ArgumentBuilders.literal;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;

public class AntiGhost implements ClientModInitializer, ClientCommandPlugin
{
    static final String MODID="antighost";
    static final String VERSION="1.1";
    static KeyBinding requestBlocks;
    
    @Override
    public void onInitializeClient()
    {
        final String category="key.categories.antighost";
        requestBlocks = new KeyBinding("antighost:reveal", GLFW_KEY_G, category);
        KeyBindingHelper.registerKeyBinding(requestBlocks);
        ClientTickEvents.END_CLIENT_TICK.register(e->keyPressed());
    }
    
    
    @Override
    public void registerCommands(CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(
            literal("ghost").executes(c->{
                this.execute();
                return 1;
            })
        );
    }

    public void keyPressed() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (requestBlocks.wasPressed()) {
            this.execute();
            player.sendMessage(new TranslatableText("msg.request"), false);
        }
    }
    
    public void execute() {
        MinecraftClient mc=MinecraftClient.getInstance();
        ClientPlayNetworkHandler conn = mc.getNetworkHandler();
        if (conn==null)
            return;
        BlockPos pos=mc.player.getBlockPos();
        for (int dx=-4; dx<=4; dx++)
            for (int dy=-4; dy<=4; dy++)
                for (int dz=-4; dz<=4; dz++) {
                    PlayerActionC2SPacket packet=new PlayerActionC2SPacket(
                            PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, 
                            new BlockPos(pos.getX()+dx, pos.getY()+dy, pos.getZ()+dz),
                            Direction.UP       // with ABORT_DESTROY_BLOCK, this value is unused
                    );
                    conn.sendPacket(packet);
                }
    }
}
