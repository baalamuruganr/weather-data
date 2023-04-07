#!/bin/bash
# Created database users. The usernames must be comma separated list set as environment variable FINERACT_DATABASE_USERS.
# The passwords for all these users should be provided as environment variables, for example, FINERACT_DATABASE_USER_<username-in-upper-case>_PASSWORD.
# Users created by default will have read access, for write access the users should also be included in FINERACT_DATABASE_WRITE_USERS
set -e

echo "----------Begin Database Setup (02-create-crp-fineract-db-users) ----------"

check_and_create_userroles() {
  if [ -z $FINERACT_DATABASE_USERS ]; then
    echo "No users configured to be created."
    exit;
  fi

  for user in $(echo "$FINERACT_DATABASE_USERS" | tr ',' '\n')
  do
    local username_uppercase=$(echo $user | tr '[:lower:]' '[:upper:]')
    local password_property="FINERACT_DATABASE_USER_"${username_uppercase}"_PASSWORD"
    local password=${!password_property}

    if [ -z $password ]; then
      echo "Password not found for user $user"
      exit 1;
    fi

    check_and_create_userrole $user $password
    # give users read privilege by default
    # pg_read_all_data was added in postgres 14
    grant_role $user "pg_read_all_data"
  done

}

check_and_create_userrole() {
  local username=$1
  local password=$2

  user_created_flag="$(export PGPASSWORD=$POSTGRES_PASSWORD; psql -Atq -h $POSTGRES_HOSTNAME -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" -c "SELECT 1 FROM PG_CATALOG.PG_ROLES WHERE ROLNAME = '$username';")"
    if [ "$user_created_flag" = 1 ]; then
        echo "Database Role $username already created. Skipping..."
    else
      echo "Creating Role $username"
      export PGPASSWORD=$POSTGRES_PASSWORD;
      psql -h $POSTGRES_HOSTNAME -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" -c "CREATE USER $username WITH PASSWORD '$password';"
      echo "Role $username created."
    fi
}

grant_role() {
  local username=$1
  local role=$2
  echo "Granting $role role to $username."
  export PGPASSWORD=$POSTGRES_PASSWORD;
  psql -h $POSTGRES_HOSTNAME -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" -c "GRANT $role TO $username;"
  echo "Granted $role role to $username."
}

grant_write_roles() {
  for user in $(echo "$FINERACT_DATABASE_WRITE_USERS" | tr ',' '\n')
  do
      grant_role $user "pg_write_all_data"
  done
}

grant_admin_privileges() {
  for user in $(echo "$FINERACT_DATABASE_ADMIN_USERS" | tr ',' '\n')
  do
      grant_role $user "pg_write_all_data"
      grant_role $user "pg_monitor"
      grant_role $user "pg_signal_backend"
  done
}

#Create DB users
check_and_create_userroles
grant_write_roles
grant_admin_privileges

echo "----------Database Setup Complete (02-create-crp-fineract-db-users) ----------"