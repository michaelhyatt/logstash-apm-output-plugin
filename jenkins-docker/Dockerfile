ARG tag

FROM docker.elastic.co/logstash/logstash:$tag

COPY logstash-output-elastic_apm_server-0.0.2.gem /tmp

RUN /usr/share/logstash/bin/logstash-plugin install --no-verify --local /tmp/logstash-output-elastic_apm_server-0.0.2.gem
