# 在collect表中增加doCollect字段 1：收藏 0：取消收藏
alter table collect
    add column do_collect smallint not null default 1 comment '是否收藏（1=收藏，0=取消收藏）';