package net.bdavies.app;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.bdavies.api.IApplication;
import net.bdavies.api.IPacketReceiver;
import net.bdavies.api.config.IConfig;
import net.bdavies.api.strip.IStrip;
import net.bdavies.api.strip.StripMode;
import net.bdavies.api.strip.StripOperation;
import net.bdavies.app.config.Config;
import net.bdavies.app.strip.StripFactory;
import net.bdavies.env.Environment;
import net.bdavies.fx.EffectRegistry;
import net.bdavies.network.MQTTClient;
import net.bdavies.network.PacketReceiver;
import net.bdavies.network.RemoteStripServer;
import org.fusesource.jansi.Ansi;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.bdavies.api.util.TimingUtils.setTimeout;

/**
 * Main Application class for LedPi
 *
 * @author ben.davies
 */
@Slf4j
public class Application implements IApplication
{
	private final List<IStrip> strips = new ArrayList<>();
	//	private final Disposable gcDisposable;
	private final ExecutorService service = Executors.newCachedThreadPool();
	private final PacketReceiver packetReceiver;
	private final MQTTClient client;
	@Getter
	private final IConfig config;
	private RemoteStripServer stripServer;

	@SneakyThrows
	private Application()
	{
		File f = new File("config.json");
		if (!f.exists()) {
			log.error("Please create a config file called config.json");
			System.exit(-1);
		}

		Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown)); //Handle Force Shutdown
		Signal.handle(new Signal("INT"), sig -> this.shutdown()); //Handle SIGINT
		config = new GsonBuilder().create().fromJson(new FileReader(f), Config.class);
		log.trace("Starting the application");

		service.submit(EffectRegistry::findEffects);

		if (config.getApplicationConfig().isDebugMode()) {
			stripServer = new RemoteStripServer(config.getApplicationConfig().getDebugPort());
		}

		log.trace("Registering strips");
		Arrays.stream(config.getStripConfigurations()).forEach(sc ->
				strips.add(StripFactory.makeStrip(sc, stripServer)));

//		gcDisposable = Flux.interval(Duration.ofSeconds(10)).subscribe(l -> System.gc());

		log.trace("Setting up UDP Server");
		packetReceiver = new PacketReceiver(config.getNetworkConfig().getReactivePort());
		packetReceiver.subscribeToPackets(cp -> {
			val strip = strips.stream()
					.filter(s -> (byte)(s.getUID() & 0xFF) == cp.getStrip())
					.filter(s -> s.getMode() == StripMode.NETWORK_UDP)
					.filter(s -> s.getBrightness() > 0)
					.findFirst();
			strip.ifPresent(s -> s.setStripColors(cp.getColors()));
		});

		log.trace("Ensuring the udp is not wasting time processing unneeded packets");
		strips.forEach(s -> s.on(StripOperation.REACTIVE, Boolean.class).subscribe(b -> {
			if (b) {
				packetReceiver.listenForPackets();
			} else {
				if (strips.stream().anyMatch(str -> str.getMode().equals(StripMode.NETWORK_UDP))) {
					packetReceiver.listenForPackets();
				} else {
					packetReceiver.ignorePackets();
				}
			}
		}));

		client = new MQTTClient(this);

		if (Environment.isDebugDisplayEnabled)
		{
			log.trace("Setting up debugging displays");
			DisplayFactory.makeDisplay(this);
		}
	}

	public static void main(String[] args)
	{
		setupLogging();
		new Application();
	}

	/**
	 * Setup the logging for the application
	 */
	private static void setupLogging()
	{
		if (Environment.isTraceEnabled)
		{
			Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
			root.setLevel(Level.ALL);
			log.trace(Ansi.ansi().fgGreen().a("Enabling Trace because lp.trace is set").reset().toString());
		}
	}

	/**
	 * Get a copy of the application strips
	 *
	 * @return the strips
	 */
	@Override
	public List<IStrip> getStrips()
	{
		return new ArrayList<>(strips);
	}

	/**
	 * Shutdown the application and forcibly shutdown after 3 seconds
	 */
	@Override
	public void shutdown()
	{
		setTimeout(() -> {
			service.shutdown();
			client.shutdown();
			if (stripServer != null) {
				stripServer.dispose();
			}
			System.exit(0);
		}, 3500);
		packetReceiver.dispose();
		strips.forEach(IStrip::dispose);
//		gcDisposable.dispose();
		log.info("System will shutdown in 3 seconds...");
	}

	/**
	 * Get the app packet receiver
	 *
	 * @return the main pr
	 */
	@Override
	public IPacketReceiver getPacketReceiver()
	{
		return packetReceiver;
	}
}
