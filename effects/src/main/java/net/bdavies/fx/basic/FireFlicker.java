package net.bdavies.fx.basic;

import java.awt.*;
import java.util.Random;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.bdavies.fx.Effect;
import net.bdavies.fx.SingleColorBaseEffect;

/**
 * The fire flicker effect creates a sort of barcode effect it uses the color from the c1 config
 *
 * @author ben.davies
 */
@Slf4j
@Effect
public class FireFlicker extends SingleColorBaseEffect
{
	/**
	 * @param pixelCount the num of leds
	 */
	public FireFlicker(int pixelCount)
	{
		super(pixelCount);
	}

	/**
	 * Start the effect
	 */
	@Override
	public void start()
	{
		val rand = new Random();
		setupRenderer(100, () -> {
			Color c = new Color(getColor());
			int lum = Math.max(c.getRed(), Math.max(c.getGreen(), c.getBlue()));
			int[] cols = new int[pixelCount];
			for (int i = 0; i < pixelCount; i++)
			{
				int flicker = rand.nextInt(Math.abs(lum) + 1);
				cols[i] = new Color(Math.max(c.getRed() - flicker, 0),
						Math.max(c.getGreen() - flicker, 0), Math.max(c.getBlue() - flicker, 0)).getRGB();
			}
			sendRenderData(generateRenderCall(cols, false, true));
		});
	}
}
