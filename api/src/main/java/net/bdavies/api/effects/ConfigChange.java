package net.bdavies.api.effects;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * A record for a config change event to the effect
 *
 * @author ben.davies
 */
@Slf4j
@Data
public class ConfigChange
{
	private final String configName;
	private final Object value;
}
