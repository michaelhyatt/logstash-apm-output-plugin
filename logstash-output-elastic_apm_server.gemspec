PLUGIN_VERSION = File.read(File.expand_path(File.join(File.dirname(__FILE__), "VERSION"))).strip unless defined?(PLUGIN_VERSION)

Gem::Specification.new do |s|
  s.name            = 'logstash-output-elastic_apm_server'
  s.version         = PLUGIN_VERSION
  s.licenses        = ['Apache-2.0']
  s.summary         = "Elastic APM Server output"
  s.description     = ""
  s.authors         = ['Michael Hyatt']
  s.email           = 'mhyatt8080@gmail.com'
  s.homepage        = "http://www.github.com/michaelhyatt/logstash-output-apm"
  s.require_paths = ['lib', 'vendor/jar-dependencies']

  # Files
  s.files = Dir["lib/**/*","spec/**/*","*.gemspec","*.md","CONTRIBUTORS","Gemfile","LICENSE","NOTICE.TXT", "vendor/jar-dependencies/**/*.jar", "vendor/jar-dependencies/**/*.rb", "VERSION", "docs/**/*"]

  # Special flag to let us know this is actually a logstash plugin
  s.metadata = { 'logstash_plugin' => 'true', 'logstash_group' => 'output'}

  # Gem dependencies
  s.add_runtime_dependency "logstash-core-plugin-api", ">= 1.60", "<= 2.99"
  s.add_runtime_dependency 'jar-dependencies'

  s.add_development_dependency 'logstash-devutils'
end
