package net.bdavies.fx.basic;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.fx.Effect;
import net.bdavies.fx.SingleColorBaseEffect;

/**
 * A Strobe effect that will flash a color that is set using the c1 config
 * The speed of the flash is set by the speed config
 *
 * @author ben.davies
 */
@Slf4j
@Effect
public class Strobe extends SingleColorBaseEffect
{
    private int speed = 50; //ms
    private boolean showing = false;

    /**
     * @param pixelCount the number of leds
     */
    public Strobe(int pixelCount)
    {
        super(pixelCount);

        //Speed of the show
        listenForConfigChange("speed", Integer.class)
            .subscribe(s -> {
                this.speed = s;
                start();
            });
    }

    /**
     * Start the effect
     */
    public void start() {
        setupRenderer(speed, () -> {
            if (showing) {
                sendRenderData(generateRenderCall(genColArray(0xFF000000), false, true));
                showing = false;
            } else {
                sendRenderData(generateRenderCall(genColArray(getColor()), false, true));
                showing = true;
            }
        });
    }

}
