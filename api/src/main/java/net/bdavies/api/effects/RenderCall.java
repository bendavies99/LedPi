package net.bdavies.api.effects;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * A record for a render call to the strip
 *
 * @author ben.davies
 */
@Slf4j
@Data
public class RenderCall
{
	private final boolean blend;
	private final boolean blankSlate;
	private final int pixelCount;
	private final int[] pixelData;
	//Short (this method drops memory usage by 4x)
	//[0] -> PixelIndex (0-16bits)
	//[1] -> Red (0-8bits), Green(8-16 bits)
	//[2] -> Blue (0-8bits)
}
