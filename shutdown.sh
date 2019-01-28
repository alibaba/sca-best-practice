#!/bin/sh

if [ $# -lt 1 ];then
    echo "Usage: sh shutdown.sh /home/user/temp"
    exit 1
fi

command -v java >/dev/null 2>&1 || { echo >&2 "Require java but it's not installed.  Aborting."; exit 1; }
command -v mvn >/dev/null 2>&1 || { echo >&2 "Require mvn but it's not installed.  Aborting."; exit 1; }

DIR=$1

if [ ! -d "$DIR" ];then
    echo "$DIR is not exists."
    exit 1
fi

if [ ! -f "$DIR/sentinel-dashboard/shutdown.sh" ];then
    echo "ps -ef | grep sentinel-dashboard | grep -v grep | awk '{print \$2}' | xargs kill -9" > $DIR/sentinel-dashboard/shutdown.sh
fi

BASE_DIR=$(cd `dirname $0`; pwd)

cd $DIR/nacos/bin && sh shutdown.sh
cd $DIR/sentinel-dashboard && sh shutdown.sh
cd $DIR/rocketmq-all-4.3.2-bin-release/bin && sh mqshutdown broker
cd $DIR/rocketmq-all-4.3.2-bin-release/bin && sh mqshutdown namesrv
cd $DIR/redis-5.0.3/src && ./redis-cli shutdown

if [ ! -d "$DIR/sca-best-practice" ];then
    mkdir -p $DIR/sca-best-practice
fi

if [ ! -f "$DIR/sca-best-practice/shutdown-order.sh" ];then
    echo "ps -ef | grep $BASE_DIR/sca-best-practice/sca-order | grep -v grep | awk '{print \$2}' | xargs kill -9" > $DIR/sca-best-practice/shutdown-order.sh
fi

if [ ! -f "$DIR/sca-best-practice/shutdown-user-center.sh" ];then
    echo "ps -ef | grep $BASE_DIR/sca-best-practice/sca-user-center | grep -v grep | awk '{print \$2}' | xargs kill -9" > $DIR/sca-best-practice/shutdown-user-center.sh
fi

if [ ! -f "$DIR/sca-best-practice/shutdown-gateway.sh" ];then
    echo "ps -ef | grep $BASE_DIR/sca-best-practice/sca-gateway | grep -v grep | awk '{print \$2}' | xargs kill -9" > $DIR/sca-best-practice/shutdown-gateway.sh
fi

ps -ef | grep "startup.sh $DIR" | grep -v grep | awk '{print $2}' | xargs kill -9

cd $DIR/sca-best-practice && sh shutdown-order.sh
cd $DIR/sca-best-practice && sh shutdown-user-center.sh
cd $DIR/sca-best-practice && sh shutdown-gateway.sh