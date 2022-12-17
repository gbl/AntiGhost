package de.guntram.mcmod.antighost;

import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.time.Instant;

@Mod.EventBusSubscriber(modid = AntiGhostMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class AntiGhost {
    // The x, y, and z range to refresh blocks at
    private static final int REFRESH_RANGE = 4;
    // The delay between executes
    private static final int DELAY = 1;
    // Instant next execute is allowed
    private static Instant NEXT_AVAILABLE = Instant.now();

    // Register the /ghost command
    @SubscribeEvent
    private static void registerCommands(RegisterClientCommandsEvent e) {
        e.getDispatcher().register(Commands.literal("ghost")
                .executes(c -> {
                    refreshBlocks();
                    return 0;
                })
        );
    }

    // Event for checking key press in-game
    @SubscribeEvent
    private static void onClientTick(ClientTickEvent e) {
        // Only check on the second tick event
        if (e.phase == TickEvent.Phase.END) {
            // Consume all the key presses
            while (AntiGhostMain.REFRESH_BLOCKS_MAPPING.get().consumeClick()) {
                refreshBlocks();
            }
        }
    }

    // Refreshes all blocks within range of the player
    private static void refreshBlocks() {
        // Short-circuit if the connection is null
        if (AntiGhostMain.conn == null)
            return;

        // Throttle execution
        Instant now = Instant.now();
        if (now.isBefore(NEXT_AVAILABLE))
            return;
        NEXT_AVAILABLE = now.plusSeconds(DELAY);

        // Refreshes each block in range
        for (BlockPos pos : getBlocksToRefresh()) {
            refreshBlock(pos);
        }

        // Send confirmation message to the player
        AntiGhostMain.player.displayClientMessage(Component.translatable("msg.request"), true);
    }

    // Refreshes the block at the given position
    private static void refreshBlock(BlockPos pos) {
        ServerboundPlayerActionPacket packet = new ServerboundPlayerActionPacket(
                ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK,
                pos,
                Direction.UP       // with ABORT_DESTROY_BLOCK, this value is unused
        );
        AntiGhostMain.conn.send(packet);
    }

    // Gets the positions of all the blocks to refresh
    private static Iterable<BlockPos> getBlocksToRefresh() {
        BlockPos playerPos = AntiGhostMain.player.blockPosition();
        return BlockPos.betweenClosed(
                playerPos.offset(REFRESH_RANGE, REFRESH_RANGE, REFRESH_RANGE),
                playerPos.offset(-REFRESH_RANGE, -REFRESH_RANGE, -REFRESH_RANGE)
        );
    }
}
