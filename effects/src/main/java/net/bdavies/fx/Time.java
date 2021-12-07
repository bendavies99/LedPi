package net.bdavies.fx;

import lombok.extern.slf4j.Slf4j;

/**
 * Class for monitoring the time of the application
 *
 * @author ben.davies
 */
@Slf4j
public class Time
{
    private static long START_TIME = System.currentTimeMillis();

    /**
     * Get the milliseconds passed since the application started
     *
     * @return the milliseconds
     */
    public static long getMillisSinceStart() {
        long timeDif = System.currentTimeMillis() - START_TIME;
        if (timeDif >= 2045382647) {
            START_TIME = System.currentTimeMillis();
        }
        return timeDif;
    }
}
