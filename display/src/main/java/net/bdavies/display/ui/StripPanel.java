package net.bdavies.display.ui;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

import lombok.SneakyThrows;
import net.bdavies.api.util.FXUtil;

public class StripPanel extends Canvas implements Runnable
{
	private final int pixelCount;
	private int[] pixels, imagePixels;
	private boolean running;
	private final Thread thread;
	private final BufferedImage image;

	public StripPanel(int pixelCount)
	{
		this.pixelCount = pixelCount;
		Dimension d = new Dimension(pixelCount * 6, 200);
		setMinimumSize(d);
		setMaximumSize(d);
		setPreferredSize(d);
		this.thread = new Thread(this);
		pixels = new int[pixelCount];
		image = new BufferedImage(pixelCount, 200, BufferedImage.TYPE_INT_RGB);
		imagePixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	}

	public synchronized void start() {
		if (running) return;
		running = true;
		thread.start();
	}

	@SneakyThrows
	public synchronized void stop() {
		if (!running) return;
		running = false;
		thread.join(0);
	}

	@Override
	public void run()
	{
		requestFocus();
		while (running) {
			render();
		}
	}

	private void render()
	{
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		clear();
		copy();

		g.drawImage(image, 0, 0, pixelCount * 6, 200, null);

		g.dispose();
		bs.show();
	}

	public synchronized void setPixel(int pixel, int color, int brightness) {
		 this.pixels[pixel] = FXUtil.colorBlend(color, 0xFF000000, 255 - brightness);
	}

	private void copy() {
		for (int y = 0; y < 200; y++)
		{
			for (int x = 0; x < pixelCount; x++)
			{
				imagePixels[x + y * pixelCount] = pixels[x];
			}
		}
	}

	private void clear()
	{
		Arrays.fill(imagePixels, 0x000000);
	}
}
