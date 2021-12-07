package net.bdavies.api.config;

/**
 * The network part of the configuration file
 *
 * @author ben.davies
 */
public interface INetworkConfig
{
    /**
     * Get the mqtt configuration under "mqtt"
     *
     * @return {@link IMQTTConfig}
     */
    IMQTTConfig getMqtt();

    /**
     * Get the port used for the reactive mode
     *
     * @return the reactive port default: 25562
     */
    default int getReactivePort() { return 25562; }
}
