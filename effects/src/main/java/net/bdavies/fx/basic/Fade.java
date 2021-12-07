package net.bdavies.fx.basic;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.util.FXUtil;
import net.bdavies.fx.Effect;
import net.bdavies.fx.SingleColorBaseEffect;

/**
 * @author ben.davies
 */
@Slf4j
@Effect
public class Fade extends SingleColorBaseEffect
{
	private final AtomicInteger counter = new AtomicInteger();
	/**
	 * Single color base effect
	 *
	 * @param pixelCount the number of pixels
	 */
	public Fade(int pixelCount)
	{
		super(pixelCount);
	}

	/**
	 * Start the effect and this will begin any effect render loop
	 * or just pass a color to the subscriber
	 */
	@Override
	public void start()
	{
		setupRenderer(50, () -> {
			int lum = counter.get();
			if (lum > 255) lum = 511 - lum;
			int[] cols = genColArray(FXUtil.colorBlend(getColor(), Color.BLACK.getRGB(), lum));
			sendRenderData(generateRenderCall(cols, false, true));
			counter.set(counter.get() + 4);
			if (counter.get() > 511) {
				counter.set(0);
			}
		});
	}
}
