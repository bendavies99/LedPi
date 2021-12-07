package net.bdavies.network.mqtt.command;

import java.time.Duration;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.IApplication;
import net.bdavies.api.util.ResourceUtil;
import reactor.core.publisher.Flux;

/**
 * The command registry for the system commands
 *
 * @author ben.davies
 */
@Slf4j
@ToString
public class SystemCommandRegister extends BaseCommandRegister
{
	/**
	 * Make a System command register
	 *
	 * @param application the application
	 */
	public SystemCommandRegister(IApplication application)
	{
		super(application, application.getConfig().getApplicationConfig().getApplicationName());
		registerPublishers();
	}

	/**
	 * Register the commands
	 *
	 * /shutdown -> shutdown the system when "doit" is passed as the payload
	 */
	@Override
	protected void registerCommands()
	{
		registerCommandWithFilter("shutdown", s -> s.equalsIgnoreCase("doit"), s -> application.shutdown());
	}

	/**
	 * Register the system publishers
	 *
	 * /res -> Send the system resources {@link ResourceUtil#getSystemResourcesAsString()}
	 */
	@Override
	protected void registerPublishers()
	{
		registerPublisher("res", Flux.interval(Duration.ofSeconds(2))
				.map(l -> ResourceUtil.getSystemResourcesAsString()));
	}
}
