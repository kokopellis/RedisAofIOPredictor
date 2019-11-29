#!/bin/bash
# Deploy redis service on single machine
# Author:  lhz6@outlook.com
# Written in Apr.29.2019

CURRENT_USER=hliu91
REDIS_SERVER=/home/hliu91/integration/redis-3.2.13/src/redis-server
REDIS_HOME=/mnt/hdd/$CURRENT_USER/redis_shared/
REDIS_PORT=

echo Deploy redis on `hostname`.
pkill redis-server
rm -rf $REDIS_HOME 
for REDIS_PORT in {7001,7002}
do
	mkdir -p $REDIS_HOME$REDIS_PORT
	cd $REDIS_HOME$REDIS_PORT
	echo "# bind 127.0.0.1"  > $REDIS_HOME$REDIS_PORT/node.conf
	echo "protected-mode no"  >> $REDIS_HOME$REDIS_PORT/node.conf
	echo "port $REDIS_PORT"  >> $REDIS_HOME$REDIS_PORT/node.conf
	echo "pidfile $REDIS_PORT.pid"  >> $REDIS_HOME$REDIS_PORT/node.conf
	echo "cluster-enabled yes"  >> $REDIS_HOME$REDIS_PORT/node.conf
	echo "appendonly yes"  >> $REDIS_HOME$REDIS_PORT/node.conf
	#echo "appendfsync always"  >> $REDIS_HOME$REDIS_PORT/node.conf
	echo "cluster-config-file nodes-$REDIS_PORT.conf"  >> $REDIS_HOME$REDIS_PORT/node.conf
	echo "cluster-node-timeout 15000"  >> $REDIS_HOME$REDIS_PORT/node.conf
	echo Install finished
	
	echo Starting service...
	sleep 1
	nohup $REDIS_SERVER $REDIS_HOME$REDIS_PORT/node.conf  >> $REDIS_HOME$REDIS_PORT/`hostname`-redis.log &
	done

if [ $? -eq 0 ]; then
        echo redis service started on `hostname` port $REDIS_PORT.
else
	echo "Error: redis service is not started!"
fi
