package net.bdavies.fx.basic;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.util.FXUtil;
import net.bdavies.fx.BaseEffect;
import net.bdavies.fx.Effect;
import net.bdavies.fx.Time;

/**
 * BPM Effect implementation
 *
 * @author ben.davies
 */
@Slf4j
@Effect
public class BPM extends BaseEffect
{
	public BPM(int pixelCount)
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
		setupRenderer(50, () -> {
			int step = (int) ((Time.getMillisSinceStart() / 20) & 0xFF);

			//Sawtooth wave
			int calc = Math.abs((int) ((2 * (((float)step % 48)/48) -1) * 255));

			int[] cols = new int[pixelCount];
			for (int i = 0; i < pixelCount; i++)
			{
				cols[i] = FXUtil.colorWheel(calc + (i + step));
			}

			sendRenderData(generateRenderCall(cols, false, true));
		});
	}
}
