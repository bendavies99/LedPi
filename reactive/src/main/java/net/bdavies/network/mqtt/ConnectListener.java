package net.bdavies.network.mqtt;

import static net.bdavies.api.util.TimingUtils.setTimeout;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.bdavies.api.IApplication;
import net.bdavies.api.effects.IEffect;
import net.bdavies.fx.basic.Solid;
import net.bdavies.fx.internal.Connecting;
import net.bdavies.network.MQTTClient;

/**
 * The connection listener for the MQTT Client this will change the effect on the strip accordingly based on if the
 * client has connected or not and will attempt the reconnect cycle
 *
 * @author ben.davies
 */
@Slf4j
@RequiredArgsConstructor
public class ConnectListener implements IMqttActionListener
{
    @Getter
    private final IMqttAsyncClient client;
    private final MqttConnectOptions options;
    @Getter
    private final IApplication application;
    private final MQTTClient clientImpl;
    private int connectAttempts = 1;
    private final Map<Integer, IEffect> previousEffects = new HashMap<>();
    private final Map<Integer, Integer> previousBrightnesses = new HashMap<>();
    private MQTTOperationListener listener;

    /**
     * This method is invoked when an action has completed successfully.
     *
     * @param asyncActionToken associated with the action that has completed
     */
    @Override
    public void onSuccess(IMqttToken asyncActionToken)
    {
        try
        {
            log.info("MQTT Server connected!!!");

            for (int i = 0; i < application.getStrips().size(); i++)
            {
                val s = application.getStrips().get(i);
                Connecting c = (Connecting) s.getEffect();
                c.success();
                int finalI = i;
                setTimeout(() -> {
                    if (previousEffects.containsKey(finalI))
                    {
                        s.setEffect(previousEffects.get(finalI).getClass(),
                            previousEffects.get(finalI).getCurrentConfig());
                        s.setBrightness(previousBrightnesses.get(finalI));
                    } else {
                        s.setEffect(Solid.class);
                        s.setBrightness(150); //Starting brightness
                    }
                }, 350 * 5);
            }
            connectAttempts = 0;
            if (listener == null)
            {
                listener = new MQTTOperationListener(this);
            }
            client.setCallback(listener);
            listener.subscribeToCommands();
        } catch (Exception e) {
            log.error("Something went wrong", e);
        }
    }

    /**
     * This method is invoked when an action fails.
     * If a client is disconnected while an action is in progress
     * onFailure will be called. For connections
     * that use cleanSession set to false, any QoS 1 and 2 messages that
     * are in the process of being delivered will be delivered to the requested
     * quality of service next time the client connects.
     *
     * @param asyncActionToken associated with the action that has failed
     * @param exception        thrown by the action that has failed
     */
    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception)
    {
        if (clientImpl.isShutdown()){
            System.exit(0);
            return;
        }
        try
        {
            options.setConnectionTimeout(1);
            options.setAutomaticReconnect(false);
            options.setCleanSession(true);
            log.debug("Failed to connect to the MQTT Server Retrying in 5 seconds! Attempts made: {}", connectAttempts);
            for (int i = 0; i < application.getStrips().size(); i++)
            {
                val s = application.getStrips().get(i);
                if (s.getBrightness() > 0)
                {
                    //Only do this if it is on
                    if (s.getEffect() instanceof Connecting)
                    {
                        ((Connecting) s.getEffect()).fail();
                    }
                    else
                    {
                        previousBrightnesses.put(i, s.getBrightness());
                        previousEffects.put(i, s.getEffect());
                        s.setEffect(Connecting.class);
                        Connecting c = (Connecting) s.getEffect();
                        c.fail();
                        previousEffects.get(i).stop();
                        s.setBrightness(255);
                    }
                }
            }
            setTimeout(() -> {
                try
                {
                    if (!client.isConnected())
                    {
                        connectAttempts++;
                        client.connect(options, null, this);
                    }
                }
                catch (MqttException e)
                {
                    log.error("Something went wrong", e);
                }
            }, 5000);
        } catch (Exception e) {
            log.error("Something went wrong", e);
        }
    }
}
