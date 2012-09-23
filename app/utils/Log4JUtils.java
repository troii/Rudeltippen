package utils;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.TriggeringEventEvaluator;

public class Log4JUtils implements TriggeringEventEvaluator {

	@Override
	public boolean isTriggeringEvent(final LoggingEvent event) {
		final String message = event.getMessage().toString();
		boolean trigger = true;

		if (("Eine vorhandene Verbindung wurde vom Remotehost geschlossen").contains(message)) {
			trigger = false;
		}

		return trigger;
	}
}