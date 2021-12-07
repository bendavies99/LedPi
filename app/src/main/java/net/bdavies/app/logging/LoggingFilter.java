package net.bdavies.app.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.env.Environment;

/**
 * Filter the log when in Production mode
 *
 * @see Environment
 *
 * @author ben.davies
 */
@Slf4j
public class LoggingFilter extends Filter<ILoggingEvent>
{
    /**
     * If the decision is <code>{@link FilterReply#DENY}</code>, then the event will be
     * dropped. If the decision is <code>{@link FilterReply#NEUTRAL}</code>, then the next
     * filter, if any, will be invoked. If the decision is
     * <code>{@link FilterReply#ACCEPT}</code> then the event will be logged without
     * consulting with other filters in the chain.
     *
     * @param event The event to decide upon.
     */
    @Override
    public FilterReply decide(ILoggingEvent event)
    {
        if (Environment.isProduction && event.getLevel().isGreaterOrEqual(Level.ERROR)) {
            return FilterReply.ACCEPT;
        }

        if (Environment.isProduction && !event.getLevel().isGreaterOrEqual(Level.ERROR)) {
            return FilterReply.DENY;
        }

        return FilterReply.NEUTRAL;
    }
}
