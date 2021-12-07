package net.bdavies.fx.basic;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.fx.Effect;
import net.bdavies.fx.SingleColorBaseEffect;

/**
 * The solid effect that will only render once every time the color changes using the c1 config
 *
 * @author ben.davies
 */
@Slf4j
@Effect
public class Solid extends SingleColorBaseEffect
{
    /**
     * @param pixelCount the num of leds
     */
    public Solid(int pixelCount)
    {
        super(pixelCount);
    }

    /**
     * Handle the color change
     *
     * @param newColor The new color
     */
    @Override
    protected void onColorChange(int newColor)
    {
        start();
    }

    /**
     * Render the color
     */
    @Override
    public void start()
    {
        if (getColor() != null)
        {
            sendRenderData(generateRenderCall(genColArray(getColor())));
        }
    }
}
