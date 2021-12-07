package net.bdavies.fx.running;

import java.awt.*;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.fx.Effect;

/**
 * @author ben.davies
 */
@Slf4j
@Effect
public class Halloween extends Running
{
	/**
	 * Single color base effect
	 *
	 * @param pixelCount  the number of pixels
	 */
	public Halloween(int pixelCount)
	{
		super(pixelCount, new Color(48, 1, 37).getRGB(), Color.ORANGE.getRGB());
	}
}
