package net.bdavies.network;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Packet for the sending the colors down the line
 *
 * @author ben.davies
 */
@Slf4j
@Data
public class ColorPacket
{
	private final byte strip;
	private final int[] colors;
}
