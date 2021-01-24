package de.guntram.mcmod.antighost;



import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class AntiGhostBlockMixin {

    @Inject(at = @At("HEAD"), method = "sendPacket", cancellable = true)
    private void onSendPacketHead(Packet<?> packet, CallbackInfo info) {
        if(packet instanceof PlayerActionC2SPacket){
            
            PlayerActionC2SPacket.Action action = ((PlayerActionC2SPacket) packet).getAction();
            
            if(action == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK){
                
                BlockPos blockPos = ((PlayerActionC2SPacket) packet).getPos();
                
                PlayerActionC2SPacket customPacket = new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, blockPos, Direction.UP);
                MinecraftClient.getInstance().getNetworkHandler().sendPacket(customPacket);
            }
        }
    }
}
