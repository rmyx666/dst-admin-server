#!/usr/bin/env bash
# Jar包名称
server_name="dst-admin"
update_jar="dst-admin-update.jar"
current_jar="${server_name}.jar"
log_file="update_log.txt"  # 日志文件

# 启动日志记录
echo "=================== 更新开始 $(date) ===================" >> $log_file

# 停止当前运行的服务
echo "停止当前服务..." >> $log_file
ps -ef | grep -v grep | grep ${server_name} | sed -n '1P' | awk '{print $2}' | xargs kill -9

if [[ -z $(ps -ef | grep -v grep | grep ${server_name} | sed -n '1P' | awk '{print $2}') ]]; then
  echo -e "\033[32m ##: 已停止(stopped) ... \033[0m" >> $log_file
  echo "服务已停止" >> $log_file
fi

# 删除当前目录下的旧 Jar 包
if [ -f "$current_jar" ]; then
  echo "删除旧的 Jar 包: $current_jar" >> $log_file
  rm -f "$current_jar"
else
  echo "旧的 Jar 包不存在: $current_jar" >> $log_file
fi

# 将下载的 Jar 包重命名为 dst-admin.jar
if [ -f "$update_jar" ]; then
  echo "重命名更新后的 Jar 包: $update_jar -> $current_jar" >> $log_file
  mv "$update_jar" "$current_jar"
else
  echo -e "\033[31m 更新包不存在! ($update_jar) \033[0m" >> $log_file
  echo "更新包不存在: $update_jar" >> $log_file
  exit 1
fi

# 启动新的服务
echo "尝试启动新的服务..." >> $log_file
if [[ -z $(ps -ef | grep -v grep | grep ${server_name} | sed -n '1P' | awk '{print $2}') ]]; then
  nohup java -jar -Xms100m -Xmx100m ${current_jar} --server.port=8080 >/dev/null 2>&1 &
  sleep 2
  if [[ -n $(ps -ef | grep -v grep | grep ${server_name} | sed -n '1P' | awk '{print $2}') ]]; then
    echo -e "\033[36m ##: 启动成功(Start successfully) ~ \033[0m" >> $log_file
    echo "服务启动成功" >> $log_file
  else
    echo -e "\033[31m 启动失败(failed to activate) \033[0m" >> $log_file
    echo "服务启动失败" >> $log_file
  fi
else
  echo -e "\033[32m ##: 服务已经在运行中(The service is already running) \033[0m" >> $log_file
  echo "服务已经在运行中" >> $log_file
fi

echo "=================== 更新结束 $(date) ===================" >> $log_file
