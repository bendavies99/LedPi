package net.bdavies.api.util;

import java.awt.*;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * A utility class for effects to use
 *
 * @author ben.davies
 */
@Slf4j
public class FXUtil
{
    private static final int[] colorWheel = new int[256];

    static {
        for (int i = 0; i < 256; i++)
        {
            colorWheel[i] = getWheelCol(i);
        }
        System.gc();
    }

    /**
     * Get a color for the color wheel at a position
     *
     * @param i The index
     * @return The color
     */
    private static int getWheelCol(int i)
    {
        int pos = i & 255;
        if (pos < 85) {
            return new Color(pos * 3, 255 - pos * 3, 0).getRGB();
        } else if (pos < 170) {
            pos -= 85;
            return new Color(255 - pos * 3, 0, pos * 3).getRGB();
        } else {
            pos -= 170;
            return new Color(0, pos * 3, 255 - pos * 3).getRGB();
        }
    }

    /**
     * Get a color from the color wheel at a position
     *
     * @param pos The position 0-255
     * @return a color
     */
    public static int colorWheel(int pos) {
        pos = pos & 255;
        return colorWheel[pos];
    }

    /**
     * Get a color from the color wheel at a position
     *
     * @param pos The position 0-255
     * @return a color
     */
    public static int colorWheel(int pos, int brightness) {
        pos = pos & 255;
        brightness = brightness & 255;
        return colorBlend(colorWheel[pos], Color.BLACK.getRGB(), brightness);
    }

    /**
     * Blend two colors together
     *
     * @param col1 The first color
     * @param col2 The second color
     * @param blendAmt The amount to blend by 0-255
     * @return the final color
     */
    public static int colorBlend(int col1, int col2, int blendAmt) {
        if (blendAmt <= 0) return col1;
        if (blendAmt >= 255) return col2;
        val c1 = new Color(col1);
        val c2 = new Color(col2);

        int r = ((c2.getRed() * blendAmt) + (c1.getRed() * (255 - blendAmt))) / 256;
        int g = ((c2.getGreen() * blendAmt) + (c1.getGreen() * (255 - blendAmt))) / 256;
        int b = ((c2.getBlue() * blendAmt) + (c1.getBlue() * (255 - blendAmt))) / 256;

        return new Color(r, g, b).getRGB();
    }

    /**
     * Converted a hex string e.g. #FF00FF to a color value e.g. 0xFFFF00FF
     *
     * @param hex The hex string
     * @return the new color from the hex string
     */
    public static int hexToColor(String hex) {
        hex = hex.replace("#", "");
        switch (hex.length()) {
        case 6:
            return new Color(
                Integer.valueOf(hex.substring(0, 2), 16),
                Integer.valueOf(hex.substring(2, 4), 16),
                Integer.valueOf(hex.substring(4, 6), 16)).getRGB();
        case 8:
            return new Color(
                Integer.valueOf(hex.substring(2, 4), 16),
                Integer.valueOf(hex.substring(4, 6), 16),
                Integer.valueOf(hex.substring(6, 8), 16)).getRGB();
        }
        log.warn("Invalid hex supplied! {}", hex);
        return Color.BLACK.getRGB();
    }

    /**
     * Convert a integer color to a hex string e.g. 0xFFFF00FF -> #FF00FF
     *
     * @param color the color to convert
     * @return the new hex string
     */
    public static String colorToHex(int color)
    {
        return "#" + Integer.toHexString(new Color(color).getRGB()).substring(2);
    }
}
