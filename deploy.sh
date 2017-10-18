#!/bin/bash

#发布的APP
APPS=("payment-provider")
#版本号
VERSION="1.0-SNAPSHOT"
#远程路径
REMOTES=("work@192.168.60.59")

#远程部署目录
REMOTE_PATH="/home/work/lsh-payment/"

#本地路径
ROOT=$(cd `dirname $0`; pwd)

echo "------------ 本地路径为 $ROOT ------------"

#发布打包环境
echo "请选择发布环境 dev test pro ?"
read env

echo "------------ 准备发布 $env ------------"


#Maven打包
echo "是否要Maven打包[y/n]: ?"
read mvn
if [ "$mvn" = "y" -o "$mvn" = "Y" ]
then
  cd $ROOT
  #mvn clean compile package -P$env
  mvn clean install -Dmaven.test.skip=true -P $env
fi

#循环部署代码
for APP in ${APPS[@]}
do
  for REMOTE in ${REMOTES[@]}
  do
    echo "是否部署 ${REMOTE} ${APP} 模块代码[y/n]: ?"
    read deploy
    if [ "$deploy" = "y" -o  "$deploy" = "Y" ]
    then
        APPROOT="${ROOT}/${APP}/target"
        #解压到APPROOT
        tar -zxvf $APPROOT/$APP-$VERSION-$env.tar.gz -C $APPROOT/
        echo "------------ 备份 ${REMOTE} ${APP} 开始  ------------"

        timeStr=$(date +%Y%m%d%H%M%S)

        echo "timeStrtimeStrtimeStr: " + ${timeStr}

       # ssh $REMOTE "rsync -avzP --exclude=log --exclude=logs --exclude=out.log $REMOTE_PATH/${APP}/* $REMOTE_PATH/${APP}.bak${timeStr}"
        ssh $REMOTE "mv $REMOTE_PATH/${APP} $REMOTE_PATH/${APP}.${timeStr}.bak"
        echo "------------ 备份 ${REMOTE} ${APP} 完成 ------------"

        echo "------------ 上传 ${REMOTE} ${APP} 开始 ------------"

        rsync -avzP $APPROOT/$APP-$VERSION-$env/ $REMOTE:$REMOTE_PATH/$APP/

        echo "------------ 上传 ${REMOTE} ${APP} 成功 ------------"
        echo "------------ 是否要启动 ${REMOTE} ${APP} 服务 [y/n] ------------"
        read isRun
        if [ "$isRun" = "y" -o  "$isRun" = "Y" ]
        then
            ssh $REMOTE "sh $REMOTE_PATH/$APP/bin/run.sh /home/work/lsh-payment/payment-provider 1"
            echo "------------ ${REMOTE} ${APP} 启动成功 ------------"
        fi
        rm -rf $APPROOT/$APP-$VERSION-$env
    fi
  done
done

echo "------------ 执行完毕 ------------"