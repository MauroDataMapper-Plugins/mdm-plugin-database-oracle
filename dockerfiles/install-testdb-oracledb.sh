#!/bin/bash

# Create the test databases
cd /data/metadata_catalogue_testing
echo exit | sqlplus64 SYSTEM/BOpVnzFi9Ew=1@//localhost:1521/ORCLPDB1 @oracle_create_simple.sql
