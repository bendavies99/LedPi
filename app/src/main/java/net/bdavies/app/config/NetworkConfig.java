package net.bdavies.app.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.config.INetworkConfig;

/**
 * The network part of the configuration file
 *
 * @author ben.davies
 */
@Slf4j
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NetworkConfig implements INetworkConfig
{
    private int reactivePort;
    private MQTTConfig mqtt;
}
