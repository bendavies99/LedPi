package net.bdavies.app.strip;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.app.Strip;
import net.bdavies.network.RemoteStripServer;

/**
 * A simple Strip implementation that doesn't require anything external
 *
 * @author ben.davies
 */
@Slf4j
public class DebugStrip extends Strip
{
	private RemoteStripServer stripServer;


	/**
	 * Construct a Debug Strip for testing without the native libraries
	 *
	 * @param name The name of the strip
	 * @param pixelCount The led count
	 * @param uId The unique id
	 */
	public DebugStrip(String name, int pixelCount, int uId, RemoteStripServer stripServer)
	{
		super(name, pixelCount, uId);

		if (stripServer != null) {
			this.stripServer = stripServer;
		}
	}

	/**
	 * Render the strip colours
	 */
	@Override
	public void render(int[] colors) {
		if (stripServer != null) {
			stripServer.sendRenderData(getUID(), colors);
		}
	}

	/**
	 * Dispose the strip stopping any services and processes that are in the background
	 */
	@Override
	public void dispose()
	{
		super.dispose();
	}
}
