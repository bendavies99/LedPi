package net.bdavies.app.strip;

import com.github.mbelling.ws281x.Color;
import com.github.mbelling.ws281x.LedStripType;
import com.github.mbelling.ws281x.Ws281xLedStrip;
import com.google.common.util.concurrent.RateLimiter;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.bdavies.api.config.IStripConfig;
import net.bdavies.app.Strip;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The main Native Strip implementation which uses the
 * rpi_ws281x_java library and will connect to a strip through the GPIO ports on a RPi
 *
 * @author ben.davies
 * @see Ws281xLedStrip
 */
@Slf4j
public class NativeStrip extends Strip {
    private static int DMA_CHANNEL = 10;
    private final AtomicReference<Ws281xLedStrip> strip = new AtomicReference<>(null);
    private final AtomicBoolean isRendering = new AtomicBoolean(false);
    private int[] previousRender;
    private int previousBrightness = 255;
    private final RateLimiter rateLimiter = RateLimiter.create(15);

    /**
     * Construct a native Strip
     *
     * @param config The config for the strip
     */
    public NativeStrip(IStripConfig config) {
        super(config.getName(), config.getLedCount(), config.getUid());
        try {
            DMA_CHANNEL++;
            log.info("Rendering using DMA CHAN: {}", DMA_CHANNEL);
            strip.set(new Ws281xLedStrip(getPixelCount(), config.getPinNumber(), 800000, DMA_CHANNEL,
                    getBrightness(), getPwmChannel(config.getPinNumber()), false,
                    LedStripType.WS2811_STRIP_GRB, true));
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
    private int getPwmChannel(int pinNumber) {
        return (pinNumber == 18 || pinNumber == 12 || pinNumber == 10 || pinNumber == 9 || pinNumber == 21) ? 0 : 1;
    }

    /**
     * Render the colors to the native strip back-end
     */
    @Override
    public void render(int[] colors) {
        if (strip == null) return;
        if (isRendering == null) return;
        if (isRendering.get()) return;
        isRendering.set(true);
        //Restrict the strip to no more than 15 render calls per second
        val waited = rateLimiter.acquire();
        log.info("Waited: {}", waited);
        synchronized (this) {
            if (previousRender == null) {
                previousRender = new int[getPixelCount()];
                Arrays.fill(previousRender, 0xFF000000);
            }
            if (strip.get() != null) {
                boolean changed = false;
                for (int i = 0; i < getPixelCount(); i++) {
                    if (colors[i] != previousRender[i]) {
                        changed = true;
                        strip.get().setPixel(i, new Color(colors[i]));
                        strip.get().render();
                    }
                }
                if (previousBrightness != getBrightness()) {
                    changed = true;
                    strip.get().setBrightness(getBrightness());
                    strip.get().render();
                }

                if (changed) {
                    previousRender = Arrays.copyOf(colors, colors.length);
                    previousBrightness = getBrightness();
                }
                isRendering.set(false);
            }
        }

    }
}
