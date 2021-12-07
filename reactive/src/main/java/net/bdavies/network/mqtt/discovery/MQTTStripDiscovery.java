package net.bdavies.network.mqtt.discovery;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.config.IStripConfig;
import net.bdavies.fx.EffectRegistry;

/**
 * Home assistant Strip discovery config
 *
 * @author ben.davies
 */
@Slf4j
@Getter
@ToString
public class MQTTStripDiscovery
{
    private final String stat_t, cmd_t, rgb_stat_t, unique_id;
		private final String rgb_cmd_t, bri_cmd_t, bri_stat_t, bri_val_t;
		private final String rgb_cmd_tpl, rgb_val_tpl;
		private final int qos;
		private final boolean opt;
		private final String pl_on, pl_off, fx_cmd_t, fx_stat_t, fx_val_tpl;
		private final String[] fx_list;
		private final String name;

		public MQTTStripDiscovery(IStripConfig config) {
			String deviceTopic = config.getName().replace(" ", "_");
			name = config.getName();
			stat_t = deviceTopic + "/s";
			cmd_t = deviceTopic + "/state";
			rgb_stat_t = deviceTopic + "/c";
			rgb_cmd_t = deviceTopic + "/col";
			bri_stat_t = deviceTopic + "/b";
			bri_cmd_t = deviceTopic + "/bri";
			bri_val_t = "{{value}}";
			rgb_cmd_tpl = "{{'#%02x%02x%02x' | format(red, green, blue)}}";
			rgb_val_tpl = "{{value[1:3]|int(base=16)}},{{value[3:5]|int(base=16)}},{{value[5:7]|int(base=16)}}";
			unique_id = config.getName().replace(" ", "-") + "-" + config.getUid();
			qos = 0;
			opt = true;
			pl_on = "ON";
			pl_off = "OFF";
			fx_cmd_t = deviceTopic + "/sfx";
			fx_stat_t = deviceTopic + "/fx";
			fx_val_tpl = "{{value}}";
			fx_list = EffectRegistry.getEffectNames().toArray(new String[0]);
		}
}
