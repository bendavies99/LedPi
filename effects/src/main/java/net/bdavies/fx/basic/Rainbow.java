package net.bdavies.fx.basic;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.util.FXUtil;
import net.bdavies.fx.BaseEffect;
import net.bdavies.fx.Effect;

/**
 * The rainbow effect uses the color wheel in the fx util class to render a rainbow
 *
 * @author ben.davies
 */
@Slf4j
@Effect
public class Rainbow extends BaseEffect
{
	private final AtomicInteger counter = new AtomicInteger(0);

	/**
	 * @param pixelCount the num of leds
	 */
	public Rainbow(int pixelCount)
	{
		super(pixelCount);
	}

	/**
	 * Start the effect
	 */
	@Override
	public void start()
	{
		setupRenderer(50, () -> {
			if (counter.get() > 256)
			{
				counter.set(0);
			}
			int[] colors = new int[pixelCount];
			for (int i = 0; i < pixelCount; i++)
			{
				colors[i] = FXUtil.colorWheel(counter.get() + i);
			}

			sendRenderData(generateRenderCall(colors, false, true));
			counter.getAndIncrement();
		});
	}
}
