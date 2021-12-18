package net.bdavies.app.strip;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import com.github.mbelling.ws281x.Color;
import com.github.mbelling.ws281x.LedStripType;
import com.github.mbelling.ws281x.Ws281xLedStrip;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.config.IStripConfig;
import net.bdavies.app.Strip;

/**
 * The main Native Strip implementation which uses the
 * rpi_ws281x_java library and will connect to a strip through the GPIO ports on a RPi
 *
 * @see Ws281xLedStrip
 * @author ben.davies
 */
@Slf4j
public class NativeStrip extends Strip
{
	private final AtomicReference<Ws281xLedStrip> strip = new AtomicReference<>(null);
	private int[] previousRender;
	private int previousBrightness = 255;
	private static int DMA_CHANNEL = 10;

	/**
	 * Construct a native Strip
	 *
	 * @param config The config for the strip
	 */
	public NativeStrip(IStripConfig config)
	{
		super(config.getName(), config.getLedCount(), config.getUid());
		try
		{
			strip.set(new Ws281xLedStrip(getPixelCount(), config.getPinNumber(), 800000, DMA_CHANNEL,
					getBrightness(), getPwmChannel(config.getPinNumber()), false,
					LedStripType.WS2811_STRIP_GRB, true));
			DMA_CHANNEL++;
			if (DMA_CHANNEL > 14) DMA_CHANNEL = 14;
			log.info("Setup a native strip on pin: {}", config.getPinNumber());
		} catch (UnsatisfiedLinkError e) {
			log.error("Strip cannot load on this machine: {} you will need to fix to continue", getName(), e);
			System.exit(-1);
		}
	}

	/**
	 * Get the PWM Channel based on the pin number supplied to the strip
	 *
	 * @param pinNumber The pin number
	 * @return 0 or 1
	 */
	private int getPwmChannel(int pinNumber)
	{
		return (pinNumber == 18 || pinNumber == 12 || pinNumber == 10 || pinNumber == 9 || pinNumber == 21) ? 0 : 1;
	}

	/**
	 * Render the colors to the native strip back-end
	 */
	@Override
	public synchronized void render()
	{
		synchronized (strip) {
			if (previousRender == null) {
				previousRender = new int[getPixelCount()];
				Arrays.fill(previousRender, 0x00FF00FF);
			}
			if (strip.get() != null)
			{
				for (int i = 0; i < getPixelCount(); i++)
				{
					if (getColorAtPixel(i) != previousRender[i])
					{
						strip.get().setPixel(i, new Color(getColorAtPixel(i)));
					}
				}
				if (previousBrightness != getBrightness())
				{
					strip.get().setBrightness(getBrightness());
				}
				strip.get().render();
			}

			previousRender = Arrays.copyOf(getColors(), getPixelCount());
			previousBrightness = getBrightness();
		}
	}
}
