# 给collect表加入is_top字段
alter table collect
    add column is_top smallint not null default 0 comment '置顶';