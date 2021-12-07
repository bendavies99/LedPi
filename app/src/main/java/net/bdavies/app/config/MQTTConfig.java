package net.bdavies.app.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.config.IMQTTConfig;

/**
 * The MQTT Configuration settings
 *
 * @author ben.davies
 */
@Slf4j
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MQTTConfig implements IMQTTConfig
{
    private String host;
    private String username = null;
    private String password = null;
    private int port = 1883;
}
