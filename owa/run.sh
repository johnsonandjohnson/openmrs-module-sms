#!/bin/bash

SMS_REPO=/home/user/cfl/omrs-sms
SMS_OMOD=sms-1.0.0-SNAPSHOT.omod
CFL_REPO=/home/user/cfl/cfl-openmrs

cd $SMS_REPO
mvn clean install -DskipTests

rm $CFL_REPO/cfl/web/modules/sms*
mv $SMS_REPO/omod/target/$SMS_OMOD $CFL_REPO/cfl/web/modules

cd $CFL_REPO/cfl/
docker-compose down
docker-compose up -d --build

cd $SMS_REPO/owa
