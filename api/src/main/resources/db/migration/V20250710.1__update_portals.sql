# 修改portals表里的accessType字段类型 smallint -> varchar(255)
alter table portals
    modify column access_type varchar(255) default 'PC';