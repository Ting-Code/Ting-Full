#!/usr/bin/env bash
# 多人协作：一键启动本机基础设施（MySQL 自管 + Redis + Nacos）
# 用法：bash scripts/start-infra.sh
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

# shellcheck disable=SC1091
source "$ROOT_DIR/scripts/lib/infra-common.sh"

load_infra_env
ensure_dirs

echo "==> 检查 MySQL (${MYSQL_HOST}:${MYSQL_PORT})"
if ! port_listening "$MYSQL_PORT"; then
  echo "ERROR: MySQL 未在 ${MYSQL_PORT} 监听。请先启动本机 MySQL，并执行 sql/init.sql 初始化库表。"
  exit 1
fi
echo "    MySQL OK"

echo "==> 启动 Redis (${REDIS_PORT})"
start_redis
echo "    Redis OK"

echo "==> 启动 Nacos (${NACOS_PORT})"
start_nacos
echo "    Nacos OK  控制台: http://127.0.0.1:${NACOS_PORT}/nacos  (nacos/nacos)"

echo
echo "基础设施就绪。接下来启动业务："
echo "  mvn -pl ting-user spring-boot:run"
echo "  mvn -pl ting-biz spring-boot:run"
echo "  mvn -pl ting-gateway spring-boot:run"
