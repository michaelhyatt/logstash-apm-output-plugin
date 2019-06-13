ARG tag

FROM docker.elastic.co/logstash/logstash:$tag

COPY ./jenkins-docker/logstash-output-elastic_apm_server-0.0.2.plugin /tmp/logstash-output-elastic_apm_server-0.0.2.gem

RUN /usr/share/logstash/bin/logstash-plugin install --no-verify --local /tmp/logstash-output-elastic_apm_server-0.0.2.gem
