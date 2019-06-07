package org.logstash.javaapi;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import co.elastic.apm.api.Span;
import co.elastic.apm.api.Transaction;

/**
 * @author michaelhyatt
 * 
 *         This is a thread-safe map store of transaction and span maps (LIFO)
 *         to keep ensure asynchronous callbacks can find transactions by Mule
 *         rootMessageId to finish them.
 *
 */
public class SpanStore {

	private Map<String, Stack<Span>> map = new ConcurrentHashMap<>();

	public boolean isEmpty(String id) {
		return map.get(id) == null;
	}

	public void pushTransaction(String id, Transaction transaction) {

		if (!this.isEmpty(id))
			throw new IllegalArgumentException("Key " + id + " already exists");

		Stack<Span> value = new Stack<Span>();
		value.push(transaction);
		map.put(id, value);
	}

	public void pushSpan(String id, Span span) {

		map.putIfAbsent(id, new Stack<Span>());
		map.get(id).push(span);
	}

	public Span peek(String id) {
		return map.get(id).peek();
	}

	public Span pop(String id) {
		Stack<Span> stack = map.getOrDefault(id, new Stack<Span>());
		
		Span value = stack.pop();

		if (stack.isEmpty())
			map.remove(id);

		return value;
	}

	public int size(String id) {
		return map.getOrDefault(id, new Stack<Span>()).size();
	}

}
