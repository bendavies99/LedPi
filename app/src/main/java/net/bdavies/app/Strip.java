package net.bdavies.app;

import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.bdavies.api.effects.IEffect;
import net.bdavies.api.effects.RenderCall;
import net.bdavies.api.strip.IStrip;
import net.bdavies.api.strip.StripMode;
import net.bdavies.api.strip.StripOperation;
import net.bdavies.api.util.FXUtil;
import net.bdavies.fx.internal.Connecting;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;
import reactor.core.publisher.Sinks;

/**
 * The base LED Strip class to be extended by the different type of strip based on the
 * {@link net.bdavies.app.strip.StripFactory}
 *
 * @author ben.davies
 */
@Slf4j
@Data
public abstract class Strip implements IStrip
{
	private static final int STARTING_BRIGHTNESS = 255;
	private final String name;
	private final int pixelCount;
	private int[] colors;
	private IEffect currentEffect = null;
	private int brightness = STARTING_BRIGHTNESS;
	private int savedBrightness = STARTING_BRIGHTNESS;
	private final ExecutorService service = Executors.newCachedThreadPool();
	private Disposable currentEffectDs = null;
	private final int uId;
	private StripMode mode = StripMode.EFFECTS;
	private int effectColor = 0xFF0000AA;
	private boolean isOn = true;
	private Sinks.Many<StripChange> sink = Sinks.many().multicast().onBackpressureBuffer(10);

	static
	{
		//noinspection ResultOfMethodCallIgnored
		FXUtil.colorWheel(0);
	}

	/**
	 * Construct a LED Strip and setup everything
	 *
	 * @param name       The name of LED Strip
	 * @param pixelCount The count of leds
	 * @param uId        The unique id for the strip
	 */
	public Strip(String name, int pixelCount, int uId)
	{
		this.name = name;
		this.pixelCount = pixelCount;
		this.colors = new int[pixelCount];
		this.uId = uId;
		Arrays.fill(this.colors, 0xFF000000);
		setEffect(Connecting.class);
		render();
	}

	/**
	 * Get a color of a pixel at a given index
	 *
	 * @param index The index to get the color at
	 * @return color A(24-32bit)(no effect) R(16-24bit) G(8-16bit) B(0-8bit)
	 */
	@Override
	public int getColorAtPixel(int index)
	{
		return colors[index];
	}

	/**
	 * Set a color of a pixel at a given index
	 *
	 * @param index The index to set the color at
	 * @param col   color A(24-32bit)(no effect) R(16-24bit) G(8-16bit) B(0-8bit)
	 */
	@Override
	public void setColorAtPixel(int index, int col)
	{
		colors[index] = col;
		render();
	}

	/**
	 * Set the color array for the LED Strip
	 *
	 * @param colors a list of colors
	 */
	@Override
	public void setStripColors(List<Integer> colors)
	{
		this.colors = colors.stream().mapToInt(Integer::intValue).toArray();
		render();
	}

	/**
	 * Set the color array for the LED Strip
	 *
	 * @param colors a list of colors
	 */
	@Override
	public void setStripColors(int[] colors)
	{
		if (colors.length != pixelCount) {
			colors = Arrays.copyOf(colors, pixelCount);
		}
		this.colors = colors;
		render();
	}

	/**
	 * Handle a render call from an effect this can be manipulated by the
	 * LED strip implementation if needed be
	 *
	 * @param call The render call to process
	 */
	protected void handleRenderCall(RenderCall call)
	{
		val scs = call.getPixelData();
		int[] colors = call.isBlankSlate() ? new int[pixelCount] : Arrays.copyOf(this.colors, pixelCount);
		for (int i = 0; i < call.getPixelCount(); i++)
		{
			int currIdx = i * 3;
			int pixel = scs[currIdx];
			short r = (short) (((scs[currIdx + 1] & 0xFFFF) >> 8) & 0xFF);
			short g = (short) (((scs[currIdx + 1] & 0xFFFF)) & 0xFF);
			colors[pixel] = ((byte) 0xFF & 0xFF << 24) | (r << 16) | (g << 8) | ((byte) scs[currIdx + 2] & 0xFF);
		}
		if (call.getPixelCount() == pixelCount)
		{
			boolean allTheSame = Arrays.stream(colors).allMatch(s -> s == colors[0]);
			if (allTheSame && call.isBlend())
			{
				//Blend the colour
				blendStripColor(colors[0]);
			}
			else
			{
				setStripColors(colors);
			}
		}
		else
		{
			setStripColors(colors);
		}
	}

	/**
	 * Set the main effect color that will set the default c1
	 * for the effect is not one is set
	 *
	 * @param color the new color
	 */
	@Override
	public void setEffectColor(int color)
	{
		effectColor = color;
		if (currentEffect != null)
		{
			currentEffect.updateConfig("c1", color);
		}
		val currentColor = new Color(color);
		val bits = String.format("#%02x%02x%02x",currentColor.getRed(),currentColor.getGreen(),
				currentColor.getBlue());
		sink.emitNext(new StripChange(StripOperation.EFFECT_COLOR, bits), FAIL_FAST);
	}

	/**
	 * Blend the current strip from one color to another
	 *
	 * @param c color A(24-32bit)(no effect) R(16-24bit) G(8-16bit) B(0-8bit)
	 */
	protected final void blendStripColor(int c)
	{
		AtomicInteger blendAmt = new AtomicInteger(0);
		service.submit(() -> {
			while (blendAmt.get() < 255)
			{
				int[] newColors = new int[pixelCount];
				blendAmt.addAndGet(15);
				if (blendAmt.get() > 255) blendAmt.set(255);
				for (int i = 0; i < pixelCount; i++)
				{
					newColors[i] = FXUtil.colorBlend(colors[i], c, blendAmt.get());
				}
				setStripColors(newColors);
				try
				{
					//noinspection BusyWait
					Thread.sleep(50);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Set the effect of the LED Strip
	 *
	 * @param effect The effect class
	 * @see IEffect
	 */
	@Override
	public void setEffect(Class<? extends IEffect> effect)
	{
		setEffect(effect, Map.of());
	}

	/**
	 * Set the effect of the LED Strip
	 *
	 * @param effect  The effect class
	 * @param options The base config settings for the effect
	 * @see IEffect
	 */
	@Override
	public void setEffect(Class<? extends IEffect> effect, Map<String, Object> options)
	{
		if (effect == null) return;
		sink.emitNext(new StripChange(StripOperation.EFFECT, effect), FAIL_FAST);
		if (currentEffect != null)
		{
			if (effect != currentEffect.getClass())
			{
				currentEffect.dispose();
				setupEffect(effect);
			}
		}
		else
		{
			setupEffect(effect);
		}
		if (!options.containsKey("c1"))
		{
			currentEffect.updateConfig("c1", effectColor);
		}
		if (currentEffect != null)
		{
			options.forEach(currentEffect::updateConfig);
		}
	}

	/**
	 * Get the current effect that this LED Strip is using
	 *
	 * @return the effect object
	 */
	@Override
	public IEffect getEffect()
	{
		return currentEffect;
	}

	/**
	 * Setup a specified effect
	 *
	 * @param effect The effect class
	 */
	@SneakyThrows
	protected synchronized final void setupEffect(Class<? extends IEffect> effect)
	{
		if (effect == null) return;
		val constructor = effect.getConstructor(int.class);
		if (currentEffectDs != null && !currentEffectDs.isDisposed()) {
			currentEffectDs.dispose();
			currentEffectDs = null;
		}
		currentEffect = constructor.newInstance(pixelCount);
		if (brightness > 0)
		{
			turnOnEffect();
		}
	}

	/**
	 * Render the strip to a source
	 */
	@Override
	public abstract void render();

	/**
	 * Turn the LED Strip off
	 * This will turn off the effect to it is not using up resources while it is switched off
	 */
	@Override
	public void off()
	{
		if (isOn)
		{
			isOn = false;
			log.debug("Turning off strip: {}", name);
			sink.emitNext(new StripChange(StripOperation.STATE, false), FAIL_FAST);
			savedBrightness = brightness;
			setBrightness(0);
		}
	}

	/**
	 * Turn the LED Strip on
	 * This will turn any effects back on
	 */
	@Override
	public void on()
	{
		if (!isOn)
		{
			isOn = true;
			log.debug("Turning on strip: {} and setting brightness to: {}", name, brightness);
			sink.emitNext(new StripChange(StripOperation.STATE, true), FAIL_FAST);
			setBrightness(savedBrightness);
		}
	}

	/**
	 * Turn an effect off
	 */
	protected final void turnOffEffect()
	{
		if (currentEffect != null)
		{
			currentEffect.stop();
		}
	}

	/**
	 * Turn an effect on if the StripMode is not REACTIVE
	 */
	private synchronized void turnOnEffect()
	{
		if (mode == StripMode.EFFECTS)
		{
			if (currentEffect != null && currentEffectDs == null)
			{
				currentEffectDs = currentEffect.subscribe(this::handleRenderCall);
				currentEffect.start();
			}
			else if (currentEffect != null)
			{
				currentEffect.start();
			}
		}
	}

	/**
	 * Set the brightness of the strip
	 *
	 * @param brightness The brightness
	 */
	public void setBrightness(int brightness)
	{
		int currBrightness = this.brightness;
		AtomicInteger bChange = new AtomicInteger(currBrightness);
		boolean isDrop = brightness < currBrightness;
		if (brightness != this.brightness)
		{
			log.trace("Setting brightness to: {} for strip: {}", brightness, getName());
			sink.emitNext(new StripChange(StripOperation.BRIGHTNESS, brightness), FAIL_FAST);
			service.submit(() -> {
				if (brightness != 0)
				{
					turnOnEffect();
				}
				while (bChange.get() != brightness)
				{
					if (isDrop && brightness < bChange.get())
					{
						bChange.set(bChange.get() - 20);
						if (bChange.get() < 0)
							bChange.set(0);
						if (bChange.get() < brightness)
							bChange.set(brightness);
					}
					else if (!isDrop && brightness > bChange.get())
					{
						bChange.set(bChange.get() + 20);
						if (bChange.get() > 255)
							bChange.set(255);
						if (bChange.get() > brightness)
							bChange.set(brightness);
					}
					this.brightness = bChange.get();
					render();
					try
					{
						//noinspection BusyWait
						Thread.sleep(50);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				if (brightness == 0)
				{
					turnOffEffect();
				}
			});
		}
	}

	/**
	 * Listen to strip events
	 *
	 * @param op       The operation to listen for
	 * @param castType The type to cast to
	 * @return Flux
	 */
	@Override
	public <T> Flux<T> on(StripOperation op, Class<T> castType)
	{
		return sink.asFlux().filter(sc -> sc.getOp().equals(op))
				.log("StripEvent: " + getName().replace(" ", "-"), Level.FINEST, SignalType.ON_NEXT)
				.map(StripChange::getValue).cast(castType);
	}

	/**
	 * Get the unique identifier for the strip this is so it can be identified by other sources
	 * Using a low memory footprint
	 *
	 * @return int (byte) 0xFF
	 */
	@Override
	public int getUID()
	{
		return uId;
	}

	/**
	 * Dispose the strip stopping any services and processes that are in the background
	 */
	@Override
	public void dispose()
	{
		log.debug("Disposing strip: {}", getName());
		if (currentEffect != null)
		{
			currentEffect.dispose();
		}
		if (currentEffectDs != null)
		{
			currentEffectDs.dispose();
		}
		int[] cols = new int[pixelCount];
		Arrays.fill(cols, 0xFF000000);
		setColors(cols);
		off();
		service.shutdown();
	}

	/**
	 * Get the current strip mode
	 *
	 * @return the strip mode
	 */
	@Override
	public StripMode getMode()
	{
		return mode;
	}

	/**
	 * Set the current strip mode
	 *
	 * @param mode The mode of the strip
	 */
	@Override
	public void setMode(StripMode mode)
	{
		if (mode != this.mode)
		{
			this.mode = mode;
			if (mode == StripMode.NETWORK_UDP)
			{
				log.debug("Setting mode to listening to UDP packets for strip: {} - turning off current effect", name);
				turnOffEffect();
				int[] cols = new int[pixelCount];
				Arrays.fill(cols, Color.BLACK.getRGB());
				setStripColors(cols);
				sink.emitNext(new StripChange(StripOperation.REACTIVE, true), FAIL_FAST);
			}
			else
			{
				log.debug("Setting mode to listening to effect render calls for strip: {} - turning on current effect", name);
				sink.emitNext(new StripChange(StripOperation.REACTIVE, false), FAIL_FAST);
				turnOnEffect();
			}
		}
	}

	@Data
	private static class StripChange
	{
		private final StripOperation op;
		private final Object value;
	}
}
