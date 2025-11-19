# 给collect表加入update_time字段
alter table collect
    add column update_time timestamp not null default now() comment '更新时间';