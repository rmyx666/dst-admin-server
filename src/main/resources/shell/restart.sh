#!/usr/bin/env bash
#jar包名称
server_name="dst-admin"





  ps -ef | grep -v grep | grep ${server_name} | sed -n '1P' | awk '{print $2}' | xargs kill -9
  if [[ -z $(ps -ef | grep -v grep | grep ${server_name} | sed -n '1P' | awk '{print $2}') ]]; then
    echo -e "\033[32m ##: 已停止(stopped) ... \033[0m"
  fi



  if [[ -z $(ps -ef | grep -v grep | grep ${server_name} | sed -n '1P' | awk '{print $2}') ]]; then
    #    停止了
    nohup java -jar -Xms100m -Xmx100m ${server_name}'.jar' --server.port=8080  >/dev/null 2>&1 &
    sleep 2
    if [[ -n $(ps -ef | grep -v grep | grep ${server_name} | sed -n '1P' | awk '{print $2}') ]]; then
      echo -e "\033[36m ##: 启动成功(Start successfully) ~ \033[0m"
    else
      echo -e "\033[31m 启动失败(failed to activate) \033[0m"
    fi
  else
    echo -e "\033[32m ##: 服务已经在运行中(The service is already running) \033[0m"
  fi

