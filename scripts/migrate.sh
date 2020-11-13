#!/usr/bin/env sh
export DB_JDBC_URL="jdbc:postgresql://${DB_HOST}:5432/pamannonsemottak"
java -jar pam-annonsemottak-migration.jar migrate date $1 > migration.log
