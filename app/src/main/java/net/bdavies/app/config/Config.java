package net.bdavies.app.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.config.IConfig;
import net.bdavies.app.strip.StripConfig;

/**
 * The Main config interface that connects all the config data together
 *
 * @author ben.davies
 */
@Slf4j
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Config implements IConfig
{
    private ApplicationConfig applicationConfig;
    private StripConfig[] stripConfigurations;
    private NetworkConfig networkConfig;
}
