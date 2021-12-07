package net.bdavies.api.strip;

import lombok.extern.slf4j.Slf4j;

/**
 * ENUM of operations that the strip can take and it can be listened to by other services
 *
 * @see IStrip#on(StripOperation, Class)
 *
 * @author ben.davies
 */
@Slf4j
public enum StripOperation
{
    /**
     * When an Effect is changed
     */
    EFFECT,
    /**
     * When Reactive mode is turned on or off
     */
    REACTIVE,
    /**
     * When the Effect Color is changed
     */
    EFFECT_COLOR,
    /**
     * When the brightness is changed
     */
    BRIGHTNESS,
    /**
     * When the strip is turned off / on
     */
    STATE
}
