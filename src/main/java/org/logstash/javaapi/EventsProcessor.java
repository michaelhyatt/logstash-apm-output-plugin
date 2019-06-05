package org.logstash.javaapi;

import java.util.Map;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.logstash.Timestamp;

import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Span;
import co.elastic.apm.api.Transaction;
import co.elastic.logstash.api.Event;

public class EventsProcessor {

	final static Logger logger = LogManager.getLogger(EventsProcessor.class);

	private static Stack<Span> txStore = new Stack<Span>();

	public static void process(Event event) {

		String eventType = event.getField(APM_COMMAND).toString();
		String name = event.getField(APM_NAME).toString();
		String id = event.getField(APM_ID).toString();
		Timestamp timestamp = (Timestamp) event.getField(APM_TIMESTAMP);

		logger.info("Received eventType=" + eventType + ", id=" + id + ", name=" + name + ", timestamp="
				+ timestamp.toEpochMilli());

		// Only allow empty stack for transaction starts
		if (txStore.isEmpty() && !TRANSACTION_START.equals(eventType))
			return;

		if (TRANSACTION_START.equals(eventType)) {
			Transaction transaction = ElasticApm.startTransaction();
			transaction.setName(name);
			createSpanTags(transaction, event);
			transaction.setStartTimestamp(timestamp.toEpochMilli() * 1_000);
			txStore.push(transaction);
		} else if (SPAN_START.equals(eventType)) {

			Span span = txStore.peek().startSpan();
			span.setName(name);
			createSpanTags(span, event);
			span.setStartTimestamp(timestamp.toEpochMilli() * 1_000);
			txStore.push(span);
		} else if (EXCEPTION.equals(eventType)) {

			Span span = txStore.peek().startSpan();
			span.setName(name);
			createSpanTags(span, event);
			span.setStartTimestamp(timestamp.toEpochMilli() * 1_000);
			Throwable throwable = new RuntimeException(name);
			span.captureException(throwable);
			span.end(timestamp.toEpochMilli() * 1_000 + 1_000);

		} else if (FLUSH.equals(eventType)) {

			// End all spans
			int size = txStore.size() - 1;
			for (int i = 0; i < size; i++) {
				txStore.pop().end(timestamp.toEpochMilli() * 1_000 + i * 1_000 + 1_000);
			}

		} else if (SPAN_END.equals(eventType)) {
			txStore.pop().end(timestamp.toEpochMilli() * 1_000);

		} else if (TRANSACTION_END.equals(eventType)) {
			int size = txStore.size();
			for (int i = 0; i < size; i++) {
				txStore.pop().end(timestamp.toEpochMilli() * 1_000 + i * 1_000 + 1_000);
			}
		}
	}

	private static void createSpanTags(Span span, Event event) {
		Map<String, Object> data = event.getData();
		data.keySet().forEach(key -> {
			Object value = data.get(key);
			span.addLabel(key, value.toString());
		});
	}

	protected static final String APM_TIMESTAMP = "apm_timestamp";

	protected static final String APM_ID = "apm_id";

	protected static final String APM_NAME = "apm_name";

	protected static final String APM_COMMAND = "apm_command";

	protected static final String TRANSACTION_END = "TRANSACTION_END";

	protected static final String SPAN_END = "SPAN_END";

	protected static final String FLUSH = "FLUSH";

	protected static final String EXCEPTION = "EXCEPTION";

	protected static final String SPAN_START = "SPAN_START";

	protected static final String TRANSACTION_START = "TRANSACTION_START";
}
