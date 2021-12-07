package net.bdavies.fx.basic;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.util.FXUtil;
import net.bdavies.fx.BaseEffect;
import net.bdavies.fx.Effect;

/**
 * @author ben.davies
 */
@Slf4j
@Effect
public class RainbowCycle extends BaseEffect
{
	private final AtomicInteger counter = new AtomicInteger(0);
	public RainbowCycle(int pixelCount)
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
			if (counter.get() > 256) {
				counter.set(0);
			}
			int[] cols = new int[pixelCount];
			for (int i = 0; i < pixelCount; i++)
			{
				int index = (i * (16 << 4 / 29) / pixelCount) + counter.get();
				cols[i] = FXUtil.colorWheel(index);
			}

			sendRenderData(generateRenderCall(cols, false, true));
			counter.getAndIncrement();
		});
	}
}
