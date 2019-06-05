package org.logstash.javaapi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.logstash.Event;
import org.logstash.Timestamp;

import co.elastic.apm.agent.impl.transaction.Span;
import co.elastic.apm.agent.impl.transaction.Transaction;

public class SimpleTest extends BaseApmTest {

	@Test
	public void testOneTransactionWithOneSpan() {

		Event eventTxCreate = new Event();

		eventTxCreate.setField(EventsProcessor.APM_COMMAND, EventsProcessor.TRANSACTION_START);
		eventTxCreate.setField(EventsProcessor.APM_ID, "simple-test-id-1");
		eventTxCreate.setField(EventsProcessor.APM_NAME, "simple-test-tx-name-1");
		Timestamp timestamp = new Timestamp();
		eventTxCreate.setField(EventsProcessor.APM_TIMESTAMP, timestamp);

		Event eventSpanCreate = eventTxCreate.clone();
		eventSpanCreate.setField(EventsProcessor.APM_COMMAND, EventsProcessor.SPAN_START);
		eventSpanCreate.setField(EventsProcessor.APM_NAME, "simple-test-span-name-1");

		Event eventSpanEnd = eventTxCreate.clone();
		eventSpanEnd.setField(EventsProcessor.APM_COMMAND, EventsProcessor.SPAN_END);

		Event eventTxEnd = eventTxCreate.clone();
		eventTxEnd.setField(EventsProcessor.APM_COMMAND, EventsProcessor.TRANSACTION_END);

		EventsProcessor.process(eventTxCreate);
		EventsProcessor.process(eventSpanCreate);
		EventsProcessor.process(eventSpanEnd);
		EventsProcessor.process(eventTxEnd);

		assertEquals(1, transactions.size());
		assertEquals(1, spans.size());
		assertEquals(0, errors.size());

		Transaction transaction = transactions.get(0);
		assertEquals("simple-test-tx-name-1", transaction.getName().toString());
		assertEquals(timestamp.toEpochMilli() * 1_000, transaction.getTimestamp());
		assertEquals(1.0, transaction.getDuration(), 0);

		Span span = spans.get(0);
		assertEquals("simple-test-span-name-1", span.getName().toString());
		assertEquals(timestamp.toEpochMilli() * 1_000, span.getTimestamp());
		assertEquals(0.0, span.getDuration(), 0);

	}

}
