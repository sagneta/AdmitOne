FROM tomcat:7

LABEL OpenEMPI-Version="3.1.0" \
      Description="Docker image for the OpenEMPI application"

# Install postgres to run psql commands
RUN apt-get update \
    && apt-get install -y postgresql

# Get OpenEMPI binaries from GIT
ENV OPENEMPI_VERSION=3.1.0
ENV OPENEMPI_SHA1=fa0aadaac15bc6ca7fb56378afb675f817b436f9

RUN cd ./ \
    && wget https://s3.amazonaws.com/bjondhealth.fileshare/docker-openempi-$OPENEMPI_VERSION.tar.gz \
    && shasum  docker-openempi-$OPENEMPI_VERSION.tar.gz | grep $OPENEMPI_SHA1 \
    && tar xf docker-openempi-$OPENEMPI_VERSION.tar.gz \
    && rm -f docker-openempi-$OPENEMPI_VERSION.tar.gz

# Copy OpenEMPI files into their corresponding directories
RUN mkdir -p /opt/openempi-$OPENEMPI_VERSION \
    && mkdir -p /etc/opt/openempi-$OPENEMPI_VERSION \
    && mv ./docker-openempi-$OPENEMPI_VERSION/application/openempi-$OPENEMPI_VERSION/lib /opt/openempi-$OPENEMPI_VERSION/ \
    && mv ./docker-openempi-$OPENEMPI_VERSION/application/openempi-$OPENEMPI_VERSION/conf /etc/opt/openempi-$OPENEMPI_VERSION/ \
    && mkdir -p /usr/local/tomcat/conf/Catalina/localhost \
    && cp /etc/opt/openempi-$OPENEMPI_VERSION/conf/openempi-admin.xml /usr/local/tomcat/conf/Catalina/localhost

COPY ./setenv.sh /usr/local/tomcat/bin/setenv.sh
COPY ./jdbc.properties /opt/openempi-3.1.0/conf/jdbc.properties
COPY ./wait-for-it.sh ./wait-for-it.sh
RUN chmod +x ./wait-for-it.sh

# Expose port 8080 outside of Docker
EXPOSE 8080

# Initialization scripts
CMD catalina.sh run