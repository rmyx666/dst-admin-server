#!/bin/bash

# 下载 dst-admin-1.5.0.jar 文件并重命名为 dst-admin.jar
wget http://156.236.75.110:9000/download/dst-admin-1.5.0.jar -O dst-admin.jar

# 检查 wget 是否成功
if [ $? -ne 0 ]; then
  echo "下载失败"
  exit 1
fi

ps -ef | grep -v grep | grep ${server_name} | sed -n '1P' | awk '{print $2}' | xargs kill -9
if [[ -z $(ps -ef | grep -v grep | grep ${server_name} | sed -n '1P' | awk '{print $2}') ]]; then
  echo -e "\033[32m ##: 已停止(stopped) ... \033[0m"
fi

if [[ -z $(ps -ef | grep -v grep | grep ${server_name} | sed -n '1P' | awk '{print $2}') ]]; then
  #    停止了
  nohup java -jar -Xms100m -Xmx100m ${server_name}'.jar' --server.port=8080  >>server.log 2>&1 &
  sleep 2
  if [[ -n $(ps -ef | grep -v grep | grep ${server_name} | sed -n '1P' | awk '{print $2}') ]]; then
    echo -e "\033[36m ##: 启动成功(Start successfully) ~ \033[0m"
  else
    echo -e "\033[31m 启动失败(failed to activate) \033[0m"
  fi
else
  echo -e "\033[32m ##: 服务已经在运行中(The service is already running) \033[0m"
fi