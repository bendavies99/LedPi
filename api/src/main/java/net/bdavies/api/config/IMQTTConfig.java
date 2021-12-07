package net.bdavies.api.config;

/**
 * The MQTT Configuration settings
 *
 * @author ben.davies
 */
public interface IMQTTConfig
{
    /**
     * Get the host address of the MQTT Broker
     *
     * @return the host address in ipv4 format e.g. 127.0.0.1
     */
    String getHost();

    /**
     * Get the broker username
     *
     * @return The username
     */
    default String getUsername() { return null; }

    /**
     * Get the broker password
     *
     * @return broker password
     */
    default String getPassword() { return null; }

    /**
     * Get the broker port
     *
     * @return the port of the broker default: 1883
     */
    default int getPort() { return 1883; }
}
