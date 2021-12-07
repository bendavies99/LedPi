package net.bdavies.api.config;

/**
 * The Main config interface that connects all the config data together
 *
 * @author ben.davies
 */
public interface IConfig
{
    /**
     * Get the configuration under "application"
     *
     * @return {@link IApplicationConfig}
     */
    IApplicationConfig getApplicationConfig();

    /**
     * Get an array of strip configs
     *
     * @return {@link IStripConfig}[]
     */
    IStripConfig[] getStripConfigurations();

    /**
     * Get the network configuration
     *
     * @return {@link INetworkConfig}
     */
    INetworkConfig getNetworkConfig();
}
