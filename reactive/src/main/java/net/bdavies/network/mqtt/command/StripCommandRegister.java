package net.bdavies.network.mqtt.command;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.IApplication;
import net.bdavies.api.strip.IStrip;
import net.bdavies.api.strip.StripMode;
import net.bdavies.api.strip.StripOperation;
import net.bdavies.api.util.FXUtil;
import net.bdavies.fx.EffectRegistry;

/**
 * The strip command register
 *
 * @author ben.davies
 */
@Slf4j
@ToString
public class StripCommandRegister extends BaseCommandRegister
{
	private final IStrip strip;

	/**
	 * Make a strip command register
	 *
	 * @param application the application
	 * @param strip the strip to handle
	 */
	public StripCommandRegister(IApplication application, IStrip strip)
	{
		super(application, strip.getName().replaceAll("\\s", "_"));
		this.strip = strip;
		registerPublishers();
	}

	/**
	 * Register the commands for the strip
	 *
	 * /col -> Change effect color
	 * /bri -> Change the brightness
	 * /sfx -> Change the effect to use based on its name
	 * /state -> Turn the strip on / off
	 */
	@Override
	protected void registerCommands()
	{
		registerCommand("col", col -> strip.setEffectColor(FXUtil.hexToColor(col)));
		registerCommand("bri", bri -> strip.setBrightness(Integer.parseInt(bri) & 255));
		registerCommand("sfx", sfx -> {
			if (sfx.equalsIgnoreCase("reactive"))
				strip.setMode(StripMode.NETWORK_UDP);
			if (!sfx.equalsIgnoreCase("reactive"))
				strip.setMode(StripMode.EFFECTS);
			strip.setEffect(EffectRegistry.getEffect(sfx));
		});
		registerCommand("state", v -> {
			if (v.equalsIgnoreCase("on"))
			{
				strip.on();
			}
			else
			{
				strip.off();
			}
		});
	}

	/**
	 * Register the publishers for the strip
	 *
	 * /b -> On brightness change
	 * /s -> On State change
	 * /c -> On effect color change
	 * /fx -> On effect change
	 */
	@Override
	protected void registerPublishers()
	{
		registerPublisher("b", strip.on(StripOperation.BRIGHTNESS, Integer.class).map(String::valueOf));
		registerPublisher("s", strip.on(StripOperation.STATE, Boolean.class).map(b -> b ? "ON" : "OFF"));
		registerPublisher("c", strip.on(StripOperation.EFFECT_COLOR, String.class));
		registerPublisher("fx", strip.on(StripOperation.EFFECT, Class.class)
				.map(EffectRegistry::getEffectName));
	}
}
