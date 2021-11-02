#!/bin/bash

docker run --rm -d \
-e ORACLE_PWD='BOpVnzFi9Ew=1' \
-v /data/docker_volumes/oracle:/opt/oracle/oradata \
-p 1521:1521 \
--name oracledb_12.1.0.2-ee \
oracle/database:12.1.0.2-ee