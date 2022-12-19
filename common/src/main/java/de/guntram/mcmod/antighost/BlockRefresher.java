package de.guntram.mcmod.antighost;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.time.Instant;

import static de.guntram.mcmod.antighost.Constants.REFRESH_DELAY;
import static de.guntram.mcmod.antighost.Constants.REFRESH_RANGE;

// Holds logic for refreshing blocks
public class BlockRefresher {
    // Instant next execute is allowed
    private static Instant NEXT_AVAILABLE = Instant.now();

    // Refreshes all blocks within range of the player
    public static void refreshBlocks() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayNetworkHandler conn = mc.getNetworkHandler();
        ClientPlayerEntity player = mc.player;

        // Short-circuit if the connection or player is null
        if (conn == null || player == null)
            return;

        // Throttle execution
        if (isOnCooldown()) {
            player.sendMessage(Text.translatable("msg.onCooldown", REFRESH_DELAY), true);
            return;
        }

        // Refreshes each block in range
        for (BlockPos pos : getBlocksToRefresh(player.getBlockPos())) {
            refreshBlock(pos, conn);
        }

        // Send confirmation message to the player
        player.sendMessage(Text.translatable("msg.request"), true);
    }

    // Refreshes the block at the given position
    private static void refreshBlock(BlockPos pos, ClientPlayNetworkHandler conn) {
        PlayerActionC2SPacket packet = new PlayerActionC2SPacket(
                PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK,
                pos,
                Direction.UP       // with ABORT_DESTROY_BLOCK, this value is unused
        );
        conn.sendPacket(packet);
    }

    // Gets the positions of all the blocks to refresh
    private static Iterable<BlockPos> getBlocksToRefresh(BlockPos centerPos) {
        return BlockPos.iterate(
                centerPos.add(REFRESH_RANGE, REFRESH_RANGE, REFRESH_RANGE),
                centerPos.add(-REFRESH_RANGE, -REFRESH_RANGE, -REFRESH_RANGE)
        );
    }

    // Throttle execution
    private static boolean isOnCooldown() {
        Instant now = Instant.now();
        if (now.isBefore(NEXT_AVAILABLE)) {
            return true;
        }
        NEXT_AVAILABLE = now.plusSeconds(REFRESH_DELAY);
        return false;
    }
}
