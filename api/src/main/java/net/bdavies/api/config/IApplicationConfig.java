package net.bdavies.api.config;


/**
 * Configuration interface for the application
 *
 * @author ben.davies
 */
public interface IApplicationConfig
{
	/**
	 * Get the application name used for the MQTT Commands
	 *
	 * @return the name
	 */
	String getApplicationName();

	/**
	 * Get if the application is in debug mode meaning all render calls that would goto a LED strip
	 * goto a network client
	 *
	 * @return true if debug mode is on
	 */
	boolean isDebugMode();

	/**
	 * Get the debug port used for the debug mode
	 *
	 * @return The debug port
	 */
	int getDebugPort();
}
