#!/bin/bash

VerifyBackupDirPresence(){
    if [ ! -d $BACKUP_PATH ]; then
        mkdir /backup
    fi
    if [ ! -d $BACKUP_PATH/database ]; then
        mkdir $BACKUP_PATH/database
    fi
    if [ ! -d $BACKUP_PATH/xml ]; then
        mkdir $BACKUP_PATH/xml
    fi
    if [ ! -d $BACKUP_PATH/war ]; then
        mkdir $BACKUP_PATH/war
    fi
}

UpdateWarFile(){
	echo 'Generating war file'
    mvn package -Pprod -Dmaven.test.skip=true
	echo 'War generated...'
}

RetrieveXML() {
    echo 'Retrieve old xml'
    python scripts/xml/xml-download-uuid.py -b localhost:8080 -o $BACKUP_PATH/xml/ontrack_$CURRENT_DATE
    echo 'Xml saved!'
}

StopTomcat(){
    echo 'Stoping instance'
    mv $TOMCAT_PATH/$WAR_NAME $BACKUP_PATH/war/ontrack_$CURRENT_DATE.war
    x=`curl -I  --stderr /dev/null $APP_URL | head -1 | cut -d' ' -f2`
    while [ "$x" != '404' ]; do
        sleep 1
        x=`curl -I  --stderr /dev/null $APP_URL | head -1 | cut -d' ' -f2`
    done
    rm -rf $TOMCAT_PATH/../work/Catalina/localhost/*
    echo 'Instance stopped!'
}

DumpDataBase(){
    echo "Making database Backup..."
    mysqldump --user=$DB_USER --password=$DB_PASSWORD --host=$DB_URL --databases $DB_SCHEMA | bzip2 > $BACKUP_PATH/database/ontrack_$CURRENT_DATE.bz2
    echo "Backup done!"
}

CleanDatabase(){
    echo 'Dropping old database'
    mysql -u$DB_USER --password$DB_PASSWORD -h $DB_URL -e "drop database $DB_SCHEMA;"
    mysql -u$DB_USER --password$DB_PASSWORD -h $DB_URL -e "create database $DB_SCHEMA character set utf8 collate utf8_general_ci;"
    echo 'Database cleaned'
}

DeployNewWar(){
    echo 'Deploing instance'
    cp target/ontrackExt-0.0.1.war $TOMCAT_PATH/$WAR_NAME
    x=`curl -I --stderr /dev/null $APP_URL | head -1 | cut -d' ' -f2`
    while [ "$x" = '404' ]; do
        sleep 1
        x=`curl -I --stderr /dev/null $APP_URL | head -1 | cut -d' ' -f2`
    done
    echo 'Instance deployed!'
}

UploadXML(){
    echo 'Uploading xml to the new instance'
    python scripts/xml/xml-upload-uuid.py -b localhost:8080 -d ontrack_$CURRENT_DATE/
    echo 'Xml uploaded!'
}

PrintHelp(){
    echo 'Usage: sh migrate.sh http://url /path/to/tomcat instance'
    echo 'Production: sh ./script/migrate.sh https://ontrack.oncast.com.br $CATALINA_HOME/webapps ROOT'
}

if [ $# -ne 3 ]
then
    PrintHelp
else
    APP_URL="$1"
    TOMCAT_PATH="$2"
    WAR_INSTANCE="$3"
    WAR_NAME=$WAR_INSTANCE.war

    BACKUP_PATH="/var/lib/ontrack/backup"
    DB_USER="ontrack"
    DB_PASSWORD="xtlhpuFY1VvU6w"
    DB_URL="localhost:3306"
    DB_SCHEMA="ontrack"
    CURRENT_DATE=`date +%Y-%m-%d_%H:%M:%S`
    
    VerifyBackupDirPresence
    
    echo 'Starting migration process...'
	UpdateWarFile
	echo ""
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
