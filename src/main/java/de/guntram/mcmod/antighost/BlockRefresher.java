package de.guntram.mcmod.antighost;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;

import java.time.Instant;

// Holds logic for refreshing blocks
public class BlockRefresher {
    // The x, y, and z range to refresh blocks at
    private static final int REFRESH_RANGE = 4;
    // The delay between executes
    private static final int DELAY = 10;
    // Instant next execute is allowed
    private static Instant NEXT_AVAILABLE = Instant.now();

    // Refreshes all blocks within range of the player
    public static void refreshBlocks() {
        Minecraft mc = Minecraft.getInstance();
        ClientPacketListener conn = mc.getConnection();
        LocalPlayer player = mc.player;

        // Short-circuit if the connection or player is null
        if (conn == null || player == null)
            return;

        // Throttle execution
        if (isOnCooldown()) {
            player.displayClientMessage(Component.translatable("msg.onCooldown", DELAY), true);
            return;
        }

        // Refreshes each block in range
        for (BlockPos pos : getBlocksToRefresh(player.blockPosition())) {
            refreshBlock(pos, conn);
        }

        // Send confirmation message to the player
        player.displayClientMessage(Component.translatable("msg.request"), true);
    }

    // Refreshes the block at the given position
    private static void refreshBlock(BlockPos pos, ClientPacketListener conn) {
        ServerboundPlayerActionPacket packet = new ServerboundPlayerActionPacket(
                ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK,
                pos,
                Direction.UP       // with ABORT_DESTROY_BLOCK, this value is unused
        );
        conn.send(packet);
    }

    // Gets the positions of all the blocks to refresh
    private static Iterable<BlockPos> getBlocksToRefresh(BlockPos centerPos) {
        return BlockPos.betweenClosed(
                centerPos.offset(REFRESH_RANGE, REFRESH_RANGE, REFRESH_RANGE),
                centerPos.offset(-REFRESH_RANGE, -REFRESH_RANGE, -REFRESH_RANGE)
        );
    }

    // Throttle execution
    private static boolean isOnCooldown() {
        Instant now = Instant.now();
        if (now.isBefore(NEXT_AVAILABLE)) {
            return true;
        }
        NEXT_AVAILABLE = now.plusSeconds(DELAY);
        return false;
    }
}
