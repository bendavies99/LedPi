package net.bdavies.api.config;


/**
 * The strips part of the configuration file
 *
 * @author ben.davies
 */
public interface IStripConfig
{
	/**
	 * The name of the strip
	 *
	 * @return the name
	 */
	String getName();

	/**
	 * The GPIO Pin used on the raspberry pi
	 *
	 * @return the pin number
	 */
	int getPinNumber();

	/**
	 * The amount of LEDs on the LED Strip
	 *
	 * @return num of leds
	 */
	int getLedCount();

	/**
	 * The unique identifier for the strip for reactive mode and other services like Home assistant
	 *
	 * @return a unique id in hexadecimal format
	 */
	int getUid();
}
