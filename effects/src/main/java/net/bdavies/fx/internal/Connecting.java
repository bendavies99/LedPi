package net.bdavies.fx.internal;

import static net.bdavies.api.util.TimingUtils.setTimeout;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.fx.BaseEffect;
import net.bdavies.fx.Effect;
import net.bdavies.fx.basic.Strobe;

/**
 * The connecting effect it will creating a loading bar with a pixel space inbetween and
 * keep doing on a cycle until stopped or changed
 *
 * @author ben.davies
 */
@Slf4j
@Effect(internal = true)
public class Connecting extends BaseEffect
{
    /**
     * @param pixelCount the number of leds
     */
    public Connecting(int pixelCount)
    {
        super(pixelCount);
    }

    public void fail() {
        switchTo(Strobe.class, Map.of("speed", 350, "c1", 0xFFFF0000));
        setTimeout(this::switchBack, 350 * 6);
    }

    public void success() {
        switchTo(Strobe.class, Map.of("speed", 350, "c1", 0xFF00FF00));
        setTimeout(this::switchBack, 350 * 6);
    }

    /**
     * Start the effect
     */
    @Override
    public void start()
    {
        int space = 2;
        AtomicInteger counter = new AtomicInteger(0);
        int maxCount = pixelCount / space;
        setupRenderer(50, () -> {
            int[] colors = new int[pixelCount];
            for (int i = 0; i < colors.length; i+=space)
            {
                int count = i / space;
                if (counter.get() >= count)
                {
                    colors[i] = 0xFF0000FF;
                } else {
                    colors[i] = 0xFF000000;
                }
                colors[i + 1] = 0xFF000000;
            }
            sendRenderData(generateRenderCall(colors));
            counter.getAndIncrement();
            if (counter.get() >= maxCount) {
                counter.set(0);
            }
        });
    }
}
