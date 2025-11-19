# 取消phone_number的默认值
alter table users
    modify column phone_number varchar(20) null;
alter table users
    alter column phone_number drop default;
alter table users
    modify column phone_number varchar(20) not null;

# 取消avatar非空
alter table users
    modify column avatar varchar(255) null;