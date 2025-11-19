Drop TABLE IF exists `users`;
CREATE TABLE IF not exists `users` (
	`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE COMMENT '用户id，主键，唯一标识',
	`ep_wx_id` VARCHAR(255) COMMENT '企业微信中的ID',
	`unionid` VARCHAR(255) COMMENT '绑定微信用户标识',
	`user_name` VARCHAR(255) COMMENT '用户姓名',
	`password` VARCHAR(255) COMMENT '密码',
	`role` SMALLINT NOT NULL DEFAULT 0 COMMENT '默认角色',
	`avatar` VARCHAR(255) NOT NULL COMMENT '头像',
	`deleted` SMALLINT NOT NULL DEFAULT 0 COMMENT '删除标志位',
	`last_login` TIMESTAMP COMMENT '最后登录时间戳',
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	PRIMARY KEY(`id`)
) COMMENT='用户信息表';


Drop TABLE IF exists `portals`;
CREATE TABLE IF not exists `portals` (
	`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE COMMENT '站点id，主键，唯一标识',
	`user_id` BIGINT NOT NULL COMMENT '创建者id',
	`title` VARCHAR(255) NOT NULL COMMENT '站点名称',
	`portal_url` VARCHAR(255) COMMENT '站点链接',
	`public_flag` SMALLINT NOT NULL DEFAULT 0 COMMENT '是否公共共享，0:私有,1:公共',
	`portal_QRcode` VARCHAR(255) COMMENT '二维码图',
	`status` SMALLINT NOT NULL DEFAULT 0 COMMENT '状态 0正常',
	`description` VARCHAR(255) COMMENT '信息描述',
	`access_type` SMALLINT NOT NULL DEFAULT 0 COMMENT '访问方式,dic:access.type',
	`tag` VARCHAR(255) COMMENT '站点标签便于分类',
	`remark` VARCHAR(255),
	`create_by` VARCHAR(80) COMMENT '创建者',
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	`deleted` SMALLINT NOT NULL DEFAULT 0 COMMENT '删除标志位',
	PRIMARY KEY(`id`)
) COMMENT='门户站点列表';


Drop TABLE IF exists `collect`;
CREATE TABLE IF not exists `collect` (
	`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE COMMENT '收藏id，主键，唯一标识',
	`portal_id` BIGINT NOT NULL COMMENT '站点id',
	`user_id` BIGINT NOT NULL COMMENT '用户id',
	`remark` VARCHAR(255) COMMENT '备注',
	PRIMARY KEY(`id`)
) COMMENT='用户站点收藏关系表';


Drop TABLE IF exists `tags`;
CREATE TABLE IF not exists `tags` (
	`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE COMMENT '标签id，主键，唯一标识',
	`name` VARCHAR(255) NOT NULL COMMENT '标签名称',
	PRIMARY KEY(`id`)
) COMMENT='项目标签表';


Drop TABLE IF exists `user_portals`;
CREATE TABLE IF not exists `user_portals` (
	`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE COMMENT '用户和站点关系id，主键，唯一标识',
	`user_id` BIGINT NOT NULL COMMENT '用户id',
	`portal_id` BIGINT NOT NULL COMMENT '站点id',
	`last_use_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后访问时间',
	`permission` VARCHAR(50) NOT NULL DEFAULT 'normal' COMMENT '表示用户和站点的关系',
	PRIMARY KEY(`id`)
);


ALTER TABLE `collect`
ADD FOREIGN KEY(`portal_id`) REFERENCES `portals`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `collect`
ADD FOREIGN KEY(`user_id`) REFERENCES `users`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;