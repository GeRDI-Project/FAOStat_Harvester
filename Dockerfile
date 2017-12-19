# GeRDI Harvester Image for 'FAOSTAT'

FROM jetty:9.4.7-alpine

COPY \/target\/*.war $JETTY_BASE\/webapps\/faostat.war

EXPOSE 8080