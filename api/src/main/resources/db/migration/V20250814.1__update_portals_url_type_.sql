# 将portals表的portal_url字段长度从255改为1000
alter table portals
    modify column portal_url varchar(1000) comment '站点链接';