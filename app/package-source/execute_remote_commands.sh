#!/bin/bash

# SSH到远程服务器并执行命令或脚本
ssh root@chenlong.site << EOF
cd /root/docker-compose/app/mall/
# 假设有一个启动应用的脚本
./mall.sh
EOF
