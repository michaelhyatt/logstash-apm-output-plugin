package org.logstash.javaapi;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import co.elastic.apm.agent.bci.ElasticApmAgent;
import co.elastic.apm.agent.impl.ElasticApmTracerBuilder;
import co.elastic.apm.agent.impl.error.ErrorCapture;
import co.elastic.apm.agent.impl.transaction.Span;
import co.elastic.apm.agent.impl.transaction.Transaction;
import co.elastic.apm.agent.report.Reporter;
import co.elastic.apm.attach.bytebuddy.agent.ByteBuddyAgent;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseApmTests {

	protected List<Span> spans;
	protected List<Transaction> transactions;
	protected List<ErrorCapture> errors;

	@Mock
	protected Reporter reporter;

	public BaseApmTests() {
		super();
	}

	@Before
	public void setup() {

		transactions = new ArrayList<Transaction>();
		spans = new ArrayList<Span>();
		errors = new ArrayList<ErrorCapture>();

		Mockito.doAnswer(new Answer<Span>() {
			@Override
			public Span answer(InvocationOnMock invocation) throws Throwable {
				spans.add(invocation.getArgumentAt(0, Span.class));
				return null;
			}
		}).when(reporter).report(Mockito.any(Span.class));

		Mockito.doAnswer(new Answer<Transaction>() {
			@Override
			public Transaction answer(InvocationOnMock invocation) throws Throwable {
				transactions.add(invocation.getArgumentAt(0, Transaction.class));
				return null;
			}
		}).when(reporter).report(Mockito.any(Transaction.class));

		Mockito.doAnswer(new Answer<ErrorCapture>() {
			@Override
			public ErrorCapture answer(InvocationOnMock invocation) throws Throwable {
				errors.add(invocation.getArgumentAt(0, ErrorCapture.class));
				return null;
			}
		}).when(reporter).report(Mockito.any(ErrorCapture.class));

		ElasticApmAgent.initInstrumentation(new ElasticApmTracerBuilder().reporter(reporter).build(),
				ByteBuddyAgent.install());

	}

	@After
	public void tearDown() {
		ElasticApmAgent.reset();
	}

}