package net.bdavies.network;

import java.time.Duration;
import java.util.Arrays;
import java.util.function.Consumer;

import org.reactivestreams.Publisher;

import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.bdavies.api.IPacketReceiver;
import reactor.core.publisher.Sinks;
import reactor.netty.Connection;
import reactor.netty.udp.UdpInbound;
import reactor.netty.udp.UdpOutbound;
import reactor.netty.udp.UdpServer;

/**
 * The class that handles getting the packets from an external
 * source so it can change how the strip pixel data used mainly for the reactive mode
 *
 * @author ben.davies
 */
@Slf4j
public class PacketReceiver implements IPacketReceiver
{
	private final Connection connection;
	private final Sinks.Many<ColorPacket> buffer = Sinks.many().unicast().onBackpressureBuffer();
	private boolean listenForData = false;

	/**
	 * Make the packet receiver
	 *
	 * @param port the port to listen on
	 */
	public PacketReceiver(int port)
	{
		connection = UdpServer.create().host("0.0.0.0").port(port).handle(this::handleInOutStream).bindNow();
	}

	/**
	 * Handle all the data passed to the server
	 *
	 * @param in the input
	 * @param out the output
	 * @return a null publisher
	 */
	private Publisher<Void> handleInOutStream(UdpInbound in, UdpOutbound out)
	{
		return in.receiveObject().mapNotNull(o -> {
			if (!listenForData)
				return null;
			if (o instanceof DatagramPacket)
			{
				val p = (DatagramPacket)o;
				byte[] bytes = new byte[p.content().readableBytes()];
				p.content().getBytes(0, bytes);
				handlePacket(bytes);
			}
			return null;
		}).cast(Void.class);
	}

	/**
	 * Handle the packet passed to the sever and parse the colors into a color packet
	 *
	 * @param bytes the bytes sent
	 * @see ColorPacket
	 */
	private void handlePacket(byte[] bytes)
	{
		int id = bytes[0];
		int[] colors = new int[bytes.length - 1];
		Arrays.fill(colors, 0xFF000000);
		for (int i = 1; i < bytes.length - 1; i += 3)
		{
			byte r = (byte) (bytes[i] & 0xFF);
			byte g = (byte) (bytes[i + 1] & 0xFF);
			byte b = (byte) (bytes[i + 2] & 0xFF);
			int c = (0xFF << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
			colors[(int) Math.floor((float) i / 3.0f)] = c;
		}
		buffer.emitNext(new ColorPacket((byte) (id & 0xFF), colors), Sinks.EmitFailureHandler.FAIL_FAST);
	}

	/**
	 * Subscribe to the packets coming into the server and handle them accordingly
	 *
	 * @param packet the packet consumer
	 */
	public void subscribeToPackets(Consumer<ColorPacket> packet)
	{
		buffer.asFlux().subscribe(packet);
	}

	/**
	 * Start listening for packets
	 */
	public void listenForPackets()
	{
		this.listenForData = true;
	}

	/**
	 * Start ignoring packets
	 */
	public void ignorePackets()
	{
		this.listenForData = false;
	}

	/**
	 * Shutdown the server
	 */
	public void dispose()
	{
		log.info("Packet receiver is shutting down");
		connection.disposeNow(Duration.ZERO);
	}
}
