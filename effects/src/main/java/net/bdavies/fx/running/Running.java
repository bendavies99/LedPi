package net.bdavies.fx.running;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.fx.BaseEffect;

/**
 * Base running for the running implementation
 *
 * @author ben.davies
 */
@Slf4j
public abstract class Running extends BaseEffect
{
	private final AtomicInteger firstColor;
	private final AtomicInteger secondColor;
	private final AtomicInteger counter = new AtomicInteger(0);
	/**
	 * Single color base effect
	 *
	 * @param pixelCount the number of pixels
	 * @param firstColor The first color
	 * @param secondColor The second color
	 */
	protected Running(int pixelCount, int firstColor, int secondColor)
	{
		super(pixelCount);
		this.firstColor = new AtomicInteger(firstColor);
		this.secondColor = new AtomicInteger(secondColor);
	}

	/**
	 * Start the effect and this will begin any effect render loop
	 * or just pass a color to the subscriber
	 */
	@Override
	public void start()
	{
		setupRenderer(150, () -> {
			int c1 = (counter.get() & 2) > 0 ? firstColor.get() : secondColor.get();
			int c2 = (counter.get() & 2) > 0 ? secondColor.get() : firstColor.get();

			int[] cols = new int[pixelCount];
			for (int i = 0; i < pixelCount; i++) {
				if (i % 2 == 0) {
					cols[i] = c1;
				} else {
					cols[i] = c2;
				}
			}

			sendRenderData(generateRenderCall(cols, false, true));
			counter.set((counter.get() + 1) % pixelCount);
		});
	}
}
