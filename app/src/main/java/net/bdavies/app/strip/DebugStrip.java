package net.bdavies.app.strip;

import com.google.common.util.concurrent.RateLimiter;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
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

	private final RateLimiter rateLimiter = RateLimiter.create(15);


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
		val waited = rateLimiter.acquire();
		if (waited > 0.0)
		{
			log.info("Waited: {}", waited);
		}
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
