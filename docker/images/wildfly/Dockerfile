# Docker file to build standalone bjond_health app with external postgres instance
#

### Start with CentOS-7
FROM centos:7
# Install other packages
RUN yum -y install git wget python-pip bzip2 epel-release
RUN yum -y install nodejs npm
### Install postgres for psql and pg commands
RUN yum install -y postgresql

### Configure jboss user
# Install packages necessary to run EAP
RUN yum update -y && yum -y install xmlstarlet saxon augeas bsdtar unzip && yum clean all
# Create a user and group used to launch processes
# The user ID 1000 is the default for the first "regular" user on Fedora/RHEL,
# so there is a high chance that this ID will be equal to the current user
# making it easier to use volumes (no permission issues)
RUN groupadd -r jboss -g 1000 && useradd -u 1000 -r -g jboss -m -d /opt/jboss -s /sbin/nologin -c "JBoss user" jboss && \
    chmod 755 /opt/jboss
# Set the working directory to jboss' user home directory
WORKDIR /opt/jboss

### Install jdk 8
# Install necessary packages
RUN yum -y install java-1.8.0-openjdk-devel && yum clean all
# Specify the user which should be used to execute all commands below
USER jboss
# Set the JAVA_HOME variable to make it clear where Java is located
ENV JAVA_HOME /usr/lib/jvm/java


### Install Wildfly
# Set the WILDFLY_VERSION env variable
ENV WILDFLY_VERSION 9.0.2.Final
ENV WILDFLY_SHA1 75738379f726c865d41e544e9b61f7b27d2853c7
ENV JBOSS_HOME /opt/jboss/wildfly
# Add the WildFly distribution to /opt, and make wildfly the owner of the extracted tar content
# Make sure the distribution is available from a well-known place
RUN cd $HOME \
    && curl -O https://download.jboss.org/wildfly/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.tar.gz \
    && sha1sum wildfly-$WILDFLY_VERSION.tar.gz | grep $WILDFLY_SHA1 \
    && tar xf wildfly-$WILDFLY_VERSION.tar.gz \
    && mv $HOME/wildfly-$WILDFLY_VERSION $JBOSS_HOME \
    && rm wildfly-$WILDFLY_VERSION.tar.gz

# Ensure signals are forwarded to the JVM process correctly for graceful shutdown
ENV LAUNCH_JBOSS_IN_BACKGROUND true

# Expose the ports we're interested in
EXPOSE 8080

# Install gradle for building
RUN cd $HOME \
    && wget https://services.gradle.org/distributions/gradle-3.2.1-bin.zip \
    && unzip gradle-3.2.1-bin.zip \
    && rm -f gradle-3.2.1-bin.zip
ENV PATH=$PATH:/opt/jboss/gradle-3.2.1/bin

# Install Postgres JDBC driver
COPY postgresql93.tgz $JBOSS_HOME/modules/system/layers/base/
RUN cd $JBOSS_HOME/modules/system/layers/base \
    && tar xvzf postgresql93.tgz \
    && rm -f postgresql93.tgz
# Copy custom standalone.xml
COPY standalone.xml $JBOSS_HOME/standalone/configuration/
COPY mgmt-users.properties $JBOSS_HOME/standalone/configuration/

###
# Set the default command to run on boot
# This will boot WildFly in the standalone mode and bind to all interface
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
