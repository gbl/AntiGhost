package de.guntram.mcmod.antighost.fabric;

import de.guntram.mcmod.antighost.BlockRefresher;
import de.guntram.mcmod.crowdintranslate.CrowdinTranslate;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import static de.guntram.mcmod.antighost.Constants.*;

public class AntiGhost implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        CrowdinTranslate.downloadTranslations(MOD_ID);
        KeyBindingHelper.registerKeyBinding(REFRESH_KEY_BINDING);
        ClientTickEvents.END_CLIENT_TICK.register(e->keyPressed());
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
            ClientCommandManager.literal(REFRESH_COMMAND_NAME).executes( c-> {
                BlockRefresher.refreshBlocks();
                return 0;
            })
        ));
    }

    public void keyPressed() {
        if (REFRESH_KEY_BINDING.wasPressed()) {
            BlockRefresher.refreshBlocks();
        }
    }
}
