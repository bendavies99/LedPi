package net.bdavies.fx;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Effect annotation to define metadata for the effect
 * The effect registry will use the info in this metadata to construct information about a Effect
 *
 * @author ben.davies
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Effect
{
	/**
	 * The name of the effect
	 *
	 * @return use $$_class to use the class name as the effect name
	 */
	String value() default "$$_class";

	/**
	 * This is for internal rendering effects so this is not available in the effect registry
	 *
	 * @return true if internal
	 */
	boolean internal() default false;
}
