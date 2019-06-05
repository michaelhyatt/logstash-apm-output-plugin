package org.logstash.javaapi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.logstash.Event;
import org.logstash.Timestamp;

public class SimpleTest extends BaseApmTest {

	@Test
	public void test() {

		Event eventTxCreate = new Event();

		eventTxCreate.setField(EventsProcessor.APM_COMMAND, EventsProcessor.TRANSACTION_START);
		eventTxCreate.setField(EventsProcessor.APM_ID, "simple-test-id-1");
		eventTxCreate.setField(EventsProcessor.APM_NAME, "simple-test-tx-name-1");
		eventTxCreate.setField(EventsProcessor.APM_TIMESTAMP, new Timestamp());

		Event eventSpanCreate = eventTxCreate.clone();
		eventSpanCreate.setField(EventsProcessor.APM_COMMAND, EventsProcessor.SPAN_START);
		eventSpanCreate.setField(EventsProcessor.APM_NAME, "simple-test-span-name-1");
		
		Event eventSpanEnd = eventSpanCreate.clone();
		eventSpanEnd.setField(EventsProcessor.APM_COMMAND, EventsProcessor.SPAN_END);

		Event eventTxEnd = eventSpanCreate.clone();
		eventTxEnd.setField(EventsProcessor.APM_COMMAND, EventsProcessor.TRANSACTION_END);

		EventsProcessor.process(eventTxCreate);
		EventsProcessor.process(eventSpanCreate);
		EventsProcessor.process(eventSpanEnd);
		EventsProcessor.process(eventTxEnd);

		assertEquals(1, tx.size());
		assertEquals(1, spans.size());
		assertEquals("simple-test-tx-name-1", tx.get(0).getName().toString());
		assertEquals("simple-test-span-name-1", spans.get(0).getName().toString());

	}

}
