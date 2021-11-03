#!/bin/bash

# This script is designed to get around Oracles asinine license setup on its database
# As we're using docker and we don't actually care about the data in the database we can scrap and rebuild the volume
# This can take a while and should only be done when the warning about password change appears

# Stop any running containers
echo 'Stopping running container'
docker stop oracledb_12.1.0.2-ee

# Remove the old database
echo 'Removing the old database files'
sudo rm -rf /data/docker_volumes/oracle/*

# Start oracle container
echo 'Starting oracledb_12.1.0.2-ee'
/usr/local/bin/start-oracledb

# Follow the logs until the db is built
echo 'Building new oracle database'
echo '>> Press ctrl+c when the database is built to continue this script'
echo '>> When the database is built please run /usr/local/bin/install-testdb-oracledb'
echo ''
docker logs -f oracledb_12.1.0.2-ee
