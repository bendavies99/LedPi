package net.bdavies.api.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for colors and color manipulation
 *
 * @author ben.davies
 */
@Slf4j
@UtilityClass
public class ColorUtil {
    /**
     * Get luminance of color
     *
     * @param argb the int value of the color
     * @return the luminosity
     */
    private static int getLuminance(int argb) {
        return (77 * ((argb >> 16) & 255)
                + 150 * ((argb >> 8) & 255)
                + 29 * ((argb) & 255)) >> 8;
    }

    /**
     * Determine if the color is too bright to render at full brightness
     *
     * @param color the color value
     * @return true if it is too bright
     */
    public static boolean shouldReduceBrightness(int color) {
        return getLuminance(color) >= 171;
    }
}
