package net.bdavies.app.strip;

import static net.bdavies.api.util.TimingUtils.setInterval;

import com.github.mbelling.ws281x.Color;
import com.github.mbelling.ws281x.LedStripType;
import com.github.mbelling.ws281x.Ws281xLedStrip;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.bdavies.api.config.IStripConfig;
import net.bdavies.app.Strip;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main Native Strip implementation which uses the
 * rpi_ws281x_java library and will connect to a strip through the GPIO ports on a RPi
 *
 * @author ben.davies
 * @see Ws281xLedStrip
 */
@Slf4j
public class NativeStrip extends Strip {
    private static int DMA_CHANNEL = 9;
    private volatile Ws281xLedStrip strip;
    private final AtomicBoolean isRendering = new AtomicBoolean(false);
    private int[] previousRender;
    private int previousBrightness = 255;

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
            strip = new Ws281xLedStrip(getPixelCount(), config.getPinNumber(), 800000, DMA_CHANNEL,
                    getBrightness(), getPwmChannel(config.getPinNumber()), true,
                    LedStripType.WS2811_STRIP_GRB, false);
            if (DMA_CHANNEL > 14) DMA_CHANNEL = 14;
            log.info("Setup a native strip on pin: {}", config.getPinNumber());
        } catch (UnsatisfiedLinkError e) {
            log.error("Strip cannot load on this machine: {} you will need to fix to continue", getName(), e);
            System.exit(-1);
        }
        setInterval(() -> strip.render(), 52, 100);
    }

    /**
     * Get the PWM Channel based on the pin number supplied to the strip
     *
     * @param pinNumber The pin number
     * @return 0 or 1
     */
    private int getPwmChannel(int pinNumber) {
        return (pinNumber == 13 || pinNumber == 19 || pinNumber == 41 || pinNumber == 45 || pinNumber == 53) ? 1 : 0;
    }

    /**
     * Render the colors to the native strip back-end
     */
    @Override
    public void render(int[] colors) {
        if (strip == null) return;
        if (isRendering == null) return;
        if (isRendering.get()) return;
        if (previousRender == null) {
            previousRender = new int[getPixelCount()];
            Arrays.fill(previousRender, 0xFF000000);
        }
        if (strip != null) {
            boolean changed = false;
            for (int i = 0; i < getPixelCount(); i++) {
                if (colors[i] != previousRender[i]) {
                    changed = true;
                    val c = new java.awt.Color(colors[i], true);
                    strip.setPixel(i, new Color(c.getRed(), c.getGreen(), c.getBlue()));
                }
            }
            if (previousBrightness != getBrightness()) {
                changed = true;
                strip.setBrightness(getBrightness());
            }

            if (changed) {
                previousRender = Arrays.copyOf(colors, colors.length);
                previousBrightness = getBrightness();
            }

            isRendering.set(false);
        }
    }
}
