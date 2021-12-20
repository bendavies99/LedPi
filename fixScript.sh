#!/bin/bash

# Quick update script for updating a local script (DO NOT Use in production)

if [ -z "$1" ]
then
  echo "You must provide an IP_ADDRESS"
  exit -1
else
  echo "Updating PI with address $1"
fi

scp -o ConnectTimeout=1 -o StrictHostKeyChecking=no app/build/distributions/LedPi-0.0.1.zip root@"$1":/home/led/LedPi.zip

# Update remote
ssh -o ConnectTimeout=1 -o StrictHostKeyChecking=no root@"$1" 'rm -rf /home/led/LedPi-0.0.1'
ssh -o ConnectTimeout=1 -o StrictHostKeyChecking=no root@"$1" 'rm -rf /home/root/LedPi-0.0.1'
ssh -o ConnectTimeout=1 -o StrictHostKeyChecking=no root@"$1" 'rm -rf /home/led/LedPi/lib'
ssh -o ConnectTimeout=1 -o StrictHostKeyChecking=no root@"$1" 'unzip /home/led/LedPi.zip'
ssh -o ConnectTimeout=1 -o StrictHostKeyChecking=no root@"$1" 'mv /root/LedPi-0.0.1 /home/led/LedPi-0.0.1'
ssh -o ConnectTimeout=1 -o StrictHostKeyChecking=no root@"$1" 'mv /home/led/LedPi-0.0.1/lib /home/led/LedPi/lib'
ssh -o ConnectTimeout=1 -o StrictHostKeyChecking=no root@"$1" 'pm2 restart 0'
ssh -o ConnectTimeout=1 -o StrictHostKeyChecking=no root@"$1" 'tail -f /home/led/LedPi-Runner/logs/app.log'
