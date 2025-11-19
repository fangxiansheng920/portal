# 给tags表加入icon字段
alter table portals
    add column icon varchar(255) comment '图标路径';