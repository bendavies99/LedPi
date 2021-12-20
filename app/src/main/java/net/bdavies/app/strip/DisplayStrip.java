package net.bdavies.app.strip;

import net.bdavies.app.Strip;
import net.bdavies.display.DevFrame;

public class DisplayStrip extends Strip
{
	private final DevFrame frame;
	/**
	 * Construct a LED Strip and setup everything
	 *
	 * @param name       The name of LED Strip
	 * @param pixelCount The count of leds
	 * @param uId        The unique id for the strip
	 */
	public DisplayStrip(String name, int pixelCount, int uId)
	{
		super(name, pixelCount, uId);
		frame = new DevFrame(pixelCount, name);
		frame.getPanel().start();
	}

	@Override
	public synchronized void render(int[] colors)
	{
		if (frame == null) return;
		for (int i = 0; i < getPixelCount(); i++)
		{
			frame.getPanel().setPixel(i, colors[i], getBrightness());
		}
	}
}
