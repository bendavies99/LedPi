package net.bdavies.api.strip;

import java.util.List;
import java.util.Map;

import net.bdavies.api.effects.IEffect;
import reactor.core.publisher.Flux;

/**
 * The main interface for a LED Strip in the application
 *
 * @author ben.davies
 */
public interface IStrip
{
    /**
     * Get the name of the LED Strip
     *
     * @return name
     */
    String getName();

    /**
     * Get the led count of the led strip
     *
     * @return count of pixels
     */
    int getPixelCount();

    /**
     * Get a color of a pixel at a given index
     *
     * @param index The index to get the color at
     * @return color A(24-32bit)(no effect) R(16-24bit) G(8-16bit) B(0-8bit)
     */
    int getColorAtPixel(int index);

    /**
     * Set a color of a pixel at a given index
     *
     * @param index The index to set the color at
     * @param col color A(24-32bit)(no effect) R(16-24bit) G(8-16bit) B(0-8bit)
     */
    void setColorAtPixel(int index, int col);

    /**
     * Set the color array for the LED Strip
     *
     * @param colors a list of colors
     */
    void setStripColors(List<Integer> colors);

    /**
     * Set the color array for the LED Strip
     *
     * @param colors a list of colors
     */
    void setStripColors(int[] colors);

    /**
     * Set the brightness of the strip
     *
     * @param brightness The brightness
     */
    void setBrightness(int brightness);

    /**
     * Set the main effect color that will set the default c1
     * for the effect is not one is set
     *
     * @param color the new color
     */
    void setEffectColor(int color);

    /**
     * Get the brightness of the strip
     *
     * @return number between 0-255 (byte)
     */
    int getBrightness();

    /**
     * Set the effect of the LED Strip
     *
     * @param effect The effect class
     * @see IEffect
     */
    void setEffect(Class<? extends IEffect> effect);

    /**
     * Set the effect of the LED Strip
     *
     * @param effect The effect class
     * @param options The base config settings for the effect
     * @see IEffect
     */
    void setEffect(Class<? extends IEffect> effect, Map<String, Object> options);

    /**
     * Get the current effect that this LED Strip is using
     *
     * @return the effect object
     */
    IEffect getEffect();

    /**
     * Render the strip to a source
     */
    void render();

    /**
     * Turn the LED Strip off
     * This will turn off the effect to it is not using up resources while it is switched off
     */
    void off();

    /**
     * Turn the LED Strip on
     * This will turn any effects back on
     */
    void on();

    /**
     * Get the unique identifier for the strip this is so it can be identified by other sources
     * Using a low memory footprint
     *
     * @return int (byte) 0xFF
     */
    int getUID();

    /**
     * Dispose the strip stopping any services and processes that are in the background
     */
    void dispose();

    /**
     * Get the current strip mode
     *
     * @return the strip mode
     */
    StripMode getMode();

    /**
     * Set the current strip mode
     *
     * @param mode The mode of the strip
     */
    void setMode(StripMode mode);

    /**
     * Listen to strip events
     *
     * @param op The operation to listen for
     * @param castType The type to cast to
     * @param <T> the new type
     * @return Flux
     */
    <T> Flux<T> on(StripOperation op, Class<T> castType);

    /**
     * Get the current effect color for the LED Strip
     *
     * @return the color for the effects
     */
    int getEffectColor();
}
