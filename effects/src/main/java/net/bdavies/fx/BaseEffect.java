package net.bdavies.fx;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.bdavies.api.effects.ConfigChange;
import net.bdavies.api.effects.IEffect;
import net.bdavies.api.effects.RenderCall;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.awt.*;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The base effect class that all the effects extend from
 *
 * @author ben.davies
 */
@Slf4j
public abstract class BaseEffect implements IEffect
{
    private final Sinks.Many<RenderCall> sink = Sinks.many().unicast().onBackpressureBuffer();
    private final Sinks.Many<ConfigChange> internal = Sinks.many().multicast().onBackpressureBuffer(10);
    protected final int pixelCount;
    private Disposable renderer;
    private IEffect switchedEffect;
    private final Map<String, Object> configMap = new HashMap<>();
    private int[] previousRender;

    protected BaseEffect(int pixelCount)
    {
        this.pixelCount = pixelCount;
        internal.asFlux().subscribe(c -> configMap.put(c.getConfigName(), c.getValue()));
        previousRender = new int[pixelCount];
        Arrays.fill(previousRender, Color.BLACK.getRGB());
    }

    /**
     * Send render data to the subscriber
     *
     * @param call The call to send
     */
    protected synchronized final void sendRenderData(RenderCall call) {
        if (sink.currentSubscriberCount() > 0)
        {
            sink.emitNext(call, Sinks.EmitFailureHandler.FAIL_FAST);
        }
    }

    /**
     * Listen for internal config changes
     *
     * @param configName The name of config item
     * @param valueType The class type of the config item
     * @param <T> The type of the new value
     * @return Flux to manipulate the config data
     */
    protected final <T> Flux<T> listenForConfigChange(String configName, Class<T> valueType) {
        return internal.asFlux()
            .filter(c -> c.getConfigName().equalsIgnoreCase(configName))
            .map(ConfigChange::getValue)
            .cast(valueType);
    }

    /**
     * Update the effect config and send to the config listeners
     *
     * @param configName The name of the config variable
     * @param value      The new value
     */
    public synchronized final void updateConfig(String configName, Object value) {
        internal.emitNext(new ConfigChange(configName, value),
            Sinks.EmitFailureHandler.FAIL_FAST);
    }

    /**
     * Subscribe to the render calls made from the effect
     *
     * @param consumer The subscriber consumer
     * @return A disposable to stop subscriptions
     */
    public final Disposable subscribe(Consumer<RenderCall> consumer) {
        return sink.asFlux().subscribe(consumer);
    }

    /**
     * Generate a render call class from a list of colors and options
     *
     * @param color The color array [] A(24-32bit)(no effect) R(16-24bit) G(8-16bit) B(0-8bit)
     * @param blend If you want the system to try and blend your colour with another
     * @param blankSlate If you want the colours to blank before using this render call
     * @return The made render call
     */
    protected final RenderCall generateRenderCall(int[] color, boolean blend, boolean blankSlate) {
        previousRender = color;
        return new RenderCall(blend, blankSlate, color.length, color);
    }

    /**
     * Generate a render call class from a list of colors
     *
     * @param color The color array [] A(24-32bit)(no effect) R(16-24bit) G(8-16bit) B(0-8bit)
     * @return the made render call
     */
    protected final RenderCall generateRenderCall(int[] color) {
        return generateRenderCall(color, true, false);
    }

    /**
     * Setup a render loop for the effect if it isn't static this is the best way to
     * ensure that there is no weird artifacts when running with a Strip
     *
     * @param delay the delay between passes in milliseconds
     * @param run The function to run
     */
    protected final void setupRenderer(int delay, Runnable run) {
        setupRenderer(delay, pr -> run.run());
    }

    /**
     * Setup a render loop for the effect if it isn't static this is the best way to
     * ensure that there is no weird artifacts when running with a Strip
     *
     * @param delay the delay between passes in milliseconds
     * @param run The function to run
     */
    protected final void setupRenderer(int delay, Consumer<int[]> run) {
        if (switchedEffect != null) {
            log.warn("You are currently rendering an effect please use switchBack before reapplying the renderer");
            return;
        }
        if (renderer != null) {
            renderer.dispose();
        }
        renderer = Flux.interval(Duration.ofMillis(delay)).subscribe(l -> run.accept(previousRender));
    }

    /**
     * Dispose the effect
     */
    public final void dispose() {
        internalDispose();
        if (renderer != null) {
            renderer.dispose();
        }
    }

    /**
     * Generate a color array from a single color
     *
     * @param col The color A(24-32bit)(no effect) R(16-24bit) G(8-16bit) B(0-8bit)
     * @return The new color array
     */
    protected int[] genColArray(int col) {
        int[] c = new int[pixelCount];
        for (int i = 0; i < pixelCount; i++) {
            c[i] = col;
        }
        return c;
    }

    /**
     * For running any dispose items that your effect has
     */
    protected void internalDispose() {}

    /**
     * Switch to another effect and use this render system as your render system the benefit is you can switch back to
     * your render loop at any time meaning you can create a light show and not reuse code
     *
     * @param fxClass The new effect class
     * @param config The config to use
     */
    @SneakyThrows
    protected final void switchTo(Class<? extends IEffect> fxClass, Map<String, Object> config) {
        if (switchedEffect != null) {
            switchedEffect.stop();
            switchedEffect.dispose();
        }
        val con = fxClass.getConstructor(int.class);
        val fx = con.newInstance(pixelCount);
        if (renderer != null) {
            renderer.dispose();
        }
        switchedEffect = fx;
        fx.subscribe(this::sendRenderData);
        fx.start();
        config.forEach(fx::updateConfig);
    }

    /**
     * Switch the effect back to your own render loop
     */
    protected final void switchBack() {
        if (switchedEffect != null && sink.currentSubscriberCount() > 0) {
            switchedEffect.stop();
            switchedEffect.dispose();
            switchedEffect = null;
            this.start();
        }
    }

    /**
     * Stop the current effect which will dispose the current
     * renderer if it is being used
     */
    @Override
    public synchronized void stop()
    {
        if (renderer != null) {
            renderer.dispose();
        }
        if (switchedEffect != null) {
            switchedEffect.stop();
            switchedEffect.dispose();
        }
    }

    /**
     * Get the current effect config (copied)
     *
     * @return Map of the config
     */
    @Override
    public Map<String, Object> getCurrentConfig()
    {
        return new HashMap<>(configMap);
    }
}
