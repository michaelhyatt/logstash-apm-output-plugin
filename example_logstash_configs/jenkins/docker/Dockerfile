ARG tag

FROM docker.elastic.co/logstash/logstash:$tag

COPY ./docker/logstash-output-elastic_apm_server-0.1.0.plugin /tmp/logstash-output-elastic_apm_server-0.1.0.gem

RUN /usr/share/logstash/bin/logstash-plugin install --no-verify --local /tmp/logstash-output-elastic_apm_server-0.1.0.gem
