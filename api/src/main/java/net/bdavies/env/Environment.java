package net.bdavies.env;

import lombok.extern.slf4j.Slf4j;

/**
 * The class to store environment variables passed in by the user
 *
 * @author ben.davies
 */
@Slf4j
public class Environment
{
    /**
     * Will turn off logging for anything below an error but will still output it ot a file
     */
    public static final boolean isProduction = Boolean.parseBoolean(System.getProperty("lp.prod"));
    /**
     * Will turn on trace logging
     */
    public static final boolean isTraceEnabled = Boolean.parseBoolean(System.getProperty("lp.trace"));
    /**
     * Will turn on the debug display that will render the strip
     */
    public static final boolean isDebugDisplayEnabled = Boolean.parseBoolean(System.getProperty("lp.debugDisplay"));
}
