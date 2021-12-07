package net.bdavies.network.mqtt.discovery;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.GsonBuilder;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.bdavies.api.IApplication;
import net.bdavies.network.mqtt.ConnectListener;

/**
 * Home assistant Strip discovery config
 *
 * @author ben.davies
 */
@Slf4j
@Getter
public class MQTTSystemDiscovery
{

    @SneakyThrows
    public MQTTSystemDiscovery(IApplication application, ConnectListener listener) {
        val sysName = application.getConfig().getApplicationConfig().getApplicationName();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("device_class", "current");
        data.put("unit_of_measurement", "");
        data.put("state_topic", sysName + "/res");


        if (listener.getClient().isConnected()) {
            //Handle Memory
            data.put("name", sysName + " Memory Usage");
            data.put("value_template", "{{ 'Free: ' + value_json.memory.free + ' - Used: ' + value_json.memory.used }}");
            data.put("unique_id", sysName + "-memory-res");

            val memStr = new GsonBuilder().create().toJson(data);
            val topic = "homeassistant/sensor/"+sysName.replace(" ", "-")+"-res-memory/config";
            log.info("Sending memory dis at topic: {}", topic);
            listener.getClient().publish(topic, memStr.getBytes(), 2, true);

            //Handle CPU
            data.put("name", sysName + " CPU Usage");
            data.put("unique_id", sysName + "-cpu-res");
            data.put("value_template", "{{ 'Process: ' + value_json.cpu.process + '% - System: ' + value_json.cpu.system + '%' }}");

            val cpuStr = new GsonBuilder().create().toJson(data);
            val cpuTopic = "homeassistant/sensor/"+sysName.replace(" ", "-")+"-res-cpu/config";
            log.info("Sending cpu dis at topic: {}", cpuTopic);
            listener.getClient().publish(cpuTopic, cpuStr.getBytes(), 2, true);
        }

    }
}
