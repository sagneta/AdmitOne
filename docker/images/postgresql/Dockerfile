# Originally ported from
#   https://github.com/CentOS/CentOS-Dockerfiles/tree/master/postgres/centos7

FROM centos:7

RUN yum -y update; yum clean all
RUN yum -y install sudo epel-release; yum clean all
RUN yum -y install http://yum.postgresql.org/9.5/redhat/rhel-7-x86_64/pgdg-centos95-9.5-3.noarch.rpm
RUN yum -y install postgresql95-server postgresql95 postgresql95-contrib; yum clean all

EXPOSE 5432

#Sudo requires a tty. fix that.
RUN sed -i 's/.*requiretty$/#Defaults requiretty/' /etc/sudoers

# Postgres setup
ADD ./postgresql-setup /usr/bin/postgresql-setup
RUN chmod +x /usr/bin/postgresql-setup
RUN /usr/bin/postgresql-setup initdb

# User creation scripts
ADD ./create_postgres_user.sh ./create_postgres_user.sh
RUN chmod +x ./create_postgres_user.sh

# Natural sort order extention
ADD ./postgresql.conf /var/lib/pgsql/9.5/data/postgresql.conf
ADD pg_natural_sort_order--1.0.1.sql /usr/pgsql-9.5/share/extension/pg_natural_sort_order--1.0.1.sql
ADD pg_natural_sort_order.control /usr/pgsql-9.5/share/extension/pg_natural_sort_order.control
ADD pg_natural_sort_order.so /usr/pgsql-9.5/lib/pg_natural_sort_order.so

RUN chown -v postgres.postgres /var/lib/pgsql/9.5/data/postgresql.conf

RUN echo "host    all             all             0.0.0.0/0               md5" > /var/lib/pgsql/9.5/data/pg_hba.conf
RUN echo "local   all             all                                     trust" >> /var/lib/pgsql/9.5/data/pg_hba.conf

# Create bjondhealth DB and Role
ENV DB_USER=bjondhealth
ENV DB_PASS=bjondhealth
ENV DB_NAME=bjondhealth
RUN ./create_postgres_user.sh

# Create openempi DB and Role
ENV DB_USER=openempi
ENV DB_PASS=openempi
ENV DB_NAME=openempi
RUN ./create_postgres_user.sh

# Copy openempi DB init scripts
COPY ./openempi_init_scripts ./openempi_init_scripts
RUN chmod +x -R ./openempi_init_scripts
COPY ./openempi_db_init.sh ./openempi_db_init.sh
RUN chmod +x ./openempi_db_init.sh
COPY ./postgres.sh /postgres.sh
RUN chmod +x ./postgres.sh

USER postgres

CMD ["/bin/bash", "-c", "/postgres.sh"]
