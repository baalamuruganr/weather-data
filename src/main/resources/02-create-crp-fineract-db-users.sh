#!/bin/bash
# Created database users. The usernames must be comma separated list set as environment variable FINERACT_DATABASE_USERS.
# The passwords for all these users should be provided as environment variables, for example, FINERACT_DATABASE_USER_<username-in-upper-case>_PASSWORD.
set -e

echo "----------Begin Database Setup----------"

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

    local privileges_property="FINERACT_DATABASE_USER"${username_uppercase}"_PRIVILEGES"
    local privileges=${!privileges_property}

    if [ -z $privileges ]; then
      echo "Privileges not found for user $user"
      exit 1;
    fi

    check_and_create_userrole $user $password
    assign_privileges $user $privileges
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

assign_privileges() {
    local username=$1
    local privileges=$2
}

#Create DB users
check_and_create_userroles

echo "----------Database Setup Complete----------"