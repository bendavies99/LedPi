package net.bdavies.fx.basic;

import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.fx.Effect;
import net.bdavies.fx.SingleColorBaseEffect;

/**
 * @author ben.davies
 */
@Slf4j
@Effect
public class Scan extends SingleColorBaseEffect
{
    private final AtomicInteger counter = new AtomicInteger(0);
    private final AtomicBoolean reverse = new AtomicBoolean(false);

    /**
     * Single color base effect
     *
     * @param pixelCount the number of pixels
     */
    public Scan(int pixelCount)
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
        setupRenderer(10, () -> {
            int dir = reverse.get() ? -1 : 1;
            int[] cols = new int[pixelCount];
            Arrays.fill(cols, Color.BLACK.getRGB());

            for (int i = 0; i < 2; i++)
            {
                int index = counter.get() - i;
                if (index < 0 || index > pixelCount - 1) continue;
                cols[index] = getColor();
            }

            sendRenderData(generateRenderCall(cols, false, true));

            counter.set(counter.get() + dir);

            if (counter.get() >= pixelCount) {
                counter.set(pixelCount - 1);
                reverse.set(true);
            } else if (counter.get() <= 0) {
                counter.set(0);
                reverse.set(false);
            }
        });
    }
}
