# 给portal表加入tagId字段
alter table portals
    add column tag_id bigint null comment '标签id';

# 数据迁移
update portals
    left join tags on portals.tag = tags.name
set tag_id = tags.id;