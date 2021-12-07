package net.bdavies.fx.running;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.fx.Effect;

/**
 * @author ben.davies
 */
@Slf4j
@Effect
public class Christmas extends Running
{
    /**
     * Single color base effect
     *
     * @param pixelCount  the number of pixels
     */
    public Christmas(int pixelCount)
    {
        super(pixelCount, 0xFF00AA00, 0xFFFF0000);
    }
}
