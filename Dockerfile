# GeRDI Harvester Image for 'FAOSTAT'

FROM jetty:9.4.7-alpine

# copy war file
COPY target/*.war $JETTY_BASE/webapps/faostat.war

# create log file folder with sufficient permissions
USER root
RUN mkdir -p /var/log/harvester
RUN chown jetty:jetty /var/log/harvester
USER jetty

EXPOSE 8080