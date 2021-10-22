# Setting up ORACLE Server Test Environment

## Build the ORACLE Docker Image

You first need to build the Oracle Docker image as it is not available for actual download.

You need to pick the version of Oracle Database you want to build, one of

* 11.2.0.2
* 12.1.0.2
* 12.2.0.1
* 18.3.0
* 18.4.0
* 19.3.0

Also you will have to provide the installation binaries of Oracle Database (except for Oracle Database 18c XE) and put them into the 
`dockerfiles/<version>` folder. 
You only need to provide the binaries for the edition you are going to install. 
The binaries can be downloaded from the 
[Oracle Technology Network](http://www.oracle.com/technetwork/database/enterprise-edition/downloads/index.html), make sure you use the 
linux link: `Linux x86-64`. 
The needed file is named `linuxx64__database.zip`. 
You also have to make sure to have internet connectivity for yum. Note that you must not uncompress the binaries. 
The script will handle that for you and fail if you uncompress them manually!

Then run the below using the chosen version

```bash
$ cd dockerfiles
$ ./buildDockerImage.sh -v CHOSEN_VERSION -e
```

## Start the ORACLE Docker Instance

The following command will start up a default ORACLE server instance.

```bash
$ docker run --rm -d \
-e ORACLE_PWD='BOpVnzFi9Ew=1' \
-v /data/docker_volumes/oracle:/opt/oracle/oradata \
-p 1521:1521 \
--name oracledb_12.2.0.1-ee \
oracle/database:12.2.0.1-ee
```

## To install the simple database in an Oracle server.

1. Copy the `create_metadata_simple.sql` to the server running oracle
1. Install `sqlplus64`
1. `$ sqlplus64 SYSTEM/BOpVnzFi9Ew=1@//localhost:1521/ORCLPDB1` to access the database (default install)
1. `> @create_metadata_simple.sql` in the oracle command line to install
1. `> exit` to quit the oracle command line

## To install SqlPlus on Ubuntu

First of all you need to download Instant Client Downloads:
```
http://www.oracle.com/technetwork/topics/linuxx86-64soft-092277.html
``` 
You will need:
* Instant Client Package - Basic
* Instant Client Package - SQL*Plus

Install alien package so you can install rpm packages by typing following command in terminal.
```bash
sudo apt-get install alien
```

Once that is done, go to the folder where the rpm files are located and execute the following:
```bash
sudo alien -i oracle-instantclient*-basic*.rpm
sudo alien -i oracle-instantclient*-sqlplus*.rpm
```

You need to install libaio.so. Type following command to do it:
```bash
sudo apt-get install libaio1
```
Create Oracle configuration file:
```bash
sudo sensible-editor /etc/ld.so.conf.d/oracle.conf
```
Put this line in that file:
```bash
/usr/lib/oracle/<your version>/client/lib/
``` 
Note - for 64-bit installations, the path will be:
```bash
/usr/lib/oracle/<your version>/client64/lib/
``` 
Update the configuration by running following command:
```bash
sudo ldconfig
```

## Running the Oracle Server

The following command will start up a default Oracle 12.1.0.2-ee server instance.

```bash
docker run --name oracledb_12.1.0.2-ee --rm -d -p 1521:1521 -e ORACLE_PWD='BOpVnzFi9Ew=1' oracle/database:12.1.0.2-ee
```

The above command does require you to build the image following Oracle's default instructions.

On jenkins machine there is a startup script `start-oracledb` located at `/usr/local/bin`