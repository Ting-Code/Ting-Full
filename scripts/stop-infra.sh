#!/usr/bin/env bash
# 停止脚本拉起的 Redis / Nacos（不影响本机 MySQL 服务）
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

# shellcheck disable=SC1091
source "$ROOT_DIR/scripts/lib/infra-common.sh"

load_infra_env

echo "==> 停止 Nacos"
stop_nacos || true

echo "==> 停止由脚本启动的 Redis"
stop_redis || true

echo "完成（MySQL 请自行在服务管理中停，如需要）。"
