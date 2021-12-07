package net.bdavies.fx.basic;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.fx.BaseEffect;
import net.bdavies.fx.Effect;

/**
 * @author ben.davies
 */
@Slf4j
@Effect
public class ColorWipe extends BaseEffect
{
	private final AtomicInteger counter = new AtomicInteger(0);
	private final AtomicInteger currentColor = new AtomicInteger(0xFFFF0000);

	public ColorWipe(int pixelCount)
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
		 setupRenderer(50, pr -> {
			 if (counter.get() >= pixelCount) {
				 counter.set(0);
				 if (currentColor.get() == 0xFFFF0000) {
					 currentColor.set(0xFF00FF00);
				 } else if (currentColor.get() == 0xFF00FF00) {
					 currentColor.set(0xFF0000FF);
				 } else if (currentColor.get() == 0xFF0000FF) {
					 currentColor.set(0xFFFF0000);
				 }
			 }

			 pr[counter.get()] = currentColor.get();
			 sendRenderData(generateRenderCall(pr, false, true));
			 counter.getAndIncrement();
		 });
	}
}
