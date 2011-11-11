#!/bin/bash

VerifyBackupDirPresence(){
    if [ ! -d /backup ]; then
        mkdir /backup
    fi
    if [ ! -d /backup/mysql ]; then
        mkdir /backup/mysql
    fi
    if [ ! -d /backup/xml ]; then
        mkdir /backup/xml
    fi
}

RetrieveXML() {
    echo 'Retrieve old xml'
    curl --basic --user $APP_USER:$APP_PASS $APP_URL/application/xml/download > $BACKUP_PATH/xml/ontrack-$CURRENT_DATE.xml
    echo 'Xml saved!'
}

StopTomcat(){
    echo 'Stoping instance'
    rm $TOMCAT_PATH/$WAR_NAME
    x=`curl -I  --stderr /dev/null $APP_URL | head -1 | cut -d' ' -f2`
    while [ "$x" != '404' ]; do
        sleep 1
        x=`curl -I  --stderr /dev/null $APP_URL | head -1 | cut -d' ' -f2`
    done
    echo 'Instance stopped!'
}

DumpDataBase(){
    echo "Making database Backup..."
    mysqldump --user=$DB_USER --password=$DB_PASSWORD --host=$DB_URL --databases $DB_SCHEMA | bzip2 > $BACKUP_PATH/mysql/mysql-$CURRENT_DATE.bz2
    echo "Backup done!"
}

CleanDatabase(){
    echo 'Dropping old database'
    mysql -u$DB_USER --password$DB_PASSWORD -h $DB_URL -e "drop schema "$DB_SCHEMA
    mysql -u$DB_USER --password$DB_PASSWORD -h $DB_URL -e "create schema "$DB_SCHEMA
    echo 'Database cleaned'
}

DeployNewWar(){
    echo 'Deploing instance'
    cp $WAR_NAME $TOMCAT_PATH
    x=`curl -I --stderr /dev/null $APP_URL | head -1 | cut -d' ' -f2`
    while [ "$x" = '404' ]; do
        sleep 1
        x=`curl -I --stderr /dev/null $APP_URL | head -1 | cut -d' ' -f2`
    done
    echo 'Instance deployed!'
}

UploadXML(){
    echo 'Uploading xml to the new instance'
    curl --basic --user $APP_USER:$APP_PASS -F "ontrack=@$BACKUP_PATH/xml/ontrack-$CURRENT_DATE.xml;type=text/xml" $APP_URL/application/xml/upload
    echo 'Xml uploaded!'
}

PrintHelp(){
    echo 'Usage: sh migrate.sh http://url /path/to/tomcat file.war'
    echo 'The file.war must be in this same path'
}

if [ $# -ne 3 ]
then
    PrintHelp
else
    APP_URL="$1"
    TOMCAT_PATH="$2"
    WAR_NAME="$3"

    APP_USER="admin@ontrack.com"
    APP_PASS="ontrackpoulain"
    
    BACKUP_PATH="/backup"
    DB_USER="ontrack"
    DB_PASSWORD="ontrack"
    DB_URL="192.168.2.95:3306"
    DB_SCHEMA="staging"
    CURRENT_DATE=`date +%d-%m-%Y_%H:%M:%S`
    
    VerifyBackupDirPresence
    
    echo 'Starting migration process...'
    RetrieveXML
    echo ""
    StopTomcat
    echo ""
    DumpDataBase
    echo ""
    CleanDatabase
    echo ""
    DeployNewWar
    echo ""
    UploadXML
    echo ""
    echo "Process finished!"
fi
