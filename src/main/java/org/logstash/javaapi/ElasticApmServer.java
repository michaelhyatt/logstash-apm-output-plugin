package org.logstash.javaapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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

	public static final PluginConfigSpec<String> SERVER_URLS = PluginConfigSpec.requiredStringSetting("server_urls");
	public static final PluginConfigSpec<String> SERVICE_NAME = PluginConfigSpec.requiredStringSetting("service_name");
	public static final PluginConfigSpec<String> LOG_LEVEL = PluginConfigSpec.stringSetting("log_level", "INFO");

	private final String id;

	private final CountDownLatch done = new CountDownLatch(1);

	// all plugins must provide a constructor that accepts id, Configuration, and
	// Context
	public ElasticApmServer(final String id, final Configuration configuration, final Context context) {
		
		Map<String, String> configMap = new HashMap<String, String>();
		configMap.put("server_urls", configuration.get(SERVER_URLS));
		configMap.put("service_name", configuration.get(SERVICE_NAME));
		configMap.put("log_level", configuration.get(LOG_LEVEL));

		ElasticApmAttacher.attach(configMap);
		
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
		Collection<PluginConfigSpec<?>> list = new ArrayList<PluginConfigSpec<?>>();
		// should return a list of all configuration options for this plugin
		Collections.addAll(list, 
				SERVER_URLS, 
				SERVICE_NAME
				);
		return list;
	}

	@Override
	public String getId() {
		return id;
	}
}
