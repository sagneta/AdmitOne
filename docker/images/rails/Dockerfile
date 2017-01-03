# Docker file to build adapter rails app with external postgres instance
#

### Start with CentOS-7
FROM centos:7

### Install require tools
RUN yum update -y && yum install -y wget epel-release git postgresql postgresql-devel make gcc openssl-devel zlib-devel
# nodejs is installed from epel, repo so must be a separate command
RUN yum -y install nodejs && yum clean all

### Configure rails user
# Create a user and group used to launch processes
# The user ID 1000 is the default for the first "regular" user on Fedora/RHEL,
# so there is a high chance that this ID will be equal to the current user
# making it easier to use volumes (no permission issues)
RUN groupadd -r rails -g 1000 && useradd -u 1000 -r -g rails -m -d /opt/rails -s /sbin/nologin -c "Rails user" rails && \
    chmod 755 /opt/rails
# Set the working directory to rails' user home directory
WORKDIR /opt/rails

### Install Ruby
# Set the RUBY_VERSION env variable
ENV RUBY_VERSION_MAJOR 2.3
ENV RUBY_VERSION_MINOR 1
RUN cd \
    && wget  https://cache.ruby-lang.org/pub/ruby/2.3/ruby-$RUBY_VERSION_MAJOR.$RUBY_VERSION_MINOR.tar.gz \
    && tar xvzf ruby-$RUBY_VERSION_MAJOR.$RUBY_VERSION_MINOR.tar.gz \
    && cd ruby-$RUBY_VERSION_MAJOR.$RUBY_VERSION_MINOR \
    && ./configure \
    && make \
    && make install \
    && rm -rvf ruby-$RUBY_VERSION_MAJOR.$RUBY_VERSION_MINOR*

# Install bundler and rake gems
RUN gem install rake bundler 
RUN gem install rails -v '5.0.0.1'

# Specify the user which should be used to execute all commands below
USER rails

# Expose the ports we're interested in
EXPOSE 3000
