package net.bdavies.fx.basic;

import static net.bdavies.api.util.TimingUtils.setTimeout;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.util.FXUtil;
import net.bdavies.fx.Effect;
import net.bdavies.fx.SingleColorBaseEffect;

/**
 *
 * @author ben.davies
 */
@Slf4j
@Effect
public class Breath extends SingleColorBaseEffect implements Runnable
{
	private final AtomicInteger counter = new AtomicInteger(0);
	private final AtomicBoolean running = new AtomicBoolean(false);
	private final AtomicInteger delay = new AtomicInteger(50);
	/**
	 * Single color base effect
	 *
	 * @param pixelCount the number of pixels
	 */
	public Breath(int pixelCount)
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
		running.set(true);
		setTimeout(this, delay.get());
	}

	/**
	 * Stop the current effect which will dispose the current
	 * renderer if it is being used
	 */
	@Override
	public void stop()
	{
		super.stop();
		running.set(false);
	}

	/**
	 * When an object implementing interface {@code Runnable} is used
	 * to create a thread, starting the thread causes the object's
	 * {@code run} method to be called in that separately executing
	 * thread.
	 * <p>
	 * The general contract of the method {@code run} is that it may
	 * take any action whatsoever.
	 *
	 * @see Thread#run()
	 */
	@Override
	public void run()
	{
		int lum = counter.get();
		if (lum > 255) lum = 511 - lum;

		if (lum == 15) delay.set(970);
		else if (lum <= 25) delay.set(38);
		else if (lum <= 50) delay.set(36);
		else if (lum <= 75) delay.set(75);
		else if (lum <= 100) delay.set(20);
		else if (lum <= 125) delay.set(14);
		else if (lum <= 150) delay.set(11);
		else delay.set(10);

		int[] newCols = genColArray(FXUtil.colorBlend(getColor(), Color.BLACK.getRGB(), lum));
		sendRenderData(generateRenderCall(newCols, false, true));
		counter.set(counter.get() + 2);
		if (counter.get() > 497) {
			counter.set(15);
		}

		if (running.get()) {
			setTimeout(this, delay.get());
		}
	}
}
