#!/bin/bash

# Quick update script for updating a local script (DO NOT Use in production)

if [ -z "$1" ]
then
  echo "You must provide an IP_ADDRESS"
  exit -1
else
  echo "Updating PI with address $1"
fi

rm -rf app/build/distributions
./gradlew assembleDist
scp -o ConnectTimeout=1 -o StrictHostKeyChecking=no app/build/distributions/LedPi-0.0.1.zip root@"$1":/home/led/LedPi.zip

# Update remote
ssh -o ConnectTimeout=1 -o StrictHostKeyChecking=no root@"$1" 'rm -rf /home/led/LedPi-0.0.1'
ssh -o ConnectTimeout=1 -o StrictHostKeyChecking=no root@"$1" 'rm -rf /home/ledLedPi/lib'
ssh -o ConnectTimeout=1 -o StrictHostKeyChecking=no root@"$1" 'unzip /home/led/LedPi.zip'
ssh -o ConnectTimeout=1 -o StrictHostKeyChecking=no root@"$1" 'mv /home/led/LedPi-0.0.1/lib /home/led/LedPi/lib'
ssh -o ConnectTimeout=1 -o StrictHostKeyChecking=no root@"$1" 'reboot now'
