#!/usr/bin/env bash
# Jar包名称
server_name="dst-admin"
update_jar="dst-admin-update.jar"
current_jar="${server_name}.jar"

# 停止当前运行的服务
ps -ef | grep -v grep | grep ${server_name} | sed -n '1P' | awk '{print $2}' | xargs kill -9

if [[ -z $(ps -ef | grep -v grep | grep ${server_name} | sed -n '1P' | awk '{print $2}') ]]; then
  echo -e "\033[32m ##: 已停止(stopped) ... \033[0m"
fi

# 删除当前目录下的旧 Jar 包
if [ -f "$current_jar" ]; then
  echo "删除旧的 Jar 包: $current_jar"
  rm -f "$current_jar"
fi

# 将下载的 Jar 包重命名为 dst-admin.jar
if [ -f "$update_jar" ]; then
  echo "重命名更新后的 Jar 包: $update_jar -> $current_jar"
  mv "$update_jar" "$current_jar"
else
  echo -e "\033[31m 更新包不存在! ($update_jar) \033[0m"
  exit 1
fi

# 启动新的服务
if [[ -z $(ps -ef | grep -v grep | grep ${server_name} | sed -n '1P' | awk '{print $2}') ]]; then
  nohup java -jar -Xms100m -Xmx100m ${current_jar} --server.port=8080 >/dev/null 2>&1 &
  sleep 2
  if [[ -n $(ps -ef | grep -v grep | grep ${server_name} | sed -n '1P' | awk '{print $2}') ]]; then
    echo -e "\033[36m ##: 启动成功(Start successfully) ~ \033[0m"
  else
    echo -e "\033[31m 启动失败(failed to activate) \033[0m"
  fi
else
  echo -e "\033[32m ##: 服务已经在运行中(The service is already running) \033[0m"
fi