package de.guntram.mcmod.antighost;

/**
 * AntiGhost current mode.
 *
 * @author VidTu
 */
public enum AGMode {
    /**
     * The mod is enabled.
     * Default state.
     */
    ENABLED,
    /**
     * The mod is disabled.
     */
    DISABLED,
    /**
     * The mod is enabled, but will use custom packets for requesting blocks.
     */
    CUSTOM;
}
