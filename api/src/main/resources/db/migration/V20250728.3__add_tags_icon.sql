# 给tags表加入icon字段
alter table tags
    add column icon varchar(255) comment '图标路径';