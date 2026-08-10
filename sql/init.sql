-- 仅负责「建库」。表结构请走各服务 Flyway 迁移，不要再手改这文件里的业务表。
--
-- 本地一次性：
--   CREATE DATABASE IF NOT EXISTS ting DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
--
-- 之后启动 ting-user / ting-biz 会自动执行：
--   ting-user: db/migration/V*.sql  → 历史表 flyway_history_user
--   ting-biz:  db/migration/V*.sql  → 历史表 flyway_history_biz

CREATE DATABASE IF NOT EXISTS ting DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
