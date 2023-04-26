# crp-fineract-database-init
This repository contains bash scripts for the creating crp & fineract database along with the users and schema needed for development and deployment on AWS or local.

### Steps to build the project
1. Clone the repository
2. cd crp-fineract-database-init
3. git checkout develop
4. Ensure docker is running locally
5. mvn clean install -Pdocker

### Build Artifact
A docker image is produced via the io-fabric8 maven plugin.

This docker image will expect a root user and a base postgres database be already created on startup. 
Once run, it will create databases crpfnrct_db, fineract_default, fineract_tenant & fineract_de databases along with the necessary users in [Credit-R/crp-fineract-database-init](https://github.paypal.com/Credit-R/crp-fineract-database/tree/develop/src/main/resources).
Once the databases & users are created, it will also proceed to create 2 schemas for adapter & config project under crpfnrct_db.

The admin user will be root with a password=password

Docker Repository and Image Name: artifactory.us-central1.gcp.dev.paypalinc.com/crp-fineract/crp-fineract-database-init:latest-develop

How to Run:

Start database
```
docker run --name postgres_db -itd -p 5432:5432 -e POSTGRES_USER=root -e POSTGRES_PASSWORD=password postgres:14.6
# Emulate rds_superuser role
docker exec -it postgres_db psql -U root -c 'create role rds_superuser with SUPERUSER'
```

Execute database init docker image
```
docker run --net=host -e POSTGRES_HOSTNAME=127.0.0.1 artifactory.us-central1.gcp.dev.paypalinc.com/crp-fineract/crp-fineract-database-init:latest-develop
```