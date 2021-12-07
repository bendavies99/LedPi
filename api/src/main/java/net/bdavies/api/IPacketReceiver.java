package net.bdavies.api;

/**
 * Class for the packet receiver for the reactive mode
 *
 * @author ben.davies
 */
public interface IPacketReceiver
{
    /**
     * Enable listening to packets and processing them
     */
    void listenForPackets();

    /**
     * Start ignoring packets and just dispose of them
     */
    void ignorePackets();
}
