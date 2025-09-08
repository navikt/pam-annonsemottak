#!/usr/bin/env bash
set -e
./mvnw clean install -Dspring.profiles.active=test
