package net.bdavies.network.mqtt.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.bdavies.api.IApplication;
import reactor.core.publisher.Flux;

/**
 * The base command register for handling commands and setting up
 * publishers based on events
 *
 * @author ben.davies
 */
@Slf4j
public abstract class BaseCommandRegister
{
	protected final IApplication application;
	@Getter
	private final String prefix;
	private final Map<String, Consumer<String>> commands = new HashMap<>();
	@Getter
	private final Map<String, Flux<String>> publishers = new HashMap<>();

	/**
	 * Make a command register
	 *
	 * @param application the application
	 * @param prefix the prefix for the commands
	 */
	protected BaseCommandRegister(IApplication application, String prefix)
	{
		this.application = application;
		this.prefix = prefix;
		registerCommands();
	}

	/**
	 * Register the commands
	 */
	protected abstract void registerCommands();

	/**
	 * Register the publishers
	 */
	protected abstract void registerPublishers();

	/**
	 * Register a command
	 *
	 * @param name the name of the command
	 * @param action the action to take when the command is called
	 */
	protected final void registerCommand(String name, Consumer<String> action) {
		commands.put(name, action);
	}

	/**
	 * Get the name of all the registered commands
	 *
	 * @return a set of strings
	 */
	public final Set<String> getCommandNames() {
		return commands.keySet();
	}

	/**
	 * Register a command with a filter so it will only run on a certain condition
	 *
	 * @param name the name of the command
	 * @param filter the filter
	 * @param action the action to take when the command is run
	 */
	protected final void registerCommandWithFilter(String name, Predicate<String> filter, Consumer<String> action) {
		commands.put(name, s -> {
			if (filter.test(s)) action.accept(s);
		});
	}

	/**
	 * Register a publisher
	 *
	 * @param name the name of the publisher
	 * @param publisher The Publisher to subscribe to send to the MQTT Broker
	 */
	protected final void registerPublisher(String name, Flux<String> publisher) {
		publishers.put(name, publisher);
	}

	/**
	 * Run a command based on a name and message
	 *
	 * @param name the name of the command
	 * @param message the message for the command (payload)
	 */
	public void runCommand(String name, String message)
	{
		if (!name.split("/")[0].equalsIgnoreCase(prefix)) return;
		val newName = name.split("/")[1];
		if (commands.containsKey(newName)) {
			commands.get(newName).accept(message);
		}
	}


}
