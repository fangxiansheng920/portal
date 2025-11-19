# 新建portal_tag表
create table portal_tag
(
    `id`          bigint primary key auto_increment comment '主键id',
    `portal_id`   bigint    not null comment '站点id',
    `tag_id`      bigint    not null comment '标签id',
    `create_time` timestamp not null default CURRENT_TIMESTAMP comment '创建时间',
    unique key `uk_portal_tag` (`portal_id`, `tag_id`) comment '站点id和标签id的唯一组合',
    key `idx_tag_id` (`tag_id`) comment '标签id索引'
);