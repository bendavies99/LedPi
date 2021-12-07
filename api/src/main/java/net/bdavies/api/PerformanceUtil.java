package net.bdavies.api;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * A performance utility class for looking at
 * the performance of the application
 *
 * @author ben.davies
 */
@Slf4j
@UtilityClass
public class PerformanceUtil
{
	/**
	 * A performance utility function for testing the
	 * amount of time it takes to run a function
	 *
	 * @param fnc The function to test
	 */
	public static void timeTaken(Runnable fnc) {
		long start = System.nanoTime();
		fnc.run();
		long end = System.nanoTime();
		log.debug("Time took for function: {}ns - {}ms", end - start, ((float)(end - start) / 1000000));
	}
}
