package net.bdavies.fx.fireworks;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.fx.Effect;

/**
 * @author ben.davies
 */
@Slf4j
@Effect
public class Fireworks extends FireworksBase
{
	/**
	 * Single color base effect
	 *
	 * @param pixelCount     the number of pixels
	 */
	public Fireworks(int pixelCount)
	{
		super(pixelCount, null);
		colRetriever = i -> getColor();
	}
}
