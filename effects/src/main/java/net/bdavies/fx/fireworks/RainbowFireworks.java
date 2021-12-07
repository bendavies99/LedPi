package net.bdavies.fx.fireworks;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.util.FXUtil;
import net.bdavies.fx.Effect;

/**
 * @author ben.davies
 */
@Slf4j
@Effect
public class RainbowFireworks extends FireworksBase
{
	/**
	 * Single color base effect
	 *
	 * @param pixelCount     the number of pixels
	 */
	public RainbowFireworks(int pixelCount)
	{
		super(pixelCount, FXUtil::colorWheel);
	}
}
