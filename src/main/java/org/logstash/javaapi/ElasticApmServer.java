package org.logstash.javaapi;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

import co.elastic.apm.attach.ElasticApmAttacher;
import co.elastic.logstash.api.Configuration;
import co.elastic.logstash.api.Context;
import co.elastic.logstash.api.Event;
import co.elastic.logstash.api.LogstashPlugin;
import co.elastic.logstash.api.Output;
import co.elastic.logstash.api.PluginConfigSpec;

// class name must match plugin name
@LogstashPlugin(name = "elastic_apm_server")
public class ElasticApmServer implements Output {

	public static final PluginConfigSpec<String> PREFIX_CONFIG = PluginConfigSpec.stringSetting("prefix", "");

	private final String id;

	private final CountDownLatch done = new CountDownLatch(1);

	// all plugins must provide a constructor that accepts id, Configuration, and
	// Context
	public ElasticApmServer(final String id, final Configuration configuration, final Context context) {
		
		ElasticApmAttacher.attach();

		this.id = id;
	}

	@Override
	public void output(final Collection<Event> events) {

	}

	@Override
	public void stop() {
		done.countDown();
	}

	@Override
	public void awaitStop() throws InterruptedException {
		done.await();
	}

	@Override
	public Collection<PluginConfigSpec<?>> configSchema() {
		// should return a list of all configuration options for this plugin
		return Collections.singletonList(PREFIX_CONFIG);
	}

	@Override
	public String getId() {
		return id;
	}
}
