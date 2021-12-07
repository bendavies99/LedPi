package net.bdavies.app.logging;

import ch.qos.logback.core.ConsoleAppender;
import lombok.extern.slf4j.Slf4j;

/**
 * Setup a custom ConsoleAppender to solve the issue with Jansi nothing having the WindowsAnsiOutput
 *
 * @author ben.davies
 */
@Slf4j
public class AnsiConsoleAppender<E> extends ConsoleAppender<E>
{
	/**
	 * Ensure the console appender uses Jansi for coloring
	 */
	@Override
	public void start()
	{
		setWithJansi(false);
		super.start();
		setWithJansi(true);
	}
}
