# Docker file to build datomic transactor, no default command run
#

### Start with CentOS-7
FROM centos:7
# Install yum packages
RUN yum -y install wget unzip java-1.8.0-openjdk less
RUN yum update -y && yum clean all

### Configure datomic user
RUN groupadd -r datomic -g 1000 && useradd -u 1000 -r -g datomic -m -d /opt/datomic -s /sbin/nologin -c "Datomic user" datomic && \
    chmod 755 /opt/datomic
# Set the working directory to datomic user home directory
WORKDIR /opt/datomic

# Specify the user which should be used to execute all commands below
USER datomic
# Set the JAVA_HOME variable to make it clear where Java is located
ENV JAVA_HOME /usr/lib/jvm/java


### Install Datomic
# Set the DATOMIC_VERSION env variable
ENV DATOMIC_VERSION 0.9.5394

# Expose the ports we're interested in
EXPOSE 4334

# Install gradle for building
RUN cd $HOME \
    && wget --http-user=benjamin.flynn@bjondinc.com --http-password=33fbefca-2d90-40f0-8a7c-c6416c69d470 https://my.datomic.com/repo/com/datomic/datomic-pro/$DATOMIC_VERSION/datomic-pro-$DATOMIC_VERSION.zip -O datomic-pro-$DATOMIC_VERSION.zip \
    && unzip datomic-pro-$DATOMIC_VERSION.zip \
    && mv datomic-pro-$DATOMIC_VERSION transactor \
    && rm -f datomic-pro-$DATOMIC_VERSION.zip
