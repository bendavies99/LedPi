package net.bdavies.fx.basic;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.fx.BaseEffect;

/**
 * This effect does nothing
 *
 * @author ben.davies
 */
@Slf4j
public class NoOp extends BaseEffect
{
	public NoOp(int pixelCount)
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
		//Do Nothing
	}
}
