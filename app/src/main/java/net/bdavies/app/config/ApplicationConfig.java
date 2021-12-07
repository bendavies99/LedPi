package net.bdavies.app.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.config.IApplicationConfig;

/**
 * Configuration interface for the application
 *
 * @author ben.davies
 */
@Slf4j
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationConfig implements IApplicationConfig
{
    private String applicationName;
    private boolean debugMode = false;
    private int debugPort = 25561;
}
