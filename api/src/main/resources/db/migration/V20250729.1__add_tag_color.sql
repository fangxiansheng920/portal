# 给tags表加入color字段
alter table tags
    add column color varchar(30) not null default 'black' comment '颜色';