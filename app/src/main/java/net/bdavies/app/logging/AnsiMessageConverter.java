package net.bdavies.app.logging;

import org.fusesource.jansi.Ansi;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * This will convert the message to allow to render custom colours through the messages
 *
 * @author ben.davies
 */
@Slf4j
public class AnsiMessageConverter extends ClassicConverter
{
	/**
	 * The convert method is responsible for extracting data from the event and
	 * storing it for later use by the write method.
	 *
	 * @param event The event
	 */
	@Override
	public String convert(ILoggingEvent event)
	{
		return Ansi.ansi().render(event.getFormattedMessage()).toString();
	}
}
