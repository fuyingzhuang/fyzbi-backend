#
数据库初始化
# @author ambition
#

-- 创建库
create
database if not exists bi;

-- 切换库
use bi;

CREATE TABLE user
(
    id           BIGINT       NOT NULL AUTO_INCREMENT, -- 用户id(主键）
    userAccount  VARCHAR(256) NOT NULL,                -- 账号
    userPassword VARCHAR(512) NOT NULL,                -- 密码
    userName     VARCHAR(256),                         -- 用户昵称
    userAvatar   VARCHAR(1024),                        -- 用户头像
    userRole     VARCHAR(256),                         -- 用户角色
    createTime   DATETIME,                             -- 创建时间
    updateTime   DATETIME,                             -- 更新时间
    isDelete     TINYINT DEFAULT 0,                    -- 是否删除，默认为未删除
    PRIMARY KEY (id),                                  -- 将id设置为主键
    INDEX        idx_userAccount (userAccount)         -- 为userAccount字段创建索引，以提高检索效率
);

CREATE TABLE chart
(
    id         BIGINT NOT NULL AUTO_INCREMENT, -- 图表ID(主键)
    goal       TEXT,                           -- 分析目标
    chartData  TEXT,                           -- 图表数据
    chartType  VARCHAR(128),                   -- 图表类型
    genResult  TEXT,                           -- 生成的分析结论
    userId     BIGINT,                         -- 创建用户ID
    createTime DATETIME,                       -- 创建时间
    updateTime DATETIME,                       -- 更新时间
    isDelete   TINYINT DEFAULT 0,              -- 是否删除，默认为未删除
    PRIMARY KEY (id),                          -- 将id设置为主键
    INDEX      idx_userId (userId)             -- 为userId字段创建索引，以提高检索效率
);
