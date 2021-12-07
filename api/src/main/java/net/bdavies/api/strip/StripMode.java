package net.bdavies.api.strip;

import lombok.extern.slf4j.Slf4j;

/**
 * The mode that the strip will run in this will be
 * handled by the strip handler such as the Strip.class in Application
 *
 * @author ben.davies
 */
@Slf4j
public enum StripMode
{
	EFFECTS, //Run the effect system
	NETWORK_UDP //Run the Network mode which will look for packets
}
