#!/usr/bin/env bash
# shellcheck shell=bash

load_infra_env() {
  MYSQL_HOST="${MYSQL_HOST:-127.0.0.1}"
  MYSQL_PORT="${MYSQL_PORT:-3306}"
  MYSQL_USER="${MYSQL_USER:-root}"
  MYSQL_PASSWORD="${MYSQL_PASSWORD:-123456}"
  MYSQL_DATABASE="${MYSQL_DATABASE:-ting}"
  REDIS_HOME="${REDIS_HOME:-}"
  REDIS_PORT="${REDIS_PORT:-6379}"
  NACOS_VERSION="${NACOS_VERSION:-2.3.2}"
  NACOS_HOME="${NACOS_HOME:-}"
  NACOS_PORT="${NACOS_PORT:-8848}"

  if [[ -f "$ROOT_DIR/scripts/infra.env.example" ]]; then
    # 先加载示例默认值（可被 local 覆盖）
    set -a
    # shellcheck disable=SC1091
    source "$ROOT_DIR/scripts/infra.env.example"
    set +a
  fi
  if [[ -f "$ROOT_DIR/scripts/infra.local.env" ]]; then
    set -a
    # shellcheck disable=SC1091
    source "$ROOT_DIR/scripts/infra.local.env"
    set +a
  fi

  if [[ -z "${NACOS_HOME}" ]]; then
    NACOS_HOME="$ROOT_DIR/tools/nacos"
  fi
}

ensure_dirs() {
  mkdir -p "$ROOT_DIR/tools" "$ROOT_DIR/tools/pids" "$ROOT_DIR/tools/logs"
}

port_listening() {
  local port="$1"
  # Windows netstat（优先）
  if command -v netstat >/dev/null 2>&1; then
    if netstat -ano 2>/dev/null | grep -E "[:.]${port}[ ]+" | grep -qi LISTENING; then
      return 0
    fi
  fi
  # Git Bash 下再用 PowerShell 兜底，避免编码/过滤差异导致误判
  if command -v powershell.exe >/dev/null 2>&1; then
    local r
    r="$(powershell.exe -NoProfile -Command "if (Get-NetTCPConnection -LocalPort ${port} -State Listen -ErrorAction SilentlyContinue) { 'yes' }" 2>/dev/null | tr -d '\r')"
    [[ "$r" == *yes* ]] && return 0
  fi
  if command -v ss >/dev/null 2>&1; then
    ss -ltn 2>/dev/null | grep -q ":${port} " && return 0
  fi
  return 1
}

resolve_redis_home() {
  if [[ -n "${REDIS_HOME}" && -x "${REDIS_HOME}/redis-server.exe" ]]; then
    echo "$REDIS_HOME"
    return 0
  fi
  local candidates=(
    "D:/app/Redis-8.6.2"
    "C:/Redis"
    "/c/Program Files/Redis"
  )
  local c
  for c in "${candidates[@]}"; do
    if [[ -x "${c}/redis-server.exe" ]]; then
      echo "$c"
      return 0
    fi
  done
  return 1
}

start_redis() {
  if port_listening "$REDIS_PORT"; then
    echo "    端口 ${REDIS_PORT} 已在监听，跳过启动"
    return 0
  fi

  local home
  if ! home="$(resolve_redis_home)"; then
    echo "ERROR: 找不到 redis-server.exe。"
    echo "请安装 Redis for Windows，或在 scripts/infra.local.env 设置 REDIS_HOME。"
    exit 1
  fi

  mkdir -p "${home}/data"
  echo "    使用 Redis: ${home}"
  (
    cd "$home"
    # 后台启动，日志落到项目 tools/logs
    nohup ./redis-server.exe redis.conf --port "$REDIS_PORT" --dir "${home}/data" \
      >"$ROOT_DIR/tools/logs/redis.log" 2>&1 &
    echo $! >"$ROOT_DIR/tools/pids/redis.pid"
  )

  local i
  for i in $(seq 1 20); do
    if port_listening "$REDIS_PORT"; then
      return 0
    fi
    sleep 0.5
  done
  echo "ERROR: Redis 启动超时，查看 tools/logs/redis.log"
  exit 1
}

stop_redis() {
  if [[ -f "$ROOT_DIR/tools/pids/redis.pid" ]]; then
    local pid
    pid="$(cat "$ROOT_DIR/tools/pids/redis.pid" || true)"
    if [[ -n "$pid" ]]; then
      kill "$pid" 2>/dev/null || taskkill //PID "$pid" //F >/dev/null 2>&1 || true
    fi
    rm -f "$ROOT_DIR/tools/pids/redis.pid"
  fi
  # 若是本机服务拉起的，不强制杀，避免误伤
}

ensure_nacos_installed() {
  local home="$NACOS_HOME"
  if [[ -f "${home}/bin/startup.cmd" || -f "${home}/bin/startup.sh" ]]; then
    return 0
  fi

  echo "    未检测到 Nacos，开始下载 ${NACOS_VERSION} ..."
  mkdir -p "$ROOT_DIR/tools"
  local zip="$ROOT_DIR/tools/nacos-server-${NACOS_VERSION}.zip"
  local url_gh="https://github.com/alibaba/nacos/releases/download/${NACOS_VERSION}/nacos-server-${NACOS_VERSION}.zip"

  if command -v curl >/dev/null 2>&1; then
    curl -L --fail --retry 3 -o "$zip" "$url_gh"
  elif command -v powershell.exe >/dev/null 2>&1; then
    powershell.exe -NoProfile -Command "Invoke-WebRequest -Uri '$url_gh' -OutFile '$zip'"
  else
    echo "ERROR: 需要 curl 或 PowerShell 下载 Nacos"
    exit 1
  fi

  rm -rf "$home"
  mkdir -p "$ROOT_DIR/tools/nacos-extract"
  if command -v unzip >/dev/null 2>&1; then
    unzip -q -o "$zip" -d "$ROOT_DIR/tools/nacos-extract"
  else
    powershell.exe -NoProfile -Command "Expand-Archive -Path '$zip' -DestinationPath '$ROOT_DIR/tools/nacos-extract' -Force"
  fi

  # zip 内通常是 nacos/ 目录
  if [[ -d "$ROOT_DIR/tools/nacos-extract/nacos" ]]; then
    mv "$ROOT_DIR/tools/nacos-extract/nacos" "$home"
  else
    mkdir -p "$home"
    mv "$ROOT_DIR/tools/nacos-extract"/* "$home"/ || true
  fi
  rm -rf "$ROOT_DIR/tools/nacos-extract"

  # 单机模式 + 关闭鉴权，降低本地协作成本
  local conf="${home}/conf/application.properties"
  if [[ -f "$conf" ]]; then
    if ! grep -q '^nacos.core.auth.enabled=' "$conf"; then
      echo 'nacos.core.auth.enabled=false' >>"$conf"
    else
      sed -i 's/^nacos.core.auth.enabled=.*/nacos.core.auth.enabled=false/' "$conf" 2>/dev/null || true
    fi
  fi

  echo "    Nacos 已安装到: ${home}"
}

start_nacos() {
  if port_listening "$NACOS_PORT"; then
    echo "    端口 ${NACOS_PORT} 已在监听，跳过启动"
    return 0
  fi

  ensure_nacos_installed

  local home="$NACOS_HOME"
  export JAVA_HOME="${JAVA_HOME:-}"
  if [[ -z "${JAVA_HOME}" ]]; then
    # 常见 JDK 路径兜底
    if [[ -d "/c/Program Files/Java/jdk-21" ]]; then
      export JAVA_HOME="/c/Program Files/Java/jdk-21"
    fi
  fi

  echo "    使用 Nacos: ${home}"
  # Windows 下 startup.cmd 可能占用当前进程，必须后台拉起
  if [[ -f "${home}/bin/startup.cmd" ]]; then
    (
      cd "${home}/bin"
      cmd.exe //c "start \"nacos\" /MIN startup.cmd -m standalone" \
        >"$ROOT_DIR/tools/logs/nacos-start.log" 2>&1
    ) || true
  else
    (
      cd "${home}/bin"
      nohup bash ./startup.sh -m standalone >"$ROOT_DIR/tools/logs/nacos-start.log" 2>&1 &
    )
  fi

  local i
  for i in $(seq 1 60); do
    if port_listening "$NACOS_PORT"; then
      return 0
    fi
    sleep 1
  done
  echo "ERROR: Nacos 启动超时，查看 ${home}/logs 或 tools/logs/nacos-start.log"
  exit 1
}

stop_nacos() {
  local home="${NACOS_HOME:-$ROOT_DIR/tools/nacos}"
  if [[ -f "${home}/bin/shutdown.cmd" ]]; then
    (
      cd "${home}/bin"
      cmd.exe //c "shutdown.cmd" >/dev/null 2>&1 || true
    )
  elif [[ -f "${home}/bin/shutdown.sh" ]]; then
    (
      cd "${home}/bin"
      bash ./shutdown.sh >/dev/null 2>&1 || true
    )
  fi
}
