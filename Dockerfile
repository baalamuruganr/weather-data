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

COPY src/main/resources/01-init-crp-fineract-db.sh /
RUN chmod +x /01-init-crp-fineract-db.sh

ENTRYPOINT ["/bin/sh", "/01-init-crp-fineract-db.sh"]