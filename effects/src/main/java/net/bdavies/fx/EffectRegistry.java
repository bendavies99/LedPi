package net.bdavies.fx;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.bdavies.api.effects.IEffect;
import net.bdavies.fx.basic.NoOp;

/**
 * A class to hold all the effects
 *
 *
 * @author ben.davies
 */
@Slf4j
public class EffectRegistry
{
	private static final Map<String, Class<? extends IEffect>> effectMap = new HashMap<>();

	/**
	 * Find the effects in the classpath at runtime
	 */
	public static void findEffects()
	{
		synchronized (effectMap)
		{
			if (effectMap.size() == 0)
			{
				log.trace("Finding effects in the classpath");
				val cb = new ConfigurationBuilder();
				cb.addClassLoaders(Thread.currentThread().getContextClassLoader());
				cb.forPackages("net.bdavies.fx");
				val re = new Reflections(cb);
				val list = re.getTypesAnnotatedWith(Effect.class);
				list.forEach(i -> {
					if (!i.getAnnotation(Effect.class).internal())
					{
						//noinspection unchecked
						effectMap.put(getEffectName(i), (Class<? extends IEffect>) i);
					}
				});
				effectMap.put("reactive", NoOp.class);
			}
			log.info("Registered Effects: {}", effectMap);
		}
	}

	/**
	 * Get the effect name based on the class supplied
	 *
	 * @param effect The effect class
	 * @return String
	 */
	public static String getEffectName(Class<?> effect)
	{
		if (effect.equals(NoOp.class)) {
			return "reactive";
		}
		val fx = effect.getAnnotation(Effect.class);
		return fx.value().equalsIgnoreCase("$$_class") ? effect.getSimpleName().toLowerCase() : fx.value();
	}

	/**
	 * Get all the effect names registered
	 *
	 * @return A set of strings
	 */
	public static Set<String> getEffectNames()
	{
		return effectMap.keySet();
	}

	/**
	 * Get the formatted names in the map
	 *
	 * @return names that are formatted
	 */
	public static Set<String> getFormattedNames()
	{
		return effectMap.keySet().stream().map(s -> s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1))
				.collect(Collectors.toSet());
	}

	/**
	 * Get effect class based on a effect class
	 *
	 * @param name The name of the effect
	 * @return The effect class
	 */
	public static Class<? extends IEffect> getEffect(String name)
	{
		return effectMap.get(name.toLowerCase());
	}
}
