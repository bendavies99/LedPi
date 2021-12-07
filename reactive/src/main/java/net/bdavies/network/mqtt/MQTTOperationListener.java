package net.bdavies.network.mqtt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.bdavies.api.util.FXUtil;
import net.bdavies.network.mqtt.command.BaseCommandRegister;
import net.bdavies.network.mqtt.command.StripCommandRegister;
import net.bdavies.network.mqtt.command.SystemCommandRegister;
import net.bdavies.network.mqtt.discovery.MQTTStripDiscovery;
import net.bdavies.network.mqtt.discovery.MQTTSystemDiscovery;

/**
 * Handle Commands and sending data out
 *
 * @author ben.davies
 */
@Slf4j
public class MQTTOperationListener implements MqttCallback
{
	private final ConnectListener connectListener;
	private final List<BaseCommandRegister> commandRegisters = new ArrayList<>();

	/**
	 * Make the operation listen
	 *
	 * @param connectListener {@link ConnectListener}
	 */
	public MQTTOperationListener(ConnectListener connectListener)
	{
		this.connectListener = connectListener;
		commandRegisters.add(new SystemCommandRegister(connectListener.getApplication()));
		log.info("Sending system discovery");
		new MQTTSystemDiscovery(connectListener.getApplication(), connectListener);
		connectListener.getApplication().getStrips().forEach(s -> {
			    val scr = new StripCommandRegister(connectListener.getApplication(), s);
					commandRegisters.add(scr);
					Arrays.stream(connectListener.getApplication().getConfig().getStripConfigurations())
							.filter(c -> c.getName().equalsIgnoreCase(s.getName())).findFirst().ifPresent(sc -> {
								MQTTStripDiscovery discovery = new MQTTStripDiscovery(sc);
								if (connectListener.getClient().isConnected()) {
									val topic = "homeassistant/light/" + discovery.getUnique_id() + "/config";
									try
									{
										log.info("Sent HA Discovery at topic: {} and data: {}", topic, discovery);
										connectListener.getClient().publish(topic,
												new GsonBuilder().create().toJson(discovery).getBytes(), 2, true);
										//This will ensure home assistant is upto date with the data
										connectListener.getClient().publish(scr.getPrefix() + "/s",
												"ON".getBytes(), 0, false);
										connectListener.getClient().publish(scr.getPrefix() + "/fx",
												"solid".getBytes(), 0, false);
										connectListener.getClient().publish(scr.getPrefix() + "/b",
												"100".getBytes(), 0, false);
										connectListener.getClient().publish(scr.getPrefix() + "/c",
												FXUtil.colorToHex(s.getEffectColor()).getBytes(), 0, false);
									}
									catch (MqttException e)
									{
										log.error("Something went wrong", e);
									}
								}
							});
				});

		log.info(commandRegisters.toString());

		commandRegisters.forEach(cr -> {
			cr.getPublishers().forEach((k, v) -> {
				log.trace("Registering publisher: {}", cr.getPrefix() + "/" + k);
				v.subscribe(s -> {
					try
					{
						if (connectListener.getClient().isConnected())
						{
							connectListener.getClient().publish(cr.getPrefix() + "/" + k, s.getBytes(), 0, false);
						}
					}
					catch (MqttException e)
					{
						log.error("Something went wrong", e);
					}
				});
			});
		});
	}

	/**
	 * Subscribe and listen to the command topics to handle all the commands that the
	 * outside worlds wants this Client to act on
	 */
	public void subscribeToCommands() {
		commandRegisters.forEach(cr -> {
			cr.getCommandNames().forEach(cn -> {
				try
				{
					if (connectListener.getClient().isConnected())
					{
						log.trace("Registering command: {}", cr.getPrefix() + "/" + cn);
						connectListener.getClient().subscribe(cr.getPrefix() + "/" + cn, 0);
					}
				}
				catch (MqttException e)
				{
					log.error("Something went wrong", e);
				}
			});
		});
	}

	/**
	 * This method is called when the connection to the server is lost.
	 *
	 * @param cause the reason behind the loss of connection.
	 */
	@Override
	public void connectionLost(Throwable cause)
	{
		log.error("Connection lost to MQTT Client", cause);
		connectListener.onFailure(null, cause);
	}

	/**
	 * This method is called when a message arrives from the server.
	 *
	 * <p>
	 * This method is invoked synchronously by the MQTT client. An
	 * acknowledgment is not sent back to the server until this
	 * method returns cleanly.</p>
	 * <p>
	 * If an implementation of this method throws an <code>Exception</code>, then the
	 * client will be shut down.  When the client is next re-connected, any QoS
	 * 1 or 2 messages will be redelivered by the server.</p>
	 * <p>
	 * Any additional messages which arrive while an
	 * implementation of this method is running, will build up in memory, and
	 * will then back up on the network.</p>
	 * <p>
	 * If an application needs to persist data, then it
	 * should ensure the data is persisted prior to returning from this method, as
	 * after returning from this method, the message is considered to have been
	 * delivered, and will not be reproducible.</p>
	 * <p>
	 * It is possible to send a new message within an implementation of this callback
	 * (for example, a response to this message), but the implementation must not
	 * disconnect the client, as it will be impossible to send an acknowledgment for
	 * the message being processed, and a deadlock will occur.</p>
	 *
	 * @param topic   name of the topic on the message was published to
	 * @param message the actual message.
	 */
	@Override
	public void messageArrived(String topic, MqttMessage message)
	{
		log.debug("Received message: {} payload: {}", topic, new String(message.getPayload()));
		for (BaseCommandRegister cr : commandRegisters)
		{
			if (!topic.startsWith(cr.getPrefix())) continue;
			cr.runCommand(topic, new String(message.getPayload()));
		}
	}

	/**
	 * Called when delivery for a message has been completed, and all
	 * acknowledgments have been received. For QoS 0 messages it is
	 * called once the message has been handed to the network for
	 * delivery. For QoS 1 it is called when PUBACK is received and
	 * for QoS 2 when PUBCOMP is received. The token will be the same
	 * token as that returned when the message was published.
	 *
	 * @param token the delivery token associated with the message.
	 */
	@Override
	public void deliveryComplete(IMqttDeliveryToken token)
	{

	}
}
