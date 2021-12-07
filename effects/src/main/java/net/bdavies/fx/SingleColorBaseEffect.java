package net.bdavies.fx;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * A Base Effect extension for effects that use the c1 config for a single color
 *
 * @author ben.davies
 */
@Slf4j
public abstract class SingleColorBaseEffect extends BaseEffect
{
	@Getter(AccessLevel.PROTECTED)
	private Integer color = null;

	/**
	 * Single color base effect
	 *
	 * @param pixelCount the number of pixels
	 */
	protected SingleColorBaseEffect(int pixelCount)
	{
		super(pixelCount);
		listenForConfigChange("c1", Integer.class).subscribe(nc -> {
			this.color = nc;
			onColorChange(nc);
		});
	}

	protected void onColorChange(int newColor) {}
}
