package net.bdavies.app.strip;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.config.IStripConfig;
import net.bdavies.api.strip.IStrip;
import net.bdavies.env.Environment;
import net.bdavies.network.RemoteStripServer;

/**
 * A factory to create {@link net.bdavies.app.Strip} implementations
 *
 * @author ben.davies
 */
@Slf4j
@UtilityClass
public class StripFactory
{
	/**
	 * Make a strip from a config
	 *
	 * @param config The strip config
	 * @return The strip interface
	 */
	public IStrip makeStrip(IStripConfig config, RemoteStripServer stripServer)
	{
		if (Environment.isProduction && stripServer == null)
		{
			log.trace("Making a production strip using config: {}", config);
			return new NativeStrip(config);
		}
		else
		{
			log.trace("Making a debug strip using config: {}", config);
			return new DebugStrip(config.getName(), config.getLedCount(), config.getUid(), stripServer);
		}
	}
}
