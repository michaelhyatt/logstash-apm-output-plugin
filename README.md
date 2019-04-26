# logstash-apm-output-plugin
Java output plugin for Logstash to communicate to Elastic APM server

## Commands to run it
### Build gem
```
/usr/bin/gem build logstash-output-elastic_apm_server.gemspec
```

### Install plugin
```
./bin/logstash-plugin install --no-verify --local ../logstash-output-apm/logstash-output-elastic_apm_server-0.0.1.gem
```

### Run simple_test.conf
```
./bin/logstash --java-execution -f ../logstash-output-apm/simple_test.conf -w 1 -b 1
```