package net.bdavies.fx.fireworks;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;
import java.util.function.UnaryOperator;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.util.FXUtil;
import net.bdavies.fx.SingleColorBaseEffect;

/**
 * Base class for the fireworks effect
 *
 * @author ben.davies
 */
@Slf4j
public abstract class FireworksBase extends SingleColorBaseEffect
{
	protected UnaryOperator<Integer> colRetriever;
	/**
	 * Single color base effect
	 *
	 * @param pixelCount the number of pixels
	 * @param colorRetriever The function to get a color
	 */
	protected FireworksBase(int pixelCount, UnaryOperator<Integer> colorRetriever)
	{
		super(pixelCount);
		this.colRetriever = colorRetriever;
	}

	/**
	 * Start the effect and this will begin any effect render loop
	 * or just pass a color to the subscriber
	 */
	@Override
	public void start()
	{
		final Random random = new Random();
		int size = 5;
		setupRenderer(200, () -> {
			int[] cols = new int[pixelCount];
			for (int i = 0; i < (int) Math.max(1, (float) pixelCount / 20.0f); i++)
			{
				if (random.nextInt(4) == 0)
				{
					int index = random.nextInt(pixelCount - size + 1);
					Arrays.fill(cols, index, index + size, colRetriever.apply(index));
				}
			}

			sendRenderData(generateRenderCall(smooth(cols, 180), false, false));
		});
	}

	private int[] smooth(int[] cols, int amount)
	{
		int keep = 255 - amount;
		int carryOver = 0xFF000000;
		int[] newCols = Arrays.copyOf(cols, cols.length);
		for (int i = 0; i < cols.length; i++)
		{
			newCols[i] = FXUtil.colorBlend(newCols[i], Color.BLACK.getRGB(), amount);
			cols[i] = FXUtil.colorBlend(cols[i], Color.BLACK.getRGB(), keep);
			cols[i] += carryOver;
			Color newCol = new Color(newCols[i]);
			if (i > 0)
			{
				Color pCol = new Color(cols[i - 1]);
				newCols[i - 1] = new Color(add(pCol.getRed(), newCol.getRed()), add(pCol.getGreen(), newCol.getGreen()),
						add(pCol.getBlue(), newCol.getBlue())).getRGB();
			}
			carryOver = newCol.getRGB();
		}
		return newCols;
	}

	private int add(int red, int red1)
	{
		int newN = red + red1;
		if (newN > 255)
			newN = 255;
		return newN;
	}

}
