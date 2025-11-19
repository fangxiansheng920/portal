-- drop table if exists dic ;
create table if not exists dic
(
    dic_id       int          not null auto_increment primary key,
    dic_name     varchar(32)  not null comment '字典名如：admin.status',
    dic_key      varchar(32)  not null comment '值，如：1',
    dic_value    varchar(64)  not null comment '含义，如：未激活',
    dic_code     varchar(32)  not null comment 'variable name',
    color        varchar(32)  not null default 'black' comment '颜色',
    order_number tinyint      not null default 100 comment '排序',
    remark       varchar(128) null,
    unique index ux_dic_01 (dic_name, dic_key)
) engine = innodb
  default charset = utf8mb4 comment '数据字典表';