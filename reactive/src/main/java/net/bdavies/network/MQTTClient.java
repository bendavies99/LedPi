package net.bdavies.network;

import java.util.Random;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.bdavies.api.IApplication;
import net.bdavies.network.mqtt.ConnectListener;

/**
 * The MQTT Client class that handles all commands to the strips
 *
 * @author ben.davies
 */
@Slf4j
public class MQTTClient
{
	private final IMqttAsyncClient client;

	@SneakyThrows
	public MQTTClient(IApplication application) {
		String publisherId = "LedPiClient-" + new Random().nextInt(202653);
		val mqtt = application.getConfig().getNetworkConfig().getMqtt();
		client = new MqttAsyncClient("tcp://"+mqtt.getHost()+":"+mqtt.getPort(), publisherId, new MemoryPersistence());
		val options = new MqttConnectOptions();
		if (mqtt.getUsername() != null && !mqtt.getUsername().isEmpty()) {
			options.setUserName(mqtt.getUsername());
			if (mqtt.getPassword() != null && !mqtt.getPassword().isEmpty()) {
				options.setPassword(mqtt.getPassword().toCharArray());
			}
		}
		options.setCleanSession(true);
		options.setAutomaticReconnect(true);
		options.setKeepAliveInterval(10);
		options.setConnectionTimeout(5);
		client.connect(options, this, new ConnectListener(client, options, application));
	}

	@SneakyThrows
	public void shutdown() {
		if (client.isConnected())
		{
			client.disconnect();
		}
	}
}
