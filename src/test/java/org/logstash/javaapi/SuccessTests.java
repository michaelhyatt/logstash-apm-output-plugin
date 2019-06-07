package org.logstash.javaapi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.logstash.Event;
import org.logstash.Timestamp;

import co.elastic.apm.agent.impl.transaction.Span;
import co.elastic.apm.agent.impl.transaction.Transaction;

public class SuccessTests extends BaseApmTests {

	@Test
	public void testOneTransactionWithOneSpan() {

		Timestamp timestamp1 = new Timestamp();
		Event eventTxCreate = createEvent(EventsProcessor.TRANSACTION_START, "simple-test-1-id",
				"simple-test-tx-name-1", timestamp1);

		Event eventSpanCreate = createEvent(EventsProcessor.SPAN_START, "simple-test-1-id", "simple-test-span-name-1",
				timestamp1);

		Event eventSpanEnd = createEvent(EventsProcessor.SPAN_END, "simple-test-1-id", "simple-test-span-name-1",
				timestamp1);

		Event eventTxEnd = createEvent(EventsProcessor.TRANSACTION_END, "simple-test-1-id", "simple-test-tx-name-1",
				timestamp1);

		EventsProcessor.process(eventTxCreate);
		EventsProcessor.process(eventSpanCreate);
		EventsProcessor.process(eventSpanEnd);
		EventsProcessor.process(eventTxEnd);

		assertEquals(1, transactions.size());
		assertEquals(1, spans.size());
		assertEquals(0, errors.size());

		Transaction transaction = transactions.get(0);
		assertEquals("simple-test-tx-name-1", transaction.getName().toString());
		assertEquals(timestamp1.toEpochMilli() * 1_000, transaction.getTimestamp());
		assertEquals(1.0, transaction.getDuration(), 0);

		Span span = spans.get(0);
		assertEquals("simple-test-span-name-1", span.getName().toString());
		assertEquals(timestamp1.toEpochMilli() * 1_000, span.getTimestamp());
		assertEquals(0.0, span.getDuration(), 0);

	}

	private Event createEvent(String command, String id, String name, Timestamp timestamp) {
		Event event = new Event();

		event.setField(EventsProcessor.APM_COMMAND, command);
		event.setField(EventsProcessor.APM_ID, id);
		event.setField(EventsProcessor.APM_NAME, name);
		event.setField(EventsProcessor.APM_TIMESTAMP, timestamp);

		return event;
	}

	@Test
	public void testTwoTransactionWithOneSpan() {

		Timestamp timestamp1 = new Timestamp();
		Event eventTxCreate1 = createEvent(EventsProcessor.TRANSACTION_START, "simple-test-1-id",
				"simple-test-tx-name-1", timestamp1);

		Event eventSpanCreate1 = createEvent(EventsProcessor.SPAN_START, "simple-test-1-id", "simple-test-span-name-1",
				timestamp1);

		Event eventSpanEnd1 = createEvent(EventsProcessor.SPAN_END, "simple-test-1-id", "simple-test-span-name-1",
				timestamp1);

		Event eventTxEnd1 = createEvent(EventsProcessor.TRANSACTION_END, "simple-test-1-id", "simple-test-tx-name-1",
				timestamp1);

		Timestamp timestamp2 = new Timestamp();
		Event eventTxCreate2 = createEvent(EventsProcessor.TRANSACTION_START, "simple-test-2-id",
				"simple-test-tx-name-2", timestamp2);

		Event eventSpanCreate2 = createEvent(EventsProcessor.SPAN_START, "simple-test-2-id", "simple-test-span-name-2",
				timestamp2);

		Event eventSpanEnd2 = createEvent(EventsProcessor.SPAN_END, "simple-test-2-id", "simple-test-span-name-2",
				timestamp2);

		Event eventTxEnd2 = createEvent(EventsProcessor.TRANSACTION_END, "simple-test-2-id", "simple-test-tx-name-2",
				timestamp2);

		EventsProcessor.process(eventTxCreate1);
		EventsProcessor.process(eventTxCreate2);
		EventsProcessor.process(eventSpanCreate2);
		EventsProcessor.process(eventSpanCreate1);
		EventsProcessor.process(eventSpanEnd2);
		EventsProcessor.process(eventSpanEnd1);
		EventsProcessor.process(eventTxEnd1);
		EventsProcessor.process(eventTxEnd2);

		assertEquals(2, transactions.size());
		assertEquals(2, spans.size());
		assertEquals(0, errors.size());

		Transaction transaction1 = transactions.get(0);
		assertEquals("simple-test-tx-name-1", transaction1.getName().toString());
		assertEquals(timestamp1.toEpochMilli() * 1_000, transaction1.getTimestamp());
		assertEquals(1.0, transaction1.getDuration(), 0);

		Transaction transaction2 = transactions.get(1);
		assertEquals("simple-test-tx-name-2", transaction2.getName().toString());
		assertEquals(timestamp2.toEpochMilli() * 1_000, transaction2.getTimestamp());
		assertEquals(1.0, transaction2.getDuration(), 0);

		Span span2 = spans.get(0);
		assertEquals("simple-test-span-name-2", span2.getName().toString());
		assertEquals(timestamp2.toEpochMilli() * 1_000, span2.getTimestamp());
		assertEquals(0.0, span2.getDuration(), 0);

		Span span1 = spans.get(1);
		assertEquals("simple-test-span-name-1", span1.getName().toString());
		assertEquals(timestamp1.toEpochMilli() * 1_000, span1.getTimestamp());
		assertEquals(0.0, span1.getDuration(), 0);

	}

}
