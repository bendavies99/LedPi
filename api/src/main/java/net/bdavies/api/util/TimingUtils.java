package net.bdavies.api.util;

import java.time.Duration;
import java.util.Random;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * A class with Timing utilities for effects
 *
 * @author ben.davies
 */
@Slf4j
@UtilityClass
public class TimingUtils
{
    /**
     * Wait for a specified duration before running the runnable
     *
     * @param runnable The function to run
     * @param delay The delay
     */
    public static void setTimeout(Runnable runnable, Duration delay) {
        Mono.just("setTimeout" + new Random().nextInt(19233))
            .subscribeOn(Schedulers.boundedElastic())
            .delayElement(delay).subscribe(l -> runnable.run());
    }

    /**
     * Wait for a specified duration before running the runnable
     *
     * @param runnable The function to run
     * @param delay The delay (in milliseconds)
     */
    public static void setTimeout(Runnable runnable, long delay) {
        setTimeout(runnable, Duration.ofMillis(delay));
    }

    /**
     * Loop through a runnable until you run {@link Disposable#dispose()}
     *
     * @param runnable The function to run
     * @param intervalPeriod The period of time before running the function
     * @param delay the delay before the first run
     * @return a disposer so you can stop the loop
     */
    public static Disposable setInterval(Runnable runnable, Duration intervalPeriod, Duration delay) {
        return Flux.interval(delay, intervalPeriod)
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe(l -> runnable.run());
    }

    /**
     * Loop through a runnable until you run {@link Disposable#dispose()}
     *
     * @param runnable The function to run
     * @param intervalPeriod The period of time before running the function
     * @return a disposer so you can stop the loop
     */
    public static Disposable setInterval(Runnable runnable, Duration intervalPeriod) {
        return setInterval(runnable, intervalPeriod, Duration.ofMillis(0));
    }

    /**
     * Loop through a runnable until you run {@link Disposable#dispose()}
     *
     * @param runnable The function to run
     * @param intervalPeriod The period of time before running the function (ms)
     * @param delay the delay before the first run (ms)
     * @return a disposer so you can stop the loop
     */
    public static Disposable setInterval(Runnable runnable, long intervalPeriod, long delay) {
        return setInterval(runnable, Duration.ofMillis(intervalPeriod), Duration.ofMillis(delay));
    }

    /**
     * Loop through a runnable until you run {@link Disposable#dispose()}
     *
     * @param runnable The function to run
     * @param intervalPeriod The period of time before running the function (ms)
     * @return a disposer so you can stop the loop
     */
    public static Disposable setInterval(Runnable runnable, long intervalPeriod) {
        return setInterval(runnable, intervalPeriod, 0);
    }

}
