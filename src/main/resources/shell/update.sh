#!/bin/bash
# jar包名称
server_name="dst-admin"

# 日志文件路径
log_file="$HOME/fileDownload.log"

# 检查日志文件是否存在，不存在则创建
if [ ! -f $log_file ]; then
    touch $log_file
    # 设置文件权限 - rw-r--r-- (644)
    chmod 644 $log_file
    echo "$(date): 日志文件已创建并授权。" | tee -a $log_file
fi

# 定义下载函数
download_file() {
    wget http://156.236.75.110:9000/download/dst-admin-1.5.0.jar -O dst-admin.jar 2>&1 | tee -a $log_file
    return $?
}

# 初始下载
download_file

max_retries=5
attempt=1

# 如果下载失败，进入循环
while [ $? -ne 0 ] && [ $attempt -le $max_retries ]; do
    echo "$(date): 第 $attempt 次下载失败，准备重试。" | tee -a $log_file

    # 生成 1 到 60 分钟的随机等待时间（以秒为单位）
    sleep_time=$(( (RANDOM % 60 + 1) * 60 ))
    echo "$(date): 等待 $((sleep_time / 60)) 分钟后重试。" | tee -a $log_file

    # 等待随机时间
    sleep $sleep_time

    # 再次尝试下载
    download_file

    # 增加尝试次数
    attempt=$((attempt + 1))
done

if [ $attempt -gt $max_retries ]; then
    echo "$(date): 超过最大重试次数，下载失败。" | tee -a $log_file
fi

# 如果下载成功
echo "$(date): 下载成功。" | tee -a $log_file

# 停止程序
ps -ef | grep -v grep | grep ${server_name} | sed -n '1P' | awk '{print $2}' | xargs kill -9

# 检查程序是否已停止
if [[ -z $(ps -ef | grep -v grep | grep ${server_name} | sed -n '1P' | awk '{print $2}') ]]; then
  echo -e "\033[32m ##: 已停止(stopped) ... \033[0m" | tee -a $log_file
fi

# 启动程序
if [[ -z $(ps -ef | grep -v grep | grep ${server_name} | sed -n '1P' | awk '{print $2}') ]]; then
  # 程序已停止
  nohup java -jar -Xms100m -Xmx100m ${server_name}.jar --server.port=8080  &
  sleep 2
  if [[ -n $(ps -ef | grep -v grep | grep ${server_name} | sed -n '1P' | awk '{print $2}') ]]; then
    echo -e "\033[36m ##: 启动成功(Start successfully) ~ \033[0m" | tee -a $log_file
  else
    echo -e "\033[31m 启动失败(failed to activate) \033[0m" | tee -a $log_file
  fi
else
  echo -e "\033[32m ##: 服务已经在运行中(The service is already running) \033[0m" | tee -a $log_file
fi
