# logstash-apm-output-plugin
Java output plugin for Logstash to communicate to Elastic APM server.

## Prerequisites
You will need JDK and I won't go into how to install it here.

ELK stack version 6.7.2 with [installed and configured APM server](https://www.elastic.co/guide/en/apm/server/6.7/installing.html). [Elastic Cloud trial](https://www.elastic.co/cloud/elasticsearch-service/signup) can be a good option. Installation instructions:
https://www.elastic.co/guide/en/elasticsearch/reference/6.7/getting-started-install.html

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
./bin/logstash-plugin install --no-verify --local ../logstash-output-apm/logstash-output-elastic_apm_server-0.0.2.gem
```

### Run simple_test.conf
```
./bin/logstash --java-execution -f ../logstash-output-apm/example_logstash_configs/uipath_simple_test.conf -w 1 -b 1
```

### Sample flow
Check out simple_test.conf for an example of how the output plugin operates.

### Plugin functionality overview
Plugin relies on special fields being populated to create transactions and spans:
* apm_command: can be one of
  * TRANSACTION_START - required for the top level transaction. Should correspond to the start of overall transaction.
  * SPAN_START - start of individual task
  * SPAN_END - end of individual task
  * TRANSACTION_END - end of overall transaction
  * EXCEPTION - log the message as exception on current transaction
* apm_name - transaction or span name.
* apm_timestamp is when Transaction/Span start/end occurred.
* All the other fields in the logstash event will be translated into Transaction or Span tags, so make sure to filter out things that shouldn't be logged.

### To restore the sample data into Elasticsearch
`sample_data` directory contains a sample snapshot that can be restored into Elasticsearch to work together with the sample flow.
```
POST _snapshot/backup1/snapshot1/_restore
{
  "indices": "anz-2019.03"
}

POST _snapshot/backup1
{
  "type": "fs",
  "settings": {
    "compress": true,
    "location": "/<path_to_snapshots>/elasticbackup"
  }
}
```

## Limitations
* It doesn't handle multithreading yet, use it with `-w 1 and -b 1` command line options to ensure there is only one thread processing the messages. I promise to make it work with parallel threads soon...
