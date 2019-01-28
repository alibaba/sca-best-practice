#!/bin/sh

if [ $# -lt 1 ];then
    echo "Usage: sh startup.sh /home/user/temp"
    exit 1
fi

command -v java >/dev/null 2>&1 || { echo >&2 "Require java but it's not installed.  Aborting."; exit 1; }
command -v mvn >/dev/null 2>&1 || { echo >&2 "Require mvn but it's not installed.  Aborting."; exit 1; }

DIR=$1

BASE_DIR=$(cd `dirname $0`; pwd)

if [ ! -d "$DIR" ];then
    echo "$DIR is not exists."
    exit 1
fi

if [ ! -d "$DIR/nacos/bin" ];then
    if [ ! -f "$DIR/nacos-server-0.7.0.zip" ];then
        echo "$DIR/nacos-server-0.7.0.zip is not exists."
        exit 1
    fi
    cd $DIR && unzip nacos-server-0.7.0.zip
fi

if [ ! -d "$DIR/rocketmq-all-4.3.2-bin-release/bin" ];then
    if [ ! -f "$DIR/rocketmq-all-4.3.2-bin-release.zip" ];then
        echo "$DIR/rocketmq-all-4.3.2-bin-release.zip is not exists."
        exit 1
    fi
    cd $DIR && unzip rocketmq-all-4.3.2-bin-release.zip
fi

if [ ! -d "$DIR/redis-5.0.3/src" ];then
    if [ ! -f "$DIR/redis-5.0.3.tar.gz" ];then
        echo "$DIR/redis-5.0.3.tar.gz is not exists."
        exit 1
    fi
    cd $DIR && tar -xzvf redis-5.0.3.tar.gz
fi

if [ ! -d "$DIR/sentinel-dashboard" ];then
    mkdir -p $DIR/sentinel-dashboard
fi

if [ ! -f "$DIR/sentinel-dashboard/sentinel-dashboard-1.4.0.jar" ];then
    if [ ! -f "$DIR/sentinel-dashboard-1.4.0.jar" ];then
        echo "$DIR/sentinel-dashboard-1.4.0.jar is not exists."
        exit 1
    fi
    cp $DIR/sentinel-dashboard-1.4.0.jar $DIR/sentinel-dashboard/
fi

if [ ! -f "$DIR/sentinel-dashboard/startup.sh" ];then
    echo "java -Dserver.port=12000 -Dcsp.sentinel.dashboard.server=localhost:12000 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard-1.4.0.jar" > $DIR/sentinel-dashboard/startup.sh
    exit 1
fi

cd $DIR/nacos/bin && nohup sh startup.sh -m standalone > $DIR/nacos.log 2>&1 &
echo "Nacos start success."
cd $DIR/sentinel-dashboard && nohup sh startup.sh > $DIR/sentinel-dashboard.log 2>&1 &
echo "Sentinel-dashboard start success."
cd $DIR/rocketmq-all-4.3.2-bin-release/bin && nohup sh mqnamesrv > $DIR/mqnamesrv.log 2>&1 &
echo "Mqnamesrv start success."
cd $DIR/rocketmq-all-4.3.2-bin-release/bin && nohup sh mqbroker -n localhost:9876 > $DIR/mqbroker.log 2>&1 &
echo "Mqbroker start success."
cd $DIR/redis-5.0.3/src && nohup ./redis-server > $DIR/redis-server.log 2>&1 &
echo "Redis start success."

echo "Waiting for servers start..."

RESULT=`curl -s -X POST -d "dataId=user-center.yaml&group=DEFAULT_GROUP&content=user.id: chenzhu" http://127.0.0.1:8848/nacos/v1/cs/configs`
TIMES=60

sleep 10

while [ "$RESULT" != "true" ] && [ $TIMES -gt 0 ]
do
    RESULT=`curl -s -X POST -d "dataId=user-center.yaml&group=DEFAULT_GROUP&content=user.id: chenzhu" http://127.0.0.1:8848/nacos/v1/cs/configs`
    let TIMES=TIMES-1
    echo "Remain $TIMES seconds : Checking servers is started successfully."
    sleep 1
done

echo "Servers has been started successfully."

if [ "$RESULT" != "true" ];then
    echo "Create config failed."
    exit 1
fi

cd $BASE_DIR/codeless-framework && mvn clean install

cd $BASE_DIR/sca-best-practice/sca-gateway && nohup mvn spring-boot:run > $DIR/sca-gateway.log 2>&1 &
echo "sca-gateway start success."
cd $BASE_DIR/sca-best-practice/sca-user-center && nohup mvn spring-boot:run > $DIR/sca-user-center.log 2>&1 &
echo "sca-user-center start success."
cd $BASE_DIR/sca-best-practice/sca-order && nohup mvn spring-boot:run > $DIR/sca-order.log 2>&1 &
echo "sca-order start success."

echo "Waiting for applications start..."

RESULT_R=`curl -I -m 10 -o /dev/null -s -w %{http_code} http://127.0.0.1:9999/user-center/example/testRedis`
RESULT_S=`curl -I -m 10 -o /dev/null -s -w %{http_code} http://127.0.0.1:9999/order/example/testService`
TIMES=60

sleep 10

while [ "$RESULT_R$RESULT_S" != "200200" ] && [ $TIMES -gt 0 ]
do
    RESULT_R=`curl -I -m 10 -o /dev/null -s -w %{http_code} http://127.0.0.1:9999/user-center/example/testRedis`
    RESULT_S=`curl -I -m 10 -o /dev/null -s -w %{http_code} http://127.0.0.1:9999/order/example/testService`
    let TIMES=TIMES-1
    echo "Remain $TIMES seconds : Checking applications is started successfully."
    sleep 1
done

if [ "$RESULT_R$RESULT_S" != "200200" ];then
    echo "Applications has been started failed."
    exit 1
fi

echo "Applications has been started successfully."