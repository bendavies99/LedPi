package net.bdavies.api.effects;

import java.util.Map;
import java.util.function.Consumer;

import reactor.core.Disposable;

/**
 * The interface for an effect for more
 * of a deep dive into the effect please see the effect package
 *
 * @author ben.davies
 */
public interface IEffect
{
	/**
	 * Update the effect config
	 *
	 * @param configName The name of the config variable
	 * @param value      The new value
	 */
	void updateConfig(String configName, Object value);

	/**
	 * Subscribe to the render calls passed by the effect
	 * This uses a {@link reactor.core.publisher.Sinks.UnicastSpec} to send data
	 * so bare in mind that you can only subscribe once
	 *
	 * @param call The consumer that will provide the render call when a render call is made by the effect
	 * @return a disposable flux subscription
	 * @see Disposable
	 */
	Disposable subscribe(Consumer<RenderCall> call);

	/**
	 * Dispose the effect shutdown any background dependencies
	 * of the effect such as the effect render loop
	 */
	void dispose();

	/**
	 * Stop the effect from running but will keep all data that effect
	 * did have in a saved state
	 */
	void stop();

	/**
	 * Start the effect and this will begin any effect render loop
	 * or just pass a color to the subscriber
	 */
	void start();

	/**
	 * Get the current effect config (copied)
	 *
	 * @return Map of the config
	 */
	Map<String, Object> getCurrentConfig();
}
