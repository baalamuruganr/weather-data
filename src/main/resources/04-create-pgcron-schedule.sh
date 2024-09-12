echo "----------Begin Database Setup (04-create-pgcron-schedule)----------"

# Function to execute psql commands and return the result
execute_psql_command() {
  local database_user="$1"
  local database_name="$2"
  local sql_command="$3"

  export PGPASSWORD="$POSTGRES_PASSWORD"
  
  # Using command substitution to capture the output
  psql_output=$(psql -Atq -h "$POSTGRES_HOSTNAME" -v ON_ERROR_STOP=1 --username "$database_user" --dbname "$database_name" -c "$sql_command")
  
  echo "$psql_output"
}

# Function to create ANALYZE schedule
create_analyze_schedule() {
  local database_name="$1"
  local database_user="$2"
  local target_database_name="$3"
  local schema_name="$4"
  local table_name="$5"
  
  local full_table_name="$table_name"

  if [ "$schema_name" != "public" ]; then
    full_table_name="${schema_name}_${table_name}"
  fi

  job_name="daily__${target_database_name}__analyze__${full_table_name}"

  # Create the SQL to get the hour dynamically
  sql_gethour="SELECT EXTRACT(HOUR FROM TO_CHAR('2024-09-12T20:05:00'::timestamp at time zone '$DEFAULT_TENANT_TIMEZONE', 'YYYY-MM-DDThh24:mi')::timestamp at time zone 'UTC') as day"

  # Execute the SQL to get the hour
  hour=$(execute_psql_command "$database_user" "$database_name" "$sql_gethour")

  # Create the SQL commands for cron jobs
  sql_check="SELECT 1 from cron.job WHERE jobname = '${job_name}';"
  sql_create="SELECT cron.schedule('${job_name}', '05 ${hour} * * *', 'ANALYZE $schema_name.$table_name');"
  sql_update="UPDATE cron.job SET database='${target_database_name}' WHERE jobid=(SELECT max(j.jobid) FROM cron.job AS j WHERE j.database='${database_name}' AND j.jobname='${job_name}');"

  echo "---"
  echo "Creating ANALYZE schedule in database $database_name for:"
  echo "- Target Database: ${target_database_name}"
  echo "- User id........: ${database_user}"
  echo "- Target Table...: ${schema_name}.${table_name}"
  
  # Execute the SQL check command and store the result
  schedule_created_flag=$(execute_psql_command "$database_user" "$database_name" "$sql_check")

  if [ "$schedule_created_flag" = 1 ]; then
    echo "ANALYZE schedule for database ${target_database_name} table $schema_name.$table_name already exists. Skipping."
  else
    # Execute the SQL create schedule command
    echo "Creating initial schedule"
    execute_psql_command "$database_user" "$database_name" "$sql_create"
    
    # Change database to the tenant
    echo "Updating target database"
    execute_psql_command "$database_user" "$database_name" "$sql_update"
    
    # Verify again if the schedule was created
    schedule_created_flag=$(execute_psql_command "$database_user" "$database_name" "$sql_check")

    if [ "$schedule_created_flag" = 1 ]; then
      echo "Schedule created successfully."
    else
      echo "Error creating schedule"
      return 1
    fi
  fi
}

# Start creating the PGCron schedule
echo "----------Begin PGCron schedule creation----------"

# Default Fineract tenant - Database must always be POSTGRES
create_analyze_schedule postgres "$POSTGRES_USER" "$DEFAULT_FINERACT_TENANT" "public" "m_external_asset_owner_transfer"

# Additional fineract tenants
if [ "$MULTI_TENANT_SETUP" = true ]; then
  echo "----------Creating PGCron schedule for additional tenants---------- \n"
  for tenantDbName in $(echo "$ADDITIONAL_FINERACT_TENANTS" | tr ',' '\n')
    do
      create_analyze_schedule postgres "$POSTGRES_USER" "$tenantDbName" "public" "m_external_asset_owner_transfer"
    done
else
    echo "----------No PGCron schedules created for additional tenants---------- \n"
fi

echo "----------PGCron schedule creation Complete----------"
