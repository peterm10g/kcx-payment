#!/bin/bash

#发布的APP
APPS=("payment-provider" "payment-worker")
#版本号
VERSION="1.0-SNAPSHOT"
#远程路径
REMOTES=("work@192.168.70.5" "work@192.168.70.2")

#远程部署目录
REMOTE_PATH="/home/work/apps"

#本地路径
ROOT=$(cd `dirname $0`; pwd)

echo "------------ 本地路径为 $ROOT ------------"

#发布打包环境
env="qypro"

echo "------------ 准备发布 $env ------------"

#循环部署代码
for REMOTE in ${REMOTES[@]}
do
    for APP in ${APPS[@]}
    do
        APPROOT="${ROOT}/${APP}/target"
        #解压到APPROOT
        tar -zxvf $APPROOT/$APP-$VERSION-$env.tar.gz -C $APPROOT/

        timeStr=$(date +%Y%m%d%H%M%S)
        echo "------------ 备份 ${REMOTE} ${APP} 开始  ------------"
        ssh $REMOTE "rsync -avzP --exclude=log --exclude=logs --exclude=out.log $REMOTE_PATH/${APP}/* $REMOTE_PATH/${APP}.bak${timeStr}"
        echo "------------ 备份 ${REMOTE} ${APP} 完成 ------------"

        rsync -avzP $APPROOT/$APP-$VERSION-$env/ $REMOTE:$REMOTE_PATH/$APP/

        echo "------------ 上传 ${REMOTE} ${APP} 成功 ------------"
        
        rm -rf $APPROOT/$APP-$VERSION-$env

    done

done

echo "------------ 执行完毕 ------------"
