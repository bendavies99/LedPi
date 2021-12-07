package net.bdavies.fx.basic;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.fx.Effect;
import net.bdavies.fx.SingleColorBaseEffect;

/**
 * @author ben.davies
 */
@Slf4j
@Effect
public class TheaterChase extends SingleColorBaseEffect
{
	private final AtomicInteger q = new AtomicInteger(0);
	/**
	 * Single color base effect
	 *
	 * @param pixelCount the number of pixels
	 */
	public TheaterChase(int pixelCount)
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
			 int[] cols = new int[pixelCount];
			 for (int j = 0; j < pixelCount; j += 3) {
				 int index = j + (q.get() - 1);
				 if (index < 0 || index > pixelCount - 1) continue;
				 cols[index] = Color.BLACK.getRGB();
			 }

			 if (q.get() >= 3) {
				 q.set(0);
			 }

			for (int j = 0; j < pixelCount; j += 3) {
				int index = j + q.get();
				if (index < 0 || index > pixelCount - 1) continue;
				cols[index] = getColor();
			}

			sendRenderData(generateRenderCall(cols, false, true));
			q.getAndIncrement();
		});
	}
}
