ARG BASE_IMAGE
FROM $BASE_IMAGE AS build-stage

RUN apt-get update -y && apt-get install -y postgresql-client

#Base Postgres instance
ENV POSTGRES_HOSTNAME localhost
ENV POSTGRES_DB postgres
ENV POSTGRES_USER root
ENV POSTGRES_PASSWORD password

# Fineract Database details
ENV FINERACT_DB_USER postgres
ENV FINERACT_DB_PASS password
ENV FINERACT_DATABASES fineract_tenants,fineract_default,fineract_de

ENV MULTI_TENANT_SETUP false
ENV ADDITIONAL_FINERACT_TENANTS fineract_us

# CRP Database details
ENV CRP_DATABASE crpfnrct_db

# CRP Adapter details
ENV ADAPTER_USER crpfnrct
ENV ADAPTER_PASS crpfnrct
ENV ADAPTER_APP_USER crpfnrctapp
ENV ADAPTER_APP_PASS crpfnrctapp
ENV CRP_ADAPTER_SCHEMA crpfnrct

#CRP Config details
ENV CONF_USER crpfnrctconf
ENV CONF_PASS crpfnrctconf
ENV CONF_APP_USER crpfnrctconfapp
ENV CONF_APP_PASS crpfnrctconfapp
ENV CRP_CONF_SCHEMA crpfnrctconf

#CRP Daemon details
ENV DAEMON_USER crpfnrctdaemon
ENV DAEMON_PASS crpfnrctdaemon
ENV DAEMON_APP_USER crpfnrctdaemonapp
ENV DAEMON_APP_PASS crpfnrctdaemonapp
ENV CRP_DAEMON_SCHEMA crpfnrctdaemon

# Database Users
ENV FINERACT_DATABASE_USERS first,another,firstadmin
ENV FINERACT_DATABASE_WRITE_USERS another
ENV FINERACT_DATABASE_ADMIN_USERS firstadmin
ENV FINERACT_DATABASE_USER_FIRSTADMIN_PASSWORD firstadmin_pass
ENV FINERACT_DATABASE_USER_FIRST_PASSWORD first_pass
ENV FINERACT_DATABASE_USER_ANOTHER_PASSWORD another_pass

COPY src/main/resources/*.sh /scripts/
RUN chmod +x /scripts/*.sh

# TODO Update the entrypoint/command to run all the scripts in directory in order
ENTRYPOINT ["/bin/sh", "-c", "/scripts/01-init-crp-fineract-db.sh && /scripts/02-create-crp-fineract-db-users.sh"]