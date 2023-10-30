# Create DB extensions.
create_extension() {
  local database_name="$1"
  local database_user="$2"
  local database_pass="$3"
  local extension_name="$4"
  local schema_name="$5"
  echo "Creating extension $extension_name in database $database_name with schema $schema_name"

  export PGPASSWORD=$database_pass;
  # Create schema if the schema name is passed
  if [ ! -z $schema_name ]; then
    echo "Creating Schema $schema_name"
    psql -h $POSTGRES_HOSTNAME -v ON_ERROR_STOP=1 --username "$database_user" --dbname "$database_name" -c "CREATE SCHEMA IF NOT EXISTS $schema_name;"
    echo "Schema $schema_name created."
  fi

  extension_created_flag="$(psql -Atq -h $POSTGRES_HOSTNAME -v ON_ERROR_STOP=1 --username "$database_user" --dbname "$database_name" -c "SELECT 1 FROM PG_EXTENSION WHERE EXTNAME = '$extension_name';")"
  if [ "$extension_created_flag" = 1 ]; then
      echo "Database Extension $extension_name already created. Skipping..."
  else
    echo "Creating Extension $extension_name"
    if [ -z $schema_name ]; then
      psql -h $POSTGRES_HOSTNAME -v ON_ERROR_STOP=1 --username "$database_user" --dbname "$database_name" -c "CREATE EXTENSION $extension_name;"
    else
      psql -h $POSTGRES_HOSTNAME -v ON_ERROR_STOP=1 --username "$database_user" --dbname "$database_name" -c "CREATE EXTENSION $extension_name WITH SCHEMA $schema_name;"
    fi
    echo "Extension $extension_name created."
  fi

  echo "Created extension $extension_name in database $database_name with schema $schema_name. \n"
}


# Install the extensions
echo "----------Begin PG extension install---------- \n"

create_extension $DEFAULT_FINERACT_TENANT "$POSTGRES_USER" "$POSTGRES_PASSWORD" "pg_partman" "partman"
create_extension "postgres" "$POSTGRES_USER" "$POSTGRES_PASSWORD" "pg_cron"
create_extension "template1" "$POSTGRES_USER" "$POSTGRES_PASSWORD" "apg_plan_mgmt"
create_extension $DEFAULT_FINERACT_TENANT "$POSTGRES_USER" "$POSTGRES_PASSWORD" "apg_plan_mgmt"
create_extension $DEFAULT_FINERACT_TENANT "$POSTGRES_USER" "$POSTGRES_PASSWORD" "pg_hint_plan"

for tenantDbName in $ADDITIONAL_FINERACT_TENANTS
  do
    create_extension $tenantDbName "$POSTGRES_USER" "$POSTGRES_PASSWORD" "pg_partman" "partman"
    create_extension $tenantDbName "$POSTGRES_USER" "$POSTGRES_PASSWORD" "apg_plan_mgmt"
    create_extension $tenantDbName "$POSTGRES_USER" "$POSTGRES_PASSWORD" "pg_hint_plan"
  done

echo "----------PG extension install Complete---------- \n"
