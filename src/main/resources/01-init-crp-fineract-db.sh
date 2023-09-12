#!/bin/bash
# Configures the databases
set -e

echo "----------Begin Database Setup---------- \n"
echo "FineractDB = $FINERACT_DATABASES, AdapterDB = $CRP_DATABASE, Hostname = $POSTGRES_HOSTNAME, PostgresUser = $POSTGRES_USER, PostgresDB = $POSTGRES_DB,
      FineractDBUser = $FINERACT_DB_USER, CRPUsers=[$ADAPTER_USER,$ADAPTER_APP_USER,$CONF_USER,$CONF_APP_USER,$DAEMON_USER,$DAEMON_APP_USER], CRPSchemas=$CRP_SCHEMAS  \n"

check_and_create_userrole() {
  local username="$1"
  local password="$2"

  user_created_flag="$(export PGPASSWORD=$POSTGRES_PASSWORD; psql -Atq -h $POSTGRES_HOSTNAME -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" -c "SELECT 1 FROM PG_CATALOG.PG_ROLES WHERE ROLNAME = '$username';")"
  if [ "$user_created_flag" = 1 ]; then
      echo "Database Role $username already created. Skipping... \n"
  else
    echo "Creating Role $username"
    export PGPASSWORD=$POSTGRES_PASSWORD;
    psql -h $POSTGRES_HOSTNAME -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" -c "CREATE USER $username WITH PASSWORD '$password';"
    echo "Role $username created. \n"
  fi
}

check_and_create_database() {
  local database_name="$1"
  database_created_flag="$(export PGPASSWORD=$POSTGRES_PASSWORD; psql -Atq -h $POSTGRES_HOSTNAME -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" -c "SELECT 1 FROM PG_DATABASE WHERE DATNAME = '$database_name';")"
  if [ "$database_created_flag" = 1 ]; then
      echo "Database $database_name already created. Skipping... \n"
  else
    echo "Creating Database $database_name"
    export PGPASSWORD=$POSTGRES_PASSWORD;
    psql -h $POSTGRES_HOSTNAME -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" -c "CREATE DATABASE $database_name;"
    echo "Database $database_name created. \n"
  fi
}

assign_all_privileges() {
  local database_name="$1"
  local database_user="$2"
  echo "Granting all privileges on $database_name to $database_user."
  export PGPASSWORD=$POSTGRES_PASSWORD;
  psql -h $POSTGRES_HOSTNAME -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" -c "GRANT ALL PRIVILEGES ON DATABASE $database_name TO $database_user;"
  echo "Privileges granted on $database_name to $database_user. \n"
}

create_schema() {
  local database_name="$1"
  local database_user="$2"
  local database_pass="$3"
  local schema_name="$4"
  local schema_user="$5"
  echo "Creating schema $schema_name with authorization for $schema_user in database $database_name as user $database_user"
  export PGPASSWORD=$database_pass;
  psql -h $POSTGRES_HOSTNAME -v ON_ERROR_STOP=1 --username "$database_user" --dbname "$database_name" -c "CREATE SCHEMA IF NOT EXISTS $schema_name AUTHORIZATION $schema_user;"
  echo "Created schema $schema_name with authorization for $schema_user. \n"
}

create_multi_tenant_db() {
  local multi_tenant_setup="$1"
  local additional_fineract_tenants="$2"
  if [ "$multi_tenant_setup" = true ]; then
    #Create additional fineract tenant DBs and assign privileges to user
    echo "----------Begin Multi Tenant Setup---------- \n"
    fineractTenantsToCreate=$(echo "$additional_fineract_tenants" | tr ',' '\n')
    for tenantDbName in $additional_fineract_tenants
    do
      check_and_create_database "$tenantDbName"
      assign_all_privileges "$tenantDbName" "$FINERACT_DB_USER"
    done
    echo "----------Multi Tenant Setup Complete---------- \n"
  else
    echo "----------No Additional Tenants Setup---------- \n"
  fi
}

echo "----------Begin Fineract Database Setup---------- \n"

#Create User for Fineract DB
check_and_create_userrole "$FINERACT_DB_USER" "$FINERACT_DB_PASS"

#Create Fineract tenant store DB
check_and_create_database "$FINERACT_TENANT_STORE_DB"
assign_all_privileges "$FINERACT_TENANT_STORE_DB" "$FINERACT_DB_USER"

#Create the default tenant DB
check_and_create_database "$DEFAULT_FINERACT_TENANT"
assign_all_privileges "$DEFAULT_FINERACT_TENANT" "$FINERACT_DB_USER"

#Create additional tenants DB if multi-tenant setup is enabled
create_multi_tenant_db "$MULTI_TENANT_SETUP" "$ADDITIONAL_FINERACT_TENANTS"

echo "----------Fineract Database Setup Complete---------- \n"

echo "----------Begin CRP Database Setup---------- \n"
#Create CRP DB & User
check_and_create_database "$CRP_DATABASE"

#Create Adapter, Conf, and Daemon users
check_and_create_userrole "$ADAPTER_USER" "$ADAPTER_PASS"
check_and_create_userrole "$ADAPTER_APP_USER" "$ADAPTER_APP_PASS"
check_and_create_userrole "$CONF_USER" "$CONF_PASS"
check_and_create_userrole "$CONF_APP_USER" "$CONF_APP_PASS"
check_and_create_userrole "$DAEMON_USER" "$DAEMON_PASS"
check_and_create_userrole "$DAEMON_APP_USER" "$DAEMON_APP_PASS"
assign_all_privileges "$CRP_DATABASE" "$ADAPTER_USER"
assign_all_privileges "$CRP_DATABASE" "$CONF_USER"
assign_all_privileges "$CRP_DATABASE" "$DAEMON_USER"

#Create Schemas for Adapter, Conf, and Daemon
create_schema "$CRP_DATABASE" "$ADAPTER_USER" "$ADAPTER_PASS" "$CRP_ADAPTER_SCHEMA" "$ADAPTER_USER"
create_schema "$CRP_DATABASE" "$CONF_USER" "$CONF_PASS" "$CRP_CONF_SCHEMA" "$CONF_USER"
create_schema "$CRP_DATABASE" "$DAEMON_USER" "$DAEMON_PASS" "$CRP_DAEMON_SCHEMA" "$DAEMON_USER"

echo "----------CRP Database Setup Complete---------- \n"

echo "----------Database Setup Complete---------- \n"