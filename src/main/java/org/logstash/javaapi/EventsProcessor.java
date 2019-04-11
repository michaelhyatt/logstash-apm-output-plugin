package org.logstash.javaapi;

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

		String eventType = event.getField("apm_command").toString();
		String name = event.getField("apm_name").toString();
		String id = event.getField("apm_id").toString();
		Timestamp timestamp = (Timestamp) event.getField("apm_timestamp");

		logger.debug("Received eventType=" + eventType + ", id=" + id + ", name=" + name + ", timestamp=" + timestamp.toEpochMilli());

		if ("TRANSACTION_START".equals(eventType)) {
			Transaction transaction = ElasticApm.startTransaction();
			transaction.setName(name);
			transaction.addLabel("apm_id", id);
//			transaction.setStartTimestamp(timestamp.toEpochMilli() * 1_000);
			txStore.push(transaction);
		} else if ("SPAN_START".equals(eventType)) {
			Span span = txStore.peek().startSpan();
			span.setName(name);
			span.addLabel("apm_id", id);
//			span.setStartTimestamp(timestamp.toEpochMilli() * 1_000);
			txStore.push(span);
		} else if ("TRANSACTION_END".equals(eventType) || "SPAN_END".equals(eventType)) {
//			txStore.pop().end(timestamp.toEpochMilli() * 1_000);
			txStore.pop().end();
		} 
	}
}
