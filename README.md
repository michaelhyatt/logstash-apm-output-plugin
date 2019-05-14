# logstash-apm-output-plugin
Java output plugin for Logstash to communicate to Elastic APM server.

## Prerequisites
You will need JDK and I won't go into how to install it here.

Install Logstash, I tested it with version 6.7.2
https://www.elastic.co/downloads/past-releases/logstash-oss-6-7-2

More info on how to install Logstash:
https://www.elastic.co/guide/en/logstash/6.7/installing-logstash.html

You will need ruby gem utility as well, if you are planning to build the code. Install it with the rest of ruby, by something like `yum install ruby`, or whatever the command is on Windows.

## Commands to run it
### Build gem
If you don't feel like installing Ruby and doing the build, you can download the latest `.gem` file from the [Releases](https://github.com/michaelhyatt/logstash-apm-output-plugin/releases) section.
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

## Limitations
* It doesn't handle multithreading yet, use it with `-w 1 and -b 1` command line options to ensure there is only one thread processing the messages. I promise to make it work with parallel threads soon...
