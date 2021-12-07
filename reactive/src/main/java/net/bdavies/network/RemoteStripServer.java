package net.bdavies.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.bdavies.api.config.IApplicationConfig;
import reactor.core.publisher.Sinks;

/**
 * The server class for sending data out to clients connected to the debug server
 *
 * @see IApplicationConfig#isDebugMode()
 *
 * @author ben.davies
 */
@Slf4j
public class RemoteStripServer
{
	private final DatagramSocket socket;
	private final Sinks.Many<byte[]> buffer = Sinks.many().unicast().onBackpressureBuffer();
	private final ExecutorService service = Executors.newCachedThreadPool();
	private boolean running;
	private final List<RemoteClient> clients = new ArrayList<>();

	/**
	 * Make the new UDP Server and start listening for packets
	 *
	 * @param port the port to listen on {@link IApplicationConfig#getDebugPort()}
	 */
	@SneakyThrows
	public RemoteStripServer(int port)
	{
		log.debug("Starting Remote Strip server on port: {}", port);
		socket = new DatagramSocket(port);
		running = true;
		new Thread(() -> {
			while (running) {
				byte[] data = new byte[2];
				DatagramPacket packet = new DatagramPacket(data, data.length);
				try
				{
					socket.receive(packet);
					service.submit(() -> handlePacket(packet));
				}
				catch (IOException e)
				{
					log.error("Something went wrong", e);
				}
			}
		}, "RemoteServerStrip").start();
		buffer.asFlux().subscribe(bytes -> {
			synchronized (clients)
			{
				clients.forEach(c -> service.submit(() -> {
					val packet = new DatagramPacket(bytes, bytes.length, c.getAddress(), c.getPort());
					try
					{
						socket.send(packet);
					}
					catch (IOException e)
					{
						log.error("Something went wrong", e);
					}
				}));
			}
		});
	}

	/**
	 * Handle a packet come into the server and connect / disconnect clients
	 *
	 * @param packet the new packet
	 */
	private synchronized void handlePacket(DatagramPacket packet)
	{
		String s = new String(packet.getData());
		if (s.equalsIgnoreCase("ic")) {
			connectClient(packet.getAddress(), packet.getPort());
		} else if (s.equalsIgnoreCase("id")){
			disconnectClient(packet.getAddress(), packet.getPort());
		}
	}

	/**
	 * Disconnect a client from the server
	 *
	 * @param address the client address
	 * @param port the client port
	 */
	private synchronized void disconnectClient(InetAddress address, int port)
	{
		clients.stream().filter(rc -> rc.getAddress().equals(address) && rc.getPort() == port)
				.findFirst().ifPresent(rc -> {
					log.info("Remote client disconnected: {} : {}", address, port);
					clients.remove(rc);
				});
	}

	/**
	 * Connect a client to the server
	 *
	 * @param address the client address
	 * @param port the client port
	 */
	private synchronized void connectClient(InetAddress address, int port)
	{
		if (clients.stream().noneMatch(rc -> rc.getAddress().equals(address) && rc.getPort() == port))
		{
			log.info("Remote client connected: {} : {}", address, port);
			clients.add(new RemoteClient(address, port));
		}
	}

	/**
	 * Send render data to all the clients connected to the debug server
	 *
	 * @param uid the uid of the strip
	 * @param colors the colors
	 */
	public void sendRenderData(int uid, int[] colors) {
		if (clients.size() == 0) return;
		byte[] bytes = new byte[(colors.length * 3) + 1];
		bytes[0] = (byte) (uid & 0xFF);
		for (int i = 1; i < bytes.length; i+= 3)
		{
			int colIndex = ((i - 1)/3);
			bytes[i] = (byte) ((colors[colIndex] >> 16) & 0xFF);
			bytes[i + 1] = (byte) ((colors[colIndex] >> 8) & 0xFF);
			bytes[i + 2] = (byte) ((colors[colIndex]) & 0xFF);
		}

		buffer.emitNext(bytes, Sinks.EmitFailureHandler.FAIL_FAST);
	}

	/**
	 * Shutdown the server
	 */
	public void dispose()
	{
		log.info("Remote strip server is shutting down");
		running = false;
		socket.close();
		service.shutdown();
	}
}
