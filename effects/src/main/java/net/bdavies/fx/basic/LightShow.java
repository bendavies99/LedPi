package net.bdavies.fx.basic;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.fx.Effect;
import net.bdavies.fx.EffectRegistry;
import net.bdavies.fx.SingleColorBaseEffect;
import reactor.core.publisher.Flux;

/**
 * @author ben.davies
 */
@Slf4j
@Effect
public class LightShow extends SingleColorBaseEffect
{
    private final AtomicBoolean running = new AtomicBoolean(false);
    public LightShow(int pixelCount)
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
        switchTo(Rainbow.class, Map.of());
        Flux.fromStream(EffectRegistry.getEffectNames().stream())
            .filter(n -> !n.equalsIgnoreCase("reactive") && !n.equalsIgnoreCase("solid")
                && !n.equalsIgnoreCase("rainbow"))
            .delayElements(Duration.ofSeconds(5))
            .map(EffectRegistry::getEffect)
            .subscribe(e -> {
                if (running.get())
                    this.switchTo(e, Map.of("c1", getColor()));
            }, s -> log.error("Something", s), () -> {
                if (running.get())
                    this.switchBack();
            });
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
}
