package de.guntram.mcmod.antighost;

import de.guntram.mcmod.crowdintranslate.CrowdinTranslate;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;

public class AntiGhost implements ClientModInitializer
{
    private static final Identifier CHANNEL = new Identifier("antighost", "v1");
    static final String MODID="antighost";
    static KeyBinding requestBlocks;
    static AGMode mode;
    
    @Override
    public void onInitializeClient()
    {
        final String category="key.categories.antighost";
        requestBlocks = new KeyBinding("key.antighost.reveal", GLFW_KEY_G, category);
        CrowdinTranslate.downloadTranslations(MODID);
        KeyBindingHelper.registerKeyBinding(requestBlocks);
        ClientTickEvents.END_CLIENT_TICK.register(e->keyPressed());
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                ClientCommandManager.literal("ghost").executes(c -> {
                    this.execute();
                    return 0;
                })
            );
        });
        ClientPlayNetworking.registerGlobalReceiver(CHANNEL, (client, handler, buf, responseSender) -> {
            mode = buf.readEnumConstant(AGMode.class);
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> mode = AGMode.ENABLED);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> mode = AGMode.ENABLED);
    }

    public void keyPressed() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (requestBlocks.wasPressed()) {
            this.execute();
            player.sendMessage(Text.translatable("msg.request"), false);
        }
    }

    public void execute() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayNetworkHandler conn = mc.getNetworkHandler();
        if (conn == null || mc.player == null) {
            return;
        }
        switch (mode) {
            case ENABLED -> {
                BlockPos pos = mc.player.getBlockPos();
                for (int dx = -4; dx <= 4; dx++) {
                    for (int dy = -4; dy <= 4; dy++) {
                        for (int dz = -4; dz <= 4; dz++) {
                            PlayerActionC2SPacket packet = new PlayerActionC2SPacket(
                                    PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK,
                                    new BlockPos(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz),
                                    Direction.UP       // with ABORT_DESTROY_BLOCK, this value is unused
                            );
                            conn.sendPacket(packet);
                        }
                    }
                }
            }
            case CUSTOM -> {
                // Send a custom packet
                conn.sendPacket(new CustomPayloadC2SPacket(CHANNEL, new PacketByteBuf(Unpooled.EMPTY_BUFFER)));
            }
            case DISABLED -> {
                // Do nothing
            }
        }

    }
}
