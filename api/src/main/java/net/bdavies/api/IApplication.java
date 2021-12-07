package net.bdavies.api;

import java.util.List;
import java.util.concurrent.ExecutorService;

import net.bdavies.api.config.IConfig;
import net.bdavies.api.effects.IEffect;
import net.bdavies.api.strip.IStrip;

/**
 * The main interface for the application class
 *
 * @author ben.davies
 */
public interface IApplication
{
    /**
     * Get the LED Strips registered to the application
     *
     * @return a list of strips
     * @see IStrip
     */
    List<IStrip> getStrips();

    /**
     * Shutdown the application
     *
     * @see IEffect#dispose()
     * @see IStrip#dispose()
     * @see ExecutorService#shutdown()
     */
    void shutdown();

    /**
     * Get the app packet receiver
     *
     * @return the main pr
     */
    IPacketReceiver getPacketReceiver();

    /**
     * Get the config for the application
     *
     * @return The config class
     */
    IConfig getConfig();
}
